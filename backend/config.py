import os
from datetime import timedelta

BASE_DIR = os.path.abspath(os.path.dirname(__file__))

class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY', 'ishpata-films-kalash-secret-key-change-in-prod')
    JWT_SECRET_KEY = os.environ.get('JWT_SECRET_KEY', 'ishpata-jwt-secret-key-kalash-prod')
    JWT_EXPIRATION_HOURS = 24
    
    # Database: SQLite for local/testing, PostgreSQL for production deployment
    DATABASE_URL = os.environ.get('DATABASE_URL')
    if DATABASE_URL and DATABASE_URL.startswith("postgres://"):
        DATABASE_URL = DATABASE_URL.replace("postgres://", "postgresql://", 1)
    
    SQLALCHEMY_DATABASE_URI = DATABASE_URL or f"sqlite:///{os.path.join(BASE_DIR, 'ishpata.db')}"
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # CEO Configuration
    CEO_EMAIL = "luckyrehmat07@gmail.com"
    
    # YouTube API Configuration
    YOUTUBE_API_KEY = os.environ.get('YOUTUBE_API_KEY', '')
    GOOGLE_CLIENT_ID = os.environ.get('GOOGLE_CLIENT_ID', '')
    GOOGLE_CLIENT_SECRET = os.environ.get('GOOGLE_CLIENT_SECRET', '')
    
    # Media Storage
    UPLOAD_FOLDER = os.path.join(BASE_DIR, '..', 'public', 'images')
    MAX_CONTENT_LENGTH = 16 * 1024 * 1024  # 16 MB max upload
