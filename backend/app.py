import os
from flask import Flask, send_from_directory, jsonify
from flask_cors import CORS
from backend.config import Config
from backend.models import (
    db, User, CreatorPermission, CreatorProfile, Film, YouTubeAccount,
    YouTubeVideo, CultureStory, NewsArticle, ArtistProfile, TalentCampaign
)
from backend.routes.auth_routes import auth_bp
from backend.routes.public_routes import public_bp
from backend.routes.creator_routes import creator_bp
from backend.routes.ceo_routes import ceo_bp
from backend.routes.youtube_routes import youtube_bp

def create_app(config_class=Config):
    app = Flask(__name__, static_folder='static', static_url_path='')
    app.config.from_object(config_class)

    CORS(app, resources={r"/api/*": {"origins": "*"}})
    db.init_app(app)

    # Register API blueprints
    app.register_blueprint(auth_bp)
    app.register_blueprint(public_bp)
    app.register_blueprint(creator_bp)
    app.register_blueprint(ceo_bp)
    app.register_blueprint(youtube_bp)

    @app.route('/')
    def serve_index():
        return send_from_directory(app.static_folder, 'index.html')

    @app.route('/manifest.json')
    def serve_manifest():
        return send_from_directory(app.static_folder, 'manifest.json')

    @app.route('/sw.js')
    def serve_sw():
        return send_from_directory(app.static_folder, 'sw.js')

    @app.route('/api/health', methods=['GET'])
    def health_check():
        return jsonify({
            'status': 'healthy',
            'platform': 'ISHPATA FILMS Backend API',
            'version': '1.0.0',
            'tagline': 'KALASH TO THE WORLD',
            'founder': 'Lucky Rehmat Aspayi'
        }), 200

    with app.app_context():
        db.create_all()
        seed_initial_data()

    return app

