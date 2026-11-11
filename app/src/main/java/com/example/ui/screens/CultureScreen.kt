package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CultureStory
import com.example.ui.components.CultureStoryCard
import com.example.ui.components.EmptyStateNotice
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel

@Composable
fun CultureScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val stories by viewModel.cultureStories.collectAsState()
    var selectedStoryForDialog by remember { mutableStateOf<CultureStory?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("culture_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Cultural Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RubyRed.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RubyRed.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "KALASH TO THE WORLD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RubyRedLight,
                                letterSpacing = 1.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "KALASH CULTURE",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    )

                    Text(
                        text = "Presenting the living heritage, sacred traditions, craftsmanship, and community wisdom of Bumburet, Rumbur, and Birir with authenticity and cultural respect.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Section A: Traditional Crafts & Wool-Spinning
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KalashBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeepNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterVintage,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TRADITIONAL CRAFTS & TEXTILES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Text(
                                text = "Kalash Women Wool-Spinning",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    Text(
                        text = "Raw mountain sheep wool is hand-carded and spun using customary wooden drop spindles. Elder women pass down the continuous, meditative technique to keep ancestral weaving alive.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 20.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DeepNavy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Preserved in cultural documentation archives • Verified Kalash Heritage",
                            style = MaterialTheme.typography.labelSmall.copy(color = KalashBlue),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Section B: Youth Traditional Clothing (Prominent per Part 4 & 8)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RubyRed.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeepNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = RubyRedLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "COMMUNITY & YOUTH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RubyRed,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Text(
                                text = "Kalash Youth Traditional Clothing",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    Text(
                        text = "Young generations proudly wear traditional piran robes with geometric collar embroidery and cowrie-shell Kupas and Shushut headdresses during seasonal gatherings and communal life.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 20.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DeepNavy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Documented with community permission to celebrate living cultural continuity.",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Culture Stories List
        item {
            Text(
                text = "Archived Cultural Stories",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        items(stories) { story ->
            CultureStoryCard(
                story = story,
                onClick = { selectedStoryForDialog = story }
            )
        }
    }

    // Story Dialog
    selectedStoryForDialog?.let { story ->
        AlertDialog(
            onDismissRequest = { selectedStoryForDialog = null },
            containerColor = CardNavy,
            title = {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = KalashBlue.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = story.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = story.body,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 22.sp
                    )
                    Text(
                        text = story.culturalPreservationNote,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KalashBlue,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedStoryForDialog = null }) {
                    Text("Close", color = TextPrimary)
                }
            }
        )
    }
}
