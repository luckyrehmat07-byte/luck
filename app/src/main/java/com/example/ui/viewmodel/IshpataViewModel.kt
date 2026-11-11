package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.IshpataRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Home : ScreenDestination()
    object Films : ScreenDestination()
    object Culture : ScreenDestination()
    object Creators : ScreenDestination()
    object More : ScreenDestination()
    
    // Sub-destinations
    object CeoDashboard : ScreenDestination()
    object CreatorStudio : ScreenDestination()
    object MusicArtists : ScreenDestination()
    object NewsUpdates : ScreenDestination()
    object CulturalGallery : ScreenDestination()
    object SupportTalent : ScreenDestination()
    object AboutIshpata : ScreenDestination()
    object ContactUs : ScreenDestination()
    data class FilmDetail(val film: Film) : ScreenDestination()
}

class IshpataViewModel(
    private val repository: IshpataRepository = IshpataRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<ScreenDestination>()

    val userSession: StateFlow<UserSession?> = repository.userSession
    val ceoSetupState: StateFlow<CeoSetupState> = repository.ceoSetupState
    val films: StateFlow<List<Film>> = repository.films
    val youtubeAccounts: StateFlow<List<YouTubeAccount>> = repository.youtubeAccounts
    val youtubeVideos: StateFlow<List<YouTubeVideo>> = repository.youtubeVideos
    val cultureStories: StateFlow<List<CultureStory>> = repository.cultureStories
    val creators: StateFlow<List<CreatorProfile>> = repository.creators
    val submissions: StateFlow<List<CreatorSubmission>> = repository.submissions
    val news: StateFlow<List<NewsArticle>> = repository.news
    val artists: StateFlow<List<ArtistProfile>> = repository.artists
    val campaigns: StateFlow<List<TalentCampaign>> = repository.campaigns
    val auditLogs: StateFlow<List<AuditLogEntry>> = repository.auditLogs
    val activeYouTubeAccountId: StateFlow<Int> = repository.activeYouTubeAccountId

    // UI Message feedback
    private val _uiNotice = MutableStateFlow<String?>(null)
    val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        if (_currentScreen.value != destination) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = destination
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        } else if (_currentScreen.value != ScreenDestination.Home) {
            _currentScreen.value = ScreenDestination.Home
            return true
        }
        return false
    }

    fun clearNotice() {
        _uiNotice.value = null
    }

    // --- Authentication & First-Time CEO Setup (Part A) ---

    fun setupCeoFirstTime(
        email: String,
        token: String,
        password: String,
        confirm: String,
        onSuccess: (String) -> Unit
    ) {
        val result = repository.setupCeoFirstTime(email, token, password, confirm)
        if (result.isSuccess) {
            val recoveryKey = result.getOrNull()!!.second
            _uiNotice.value = "CEO Account securely initialized! Save your recovery key: $recoveryKey"
            onSuccess(recoveryKey)
        } else {
            _uiNotice.value = "Setup failed: ${result.exceptionOrNull()?.message}"
        }
    }

    fun resetCeoPassword(
        email: String,
        recoveryKey: String,
        newPassword: String,
        confirm: String,
        onSuccess: () -> Unit
    ) {
        val result = repository.resetCeoPassword(email, recoveryKey, newPassword, confirm)
        if (result.isSuccess) {
            _uiNotice.value = "CEO Password reset successfully."
            onSuccess()
        } else {
            _uiNotice.value = "Reset failed: ${result.exceptionOrNull()?.message}"
        }
    }

    fun loginCeo(email: String, password: String, onSuccess: () -> Unit) {
        val result = repository.loginCeo(email, password)
        if (result.isSuccess) {
            _uiNotice.value = "Welcome back, Lucky Rehmat Aspayi. CEO Dashboard unlocked."
            onSuccess()
        } else {
            _uiNotice.value = "Authentication failed: ${result.exceptionOrNull()?.message}"
        }
    }

    fun loginCreator(email: String, onSuccess: () -> Unit) {
        val result = repository.loginCreator(email)
        if (result.isSuccess) {
            _uiNotice.value = "Welcome to Creator Studio."
            onSuccess()
        } else {
            _uiNotice.value = "Login failed."
        }
    }

    fun registerCreator(fullName: String, email: String, bio: String, village: String, portfolio: String) {
        val res = repository.registerCreator(fullName, email, bio, village, portfolio)
        _uiNotice.value = res.getOrNull() ?: "Registration error"
    }

    fun logout() {
        repository.logout()
        _uiNotice.value = "Logged out successfully"
        navigateTo(ScreenDestination.Home)
    }

    // --- Creator Studio Actions ---

    fun submitContent(type: String, title: String, description: String, externalId: String, contentUrl: String) {
        val res = repository.submitContent(type, title, description, externalId, contentUrl)
        if (res.isSuccess) {
            _uiNotice.value = "Content submitted successfully! Status: Pending CEO Review."
        } else {
            _uiNotice.value = "Submission failed: ${res.exceptionOrNull()?.message}"
        }
    }

    // --- CEO Admin Actions ---

    fun reviewSubmission(id: Int, status: String, feedback: String) {
        repository.reviewSubmission(id, status, feedback)
        _uiNotice.value = "Submission #$id updated to $status"
    }

    fun updateCreatorStatus(creatorId: Int, status: String) {
        repository.updateCreatorStatus(creatorId, status)
        _uiNotice.value = "Creator #$creatorId status set to $status"
    }

    fun selectYouTubeAccount(accountId: Int) {
        repository.selectYouTubeAccount(accountId)
        _uiNotice.value = "Switched active channel view"
    }

    fun syncChannelVideos(accountId: Int) {
        val count = repository.refreshChannelVideos(accountId)
        _uiNotice.value = "Retrieved and synchronized $count videos from channel uploads playlist."
    }

    fun connectYouTubeAccount(label: String, channelId: String, channelTitle: String, isPublic: Boolean) {
        repository.connectYouTubeAccount(label, channelId, channelTitle, isPublic)
        _uiNotice.value = "Connected channel: $channelTitle"
    }

    fun disconnectYouTubeAccount(accountId: Int) {
        repository.disconnectYouTubeAccount(accountId)
        _uiNotice.value = "Channel disconnected"
    }

    fun toggleChannelVisibility(accountId: Int, isPublic: Boolean) {
        repository.toggleChannelVisibility(accountId, isPublic)
        _uiNotice.value = "Channel visibility updated"
    }

    fun setHomepageDefaultChannel(accountId: Int) {
        repository.setHomepageDefaultChannel(accountId)
        _uiNotice.value = "Default homepage channel updated"
    }

    fun toggleVideoVisibility(videoId: String, isHidden: Boolean) {
        repository.toggleVideoVisibility(videoId, isHidden)
        _uiNotice.value = if (isHidden) "Video hidden from public view" else "Video visible publicly"
    }

    fun addFilm(film: Film) {
        repository.addFilm(film)
        _uiNotice.value = "Film \"${film.title}\" added to catalog"
    }
}
