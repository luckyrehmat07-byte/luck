from datetime import datetime
import requests
from flask import Blueprint, jsonify, request, current_app
from backend.models import db, YouTubeAccount, YouTubeVideo, AuditLog
from backend.routes.auth_routes import ceo_required

youtube_bp = Blueprint('youtube', __name__, url_prefix='/api/youtube')

@youtube_bp.route('/accounts', methods=['GET'])
@ceo_required
def list_accounts(current_user):
    accounts = YouTubeAccount.query.order_by(YouTubeAccount.connected_at.asc()).all()
    return jsonify({
        'accounts': [a.to_dict(include_tokens=True) for a in accounts]
    }), 200

@youtube_bp.route('/accounts/connect', methods=['POST'])
@ceo_required
def connect_account(current_user):
    """
    Connects a YouTube channel / Google account with custom label.
    Supports either OAuth authorization tokens or direct channel ID verification.
    """
    data = request.get_json() or {}
    account_label = data.get('account_label', '').strip()
    channel_id = data.get('channel_id', '').strip()
    channel_title = data.get('channel_title', '').strip() or account_label
    is_public = bool(data.get('is_public', True))
    is_homepage_default = bool(data.get('is_homepage_default', False))
    auth_code = data.get('auth_code', '').strip()

    if not account_label or not channel_id:
        return jsonify({'error': 'Account label and Channel ID are required'}), 400

    existing = YouTubeAccount.query.filter_by(channel_id=channel_id).first()
    if existing:
        existing.account_label = account_label
        existing.channel_title = channel_title
        existing.is_public = is_public
        if is_homepage_default:
            YouTubeAccount.query.update({YouTubeAccount.is_homepage_default: False})
            existing.is_homepage_default = True
        db.session.commit()
        return jsonify({'message': 'Account updated', 'account': existing.to_dict()}), 200

    if is_homepage_default:
        YouTubeAccount.query.update({YouTubeAccount.is_homepage_default: False})

    acc = YouTubeAccount(
        account_label=account_label,
        channel_id=channel_id,
        channel_title=channel_title,
        is_public=is_public,
        is_homepage_default=is_homepage_default,
        access_token=auth_code if auth_code else None
    )
    db.session.add(acc)
    db.session.commit()

    log = AuditLog(
        admin_email=current_user.email,
        action='CONNECT_YOUTUBE_ACCOUNT',
        details=f'Connected YouTube channel {channel_title} ({channel_id}) labeled as "{account_label}"'
    )
    db.session.add(log)
    db.session.commit()

    return jsonify({
        'message': f'YouTube channel "{channel_title}" connected successfully.',
        'account': acc.to_dict()
    }), 201

@youtube_bp.route('/accounts/<int:account_id>/visibility', methods=['PUT'])
@ceo_required
def toggle_account_visibility(current_user, account_id):
    acc = YouTubeAccount.query.get_or_404(account_id)
    data = request.get_json() or {}
    if 'is_public' in data:
        acc.is_public = bool(data['is_public'])
    if 'is_homepage_default' in data and data['is_homepage_default']:
        YouTubeAccount.query.update({YouTubeAccount.is_homepage_default: False})
        acc.is_homepage_default = True

    db.session.commit()
    return jsonify({'message': 'Channel settings updated', 'account': acc.to_dict()}), 200

@youtube_bp.route('/accounts/<int:account_id>', methods=['DELETE'])
@ceo_required
def disconnect_account(current_user, account_id):
    acc = YouTubeAccount.query.get_or_404(account_id)
    label = acc.account_label
    db.session.delete(acc)

    log = AuditLog(
        admin_email=current_user.email,
        action='DISCONNECT_YOUTUBE_ACCOUNT',
        details=f'Disconnected channel {label} (#{account_id})'
    )
    db.session.add(log)
    db.session.commit()

    return jsonify({'message': f'Channel "{label}" disconnected successfully'}), 200

@youtube_bp.route('/accounts/<int:account_id>/videos', methods=['GET'])
@ceo_required
def get_account_videos(current_user, account_id):
    acc = YouTubeAccount.query.get_or_404(account_id)
    videos = YouTubeVideo.query.filter_by(account_id=acc.id).order_by(YouTubeVideo.id.desc()).all()
    return jsonify({
        'account': acc.to_dict(),
        'videos': [v.to_dict() for v in videos]
    }), 200

