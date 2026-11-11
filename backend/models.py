from datetime import datetime
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

db = SQLAlchemy()

class User(db.Model):
    __tablename__ = 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(120), unique=True, nullable=False, index=True)
    password_hash = db.Column(db.String(256), nullable=False)
    full_name = db.Column(db.String(120), nullable=False)
    role = db.Column(db.String(20), default='public', nullable=False)  # 'ceo', 'creator', 'public'
    status = db.Column(db.String(20), default='pending', nullable=False)  # 'pending', 'approved', 'suspended'
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    profile = db.relationship('CreatorProfile', backref='user', uselist=False, cascade='all, delete-orphan')
    permissions = db.relationship('CreatorPermission', backref='user', uselist=False, cascade='all, delete-orphan')
    submissions = db.relationship('CreatorSubmission', backref='author', lazy='dynamic')
    
    def set_password(self, password):
        self.password_hash = generate_password_hash(password)
        
    def check_password(self, password):
        return check_password_hash(self.password_hash, password)
        
    def to_dict(self):
        return {
            'id': self.id,
            'email': self.email,
            'full_name': self.full_name,
            'role': self.role,
            'status': self.status,
            'created_at': self.created_at.isoformat() if self.created_at else None,
            'permissions': self.permissions.to_dict() if self.permissions else None
        }

class CreatorPermission(db.Model):
    __tablename__ = 'creator_permissions'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False, unique=True)
    can_submit_video = db.Column(db.Boolean, default=False)
    can_upload_photos = db.Column(db.Boolean, default=True)
    can_submit_articles = db.Column(db.Boolean, default=True)
    can_submit_news = db.Column(db.Boolean, default=False)
    can_submit_youtube_links = db.Column(db.Boolean, default=True)
    can_publish = db.Column(db.Boolean, default=False)
    
    def to_dict(self):
        return {
            'can_submit_video': self.can_submit_video,
            'can_upload_photos': self.can_upload_photos,
            'can_submit_articles': self.can_submit_articles,
            'can_submit_news': self.can_submit_news,
            'can_submit_youtube_links': self.can_submit_youtube_links,
            'can_publish': self.can_publish
        }

class AdminSetupState(db.Model):
    __tablename__ = 'admin_setup_state'

    id = db.Column(db.Integer, primary_key=True)
    is_initialized = db.Column(db.Boolean, default=False, nullable=False)
    setup_token_hash = db.Column(db.String(256), nullable=False)
    recovery_key_hash = db.Column(db.String(256), nullable=True)
    initialized_at = db.Column(db.DateTime, nullable=True)
    initialized_ip = db.Column(db.String(50), nullable=True)

    def to_dict(self):
        return {
            'is_initialized': self.is_initialized,
            'initialized_at': self.initialized_at.isoformat() if self.initialized_at else None
        }

class CreatorProfile(db.Model):
    __tablename__ = 'creator_profiles'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False, unique=True)
    bio = db.Column(db.Text, nullable=True)
    portfolio_link = db.Column(db.String(255), nullable=True)
    village_origin = db.Column(db.String(100), default='Kalash Valley')
    avatar_url = db.Column(db.String(255), nullable=True)
    is_featured = db.Column(db.Boolean, default=False)
    
    def to_dict(self):
        return {
            'id': self.id,
            'user_id': self.user_id,
            'full_name': self.user.full_name if self.user else '',
            'bio': self.bio,
            'portfolio_link': self.portfolio_link,
            'village_origin': self.village_origin,
            'avatar_url': self.avatar_url,
            'is_featured': self.is_featured
        }

