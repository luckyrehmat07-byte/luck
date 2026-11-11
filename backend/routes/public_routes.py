from flask import Blueprint, jsonify, request
from backend.models import db, Film, YouTubeVideo, YouTubeAccount, CultureStory, CreatorProfile, NewsArticle, ArtistProfile, TalentCampaign

public_bp = Blueprint('public', __name__, url_prefix='/api/public')

@public_bp.route('/home', methods=['GET'])
def get_homepage():
    # 1. Featured Films
    featured_films = Film.query.filter_by(is_published=True, is_featured=True).limit(5).all()
    if not featured_films:
        featured_films = Film.query.filter_by(is_published=True).limit(5).all()

    # 2. Latest YouTube Videos (from public channels only)
    public_accounts = YouTubeAccount.query.filter_by(is_public=True).all()
    public_acc_ids = [acc.id for acc in public_accounts]
    latest_videos = YouTubeVideo.query.filter(
        YouTubeVideo.account_id.in_(public_acc_ids),
        YouTubeVideo.is_hidden == False
    ).order_by(YouTubeVideo.id.desc()).limit(6).all() if public_acc_ids else []

    # 3. Culture highlights
    culture_highlights = CultureStory.query.filter_by(is_published=True).limit(4).all()

    # 4. Featured creators
    creators = CreatorProfile.query.filter_by(is_featured=True).limit(4).all()

    # 5. Latest News
    news = NewsArticle.query.filter_by(is_published=True).order_by(NewsArticle.published_at.desc()).limit(3).all()

    # 6. Music & Artists
    artists = ArtistProfile.query.filter_by(is_published=True).limit(4).all()

    # 7. Support Local Talent
    campaigns = TalentCampaign.query.filter_by(is_active=True).limit(3).all()

    return jsonify({
        'brand': {
            'name': 'ISHPATA FILMS',
            'tagline': 'KALASH TO THE WORLD',
            'supporting_line': 'Stories Beyond Borders. Culture Beyond Generations.',
            'founder': 'Lucky Rehmat Aspayi'
        },
        'featured_films': [f.to_dict() for f in featured_films],
        'latest_videos': [v.to_dict() for v in latest_videos],
        'culture_highlights': [c.to_dict() for c in culture_highlights],
        'creators': [c.to_dict() for c in creators],
        'news': [n.to_dict() for n in news],
        'artists': [a.to_dict() for a in artists],
        'campaigns': [c.to_dict() for c in campaigns]
    }), 200

@public_bp.route('/films', methods=['GET'])
def get_films():
    category = request.args.get('category')
    search = request.args.get('search')
    query = Film.query.filter_by(is_published=True)
    if category and category.lower() != 'all':
        query = query.filter_by(category=category)
    if search:
        query = query.filter(Film.title.ilike(f'%{search}%') | Film.synopsis.ilike(f'%{search}%'))
    films = query.order_by(Film.id.desc()).all()
    return jsonify({'films': [f.to_dict() for f in films]}), 200

@public_bp.route('/videos', methods=['GET'])
def get_videos():
    channel_id = request.args.get('channel_id')
    search = request.args.get('search')

    public_accounts = YouTubeAccount.query.filter_by(is_public=True).all()
    public_acc_ids = [acc.id for acc in public_accounts]
    if not public_acc_ids:
        return jsonify({'videos': [], 'message': 'No public YouTube channels connected yet.'}), 200

    query = YouTubeVideo.query.filter(
        YouTubeVideo.account_id.in_(public_acc_ids),
        YouTubeVideo.is_hidden == False
    )

    if channel_id and channel_id.lower() != 'all':
        acc = YouTubeAccount.query.filter_by(channel_id=channel_id).first()
        if acc:
            query = query.filter_by(account_id=acc.id)

    if search:
        query = query.filter(YouTubeVideo.title.ilike(f'%{search}%') | YouTubeVideo.description.ilike(f'%{search}%'))

    videos = query.order_by(YouTubeVideo.id.desc()).all()
    return jsonify({
        'videos': [v.to_dict() for v in videos],
        'channels': [{'channel_id': a.channel_id, 'channel_title': a.channel_title} for a in public_accounts]
    }), 200

@public_bp.route('/culture', methods=['GET'])
def get_culture():
    category = request.args.get('category')
    query = CultureStory.query.filter_by(is_published=True)
    if category and category.lower() != 'all':
        query = query.filter_by(category=category)
    stories = query.order_by(CultureStory.id.asc()).all()
    return jsonify({'stories': [s.to_dict() for s in stories]}), 200

@public_bp.route('/creators', methods=['GET'])
def get_creators():
    profiles = CreatorProfile.query.join(CreatorProfile.user).filter(
        CreatorProfile.user.has(status='approved')
    ).all()
    return jsonify({'creators': [p.to_dict() for p in profiles]}), 200

@public_bp.route('/news', methods=['GET'])
def get_news():
    category = request.args.get('category')
    query = NewsArticle.query.filter_by(is_published=True)
    if category and category.lower() != 'all':
        query = query.filter_by(category=category)
    articles = query.order_by(NewsArticle.published_at.desc()).all()
    return jsonify({'news': [a.to_dict() for a in articles]}), 200

@public_bp.route('/artists', methods=['GET'])
def get_artists():
    artists = ArtistProfile.query.filter_by(is_published=True).all()
    return jsonify({'artists': [a.to_dict() for a in artists]}), 200

@public_bp.route('/campaigns', methods=['GET'])
def get_campaigns():
    campaigns = TalentCampaign.query.filter_by(is_active=True).all()
    return jsonify({'campaigns': [c.to_dict() for c in campaigns]}), 200

@public_bp.route('/about', methods=['GET'])
def get_about():
    return jsonify({
        'brand': 'ISHPATA FILMS',
        'tagline': 'KALASH TO THE WORLD',
        'supporting_line': 'Stories Beyond Borders. Culture Beyond Generations.',
        'founder': 'Lucky Rehmat Aspayi',
        'mission': 'To document, preserve, promote and share authentic Kalash culture, original films, music, stories, traditions, artists, young creators and creative productions with audiences around the world.',
        'core_values': [
            'Authentic Kalash Cultural Integrity',
            'Independent International Cinema Standards',
            'Community-Led Storytelling and Youth Empowerment',
            'Preservation of Oral Traditions, Music, and Textile Heritage',
            'Global Educational and Cultural Exchange'
        ]
    }), 200

@public_bp.route('/contact', methods=['GET', 'POST'])
def handle_contact():
    if request.method == 'POST':
        data = request.get_json() or {}
        return jsonify({
            'message': 'Thank you for reaching out to Ishpata Films. Your message has been received by our editorial team.',
            'status': 'received'
        }), 200
    return jsonify({
        'organization': 'ISHPATA FILMS',
        'founder_director': 'Lucky Rehmat Aspayi',
        'contact_email': 'contact@ishpatafilms.com',
        'press_email': 'press@ishpatafilms.com',
        'location': 'Kalash Valley, Chitral, Khyber Pakhtunkhwa',
        'social_links': {
            'youtube': 'https://www.youtube.com',
            'platform': 'ISHPATA FILMS - Kalash to the World'
        }
    }), 200