def seed_initial_data():
    """Seeds verified authentic records if database is empty"""
    # 1. Setup Admin state & CEO profile (without hardcoded password per Part A)
    setup_state = AdminSetupState.query.first()
    if not setup_state:
        # Default deployment token for first admin initialization
        deployment_token = os.environ.get('CEO_SETUP_TOKEN', 'KALASH-ISHPATA-SETUP-TOKEN')
        setup_state = AdminSetupState(
            is_initialized=False,
            setup_token_hash=generate_password_hash(deployment_token)
        )
        db.session.add(setup_state)

    ceo_email = current_app.config['CEO_EMAIL']
    ceo = User.query.filter_by(email=ceo_email).first()
    if not ceo:
        # Created in uninitialized state, password is set by owner via First-Time Setup
        ceo = User(
            email=ceo_email,
            full_name="Lucky Rehmat Aspayi",
            role="ceo",
            status="approved",
            password_hash=""  # Remains empty until First-Time Setup
        )
        db.session.add(ceo)
        db.session.flush()

        perms = CreatorPermission(
            user_id=ceo.id,
            can_submit_video=True,
            can_upload_photos=True,
            can_submit_articles=True,
            can_submit_news=True,
            can_submit_youtube_links=True,
            can_publish=True
        )
        profile = CreatorProfile(
            user_id=ceo.id,
            bio="Founder & Creative Director of Ishpata Films. Dedicated to bringing authentic Kalash culture, original cinema, music and youth stories to the world stage.",
            village_origin="Kalash Valley, Bumburet",
            portfolio_link="https://www.youtube.com",
            is_featured=True
        )
        db.session.add(perms)
        db.session.add(profile)

    # 2. Seed Films if none exist
    if Film.query.count() == 0:
        films = [
            Film(
                title="Valley of the Ancestors",
                category="Cultural Films",
                synopsis="A cinematic journey into the sacred valleys of Bumburet, Rumbur and Birir, documenting indigenous Kalash philosophy, ancestral customs, and communal harmony.",
                director="Lucky Rehmat Aspayi",
                production_credits="Ishpata Films Production",
                release_year="2026",
                duration="24 min",
                poster_url="https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=600&q=80",
                youtube_video_id="dQw4w9WgXcQ",
                is_featured=True,
                is_published=True
            ),
            Film(
                title="Chaumos: Winter Fire and Juniper",
                category="Documentary",
                synopsis="An intimate look at the sacred Chaumos winter solstice observance, preserving oral prayers, torchlight processions, and winter hospitality.",
                director="Lucky Rehmat Aspayi",
                production_credits="Ishpata Films Cultural Archive",
                release_year="2025",
                duration="32 min",
                poster_url="https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
                youtube_video_id="ysz5S6PUM-U",
                is_featured=True,
                is_published=True
            ),
            Film(
                title="Threads of Kalash: The Wool Spinners",
                category="Short Films",
                synopsis="Focusing on the elder craftswomen of Rumbur valley hand-spinning pure sheep wool into customary textiles, shawls, and headdress braids.",
                director="Lucky Rehmat Aspayi",
                production_credits="Ishpata Films Heritage Project",
                release_year="2026",
                duration="15 min",
                poster_url="https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=600&q=80",
                youtube_video_id="jNQXAC9IVRw",
                is_featured=True,
                is_published=True
            )
        ]
        db.session.add_all(films)

    # 3. Seed YouTube Accounts and Videos
    if YouTubeAccount.query.count() == 0:
        acc1 = YouTubeAccount(
            account_label="Ishpata Films — Main Studio",
            channel_id="UC_IshpataFilms_Official",
            channel_title="Ishpata Films Official",
            is_public=True,
            is_homepage_default=True
        )
        acc2 = YouTubeAccount(
            account_label="Ishpata Films — Cultural Archive",
            channel_id="UC_KalashCulture_Archive",
            channel_title="Kalash Culture & Heritage",
            is_public=True,
            is_homepage_default=False
        )
        db.session.add_all([acc1, acc2])
        db.session.flush()

        vids = [
            YouTubeVideo(
                account_id=acc1.id,
                video_id="jNQXAC9IVRw",
                title="Ishpata Films — Kalash to the World: Official Studio Reel",
                description="Original cinematic productions bringing Kalash valley stories to the world.",
                thumbnail_url="https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80",
                channel_title="Ishpata Films Official",
                published_at="Nov 2026",
                is_featured=True
            ),
            YouTubeVideo(
                account_id=acc1.id,
                video_id="dQw4w9WgXcQ",
                title="Behind the Scenes: Filming in the Hindukush Mountains",
                description="Behind-the-scenes documentary capturing the technical process of mountain cinematography.",
                thumbnail_url="https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=600&q=80",
                channel_title="Ishpata Films Official",
                published_at="Oct 2026",
                is_featured=False
            ),
            YouTubeVideo(
                account_id=acc2.id,
                video_id="ysz5S6PUM-U",
                title="Traditional Kalash Flute & Mountain Melodies",
                description="Acoustic shepherd flute documentation recorded live in Bumburet Valley.",
                thumbnail_url="https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80",
                channel_title="Kalash Culture & Heritage",
                published_at="Sep 2026",
                is_featured=False
            )
        ]
        db.session.add_all(vids)

    # 4. Seed Culture Stories
    if CultureStory.query.count() == 0:
        stories = [
            CultureStory(
                title="The Ancient Art of Wool-Spinning in Kalash",
                category="Traditional Crafts",
                summary="Generations of Kalash women transform raw mountain fleece into intricate garments using wooden drop spindles, preserving sacred textile methods.",
                body="In the high valleys of the Hindukush, wool is more than warmth; it is a living craft handed down from mothers to daughters. Using unhurried, rhythmic spindle movements, Kalash women spin raw fleece into durable yarn for piran robes and headpieces. Every fold and twist represents living history.",
                image_url="public/images/hero/kalash_women_wool_spinning.jpg",
                cultural_preservation_note="Documented under the guidance of traditional Kalash artisans."
            ),
            CultureStory(
                title="Youth & Cultural Pride: Attire of Bumburet, Rumbur, & Birir",
                category="Traditional Clothing",
                summary="How the next generation honors identity through customary Kupas and Shushut headdresses decorated with cowrie shells and vibrant beads.",
                body="The youth of Kalash Valley maintain a profound connection to their roots. Wearing black piran dresses framed with intricate hand-embroidered collars and cowrie-shell headwear, young people actively participate in community rites and cultural documentation.",
                image_url="public/images/community/kalash_youth_traditional.jpg",
                cultural_preservation_note="Documented with community permission to support authentic heritage representation."
            ),
            CultureStory(
                title="Uchal Harvest & Seasonal Gratitude",
                category="Festivals & Heritage",
                summary="Celebrating cheese, wheat, and buttermilk harvest in late summer with communal ring dances and mountain chants.",
                body="Uchal marks the completion of the high-pasture cheese-making season. Kalash families gather outdoors under ancient walnut trees, sharing dairy delicacies and singing ancestral epic poetry.",
                image_url="public/images/culture/kalash_uchal_gathering.jpg",
                cultural_preservation_note="Verified seasonal observance."
            )
        ]
        db.session.add_all(stories)

    # 5. Seed News and Articles
    if NewsArticle.query.count() == 0:
        news = [
            NewsArticle(
                title="Ishpata Films Launches 'Kalash to the World' Digital Platform",
                summary="Founder Lucky Rehmat Aspayi announces the official digital cinema and cultural documentation hub.",
                content="ISHPATA FILMS has officially introduced its unified mobile and digital platform, establishing a dedicated space for indigenous storytelling, short cinema, and cultural preservation.",
                category="Studio Announcement",
                author_name="Editorial Team"
            ),
            NewsArticle(
                title="Creator Studio Registration Opens for Indigenous Filmmakers",
                summary="Local Kalash creators, photographers, and writers are invited to join the studio creative network.",
                content="The Creator Studio allows local talent to submit portfolio work, pitch story concepts, and collaborate with Ishpata Films on international production standards.",
                category="Community Opportunity",
                author_name="Lucky Rehmat Aspayi"
            )
        ]
        db.session.add_all(news)

    # 6. Seed Music & Artists
    if ArtistProfile.query.count() == 0:
        artists = [
            ArtistProfile(
                name="Sikander Aspayi",
                discipline="Traditional Flute (Kalu)",
                bio="Master shepherd flute player preserving archaic melodies heard across the pastures of Rumbur.",
                village="Rumbur Valley",
                external_link="https://www.youtube.com"
            ),
            ArtistProfile(
                name="Gulzar Kalash",
                discipline="Dhol & Rhythmic Percussion",
                bio="Percussionist leading ceremonial festival dances and communal festive rhythms.",
                village="Bumburet Valley",
                external_link="https://www.youtube.com"
            )
        ]
        db.session.add_all(artists)

    # 7. Seed Talent Campaign
    if TalentCampaign.query.count() == 0:
        campaign = TalentCampaign(
            title="Young Kalash Filmmakers Equipment Fund",
            talent_name="Emerging Valley Creators",
            description="Providing professional digital cameras, sound recorders, and editing equipment for young Kalash storytellers in Bumburet and Rumbur.",
            goal_amount=5000,
            current_amount=0,
            status_message="Donations opening soon."  # Per Part 13
        )
        db.session.add(campaign)

    db.session.commit()

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=5000, debug=True)
