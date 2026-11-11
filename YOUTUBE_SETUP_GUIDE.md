# ISHPATA FILMS — YOUTUBE DATA API v3 & GOOGLE OAUTH 2.0 SETUP GUIDE

This guide explains how to connect and configure the official Google Cloud Console project and YouTube Data API v3 for **ISHPATA FILMS** ("Kalash to the World"), founded by Lucky Rehmat Aspayi (`luckyrehmat07@gmail.com`).

---

## 1. Prerequisites
- A Google Account (specifically the official CEO account `luckyrehmat07@gmail.com` or authorized studio account).
- Access to the [Google Cloud Console](https://console.cloud.google.com/).

---

## 2. Step-by-Step Google Cloud Project Configuration

### Step A: Create or Select a Project
1. Log in to [Google Cloud Console](https://console.cloud.google.com/).
2. In the top navigation bar, click the project selector dropdown and click **New Project**.
3. Set **Project Name** to `Ishpata Films Production` and click **Create**.

### Step B: Enable YouTube Data API v3
1. Navigate to **APIs & Services > Library**.
2. Search for `YouTube Data API v3`.
3. Click on **YouTube Data API v3** and click the **Enable** button.

### Step C: Configure OAuth Consent Screen
1. Navigate to **APIs & Services > OAuth consent screen**.
2. Select **External** (or Internal if using Google Workspace).
3. Fill in the App Information:
   - **App name:** `ISHPATA FILMS`
   - **User support email:** `luckyrehmat07@gmail.com`
   - **Developer contact information:** `luckyrehmat07@gmail.com`
4. Under **Scopes**, click **Add or Remove Scopes** and add:
   - `https://www.googleapis.com/auth/youtube.readonly` (View YouTube account and public uploads)
5. Under **Test Users**, add:
   - `luckyrehmat07@gmail.com`
6. Click **Save and Continue**.

### Step D: Create Credentials

#### 1. API Key (For public channel video queries & pagination):
1. Navigate to **APIs & Services > Credentials**.
2. Click **+ Create Credentials** > **API Key**.
3. Copy the generated API key.
4. (Recommended) Click **Edit API Key**, set **API restrictions** to restrict key usage exclusively to **YouTube Data API v3**.
5. Save the key in your AI Studio project **Secrets panel** under `YOUTUBE_API_KEY` (or in `/backend/.env`).

#### 2. OAuth 2.0 Client ID (For Multi-Account Channel Authorization):
1. Click **+ Create Credentials** > **OAuth client ID**.
2. For Web / Backend Server:
   - **Application type:** `Web application`
   - **Name:** `Ishpata Films Web & Backend`
   - **Authorized redirect URIs:**
     - `http://localhost:5000/api/youtube/oauth/callback`
     - `https://your-production-domain.com/api/youtube/oauth/callback`
3. Save the generated `Client ID` and `Client Secret` in `/backend/.env`:
   ```bash
   GOOGLE_OAUTH_CLIENT_ID=your-client-id.apps.googleusercontent.com
   GOOGLE_OAUTH_CLIENT_SECRET=your-client-secret
   YOUTUBE_API_KEY=your-youtube-api-key
   ```

---

## 3. How Uploads Playlist Synchronization Works

In YouTube Data API v3:
1. Every channel ID starts with `UC...` (e.g., `UC_IshpataFilms_Main`).
2. The channel's public uploads playlist ID is obtained by replacing `UC` with `UU` (e.g., `UU_IshpataFilms_Main`).
3. The Ishpata Films backend and Android repository call `playlistItems.list`:
   ```http
   GET https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,status&playlistId=UU_...&maxResults=50&pageToken={nextPageToken}&key={API_KEY}
   ```
4. This systematically pages through all public uploaded videos without arbitrary limits.
5. All synchronized videos are immediately displayed in the **WATCH OUR FILMS & VIDEOS** gallery on the homepage.

---

## 4. External Playback Guarantee
- In accordance with the project specification, no videos are embedded or played within the Ishpata Films application.
- Tapping any video card launches the external YouTube app via `vnd.youtube:{videoId}`, falling back automatically to `https://www.youtube.com/watch?v={videoId}` in the web browser.
