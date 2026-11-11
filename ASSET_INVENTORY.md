# ISHPATA FILMS — ASSET INVENTORY & MEDIA REGISTRY

**Platform:** ISHPATA FILMS (Android + Shared Flask Backend + PWA/Website)  
**Founder & Creative Director:** Lucky Rehmat Aspayi  
**Tagline:** KALASH TO THE WORLD  
**Mission:** Stories Beyond Borders. Culture Beyond Generations.

---

## 1. Asset Inventory Table

| Asset Identifier | Asset Filename | Visual Description | Intended Page / Section | Status | Type | Notes & Cultural Protocol |
|---|---|---|---|---|---|---|
| **LOGO-IF-01** | `ic_if_logo.xml` / `if_emblem_logo.png` | Circular dark navy and red background with white interlocking 'IF' letters, ruby red dot above the 'I', and cyan/blue precision accents | App launch screen, App header, Drawer, About, Creator Studio, CEO Dashboard, Launcher Icon, PWA & Favicon | Active / Created | Vector & 32-bit PNG | Official emblem matching brand specification. Displayed with uncompromised aspect ratio. |
| **HERO-KLS-01** | `kalash_women_wool_spinning.jpg` | Kalash women in traditional kupas/shushut traditional headdresses, hand-spinning raw wool using customary spindles in the valley | Homepage Hero Banner, Kalash Heritage, Traditional Crafts | Pending Original Upload | Cultural Reference | Displayed with respectful cultural preservation badge. Never stretched or substituted with AI stock. |
| **COMM-KLS-02** | `kalash_youth_traditional.jpg` | Kalash youth dressed in authentic traditional attire (Piran/Chapan with vibrant cowrie-shell embroidery) | Kalash Culture page, Community section, Gallery | Pending Original Upload | Cultural Reference | Strictly kept distinct from Homepage hero. Depicts living cultural heritage of youth in Bumburet, Rumbur, and Birir. |
| **FILM-PRD-01** | `film_valley_whispers_thumb.jpg` | Cinematic landscape of Hindukush mountains and cedarwood Kalash homes | Films Discovery / Cultural Films | Active Placeholder | Production Reference | Directs out to official YouTube production channel. No in-app video playback. |
| **FILM-PRD-02** | `film_chaumos_winter_thumb.jpg` | Winter Chaumos festival documentation with sacred juniper smoke | Films Discovery / Documentary | Active Placeholder | Production Reference | Verified documentation thumbnail. Directed externally to verified YouTube link. |
| **ART-MUS-01** | `artist_dhol_flute_thumb.jpg` | Authentic Kalash folk musicians playing dhol drum and traditional shepherd flute | Music & Artists Section | Active Placeholder | Cultural Archive | Approved submission status. Opens external music/video link. |

---

## 2. Directory Structure Conventions

The project structures media outside the database into dedicated paths:
```
public/
  images/
    brand/       -> IF circular emblems, banners, watermarks
    hero/        -> Homepage hero media (wool-spinning documentation)
    culture/     -> Cultural heritage, textiles, craft photography
    community/   -> Kalash youth and village life
    gallery/     -> High-resolution cultural documentation
    films/       -> Official film posters & thumbnails
    artists/     -> Musician profiles, sound recordings
```

---

## 3. Cultural Integrity & Respect Protocols
1. **Accurate Attribution:** Never invent festival dates, ritual meanings, or villages without confirmation from Founder Lucky Rehmat Aspayi or Kalash elders.
2. **Respect for Minors & Individuals:** Consent and community permissions are required before public publication of identifiable persons.
3. **External YouTube Playback Only:** Per Part 7, videos are never played via in-app embedded players; all taps trigger official external YouTube intents (`vnd.youtube:...` or `https://www.youtube.com/...`).
4. **Empty State Fidelity:** When assets or verified productions are pending, display: *"New stories are coming soon"* or *"This section is being prepared"*.
