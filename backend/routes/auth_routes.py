import secrets
from datetime import datetime, timedelta
from functools import wraps
import jwt
from flask import Blueprint, request, jsonify, current_app
from werkzeug.security import generate_password_hash, check_password_hash
from backend.models import db, User, CreatorProfile, CreatorPermission, AuditLog, AdminSetupState

auth_bp = Blueprint('auth', __name__, url_prefix='/api/auth')

def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        auth_header = request.headers.get('Authorization')
        if auth_header:
            parts = auth_header.split()
            if len(parts) == 2 and parts[0].lower() == 'bearer':
                token = parts[1]
                
        if not token:
            return jsonify({'error': 'Authentication token is missing'}), 401
            
        try:
            data = jwt.decode(token, current_app.config['JWT_SECRET_KEY'], algorithms=['HS256'])
            current_user = User.query.get(data['user_id'])
            if not current_user:
                return jsonify({'error': 'User not found'}), 401
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token has expired'}), 401
        except Exception as e:
            return jsonify({'error': 'Invalid token', 'details': str(e)}), 401
            
        return f(current_user, *args, **kwargs)
    return decorated

def ceo_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        auth_header = request.headers.get('Authorization')
        if auth_header:
            parts = auth_header.split()
            if len(parts) == 2 and parts[0].lower() == 'bearer':
                token = parts[1]
                
        if not token:
            return jsonify({'error': 'CEO authorization required'}), 401
            
        try:
            data = jwt.decode(token, current_app.config['JWT_SECRET_KEY'], algorithms=['HS256'])
            current_user = User.query.get(data['user_id'])
            if not current_user or current_user.role != 'ceo':
                return jsonify({'error': 'Access denied: CEO role required'}), 403
        except Exception:
            return jsonify({'error': 'Invalid or expired CEO token'}), 401
            
        return f(current_user, *args, **kwargs)
    return decorated

def generate_token(user):
    payload = {
        'user_id': user.id,
        'email': user.email,
        'role': user.role,
        'exp': datetime.utcnow() + timedelta(hours=current_app.config['JWT_EXPIRATION_HOURS'])
    }
    return jwt.encode(payload, current_app.config['JWT_SECRET_KEY'], algorithm='HS256')

@auth_bp.route('/register', methods=['POST'])
def register_creator():
    data = request.get_json() or {}
    email = data.get('email', '').strip().lower()
    password = data.get('password', '').strip()
    full_name = data.get('full_name', '').strip()
    bio = data.get('bio', '').strip()
    portfolio_link = data.get('portfolio_link', '').strip()
    village = data.get('village', 'Kalash Valley').strip()

    if not email or not password or not full_name:
        return jsonify({'error': 'Email, password, and full name are required'}), 400

    if User.query.filter_by(email=email).first():
        return jsonify({'error': 'An account with this email already exists'}), 400

    # New creator accounts are always initialized with status 'pending' per Part 11
    user = User(
        email=email,
        full_name=full_name,
        role='creator',
        status='pending'
    )
    user.set_password(password)
    db.session.add(user)
    db.session.flush()

    profile = CreatorProfile(
        user_id=user.id,
        bio=bio,
        portfolio_link=portfolio_link,
        village_origin=village
    )
    permissions = CreatorPermission(
        user_id=user.id,
        can_submit_video=False,
        can_upload_photos=True,
        can_submit_articles=True,
        can_submit_news=False,
        can_submit_youtube_links=True,
        can_publish=False
    )
    db.session.add(profile)
    db.session.add(permissions)
    db.session.commit()

    return jsonify({
        'message': 'Creator registration submitted successfully. Your account is pending CEO approval.',
        'status': 'pending',
        'user': user.to_dict()
    }), 201

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json() or {}
    email = data.get('email', '').strip().lower()
    password = data.get('password', '').strip()

    if not email or not password:
        return jsonify({'error': 'Email and password required'}), 400

    user = User.query.filter_by(email=email).first()
    if not user or not user.check_password(password):
        return jsonify({'error': 'Invalid email or password'}), 401

    if user.status == 'pending':
        return jsonify({
            'error': 'Your creator account is pending CEO review and approval.',
            'status': 'pending'
        }), 403

    if user.status == 'suspended':
        return jsonify({'error': 'This account has been suspended by administration.'}), 403

    token = generate_token(user)
    return jsonify({
        'token': token,
        'user': user.to_dict(),
        'role': user.role
    }), 200

@auth_bp.route('/me', methods=['GET'])
@token_required
def get_current_user(current_user):
    user_dict = current_user.to_dict()
    if current_user.profile:
        user_dict['profile'] = current_user.profile.to_dict()
    return jsonify({'user': user_dict}), 200

# --- SECURE FIRST-TIME CEO SETUP & ADMIN AUTHENTICATION (PART A) ---

@auth_bp.route('/ceo/setup-status', methods=['GET'])
def get_ceo_setup_status():
    """
    Checks if the initial CEO Admin has been configured.
    """
    setup_state = AdminSetupState.query.first()
    is_init = bool(setup_state and setup_state.is_initialized)
    
    # Check if a CEO user exists with valid password
    ceo_email = current_app.config['CEO_EMAIL']
    ceo_user = User.query.filter_by(email=ceo_email, role='ceo').first()
    has_active_ceo = bool(ceo_user and is_init)

    return jsonify({
        'is_initialized': has_active_ceo,
        'ceo_email': ceo_email,
        'requires_setup': not has_active_ceo
    }), 200

