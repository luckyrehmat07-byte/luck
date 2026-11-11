// Service Worker Registration for PWA
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch((err) => {
      console.log('SW registration note:', err);
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  loadHomePageData();
});

async function loadHomePageData() {
  try {
    const res = await fetch('/api/public/home');
    if (!res.ok) throw new Error('API response failed');
    const data = await res.json();

    renderFilms(data.featured_films || []);
    renderCulture(data.culture_highlights || []);
    renderVideos(data.latest_videos || []);
  } catch (err) {
    console.error('Data load error:', err);
    document.getElementById('filmsGrid').innerHTML = '<div class="empty-state">New stories are coming soon.</div>';
    document.getElementById('cultureGrid').innerHTML = '<div class="empty-state">Cultural archives are being prepared.</div>';
    document.getElementById('videosGrid').innerHTML = '<div class="empty-state">No approved videos available yet.</div>';
  }
}

function renderFilms(films) {
  const container = document.getElementById('filmsGrid');
  if (!films || films.length === 0) {
    container.innerHTML = '<div class="empty-state">New stories are coming soon.</div>';
    return;
  }

  container.innerHTML = films.map(f => `
    <div class="film-card">
      <img src="${f.poster_url || 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=600&q=80'}" class="card-thumb" alt="${f.title}" loading="lazy">
      <div class="card-body">
        <span class="card-category">${f.category}</span>
        <h4 class="card-title">${f.title}</h4>
        <p class="card-desc">${f.synopsis}</p>
        <div style="font-size:0.8rem; color:#A0ABC0; margin-bottom:12px;">Director: ${f.director} • ${f.duration}</div>
        <!-- External YouTube link strictly enforced -->
        <a href="https://www.youtube.com/watch?v=${f.youtube_video_id}" target="_blank" rel="noopener noreferrer" class="btn-yt-external">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M10 15l5.19-3L10 9v6m11.56-7.83c.13.47.22 1.1.28 1.9.07.8.1 1.49.1 2.09L22 12c0 2.19-.16 3.8-.44 4.83-.25.9-.83 1.48-1.73 1.73-.47.13-1.33.22-2.65.28-1.3.07-2.49.1-3.59.1L12 22c-4.19 0-6.8-.16-7.83-.44-.9-.25-1.48-.83-1.73-1.73-.13-.47-.22-1.1-.28-1.9-.07-.8-.1-1.49-.1-2.09L2 12c0-2.19.16-3.8.44-4.83.25-.9.83-1.48 1.73-1.73.47-.13 1.33-.22 2.65-.28 1.3-.07 2.49-.1 3.59-.1L12 2c4.19 0 6.8.16 7.83.44.9.25 1.48.83 1.73 1.73z"/></svg>
          Watch on YouTube
        </a>
      </div>
    </div>
  `).join('');
}

function renderCulture(stories) {
  const container = document.getElementById('cultureGrid');
  if (!stories || stories.length === 0) {
    container.innerHTML = '<div class="empty-state">More cultural stories will be added soon.</div>';
    return;
  }

  container.innerHTML = stories.map(s => `
    <div class="culture-card">
      <div class="card-body">
        <span class="card-category">${s.category}</span>
        <h4 class="card-title">${s.title}</h4>
        <p class="card-desc">${s.summary}</p>
        <div style="font-size:0.78rem; color:#90CAF9; margin-top:8px;">${s.cultural_preservation_note || 'Living Kalash Heritage'}</div>
      </div>
    </div>
  `).join('');
}

function renderVideos(videos) {
  const container = document.getElementById('videosGrid');
  if (!videos || videos.length === 0) {
    container.innerHTML = '<div class="empty-state">No approved videos available yet.</div>';
    return;
  }

  container.innerHTML = videos.map(v => `
    <div class="video-card">
      <img src="${v.thumbnail_url}" class="card-thumb" alt="${v.title}" loading="lazy">
      <div class="card-body">
        <span class="card-category">${v.channel_title}</span>
        <h4 class="card-title">${v.title}</h4>
        <p class="card-desc">${v.description || ''}</p>
        <!-- External YouTube link strictly enforced -->
        <a href="https://www.youtube.com/watch?v=${v.video_id}" target="_blank" rel="noopener noreferrer" class="btn-yt-external">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M10 15l5.19-3L10 9v6m11.56-7.83c.13.47.22 1.1.28 1.9.07.8.1 1.49.1 2.09L22 12c0 2.19-.16 3.8-.44 4.83-.25.9-.83 1.48-1.73 1.73-.47.13-1.33.22-2.65.28-1.3.07-2.49.1-3.59.1L12 22c-4.19 0-6.8-.16-7.83-.44-.9-.25-1.48-.83-1.73-1.73-.13-.47-.22-1.1-.28-1.9-.07-.8-.1-1.49-.1-2.09L2 12c0-2.19.16-3.8.44-4.83.25-.9.83-1.48 1.73-1.73.47-.13 1.33-.22 2.65-.28 1.3-.07 2.49-.1 3.59-.1L12 2c4.19 0 6.8.16 7.83.44.9.25 1.48.83 1.73 1.73z"/></svg>
          Watch on YouTube
        </a>
      </div>
    </div>
  `).join('');
}
