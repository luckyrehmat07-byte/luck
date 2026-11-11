from datetime import datetime
from flask import Blueprint, jsonify, request
from backend.models import (
    db, User, CreatorProfile, CreatorPermission, CreatorSubmission,
    Film, YouTubeVideo, YouTubeAccount, CultureStory, NewsArticle, AuditLog
)
from backend.routes.auth_routes import ceo_required

ceo_bp = Blueprint('ceo', __name__, url_prefix='/api/ceo')

def log_action(admin_email, action, details):
    log = AuditLog(
        admin_email=admin_email,
        action=action,
        details=details
    )
    db.session.add(log)
    db.session.commit()

@ceo_bp.route('/overview', methods=['GET'])
@ceo_required
def get_overview(current_user):
    total_creators = User.query.filter_by(role='creator').count()
    pending_creators = User.query.filter_by(role='creator', status='pending').count()
    pending_submissions = CreatorSubmission.query.filter_by(status='pending').count()
    total_films = Film.query.count()
    published_films = Film.query.filter_by(is_published=True).count()
    connected_channels = YouTubeAccount.query.count()
    public_channels = YouTubeAccount.query.filter_by(is_public=True).count()
    total_videos = YouTubeVideo.query.count()

    recent_submissions = CreatorSubmission.query.order_by(CreatorSubmission.submitted_at.desc()).limit(5).all()
    recent_logs = AuditLog.query.order_by(AuditLog.timestamp.desc()).limit(10).all()

    return jsonify({
        'metrics': {
            'total_creators': total_creators,
            'pending_creators': pending_creators,
            'pending_submissions': pending_submissions,
            'total_films': total_films,
            'published_films': published_films,
            'connected_channels': connected_channels,
            'public_channels': public_channels,
            'total_videos': total_videos
        },
        'recent_submissions': [s.to_dict() for s in recent_submissions],
        'recent_audit_logs': [l.to_dict() for l in recent_logs]
    }), 200

@ceo_bp.route('/creators', methods=['GET'])
@ceo_required
def list_creators(current_user):
    status_filter = request.args.get('status')
    query = User.query.filter_by(role='creator')
    if status_filter:
        query = query.filter_by(status=status_filter)
    creators = query.order_by(User.created_at.desc()).all()
    
    result = []
    for c in creators:
        data = c.to_dict()
        if c.profile:
            data['profile'] = c.profile.to_dict()
        result.append(data)
    return jsonify({'creators': result}), 200

@ceo_bp.route('/creators/<int:creator_id>/status', methods=['PUT'])
@ceo_required
def update_creator_status(current_user, creator_id):
    creator = User.query.get_or_404(creator_id)
    data = request.get_json() or {}
    new_status = data.get('status')
    if new_status not in ['approved', 'rejected', 'suspended', 'pending']:
        return jsonify({'error': 'Invalid status'}), 400

    old_status = creator.status
    creator.status = new_status
    log_action(current_user.email, f'CREATOR_STATUS_{new_status.upper()}', f'Changed creator {creator.email} status from {old_status} to {new_status}')
    return jsonify({'message': f'Creator status updated to {new_status}', 'creator': creator.to_dict()}), 200

@ceo_bp.route('/creators/<int:creator_id>/permissions', methods=['PUT'])
@ceo_required
def update_creator_permissions(current_user, creator_id):
    creator = User.query.get_or_404(creator_id)
    perms = creator.permissions
    if not perms:
        perms = CreatorPermission(user_id=creator.id)
        db.session.add(perms)

    data = request.get_json() or {}
    for key in ['can_submit_video', 'can_upload_photos', 'can_submit_articles', 'can_submit_news', 'can_submit_youtube_links', 'can_publish']:
        if key in data:
            setattr(perms, key, bool(data[key]))

    log_action(current_user.email, 'UPDATE_CREATOR_PERMISSIONS', f'Updated granular permissions for {creator.email}: {data}')
    return jsonify({'message': 'Permissions updated successfully', 'permissions': perms.to_dict()}), 200