class Film(db.Model):
    __tablename__ = 'films'
    
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    category = db.Column(db.String(50), nullable=False)  # 'Short Films', 'Drama', 'Cultural Films', 'Documentary', etc.
    synopsis = db.Column(db.Text, nullable=False)
    director = db.Column(db.String(120), default='Lucky Rehmat Aspayi')
    production_credits = db.Column(db.String(200), default='Ishpata Films Production')
    release_year = db.Column(db.String(10), default='2026')
    duration = db.Column(db.String(20), default='18 min')
    poster_url = db.Column(db.String(255), nullable=True)
    youtube_video_id = db.Column(db.String(50), nullable=False)
    is_featured = db.Column(db.Boolean, default=False)
    is_published = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'category': self.category,
            'synopsis': self.synopsis,
            'director': self.director,
            'production_credits': self.production_credits,
            'release_year': self.release_year,
            'duration': self.duration,
            'poster_url': self.poster_url,
            'youtube_video_id': self.youtube_video_id,
            'youtube_url': f"https://www.youtube.com/watch?v={self.youtube_video_id}",
            'is_featured': self.is_featured,
            'is_published': self.is_published,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

class YouTubeAccount(db.Model):
    __tablename__ = 'youtube_accounts'
    
    id = db.Column(db.Integer, primary_key=True)
    account_label = db.Column(db.String(100), nullable=False)  # e.g. "Ishpata Films — Account 1"
    channel_id = db.Column(db.String(100), nullable=False)
    channel_title = db.Column(db.String(150), nullable=False)
    is_public = db.Column(db.Boolean, default=True)
    is_homepage_default = db.Column(db.Boolean, default=False)
    access_token = db.Column(db.Text, nullable=True)
    refresh_token = db.Column(db.Text, nullable=True)
    token_expiry = db.Column(db.DateTime, nullable=True)
    connected_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    videos = db.relationship('YouTubeVideo', backref='account', lazy='dynamic', cascade='all, delete-orphan')
    
    def to_dict(self, include_tokens=False):
        data = {
            'id': self.id,
            'account_label': self.account_label,
            'channel_id': self.channel_id,
            'channel_title': self.channel_title,
            'is_public': self.is_public,
            'is_homepage_default': self.is_homepage_default,
            'connected_at': self.connected_at.isoformat() if self.connected_at else None
        }
        if include_tokens:
            data['has_tokens'] = bool(self.access_token or self.refresh_token)
        return data

class YouTubeVideo(db.Model):
    __tablename__ = 'youtube_videos'
    
    id = db.Column(db.Integer, primary_key=True)
    account_id = db.Column(db.Integer, db.ForeignKey('youtube_accounts.id'), nullable=False)
    video_id = db.Column(db.String(50), nullable=False, unique=True)
    title = db.Column(db.String(255), nullable=False)
    description = db.Column(db.Text, nullable=True)
    thumbnail_url = db.Column(db.String(255), nullable=False)
    channel_title = db.Column(db.String(150), nullable=False)
    published_at = db.Column(db.String(50), nullable=True)
    view_count = db.Column(db.String(50), default='Verified')
    is_featured = db.Column(db.Boolean, default=False)
    is_hidden = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'account_id': self.account_id,
            'video_id': self.video_id,
            'title': self.title,
            'description': self.description,
            'thumbnail_url': self.thumbnail_url,
            'channel_title': self.channel_title,
            'published_at': self.published_at,
            'view_count': self.view_count,
            'is_featured': self.is_featured,
            'is_hidden': self.is_hidden,
            'youtube_url': f"https://www.youtube.com/watch?v={self.video_id}"
        }

class CultureStory(db.Model):
    __tablename__ = 'culture_stories'
    
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    category = db.Column(db.String(80), nullable=False)  # Heritage, Wool-Spinning, Clothing, Traditions, Youth
    summary = db.Column(db.Text, nullable=False)
    body = db.Column(db.Text, nullable=False)
    image_url = db.Column(db.String(255), nullable=True)
    cultural_preservation_note = db.Column(db.String(255), default='Documented in collaboration with Kalash community elders.')
    is_published = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'category': self.category,
            'summary': self.summary,
            'body': self.body,
            'image_url': self.image_url,
            'cultural_preservation_note': self.cultural_preservation_note,
            'is_published': self.is_published,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

