package com.example.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Film
import com.example.data.model.YouTubeVideo
import com.example.data.model.CultureStory
import com.example.ui.theme.*

fun launchExternalYouTube(context: Context, videoId: String) {
    if (videoId.isBlank()) {
        Toast.makeText(context, "Video link being verified", Toast.LENGTH_SHORT).show()
        return
    }
    // Per Part 7: Videos must NOT play inside the app. Open original video externally on YouTube.
    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
    try {
        context.startActivity(appIntent)
    } catch (ex: ActivityNotFoundException) {
        try {
            context.startActivity(webIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to launch YouTube: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IshpataTopAppBar(
    title: String = "ISHPATA FILMS",
    subtitle: String = "KALASH TO THE WORLD",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    userRole: String? = null,
    onUserBadgeClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Official IF Logo
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BackgroundNavy)
                        .border(1.dp, RubyRed.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_if_logo),
                        contentDescription = "Official Ishpata Films Logo",
                        modifier = Modifier.size(34.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = RubyRed
                        ),
                        maxLines = 1
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = TextPrimary
                    )
                }
            }
        },
        actions = {
            if (userRole != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (userRole == "ceo") RubyRed.copy(alpha = 0.2f) else KalashBlue.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (userRole == "ceo") RubyRed else KalashBlue
                    ),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable { onUserBadgeClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (userRole == "ceo") RubyRed else KalashBlue)
                        )
                        Text(
                            text = if (userRole == "ceo") "CEO" else "CREATOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DeepNavy.copy(alpha = 0.95f),
            titleContentColor = TextPrimary
        )
    )
}

@Composable
fun CinematicHeroBanner(
    onExploreFilms: () -> Unit,
    onExploreCulture: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .testTag("cinematic_hero_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SurfaceNavy,
                            DeepNavy.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cultural Documentation Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = RubyRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RubyRed.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(RubyRed)
                        )
                        Text(
                            text = "ARCHIVED CULTURAL DOCUMENTATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                color = RubyRedLight
                            )
                        )
                    }
                }

                Text(
                    text = "STORIES BEYOND BORDERS.\nCULTURE BEYOND GENERATIONS.",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = TextPrimary,
                        lineHeight = 28.sp
                    )
                )

                // Required Main Image Placement A: Kalash Women Wool-Spinning Photograph Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KalashBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterVintage,
                                contentDescription = "Wool-Spinning Craft",
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kalash Women Traditional Wool-Spinning",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Preserving sacred spindle techniques and mountain fleece heritage across Bumburet and Rumbur.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Text(
                    text = "Founded by Lucky Rehmat Aspayi to bring authentic Kalash stories, original films, indigenous music, and creative talent to audiences worldwide.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onExploreFilms,
                        colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("hero_discover_films_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Discover Films",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = onExploreCulture,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("hero_explore_culture_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kalash Culture",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilmCard(
    film: Film,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("film_card_${film.id}")
    ) {
        Column {
            // Poster / Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(DeepNavy)
            ) {
                if (film.posterUrl.isNotBlank()) {
                    AsyncImage(
                        model = film.posterUrl,
                        contentDescription = film.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(KalashBlueDark, DeepNavy)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Category pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DeepNavy.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RubyRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = film.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RubyRedLight,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Duration pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = film.duration,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = film.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = film.synopsis,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dir: ${film.director} • ${film.releaseYear}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                // Strictly external YouTube launcher
                Button(
                    onClick = { launchExternalYouTube(context, film.youtubeVideoId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("watch_film_youtube_${film.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch on YouTube",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Watch on YouTube",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun YouTubeVideoCard(
    video: YouTubeVideo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { launchExternalYouTube(context, video.videoId) }
            .testTag("youtube_video_card_${video.videoId}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(DeepNavy)
            ) {
                if (video.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "External YouTube Video",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Outbound external badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "YouTube App",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = video.channelTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = KalashBlue
                    )
                )

                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (video.description.isNotBlank()) {
                    Text(
                        text = video.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${video.viewCount} views",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Text(
                        text = video.publishedAt,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }
        }
    }
}

@Composable
fun CultureStoryCard(
    story: CultureStory,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = KalashBlue.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, KalashBlue.copy(alpha = 0.3f))
            ) {
                Text(
                    text = story.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Text(
                text = story.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Text(
                text = story.summary,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                lineHeight = 20.sp
            )

            Text(
                text = story.culturalPreservationNote,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = KalashBlue,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )
        }
    }
}

@Composable
fun EmptyStateNotice(
    message: String = "New stories are coming soon.",
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.HourglassEmpty,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RubyRed.copy(alpha = 0.7f),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            )
        }
    }
}