@ceo_bp.route('/submissions', methods=['GET'])
@ceo_required
def list_submissions(current_user):
    status_filter = request.args.get('status')
    query = CreatorSubmission.query
    if status_filter:
        query = query.filter_by(status=status_filter)
    submissions = query.order_by(CreatorSubmission.submitted_at.desc()).all()
    return jsonify({'submissions': [s.to_dict() for s in submissions]}), 200

@ceo_bp.route('/submissions/<int:submission_id>/review', methods=['PUT'])
@ceo_required
def review_submission(current_user, submission_id):
    sub = CreatorSubmission.query.get_or_404(submission_id)
    data = request.get_json() or {}
    status = data.get('status')
    feedback = data.get('feedback', '')

    if status not in ['approved', 'rejected', 'changes_requested', 'under_review', 'published']:
        return jsonify({'error': 'Invalid review status'}), 400

    sub.status = status
    sub.ceo_feedback = feedback
    sub.reviewed_at = datetime.utcnow()

    # If publishing as an article or film directly:
    if status == 'published' and sub.submission_type in ['article', 'news']:
        article = NewsArticle(
            title=sub.title,
            summary=sub.description[:180],
            content=sub.description,
            category='Cultural Story' if sub.submission_type == 'article' else 'Community News',
            author_name=sub.author.full_name if sub.author else 'Creator',
            cover_image_url=sub.content_url
        )
        db.session.add(article)

    log_action(current_user.email, f'SUBMISSION_{status.upper()}', f'Submission #{sub.id} ("{sub.title}") set to {status}')
    return jsonify({'message': f'Submission marked as {status}', 'submission': sub.to_dict()}), 200

@ceo_bp.route('/films', methods=['POST'])
@ceo_required
def create_film(current_user):
    data = request.get_json() or {}
    title = data.get('title')
    category = data.get('category', 'Cultural Films')
    synopsis = data.get('synopsis', '')
    youtube_video_id = data.get('youtube_video_id', '')

    if not title or not youtube_video_id:
        return jsonify({'error': 'Title and YouTube video ID are required'}), 400

    film = Film(
        title=title,
        category=category,
        synopsis=synopsis,
        director=data.get('director', 'Lucky Rehmat Aspayi'),
        production_credits=data.get('production_credits', 'Ishpata Films'),
        release_year=data.get('release_year', '2026'),
        duration=data.get('duration', '15 min'),
        poster_url=data.get('poster_url', ''),
        youtube_video_id=youtube_video_id,
        is_featured=bool(data.get('is_featured', False)),
        is_published=bool(data.get('is_published', True))
    )
    db.session.add(film)
    log_action(current_user.email, 'CREATE_FILM', f'Created film {title} (YT: {youtube_video_id})')
    return jsonify({'message': 'Film created successfully', 'film': film.to_dict()}), 201

@ceo_bp.route('/films/<int:film_id>', methods=['PUT', 'DELETE'])
@ceo_required
def manage_film(current_user, film_id):
    film = Film.query.get_or_404(film_id)
    if request.method == 'DELETE':
        db.session.delete(film)
        log_action(current_user.email, 'DELETE_FILM', f'Deleted film #{film_id}')
        return jsonify({'message': 'Film deleted'}), 200

    data = request.get_json() or {}
    for key in ['title', 'category', 'synopsis', 'director', 'production_credits', 'release_year', 'duration', 'poster_url', 'youtube_video_id']:
        if key in data:
            setattr(film, key, data[key])
    if 'is_featured' in data:
        film.is_featured = bool(data['is_featured'])
    if 'is_published' in data:
        film.is_published = bool(data['is_published'])

    log_action(current_user.email, 'UPDATE_FILM', f'Updated film #{film_id} ({film.title})')
    return jsonify({'message': 'Film updated successfully', 'film': film.to_dict()}), 200

@ceo_bp.route('/audit-logs', methods=['GET'])
@ceo_required
def get_audit_logs(current_user):
    logs = AuditLog.query.order_by(AuditLog.timestamp.desc()).limit(100).all()
    return jsonify({'logs': [l.to_dict() for l in logs]}), 200
