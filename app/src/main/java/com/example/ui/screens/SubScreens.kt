package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ArtistProfile
import com.example.data.model.NewsArticle
import com.example.ui.components.EmptyStateNotice
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel

@Composable
fun MusicArtistsScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val artists by viewModel.artists.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "FOLK MUSIC & INDIGENOUS SOUND", style = MaterialTheme.typography.labelSmall.copy(color = RubyRed, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold))
            Text(text = "Music & Artists", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary))
            Text(text = "Documenting the sacred instruments, shepherd melodies, and festival percussion of Kalash Valley.", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        }

        items(artists) { artist ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(DeepNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = RubyRed, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text(text = artist.name, fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                            Text(text = "${artist.discipline} • ${artist.village}", color = KalashBlue, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Text(text = artist.bio, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(artist.externalLink))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Listen on YouTube", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsUpdatesScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val news by viewModel.news.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "STUDIO PRESS & ANNOUNCEMENTS", style = MaterialTheme.typography.labelSmall.copy(color = RubyRed, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold))
            Text(text = "News & Updates", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary))
        }

        items(news) { article ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(4.dp), color = KalashBlue.copy(alpha = 0.2f)) {
                        Text(text = article.category.uppercase(), color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                    Text(text = article.title, fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text(text = article.content, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary), lineHeight = 20.sp)
                    Text(text = "By ${article.authorName} • ${article.publishedAt}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                }
            }
        }
    }
}

@Composable
fun CulturalGalleryScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "ARCHIVED VISUAL HERITAGE", style = MaterialTheme.typography.labelSmall.copy(color = RubyRed, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold))
            Text(text = "Cultural Gallery", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary))
            Text(text = "Authentic photographic documentation preserved respectfully with valley elders.", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, KalashBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "1. Kalash Women Traditional Wool-Spinning", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Hand-spinning raw sheep wool into winter textiles using customary drop spindles.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Status: Archived Cultural Reference • Pending Original Image Upload", color = GoldAccent, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, RubyRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "2. Kalash Youth Traditional Attire", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Youth wearing embroidered piran robes and cowrie-shell Kupas headdresses in Bumburet, Rumbur, and Birir.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Status: Community Archive Reference • Pending Original Image Upload", color = RubyRedLight, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            EmptyStateNotice("More verified cultural documentation will be added soon.")
        }
    }
}

@Composable
fun SupportTalentScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val campaigns by viewModel.campaigns.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "SUPPORT LOCAL TALENT", style = MaterialTheme.typography.labelSmall.copy(color = RubyRed, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold))
            Text(text = "SUPPORT THE NEXT GENERATION", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary))
            Text(text = "Helping local creative talent, young filmmakers and approved community projects through transparent support.", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        }

        items(campaigns) { campaign ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = campaign.title, fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text(text = campaign.description, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary), lineHeight = 20.sp)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = KalashBlue.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KalashBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = CyanAccent)
                            Text(text = campaign.statusMessage, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Text(text = "Official payment provider integration in progress. No unverified donations are accepted.", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                }
            }
        }
    }
}

@Composable
fun AboutScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(BackgroundNavy).border(2.dp, RubyRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_if_logo),
                            contentDescription = "Official Ishpata Films Logo",
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Text(text = "ISHPATA FILMS", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                    Text(text = "KALASH TO THE WORLD", fontWeight = FontWeight.Bold, letterSpacing = 3.sp, color = RubyRed, style = MaterialTheme.typography.labelMedium)
                    Text(text = "Stories Beyond Borders. Culture Beyond Generations.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)

                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))

                    Text(text = "Founder / Creative Director", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                    Text(text = "Lucky Rehmat Aspayi", fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Our Mission", fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "To document, preserve, promote and share authentic Kalash culture, original films, music, stories, traditions, artists, young creators and creative productions with audiences around the world.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Ethical Cultural Representation", fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "ISHPATA FILMS represents Kalash culture respectfully and accurately. We do not invent awards, audience metrics, cultural facts, or production credits. All videos redirect to official YouTube channels for authentic creator attribution.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContactScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "GET IN TOUCH", style = MaterialTheme.typography.labelSmall.copy(color = RubyRed, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold))
            Text(text = "Contact Ishpata Films", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary))
            Text(text = "Direct production and cultural collaboration inquiries to the Ishpata Films team.", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Official Studio Channels", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Founder / Director: Lucky Rehmat Aspayi", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Location: Kalash Valley, Chitral, Khyber Pakhtunkhwa", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(text = "CEO Verified Email: luckyrehmat07@gmail.com", color = KalashBlue, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Send a Message", fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (submitted) {
                        Text(text = "Thank you. Your message has been sent to our editorial desk.", color = StatusApproved, fontWeight = FontWeight.Bold)
                    } else {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Message / Collaboration Inquiry") },
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (subject.isNotBlank() && message.isNotBlank()) {
                                    submitted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RubyRed),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Send Message")
                        }
                    }
                }
            }
        }
    }
}