class CreatorSubmission(db.Model):
    __tablename__ = 'creator_submissions'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    submission_type = db.Column(db.String(50), nullable=False)  # 'video', 'youtube_link', 'photo', 'article', 'news'
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text, nullable=False)
    content_url = db.Column(db.String(255), nullable=True)
    external_youtube_id = db.Column(db.String(50), nullable=True)
    status = db.Column(db.String(30), default='pending', nullable=False)  # 'pending', 'under_review', 'changes_requested', 'approved', 'rejected', 'published'
    ceo_feedback = db.Column(db.Text, nullable=True)
    submitted_at = db.Column(db.DateTime, default=datetime.utcnow)
    reviewed_at = db.Column(db.DateTime, nullable=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'user_id': self.user_id,
            'author_name': self.author.full_name if self.author else 'Creator',
            'author_email': self.author.email if self.author else '',
            'submission_type': self.submission_type,
            'title': self.title,
            'description': self.description,
            'content_url': self.content_url,
            'external_youtube_id': self.external_youtube_id,
            'status': self.status,
            'ceo_feedback': self.ceo_feedback,
            'submitted_at': self.submitted_at.isoformat() if self.submitted_at else None,
            'reviewed_at': self.reviewed_at.isoformat() if self.reviewed_at else None
        }

class NewsArticle(db.Model):
    __tablename__ = 'news_articles'
    
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(255), nullable=False)
    summary = db.Column(db.Text, nullable=False)
    content = db.Column(db.Text, nullable=False)
    category = db.Column(db.String(80), default='Production Update')
    author_name = db.Column(db.String(120), default='Ishpata Films Editorial')
    cover_image_url = db.Column(db.String(255), nullable=True)
    is_published = db.Column(db.Boolean, default=True)
    published_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'summary': self.summary,
            'content': self.content,
            'category': self.category,
            'author_name': self.author_name,
            'cover_image_url': self.cover_image_url,
            'is_published': self.is_published,
            'published_at': self.published_at.isoformat() if self.published_at else None
        }

class ArtistProfile(db.Model):
    __tablename__ = 'artist_profiles'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    discipline = db.Column(db.String(100), nullable=False)  # Traditional Flute, Folk Vocals, Dhol, Visual Arts
    bio = db.Column(db.Text, nullable=False)
    village = db.Column(db.String(100), default='Kalash Valley')
    external_link = db.Column(db.String(255), nullable=True)
    image_url = db.Column(db.String(255), nullable=True)
    is_published = db.Column(db.Boolean, default=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'discipline': self.discipline,
            'bio': self.bio,
            'village': self.village,
            'external_link': self.external_link,
            'image_url': self.image_url,
            'is_published': self.is_published
        }

class TalentCampaign(db.Model):
    __tablename__ = 'talent_campaigns'
    
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    talent_name = db.Column(db.String(120), nullable=False)
    description = db.Column(db.Text, nullable=False)
    goal_amount = db.Column(db.Integer, default=0)
    current_amount = db.Column(db.Integer, default=0)
    status_message = db.Column(db.String(100), default='Donations opening soon.')
    is_active = db.Column(db.Boolean, default=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'talent_name': self.talent_name,
            'description': self.description,
            'goal_amount': self.goal_amount,
            'current_amount': self.current_amount,
            'status_message': self.status_message,
            'is_active': self.is_active
        }

class AuditLog(db.Model):
    __tablename__ = 'audit_logs'
    
    id = db.Column(db.Integer, primary_key=True)
    admin_email = db.Column(db.String(120), nullable=False)
    action = db.Column(db.String(100), nullable=False)
    details = db.Column(db.Text, nullable=True)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'admin_email': self.admin_email,
            'action': self.action,
            'details': self.details,
            'timestamp': self.timestamp.isoformat() if self.timestamp else None
        }