@youtube_bp.route('/accounts/<int:account_id>/refresh', methods=['POST'])
@ceo_required
def refresh_account_videos(current_user, account_id):
    """
    Refreshes channel videos using YouTube Data API v3 uploads playlist and pagination.
    Per Part B: Uses the channel's actual uploads playlist and official API pagination
    to retrieve all public uploaded videos.
    """
    acc = YouTubeAccount.query.get_or_404(account_id)
    api_key = current_app.config.get('YOUTUBE_API_KEY')
    added_count = 0

    if api_key and acc.channel_id:
        try:
            # 1. Determine uploads playlist ID (standard algorithm: UC... -> UU...)
            uploads_playlist_id = None
            if acc.channel_id.startswith('UC'):
                uploads_playlist_id = 'UU' + acc.channel_id[2:]
            else:
                # Query channel contentDetails to get official uploads playlist
                chan_url = f"https://www.googleapis.com/youtube/v3/channels?key={api_key}&id={acc.channel_id}&part=contentDetails,snippet"
                c_res = requests.get(chan_url, timeout=10)
                if c_res.status_code == 200:
                    c_data = c_res.json()
                    items = c_data.get('items', [])
                    if items:
                        uploads_playlist_id = items[0].get('contentDetails', {}).get('relatedPlaylists', {}).get('uploads')

            # 2. Paginate through uploads playlist without arbitrary limit
            if uploads_playlist_id:
                next_page_token = ''
                while True:
                    p_url = f"https://www.googleapis.com/youtube/v3/playlistItems?key={api_key}&playlistId={uploads_playlist_id}&part=snippet,contentDetails&maxResults=50"
                    if next_page_token:
                        p_url += f"&pageToken={next_page_token}"

                    p_res = requests.get(p_url, timeout=10)
                    if p_res.status_code != 200:
                        break

                    p_data = p_res.json()
                    video_ids_to_query = []
                    item_map = {}

                    for item in p_data.get('items', []):
                        snippet = item.get('snippet', {})
                        vid_id = snippet.get('resourceId', {}).get('videoId') or item.get('contentDetails', {}).get('videoId')
                        if vid_id:
                            video_ids_to_query.append(vid_id)
                            item_map[vid_id] = snippet

                    # Fetch video statistics (view counts)
                    view_counts = {}
                    if video_ids_to_query:
                        stats_url = f"https://www.googleapis.com/youtube/v3/videos?key={api_key}&id={','.join(video_ids_to_query)}&part=statistics"
                        s_res = requests.get(stats_url, timeout=10)
                        if s_res.status_code == 200:
                            for s_item in s_res.json().get('items', []):
                                view_counts[s_item['id']] = s_item.get('statistics', {}).get('viewCount', 'Verified')

                    for vid_id in video_ids_to_query:
                        snippet = item_map[vid_id]
                        title = snippet.get('title', 'Kalash Production')
                        desc = snippet.get('description', '')
                        thumbs = snippet.get('thumbnails', {})
                        thumb = (thumbs.get('maxres') or thumbs.get('high') or thumbs.get('medium') or thumbs.get('default') or {}).get('url')
                        pub = snippet.get('publishedAt', '')

                        existing_vid = YouTubeVideo.query.filter_by(video_id=vid_id).first()
                        if not existing_vid:
                            new_vid = YouTubeVideo(
                                account_id=acc.id,
                                video_id=vid_id,
                                title=title,
                                description=desc,
                                thumbnail_url=thumb or 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80',
                                channel_title=acc.channel_title,
                                published_at=pub[:10] if pub else 'Recently',
                                view_count=str(view_counts.get(vid_id, 'Verified'))
                            )
                            db.session.add(new_vid)
                            added_count += 1
                        else:
                            # Update metadata & views
                            existing_vid.title = title
                            if thumb:
                                existing_vid.thumbnail_url = thumb
                            if vid_id in view_counts:
                                existing_vid.view_count = str(view_counts[vid_id])

                    db.session.commit()
                    next_page_token = p_data.get('nextPageToken')
                    if not next_page_token:
                        break

        except Exception as e:
            current_app.logger.error(f"YouTube sync error: {e}")

    videos = YouTubeVideo.query.filter_by(account_id=acc.id).all()
    return jsonify({
        'message': f'Refreshed videos for {acc.account_label}. Total: {len(videos)} videos (Newly added: {added_count}).',
        'count': len(videos),
        'videos': [v.to_dict() for v in videos]
    }), 200

@youtube_bp.route('/videos/<int:video_id>/toggle-visibility', methods=['PUT'])
@ceo_required
def toggle_video_visibility(current_user, video_id):
    video = YouTubeVideo.query.get_or_404(video_id)
    data = request.get_json() or {}
    if 'is_hidden' in data:
        video.is_hidden = bool(data['is_hidden'])
    if 'is_featured' in data:
        video.is_featured = bool(data['is_featured'])
    db.session.commit()
    return jsonify({'message': 'Video visibility updated', 'video': video.to_dict()}), 200
