# ISHPATA FILMS — COMPLETE DEPLOYMENT & SYSTEM GUIDE

**Brand:** ISHPATA FILMS  
**Tagline:** KALASH TO THE WORLD  
**Supporting Line:** Stories Beyond Borders. Culture Beyond Generations.  
**Founder / Creative Director:** Lucky Rehmat Aspayi  
**Designated CEO Email:** luckyrehmat07@gmail.com

---

## 1. System Architecture Overview

The Ishpata Films platform is composed of three synchronized interfaces backed by a single unified REST backend:
1. **Native Android Application** (Kotlin + Jetpack Compose + Material 3)
2. **Official Responsive Website** (HTML5 + CSS3 + Modular JS)
3. **Progressive Web App (PWA)** (Installable via browser with service worker caching & offline support)
4. **Shared Flask REST API** (Python Flask, SQLAlchemy, JWT Authentication, Multi-account YouTube Integration, PostgreSQL/SQLite)

---

## 2. Shared Backend Setup & Deployment

### Local Development
```bash
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python app.py
```
The API server will listen at `http://localhost:5000`.

### Production Deployment (Docker + PostgreSQL)
```bash
cd backend
docker build -t ishpata-films-api:latest .
docker run -d \
  -p 5000:5000 \
  -e SECRET_KEY="your-production-secret-key" \
  -e JWT_SECRET_KEY="your-jwt-production-secret" \
  -e DATABASE_URL="postgresql://user:password@host:5432/ishpata_db" \
  -e YOUTUBE_API_KEY="your-google-youtube-api-key" \
  --name ishpata-api \
  ishpata-films-api:latest
```

---

## 3. Environment Variables (.env / Secrets Panel)

| Variable | Description | Example / Default |
|---|---|---|
| `SECRET_KEY` | Flask application session secret | Generated secure token |
| `JWT_SECRET_KEY` | JWT token signature secret | Generated cryptographic key |
| `DATABASE_URL` | PostgreSQL connection string | `postgresql://usr:pwd@host/ishpata` |
| `YOUTUBE_API_KEY` | Google YouTube Data API v3 key | Configured in Google Cloud Console |
| `GOOGLE_CLIENT_ID` | OAuth2 Client ID for YouTube Multi-Account Manager | `xxx.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | OAuth2 Client Secret for channel authorizations | Kept private on backend |

---

## 4. Multi-Account YouTube Manager Workflow

Per **Part 7**:
1. The CEO navigates to **CEO Dashboard → YouTube Manager**.
2. Multiple accounts (e.g. *"Ishpata Films — Main Studio"*, *"Ishpata Films — Cultural Archive"*) can be connected.
3. The CEO can switch channels, refresh videos, set the homepage default channel, and toggle channel or video public visibility.
4. **Critical Playback Rule:** Videos **never** play inside embedded webviews or in-app players. Every video card in both the Android app and website triggers an external intent to the official YouTube app (`vnd.youtube:...`) or standard browser (`https://www.youtube.com/watch?v=...`).

---

## 5. Android Application Release & Signed AAB Build

### Application Details
- **App Name:** `ISHPATA FILMS` (defined in `strings.xml` and `metadata.json`)
- **Application ID:** `com.aistudio.ishpatafilms.klshfm`
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 16)
- **Adaptive Icon:** Custom circular dark navy & red IF emblem with white interlocking letters and ruby red accent dot.

### Building Signed Release AAB
To build the signed release bundle for Google Play Console submission:
```bash
gradle :app:bundleRelease
```
The output `.aab` will be located at:
`app/build/outputs/bundle/release/app-release.aab`

To build the APK for local testing or direct distribution:
```bash
gradle :app:assembleRelease
```
Output: `app/build/outputs/apk/release/app-release-unsigned.apk`
