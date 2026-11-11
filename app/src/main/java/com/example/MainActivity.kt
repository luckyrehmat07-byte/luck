package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.IshpataTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IshpataViewModel
import com.example.ui.viewmodel.ScreenDestination

data class NavigationItem(
    val title: String,
    val destination: ScreenDestination,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                IshpataAppRoot()
            }
        }
    }
}

@Composable
fun IshpataAppRoot(
    viewModel: IshpataViewModel = viewModel()
) {
    var isSplashDone by remember { mutableStateOf(false) }

    if (!isSplashDone) {
        SplashScreen(onSplashFinished = { isSplashDone = true })
    } else {
        MainAppContent(viewModel = viewModel)
    }
}

@Composable
fun MainAppContent(
    viewModel: IshpataViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val uiNotice by viewModel.uiNotice.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display UI notices via Snackbar
    LaunchedEffect(uiNotice) {
        uiNotice?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotice()
        }
    }

    // Hardware/Gesture back handler per guidelines
    BackHandler(enabled = currentScreen != ScreenDestination.Home) {
        viewModel.navigateBack()
    }

    val navItems = remember {
        listOf(
            NavigationItem("Home", ScreenDestination.Home, Icons.Default.Home, "nav_item_home"),
            NavigationItem("Films", ScreenDestination.Films, Icons.Default.Movie, "nav_item_films"),
            NavigationItem("Culture", ScreenDestination.Culture, Icons.Default.AutoStories, "nav_item_culture"),
            NavigationItem("Creators", ScreenDestination.Creators, Icons.Default.Group, "nav_item_creators"),
            NavigationItem("More", ScreenDestination.More, Icons.Default.Menu, "nav_item_more")
        )
    }

    val isSubScreen = remember(currentScreen) {
        currentScreen !in listOf(
            ScreenDestination.Home,
            ScreenDestination.Films,
            ScreenDestination.Culture,
            ScreenDestination.Creators,
            ScreenDestination.More
        )
    }

    val screenTitle = remember(currentScreen) {
        when (currentScreen) {
            is ScreenDestination.Home -> "ISHPATA FILMS"
            is ScreenDestination.Films -> "FILM DISCOVERY"
            is ScreenDestination.Culture -> "KALASH CULTURE"
            is ScreenDestination.Creators -> if (userSession != null) "CREATOR STUDIO" else "CREATORS"
            is ScreenDestination.More -> "STUDIO PORTAL"
            is ScreenDestination.CeoDashboard -> "CEO DASHBOARD"
            is ScreenDestination.CreatorStudio -> "CREATOR STUDIO"
            is ScreenDestination.MusicArtists -> "MUSIC & ARTISTS"
            is ScreenDestination.NewsUpdates -> "NEWS & UPDATES"
            is ScreenDestination.CulturalGallery -> "CULTURAL GALLERY"
            is ScreenDestination.SupportTalent -> "SUPPORT LOCAL TALENT"
            is ScreenDestination.AboutIshpata -> "ABOUT ISHPATA FILMS"
            is ScreenDestination.ContactUs -> "CONTACT US"
            is ScreenDestination.FilmDetail -> "FILM DETAILS"
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ishpata_main_scaffold"),
        topBar = {
            IshpataTopAppBar(
                title = screenTitle,
                subtitle = "KALASH TO THE WORLD",
                showBackButton = isSubScreen,
                onBackClick = { viewModel.navigateBack() },
                userRole = userSession?.role,
                onUserBadgeClick = {
                    if (userSession?.role == "ceo") {
                        viewModel.navigateTo(ScreenDestination.CeoDashboard)
                    } else if (userSession != null) {
                        viewModel.navigateTo(ScreenDestination.Creators)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepNavy,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("ishpata_bottom_nav_bar")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.destination ||
                            (item.destination == ScreenDestination.More && isSubScreen && currentScreen != ScreenDestination.Creators) ||
                            (item.destination == ScreenDestination.Creators && (currentScreen == ScreenDestination.CreatorStudio))

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.destination) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimary,
                            selectedTextColor = RubyRed,
                            indicatorColor = RubyRed.copy(alpha = 0.35f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = DeepNavy
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is ScreenDestination.Home -> HomeScreen(viewModel = viewModel)
                is ScreenDestination.Films -> FilmsScreen(viewModel = viewModel)
                is ScreenDestination.Culture -> CultureScreen(viewModel = viewModel)
                is ScreenDestination.Creators -> CreatorsScreen(viewModel = viewModel)
                is ScreenDestination.More -> MoreScreen(viewModel = viewModel)
                is ScreenDestination.CeoDashboard -> CeoDashboardScreen(viewModel = viewModel)
                is ScreenDestination.CreatorStudio -> CreatorsScreen(viewModel = viewModel)
                is ScreenDestination.MusicArtists -> MusicArtistsScreen(viewModel = viewModel)
                is ScreenDestination.NewsUpdates -> NewsUpdatesScreen(viewModel = viewModel)
                is ScreenDestination.CulturalGallery -> CulturalGalleryScreen(viewModel = viewModel)
                is ScreenDestination.SupportTalent -> SupportTalentScreen(viewModel = viewModel)
                is ScreenDestination.AboutIshpata -> AboutScreen(viewModel = viewModel)
                is ScreenDestination.ContactUs -> ContactScreen(viewModel = viewModel)
                is ScreenDestination.FilmDetail -> FilmsScreen(viewModel = viewModel)
            }
        }
    }
}
