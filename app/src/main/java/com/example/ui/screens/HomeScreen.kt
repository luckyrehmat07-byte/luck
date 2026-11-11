package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun HomeScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val films by viewModel.films.collectAsState()
    val videos by viewModel.youtubeVideos.collectAsState()
    val cultureStories by viewModel.cultureStories.collectAsState()
    val creators by viewModel.creators.collectAsState()
    val news by viewModel.news.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()

    val featuredFilms = remember(films) { films.filter { it.isFeatured || it.isPublished } }
    val visibleVideos = remember(videos) { videos.filter { !it.isHidden } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section 1-4: Hero Area + Branding + Tagline + Introduction
        item {
            CinematicHeroBanner(
                onExploreFilms = { viewModel.navigateTo(ScreenDestination.Films) },
                onExploreCulture = { viewModel.navigateTo(ScreenDestination.Culture) }
            )
        }

        // Section 5: Featured Films
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeaderRow(
                    tag = "CINEMA PRODUCTION",
                    title = "Featured Films",
                    onSeeAll = { viewModel.navigateTo(ScreenDestination.Films) }
                )

                if (featuredFilms.isEmpty()) {
                    EmptyStateNotice("New stories are coming soon.")
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(featuredFilms) { film ->
                            FilmCard(
                                film = film,
                                onClick = { viewModel.navigateTo(ScreenDestination.FilmDetail(film)) },
                                modifier = Modifier.width(280.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 6: Prominent Video Gallery: WATCH OUR FILMS & VIDEOS (Requirement C)
        item {
            val publicAccounts = remember(viewModel.youtubeAccounts.collectAsState().value) {
                viewModel.youtubeAccounts.value.filter { it.isPublic }
            }
            var selectedGalleryTab by remember { mutableStateOf(0) }
            val galleryTabs = listOf("Featured Videos", "Latest Videos", "All Videos", "Explore More")
            var selectedChannelFilter by remember { mutableStateOf<Int?>(null) }

            val approvedPublicVideos = remember(videos, publicAccounts, selectedChannelFilter) {
                val publicAccountIds = publicAccounts.map { it.id }
                videos.filter { video ->
                    !video.isHidden && video.accountId in publicAccountIds &&
                    (selectedChannelFilter == null || video.accountId == selectedChannelFilter)
                }
            }

            val displayedVideos = remember(approvedPublicVideos, selectedGalleryTab) {
                when (selectedGalleryTab) {
                    0 -> approvedPublicVideos.filter { it.isFeatured }.ifEmpty { approvedPublicVideos }
                    1 -> approvedPublicVideos.sortedByDescending { it.id }
                    2 -> approvedPublicVideos
                    else -> approvedPublicVideos
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RubyRed.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .testTag("watch_our_films_and_videos_section")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Section Banner Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCC0000))
                                )
                                Text(
                                    text = "YOUTUBE CHANNEL INTEGRATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RubyRedLight,
                                        letterSpacing = 1.5.sp
                                    )
                                )
                            }
                            Text(
                                text = "WATCH OUR FILMS & VIDEOS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp,
                                    color = TextPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFCC0000).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCC0000))
                        ) {
                            Text(
                                text = "EXTERNAL PLAYBACK ONLY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Official film releases, documentaries, and cultural music from verified Ishpata Films YouTube channels. Videos open directly on YouTube for authentic playback.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        lineHeight = 18.sp
                    )

                    // Sub-category Tabs (Featured Videos, Latest Videos, All Videos, Explore More)
                    ScrollableTabRow(
                        selectedTabIndex = selectedGalleryTab,
                        containerColor = CardNavy,
                        contentColor = RubyRed,
                        edgePadding = 0.dp,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        galleryTabs.forEachIndexed { index, tabTitle ->
                            Tab(
                                selected = selectedGalleryTab == index,
                                onClick = {
                                    if (index == 3) {
                                        // Explore More -> Navigate to Films & Videos screen
                                        viewModel.navigateTo(ScreenDestination.Films)
                                    } else {
                                        selectedGalleryTab = index
                                    }
                                },
                                text = {
                                    Text(
                                        text = tabTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (selectedGalleryTab == index) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedGalleryTab == index) RubyRedLight else TextSecondary
                                        )
                                    )
                                }
                            )
                        }
                    }

                    // Connected Channel Switcher Filter
                    if (publicAccounts.size > 1) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedChannelFilter == null,
                                    onClick = { selectedChannelFilter = null },
                                    label = { Text("All Channels (${approvedPublicVideos.size})") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KalashBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                            items(publicAccounts) { acc ->
                                val isSelected = selectedChannelFilter == acc.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedChannelFilter = if (isSelected) null else acc.id },
                                    label = { Text(acc.channelTitle) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KalashBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Video Items Row / Cards
                    if (displayedVideos.isEmpty()) {
                        EmptyStateNotice("No approved videos available yet for this channel selection.")
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(end = 8.dp)
                        ) {
                            items(displayedVideos) { video ->
                                YouTubeVideoCard(
                                    video = video,
                                    modifier = Modifier.width(270.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 7: Kalash Culture Spotlights
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeaderRow(
                    tag = "LIVING TRADITIONS",
                    title = "Kalash Culture",
                    onSeeAll = { viewModel.navigateTo(ScreenDestination.Culture) }
                )

                if (cultureStories.isEmpty()) {
                    EmptyStateNotice("This section is being prepared.")
                } else {
                    cultureStories.take(2).forEach { story ->
                        CultureStoryCard(
                            story = story,
                            onClick = { viewModel.navigateTo(ScreenDestination.Culture) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }

        // Section 8: Music & Artists
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeaderRow(
                    tag = "FOLK MUSIC & SOUND",
                    title = "Music & Artists",
                    onSeeAll = { viewModel.navigateTo(ScreenDestination.MusicArtists) }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(artists) { artist ->
                        ArtistMiniCard(
                            artist = artist,
                            onClick = { viewModel.navigateTo(ScreenDestination.MusicArtists) }
                        )
                    }
                }
            }
        }

        // Section 9: Meet Our Creators
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeaderRow(
                    tag = "CREATIVE COMMUNITY",
                    title = "Meet Our Creators",
                    onSeeAll = { viewModel.navigateTo(ScreenDestination.Creators) }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(creators.filter { it.status == "approved" }) { creator ->
                        CreatorMiniCard(
                            creator = creator,
                            onClick = { viewModel.navigateTo(ScreenDestination.Creators) }
                        )
                    }
                }
            }
        }

        // Section 10: Latest News & Updates
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeaderRow(
                    tag = "STUDIO UPDATES",
                    title = "Latest News",
                    onSeeAll = { viewModel.navigateTo(ScreenDestination.NewsUpdates) }
                )

                news.take(2).forEach { article ->
                    NewsMiniCard(
                        article = article,
                        onClick = { viewModel.navigateTo(ScreenDestination.NewsUpdates) }
                    )
                }
            }
        }

        // Section 11: Support Local Talent
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .clickable { viewModel.navigateTo(ScreenDestination.SupportTalent) }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SUPPORT LOCAL TALENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RubyRed,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "SUPPORT THE NEXT GENERATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Empowering emerging Kalash filmmakers, musicians, and heritage documentation initiatives with transparent community equipment and grants.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = KalashBlue.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KalashBlue.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Donations opening soon. Transparent campaigns verified by Ishpata Films.",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary)
                            )
                        }
                    }
                }
            }
        }

        // Section 12: About Ishpata Films
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .clickable { viewModel.navigateTo(ScreenDestination.AboutIshpata) }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ABOUT ISHPATA FILMS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = KalashBlue,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "Founder / Creative Director: Lucky Rehmat Aspayi",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "ISHPATA FILMS is an independent cultural storytelling and cinema platform representing Kalash Valley to audiences worldwide without commercial distortion.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SectionHeaderRow(
    tag: String,
    title: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = RubyRed,
                    letterSpacing = 1.5.sp
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }
        TextButton(
            onClick = onSeeAll,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = KalashBlue
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = KalashBlue,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun ArtistMiniCard(
    artist: ArtistProfile,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = Modifier
            .width(180.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DeepNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = RubyRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = artist.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = artist.discipline,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CreatorMiniCard(
    creator: CreatorProfile,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = Modifier
            .width(180.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DeepNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = KalashBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = creator.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = creator.villageOrigin,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                maxLines = 1
            )
        }
    }
}

@Composable
fun NewsMiniCard(
    article: NewsArticle,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DeepNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Newspaper,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
