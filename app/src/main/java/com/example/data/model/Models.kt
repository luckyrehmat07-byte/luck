package com.example.data.model

data class Film(
    val id: Int,
    val title: String,
    val category: String,
    val synopsis: String,
    val director: String = "Lucky Rehmat Aspayi",
    val productionCredits: String = "Ishpata Films Production",
    val releaseYear: String = "2026",
    val duration: String = "20 min",
    val posterUrl: String = "",
    val youtubeVideoId: String,
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true
)

data class YouTubeAccount(
    val id: Int,
    val accountLabel: String,
    val channelId: String,
    val channelTitle: String,
    val isPublic: Boolean = true,
    val isHomepageDefault: Boolean = false,
    val connectedAt: String = "Verified"
)

data class YouTubeVideo(
    val id: Int,
    val accountId: Int,
    val videoId: String,
    val title: String,
    val description: String = "",
    val thumbnailUrl: String = "",
    val channelTitle: String = "Ishpata Films",
    val publishedAt: String = "Recently",
    val viewCount: String = "Verified",
    val isFeatured: Boolean = false,
    val isHidden: Boolean = false
)

data class CultureStory(
    val id: Int,
    val title: String,
    val category: String,
    val summary: String,
    val body: String,
    val imageUrl: String = "",
    val culturalPreservationNote: String = "Documented respectfully in Kalash Valley.",
    val isPublished: Boolean = true
)

data class CreatorProfile(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val bio: String,
    val portfolioLink: String = "",
    val villageOrigin: String = "Kalash Valley",
    val avatarUrl: String = "",
    val isFeatured: Boolean = false,
    val status: String = "approved"
)

data class CreatorPermission(
    val canSubmitVideo: Boolean = false,
    val canUploadPhotos: Boolean = true,
    val canSubmitArticles: Boolean = true,
    val canSubmitNews: Boolean = false,
    val canSubmitYouTubeLinks: Boolean = true,
    val canPublish: Boolean = false
)

data class CreatorSubmission(
    val id: Int,
    val userId: Int,
    val authorName: String,
    val authorEmail: String,
    val submissionType: String, // "video", "youtube_link", "photo", "article", "news"
    val title: String,
    val description: String,
    val contentUrl: String = "",
    val externalYouTubeId: String = "",
    val status: String = "pending", // "pending", "under_review", "changes_requested", "approved", "rejected", "published"
    val ceoFeedback: String = "",
    val submittedAt: String = "Today"
)

data class NewsArticle(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val category: String = "Studio News",
    val authorName: String = "Ishpata Films Editorial",
    val coverImageUrl: String = "",
    val publishedAt: String = "Nov 2026"
)

data class ArtistProfile(
    val id: Int,
    val name: String,
    val discipline: String,
    val bio: String,
    val village: String = "Kalash Valley",
    val externalLink: String = "https://www.youtube.com",
    val imageUrl: String = ""
)

data class TalentCampaign(
    val id: Int,
    val title: String,
    val talentName: String,
    val description: String,
    val goalAmount: Int = 0,
    val currentAmount: Int = 0,
    val statusMessage: String = "Donations opening soon.",
    val isActive: Boolean = true
)

data class AuditLogEntry(
    val id: Int,
    val adminEmail: String,
    val action: String,
    val details: String,
    val timestamp: String
)

data class UserSession(
    val email: String,
    val fullName: String,
    val role: String, // "ceo", "creator", "public"
    val token: String = "",
    val permissions: CreatorPermission = CreatorPermission()
)

data class CeoSetupState(
    val isInitialized: Boolean = false,
    val recoveryKey: String? = null,
    val initializedAt: String? = null
)
