from datetime import datetime
from flask import Blueprint, jsonify, request
from backend.models import db, User, CreatorProfile, CreatorPermission, CreatorSubmission
from backend.routes.auth_routes import token_required

creator_bp = Blueprint('creator', __name__, url_prefix='/api/creator')

@creator_bp.route('/dashboard', methods=['GET'])
@token_required
def get_creator_dashboard(current_user):
    submissions = CreatorSubmission.query.filter_by(user_id=current_user.id).order_by(CreatorSubmission.submitted_at.desc()).all()
    profile = current_user.profile.to_dict() if current_user.profile else None
    perms = current_user.permissions.to_dict() if current_user.permissions else None
    
    stats = {
        'total_submissions': len(submissions),
        'pending': sum(1 for s in submissions if s.status == 'pending'),
        'under_review': sum(1 for s in submissions if s.status == 'under_review'),
        'changes_requested': sum(1 for s in submissions if s.status == 'changes_requested'),
        'approved': sum(1 for s in submissions if s.status == 'approved'),
        'published': sum(1 for s in submissions if s.status == 'published')
    }
    
    return jsonify({
        'user': current_user.to_dict(),
        'profile': profile,
        'permissions': perms,
        'stats': stats,
        'submissions': [s.to_dict() for s in submissions]
    }), 200

@creator_bp.route('/profile', methods=['GET', 'PUT'])
@token_required
def manage_creator_profile(current_user):
    if request.method == 'GET':
        return jsonify({
            'user': current_user.to_dict(),
            'profile': current_user.profile.to_dict() if current_user.profile else None
        }), 200

    data = request.get_json() or {}
    profile = current_user.profile
    if not profile:
        profile = CreatorProfile(user_id=current_user.id)
        db.session.add(profile)

    if 'full_name' in data:
        current_user.full_name = data['full_name'].strip()
    if 'bio' in data:
        profile.bio = data['bio'].strip()
    if 'portfolio_link' in data:
        profile.portfolio_link = data['portfolio_link'].strip()
    if 'village_origin' in data:
        profile.village_origin = data['village_origin'].strip()
    if 'avatar_url' in data:
        profile.avatar_url = data['avatar_url'].strip()

    db.session.commit()
    return jsonify({
        'message': 'Profile updated successfully',
        'profile': profile.to_dict()
    }), 200

@creator_bp.route('/submit', methods=['POST'])
@token_required
def submit_content(current_user):
    perms = current_user.permissions
    if not perms:
        return jsonify({'error': 'No permissions configured for your account'}), 403

    data = request.get_json() or {}
    submission_type = data.get('submission_type', '').strip().lower()
    title = data.get('title', '').strip()
    description = data.get('description', '').strip()
    content_url = data.get('content_url', '').strip()
    external_youtube_id = data.get('external_youtube_id', '').strip()

    if not title or not description or not submission_type:
        return jsonify({'error': 'Title, description, and submission type are required'}), 400

    # Validate individual permissions
    if submission_type == 'video' and not perms.can_submit_video:
        return jsonify({'error': 'You do not have permission to submit direct video productions. Please request access from the CEO.'}), 403
    elif submission_type == 'youtube_link' and not perms.can_submit_youtube_links:
        return jsonify({'error': 'You do not have permission to submit YouTube links.'}), 403
    elif submission_type == 'photo' and not perms.can_upload_photos:
        return jsonify({'error': 'You do not have permission to upload photos.'}), 403
    elif submission_type == 'article' and not perms.can_submit_articles:
        return jsonify({'error': 'You do not have permission to submit cultural articles.'}), 403
    elif submission_type == 'news' and not perms.can_submit_news:
        return jsonify({'error': 'You do not have permission to submit news updates.'}), 403

    sub = CreatorSubmission(
        user_id=current_user.id,
        submission_type=submission_type,
        title=title,
        description=description,
        content_url=content_url,
        external_youtube_id=external_youtube_id,
        status='pending'  # Creators cannot self-approve per Part 11
    )
    db.session.add(sub)
    db.session.commit()

    return jsonify({
        'message': 'Content submitted successfully! It is now pending CEO review.',
        'submission': sub.to_dict()
    }), 201

@creator_bp.route('/submissions', methods=['GET'])
@token_required
def list_submissions(current_user):
    submissions = CreatorSubmission.query.filter_by(user_id=current_user.id).order_by(CreatorSubmission.submitted_at.desc()).all()
    return jsonify({'submissions': [s.to_dict() for s in submissions]}), 200