@auth_bp.route('/ceo/setup', methods=['POST'])
def setup_first_time_ceo():
    """
    One-time secure first-admin initialization for luckyrehmat07@gmail.com.
    Protected by server setup token. Once completed, permanently disabled.
    """
    setup_state = AdminSetupState.query.first()
    if setup_state and setup_state.is_initialized:
        return jsonify({
            'error': 'First-time CEO setup has already been completed. This route is permanently disabled.'
        }), 403

    data = request.get_json() or {}
    email = data.get('email', '').strip().lower()
    setup_token = data.get('setup_token', '').strip()
    password = data.get('password', '').strip()
    confirm_password = data.get('confirm_password', '').strip()

    ceo_email = current_app.config['CEO_EMAIL'].lower()

    # Rule 1: Email verification
    if email != ceo_email:
        return jsonify({'error': f'Only the designated CEO email ({ceo_email}) can be initialized as Super Admin.'}), 400

    # Rule 2: Token verification
    if setup_state and setup_state.setup_token_hash:
        if not setup_token or not check_password_hash(setup_state.setup_token_hash, setup_token):
            return jsonify({'error': 'Invalid or missing deployment setup token.'}), 401

    # Rule 3: Password validation
    if len(password) < 6:
        return jsonify({'error': 'Password must be at least 6 characters in length.'}), 400

    if password != confirm_password:
        return jsonify({'error': 'Passwords do not match.'}), 400

    # Initialize / update CEO user
    ceo = User.query.filter_by(email=ceo_email).first()
    if not ceo:
        ceo = User(
            email=ceo_email,
            full_name="Lucky Rehmat Aspayi",
            role="ceo",
            status="approved"
        )
        db.session.add(ceo)
        db.session.flush()

    ceo.set_password(password)
    ceo.role = "ceo"
    ceo.status = "approved"

    # Ensure permissions
    if not ceo.permissions:
        perms = CreatorPermission(
            user_id=ceo.id,
            can_submit_video=True,
            can_upload_photos=True,
            can_submit_articles=True,
            can_submit_news=True,
            can_submit_youtube_links=True,
            can_publish=True
        )
        db.session.add(perms)

    # Generate one-time recovery key
    recovery_key = f"ISHPATA-RECOVER-{secrets.token_hex(4).upper()}-{secrets.token_hex(4).upper()}"
    recovery_hash = generate_password_hash(recovery_key)

    if not setup_state:
        setup_state = AdminSetupState(
            is_initialized=True,
            setup_token_hash=generate_password_hash("used"),
            recovery_key_hash=recovery_hash,
            initialized_at=datetime.utcnow(),
            initialized_ip=request.remote_addr
        )
        db.session.add(setup_state)
    else:
        setup_state.is_initialized = True
        setup_state.recovery_key_hash = recovery_hash
        setup_state.initialized_at = datetime.utcnow()
        setup_state.initialized_ip = request.remote_addr

    log = AuditLog(
        admin_email=ceo_email,
        action="FIRST_TIME_CEO_SETUP",
        details="Initial CEO Admin account securely configured and activated. Setup route disabled."
    )
    db.session.add(log)
    db.session.commit()

    token = generate_token(ceo)
    return jsonify({
        'message': 'CEO Admin account securely initialized.',
        'token': token,
        'user': ceo.to_dict(),
        'recovery_key': recovery_key,
        'notice': 'Save your recovery key in a secure location. It is required for emergency password resets.'
    }), 201

@auth_bp.route('/ceo/reset-password', methods=['POST'])
def reset_ceo_password():
    """Emergency password recovery using the one-time generated recovery key."""
    data = request.get_json() or {}
    email = data.get('email', '').strip().lower()
    recovery_key = data.get('recovery_key', '').strip()
    new_password = data.get('new_password', '').strip()
    confirm_password = data.get('confirm_password', '').strip()

    ceo_email = current_app.config['CEO_EMAIL'].lower()
    if email != ceo_email:
        return jsonify({'error': 'Invalid recovery request.'}), 400

    setup_state = AdminSetupState.query.first()
    if not setup_state or not setup_state.recovery_key_hash:
        return jsonify({'error': 'No recovery key registered on the system.'}), 400

    if not check_password_hash(setup_state.recovery_key_hash, recovery_key):
        return jsonify({'error': 'Invalid recovery key.'}), 401

    if len(new_password) < 6:
        return jsonify({'error': 'New password must be at least 6 characters in length.'}), 400

    if new_password != confirm_password:
        return jsonify({'error': 'Passwords do not match.'}), 400

    ceo = User.query.filter_by(email=ceo_email, role='ceo').first()
    if not ceo:
        return jsonify({'error': 'CEO user account not found.'}), 404

    ceo.set_password(new_password)
    # Generate new recovery key after reset
    new_recovery_key = f"ISHPATA-RECOVER-{secrets.token_hex(4).upper()}-{secrets.token_hex(4).upper()}"
    setup_state.recovery_key_hash = generate_password_hash(new_recovery_key)

    log = AuditLog(
        admin_email=ceo_email,
        action="CEO_PASSWORD_RESET",
        details="CEO password successfully reset using recovery key."
    )
    db.session.add(log)
    db.session.commit()

    token = generate_token(ceo)
    return jsonify({
        'message': 'CEO password successfully reset.',
        'token': token,
        'new_recovery_key': new_recovery_key
    }), 200
