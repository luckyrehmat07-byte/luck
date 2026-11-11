package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Film
import com.example.ui.components.EmptyStateNotice
import com.example.ui.components.FilmCard
import com.example.ui.components.launchExternalYouTube
import com.example.ui.theme.*
import com.example.ui.viewmodel.IshpataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmsScreen(
    viewModel: IshpataViewModel,
    modifier: Modifier = Modifier
) {
    val films by viewModel.films.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilmForModal by remember { mutableStateOf<Film?>(null) }

    val categories = listOf("All", "Cultural Films", "Short Films", "Documentary", "Drama", "Behind the Scenes")

    val filteredFilms = remember(films, selectedCategory, searchQuery) {
        films.filter { film ->
            film.isPublished &&
            (selectedCategory == "All" || film.category.equals(selectedCategory, ignoreCase = true)) &&
            (searchQuery.isBlank() || film.title.contains(searchQuery, ignoreCase = true) || film.synopsis.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("films_screen")
    ) {
        // Search bar & Filters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceNavy)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Ishpata Films productions...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RubyRed) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RubyRed,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("films_search_input")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RubyRed,
                            selectedLabelColor = Color.White,
                            containerColor = CardNavy,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) RubyRed else BorderSubtle
                        )
                    )
                }
            }
        }

        // Film Grid / List
        if (filteredFilms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateNotice(message = "New stories are coming soon.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredFilms) { film ->
                    FilmCard(
                        film = film,
                        onClick = { selectedFilmForModal = film }
                    )
                }
            }
        }
    }

    // Film Detail Dialog
    selectedFilmForModal?.let { film ->
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { selectedFilmForModal = null },
            containerColor = CardNavy,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = film.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { selectedFilmForModal = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (film.posterUrl.isNotBlank()) {
                        AsyncImage(
                            model = film.posterUrl,
                            contentDescription = film.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RubyRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RubyRed)
                    ) {
                        Text(
                            text = film.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RubyRedLight
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = film.synopsis,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        lineHeight = 20.sp
                    )

                    HorizontalDivider(color = BorderSubtle)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Director: ${film.director}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Production: ${film.productionCredits}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                        Text(
                            text = "Release: ${film.releaseYear} • Duration: ${film.duration}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedFilmForModal = null
                        launchExternalYouTube(context, film.youtubeVideoId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Watch on YouTube", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
}
