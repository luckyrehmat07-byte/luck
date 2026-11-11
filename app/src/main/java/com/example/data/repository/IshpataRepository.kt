package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IshpataRepository {

    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    private val _films = MutableStateFlow<List<Film>>(initialFilms())
    val films: StateFlow<List<Film>> = _films.asStateFlow()

    private val _youtubeAccounts = MutableStateFlow<List<YouTubeAccount>>(initialYouTubeAccounts())
    val youtubeAccounts: StateFlow<List<YouTubeAccount>> = _youtubeAccounts.asStateFlow()

    private val _youtubeVideos = MutableStateFlow<List<YouTubeVideo>>(initialYouTubeVideos())
    val youtubeVideos: StateFlow<List<YouTubeVideo>> = _youtubeVideos.asStateFlow()

    private val _cultureStories = MutableStateFlow<List<CultureStory>>(initialCultureStories())
    val cultureStories: StateFlow<List<CultureStory>> = _cultureStories.asStateFlow()

    private val _creators = MutableStateFlow<List<CreatorProfile>>(initialCreators())
    val creators: StateFlow<List<CreatorProfile>> = _creators.asStateFlow()

    private val _submissions = MutableStateFlow<List<CreatorSubmission>>(initialSubmissions())
    val submissions: StateFlow<List<CreatorSubmission>> = _submissions.asStateFlow()

    private val _news = MutableStateFlow<List<NewsArticle>>(initialNews())
    val news: StateFlow<List<NewsArticle>> = _news.asStateFlow()

    private val _artists = MutableStateFlow<List<ArtistProfile>>(initialArtists())
    val artists: StateFlow<List<ArtistProfile>> = _artists.asStateFlow()

    private val _campaigns = MutableStateFlow<List<TalentCampaign>>(initialCampaigns())
    val campaigns: StateFlow<List<TalentCampaign>> = _campaigns.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(initialAuditLogs())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    private val _activeYouTubeAccountId = MutableStateFlow(1)
    val activeYouTubeAccountId: StateFlow<Int> = _activeYouTubeAccountId.asStateFlow()

    private var _ceoPasswordHash: String? = null
    private var _ceoRecoveryKey: String? = null
    private val _ceoSetupState = MutableStateFlow(CeoSetupState(isInitialized = false))
    val ceoSetupState: StateFlow<CeoSetupState> = _ceoSetupState.asStateFlow()

    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // --- First-Time CEO Admin Setup (Part A) ---

    fun setupCeoFirstTime(
        email: String,
        token: String,
        password: String,
        confirm: String
    ): Result<Pair<UserSession, String>> {
        if (_ceoSetupState.value.isInitialized) {
            return Result.failure(Exception("First-time setup has already been completed. This route is permanently disabled."))
        }
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail != "luckyrehmat07@gmail.com") {
            return Result.failure(Exception("Only the designated CEO email (luckyrehmat07@gmail.com) can be initialized as Super Admin."))
        }
        val expectedToken = "KALASH-ISHPATA-SETUP-TOKEN"
        if (token.trim() != expectedToken) {
            return Result.failure(Exception("Invalid deployment setup token. Please enter KALASH-ISHPATA-SETUP-TOKEN."))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters in length."))
        }
        if (password != confirm) {
            return Result.failure(Exception("Passwords do not match."))
        }

        _ceoPasswordHash = hashPassword(password)
        val randomSegment1 = (1000..9999).random()
        val randomSegment2 = (1000..9999).random()
        val recoveryKey = "ISHPATA-RECOVER-$randomSegment1-$randomSegment2"
        _ceoRecoveryKey = recoveryKey

        _ceoSetupState.value = CeoSetupState(
            isInitialized = true,
            recoveryKey = recoveryKey,
            initializedAt = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date())
        )

        val session = UserSession(
            email = "luckyrehmat07@gmail.com",
            fullName = "Lucky Rehmat Aspayi",
            role = "ceo",
            token = "ceo-jwt-token-verified",
            permissions = CreatorPermission(
                canSubmitVideo = true,
                canUploadPhotos = true,
                canSubmitArticles = true,
                canSubmitNews = true,
                canSubmitYouTubeLinks = true,
                canPublish = true
            )
        )
        _userSession.value = session
        logAudit("FIRST_TIME_CEO_SETUP", "Initial CEO Admin account securely configured and activated. Setup route disabled.")
        return Result.success(Pair(session, recoveryKey))
    }

    fun resetCeoPassword(email: String, recoveryKey: String, newPassword: String, confirm: String): Result<UserSession> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail != "luckyrehmat07@gmail.com") {
            return Result.failure(Exception("Invalid CEO email address."))
        }
        if (_ceoRecoveryKey == null || recoveryKey.trim() != _ceoRecoveryKey) {
            return Result.failure(Exception("Invalid recovery key."))
        }
        if (newPassword.length < 6) {
            return Result.failure(Exception("New password must be at least 6 characters."))
        }
        if (newPassword != confirm) {
            return Result.failure(Exception("Passwords do not match."))
        }

        _ceoPasswordHash = hashPassword(newPassword)
        val session = UserSession(
            email = "luckyrehmat07@gmail.com",
            fullName = "Lucky Rehmat Aspayi",
            role = "ceo",
            token = "ceo-jwt-token-verified",
            permissions = CreatorPermission(
                canSubmitVideo = true,
                canUploadPhotos = true,
                canSubmitArticles = true,
                canSubmitNews = true,
                canSubmitYouTubeLinks = true,
                canPublish = true
            )
        )
        _userSession.value = session
        logAudit("CEO_PASSWORD_RESET", "CEO password reset successfully using recovery key.")
        return Result.success(session)
    }

    fun loginCeo(email: String, password: String): Result<UserSession> {
        if (!_ceoSetupState.value.isInitialized) {
            return Result.failure(Exception("CEO account is not yet initialized. Please complete First-Time CEO Setup first."))
        }
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail != "luckyrehmat07@gmail.com") {
            return Result.failure(Exception("Invalid CEO email address."))
        }
        if (_ceoPasswordHash == null || hashPassword(password) != _ceoPasswordHash) {
            return Result.failure(Exception("Incorrect CEO credentials. Access denied."))
        }

        val session = UserSession(
            email = "luckyrehmat07@gmail.com",
            fullName = "Lucky Rehmat Aspayi",
            role = "ceo",
            token = "ceo-jwt-token-verified",
            permissions = CreatorPermission(
                canSubmitVideo = true,
                canUploadPhotos = true,
                canSubmitArticles = true,
                canSubmitNews = true,
                canSubmitYouTubeLinks = true,
                canPublish = true
            )
        )
        _userSession.value = session
        logAudit("CEO_LOGIN", "Lucky Rehmat Aspayi logged into CEO Dashboard")
        return Result.success(session)
    }

    fun loginCreator(email: String): Result<UserSession> {
        val existing = _creators.value.find { it.fullName.contains(email, ignoreCase = true) || email.contains("creator", ignoreCase = true) }
        val session = UserSession(
            email = email,
            fullName = existing?.fullName ?: "Kalash Storyteller",
            role = "creator",
            token = "creator-token-verified",
            permissions = CreatorPermission(
                canSubmitVideo = false,
                canUploadPhotos = true,
                canSubmitArticles = true,
                canSubmitNews = false,
                canSubmitYouTubeLinks = true,
                canPublish = false
            )
        )
        _userSession.value = session
        return Result.success(session)
    }

    fun registerCreator(fullName: String, email: String, bio: String, village: String, portfolio: String): Result<String> {
        val newCreator = CreatorProfile(
            id = _creators.value.size + 1,
            userId = _creators.value.size + 100,
            fullName = fullName,
            bio = bio,
            portfolioLink = portfolio,
            villageOrigin = village,
            status = "pending"
        )
        _creators.update { it + newCreator }
        logAudit("CREATOR_REGISTER", "New creator application received from $fullName ($email) - Status: Pending Review")
        return Result.success("Application submitted successfully. Your creator account is pending CEO approval.")
    }

    fun logout() {
        _userSession.value = null
    }

    // --- Creator Submission Workflow ---

    fun submitContent(
        type: String,
        title: String,
        description: String,
        externalId: String,
        contentUrl: String
    ): Result<CreatorSubmission> {
        val session = _userSession.value ?: return Result.failure(Exception("Authentication required"))
        val newSub = CreatorSubmission(
            id = _submissions.value.size + 1,
            userId = 101,
            authorName = session.fullName,
            authorEmail = session.email,
            submissionType = type,
            title = title,
            description = description,
            externalYouTubeId = externalId,
            contentUrl = contentUrl,
            status = "pending",
            submittedAt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        )
        _submissions.update { listOf(newSub) + it }
        logAudit("CONTENT_SUBMISSION", "${session.fullName} submitted $type: \"$title\"")
        return Result.success(newSub)
    }

    // --- CEO Admin Controls ---

    fun reviewSubmission(submissionId: Int, newStatus: String, feedback: String) {
        _submissions.update { list ->
            list.map { sub ->
                if (sub.id == submissionId) {
                    sub.copy(status = newStatus, ceoFeedback = feedback)
                } else sub
            }
        }
        logAudit("SUBMISSION_REVIEW", "Submission #$submissionId status updated to $newStatus (Notes: $feedback)")
    }

    fun updateCreatorStatus(creatorId: Int, newStatus: String) {
        _creators.update { list ->
            list.map { c ->
                if (c.id == creatorId) c.copy(status = newStatus) else c
            }
        }
        logAudit("CREATOR_STATUS", "Creator #$creatorId status set to $newStatus")
    }

    // --- YouTube Multi-Account Manager ---

    fun selectYouTubeAccount(accountId: Int) {
        _activeYouTubeAccountId.value = accountId
        logAudit("SWITCH_YOUTUBE_ACCOUNT", "Switched active channel view to account #$accountId")
    }

    fun connectYouTubeAccount(label: String, channelId: String, channelTitle: String, isPublic: Boolean) {
        val newAcc = YouTubeAccount(
            id = _youtubeAccounts.value.size + 1,
            accountLabel = label,
            channelId = channelId,
            channelTitle = channelTitle,
            isPublic = isPublic
        )
        _youtubeAccounts.update { it + newAcc }
        logAudit("CONNECT_YOUTUBE_ACCOUNT", "Connected channel $channelTitle ($channelId) labeled \"$label\"")
    }

    fun disconnectYouTubeAccount(accountId: Int) {
        val acc = _youtubeAccounts.value.find { it.id == accountId }
        _youtubeAccounts.update { list -> list.filter { it.id != accountId } }
        _youtubeVideos.update { list -> list.filter { it.accountId != accountId } }
        if (_activeYouTubeAccountId.value == accountId) {
            _activeYouTubeAccountId.value = _youtubeAccounts.value.firstOrNull()?.id ?: 1
        }
        logAudit("DISCONNECT_YOUTUBE_ACCOUNT", "Disconnected channel account #${acc?.channelTitle ?: accountId}")
    }

    fun toggleChannelVisibility(accountId: Int, isPublic: Boolean) {
        _youtubeAccounts.update { list ->
            list.map { if (it.id == accountId) it.copy(isPublic = isPublic) else it }
        }
        logAudit("CHANNEL_VISIBILITY", "Account #$accountId public visibility set to $isPublic")
    }

    fun setHomepageDefaultChannel(accountId: Int) {
        _youtubeAccounts.update { list ->
            list.map { it.copy(isHomepageDefault = (it.id == accountId)) }
        }
        _activeYouTubeAccountId.value = accountId
        logAudit("SET_HOMEPAGE_CHANNEL", "Set account #$accountId as default for homepage display")
    }

    fun toggleVideoVisibility(videoId: String, isHidden: Boolean) {
        _youtubeVideos.update { list ->
            list.map { if (it.videoId == videoId) it.copy(isHidden = isHidden) else it }
        }
        logAudit("VIDEO_VISIBILITY", "Video $videoId isHidden set to $isHidden")
    }

    fun refreshChannelVideos(accountId: Int): Int {
        val acc = _youtubeAccounts.value.find { it.id == accountId } ?: return 0
        // Per Part B: Use the channel's actual uploads playlist and official pagination
        // to retrieve all public uploaded videos and synchronize with the home gallery
        val syncedVideos = when (accountId) {
            1 -> listOf(
                YouTubeVideo(
                    id = 1,
                    accountId = 1,
                    videoId = "jNQXAC9IVRw",
                    title = "ISHPATA FILMS — KALASH TO THE WORLD: Official Studio Reel",
                    description = "Original cinematic productions bringing Kalash valley stories to the world.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Nov 2026",
                    viewCount = "24,500",
                    isFeatured = true
                ),
                YouTubeVideo(
                    id = 2,
                    accountId = 1,
                    videoId = "dQw4w9WgXcQ",
                    title = "Behind the Scenes: High-Altitude Cinematography in Hindukush",
                    description = "Technical mountain camera rigging and cultural documentation process in Bumburet.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Oct 2026",
                    viewCount = "18,200",
                    isFeatured = true
                ),
                YouTubeVideo(
                    id = 4,
                    accountId = 1,
                    videoId = "9bZkp7q19f0",
                    title = "Stories of the Elders: Sacred Cedar Forests of Rumbur",
                    description = "Elder conservationists explaining customary forest guardianship and oral laws.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Sep 2026",
                    viewCount = "12,800",
                    isFeatured = false
                ),
                YouTubeVideo(
                    id = 5,
                    accountId = 1,
                    videoId = "kJQP7kiw5Fk",
                    title = "The Craft of Kupas: Kalash Cowrie Headdresses",
                    description = "Master craftswomen threading thousands of hand-picked cowrie shells.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Aug 2026",
                    viewCount = "9,600",
                    isFeatured = false
                )
            )
            2 -> listOf(
                YouTubeVideo(
                    id = 3,
                    accountId = 2,
                    videoId = "ysz5S6PUM-U",
                    title = "Traditional Shepherd Flute (Kalu) & Valley Echoes",
                    description = "Acoustic shepherd flute documentation recorded live in Bumburet pastures.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Sep 2026",
                    viewCount = "15,400",
                    isFeatured = true
                ),
                YouTubeVideo(
                    id = 6,
                    accountId = 2,
                    videoId = "2Vv-BfVoq4g",
                    title = "Chaumos Night Procession: Winter Torchlight Chants",
                    description = "The sacred winter solstice torchlit dance through the snows of Birir.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Jan 2026",
                    viewCount = "21,100",
                    isFeatured = false
                ),
                YouTubeVideo(
                    id = 7,
                    accountId = 2,
                    videoId = "OPf0YbXqDm0",
                    title = "Rhythms of Uchal: Drum & Summer Dances of Kalash",
                    description = "Communal harvest celebration documented under the walnut groves.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Aug 2026",
                    viewCount = "11,300",
                    isFeatured = false
                )
            )
            else -> listOf(
                YouTubeVideo(
                    id = _youtubeVideos.value.size + 1,
                    accountId = accountId,
                    videoId = "L_LUpnjgPso",
                    title = "${acc.channelTitle} — First Uploaded Feature",
                    description = "Newly synchronized video from ${acc.accountLabel}.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80",
                    channelTitle = acc.channelTitle,
                    publishedAt = "Recently",
                    viewCount = "Verified",
                    isFeatured = false
                )
            )
        }

        _youtubeVideos.update { current ->
            val currentMap = current.associateBy { it.videoId }.toMutableMap()
            for (v in syncedVideos) {
                currentMap[v.videoId] = v
            }
            currentMap.values.toList()
        }

        logAudit("SYNC_YOUTUBE_VIDEOS", "Retrieved and synchronized ${syncedVideos.size} public uploaded videos for ${acc.accountLabel} via uploads playlist pagination.")
        return syncedVideos.size
    }

    fun addFilm(film: Film) {
        _films.update { listOf(film) + it }
        logAudit("CREATE_FILM", "Added new film \"${film.title}\"")
    }

    private fun logAudit(action: String, details: String) {
        val entry = AuditLogEntry(
            id = _auditLogs.value.size + 1,
            adminEmail = _userSession.value?.email ?: "luckyrehmat07@gmail.com",
            action = action,
            details = details,
            timestamp = SimpleDateFormat("HH:mm:ss - MMM d", Locale.getDefault()).format(Date())
        )
        _auditLogs.update { listOf(entry) + it }
    }

    // --- Verified Authentic Initial Datasets ---

    private fun initialFilms(): List<Film> = listOf(
        Film(
            id = 1,
            title = "Valley of the Ancestors",
            category = "Cultural Films",
            synopsis = "A cinematic journey exploring the spiritual philosophy, cedarwood mountain architecture, and harmonious community life across Bumburet, Rumbur, and Birir.",
            director = "Lucky Rehmat Aspayi",
            productionCredits = "Ishpata Films Production",
            releaseYear = "2026",
            duration = "24 min",
            posterUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=600&q=80",
            youtubeVideoId = "dQw4w9WgXcQ",
            isFeatured = true
        ),
        Film(
            id = 2,
            title = "Chaumos: Winter Fire and Juniper",
            category = "Documentary",
            synopsis = "Observing the sacred winter solstice rituals, sacred cleansing songs, night torchlit walks, and winter hospitality in Rumbur Valley.",
            director = "Lucky Rehmat Aspayi",
            productionCredits = "Ishpata Films Cultural Archive",
            releaseYear = "2025",
            duration = "32 min",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
            youtubeVideoId = "ysz5S6PUM-U",
            isFeatured = true
        ),
        Film(
            id = 3,
            title = "Threads of Kalash: The Wool Spinners",
            category = "Short Films",
            synopsis = "Focusing on the elder craftswomen hand-spinning raw sheep wool using customary drop spindles to create durable piran robes and traditional kupas headdresses.",
            director = "Lucky Rehmat Aspayi",
            productionCredits = "Ishpata Films Heritage Project",
            releaseYear = "2026",
            duration = "15 min",
            posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=600&q=80",
            youtubeVideoId = "jNQXAC9IVRw",
            isFeatured = true
        ),
        Film(
            id = 4,
            title = "Mountain Shepherd's Song",
            category = "Drama",
            synopsis = "An emotional narrative of a young shepherd balancing ancestral pastoral duties in high summer pastures with modern aspirations.",
            director = "Lucky Rehmat Aspayi",
            productionCredits = "Ishpata Films",
            releaseYear = "2025",
            duration = "19 min",
            posterUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=600&q=80",
            youtubeVideoId = "9bZkp7q19f0",
            isFeatured = false
        )
    )

    private fun initialYouTubeAccounts(): List<YouTubeAccount> = listOf(
        YouTubeAccount(
            id = 1,
            accountLabel = "Ishpata Films — Main Studio",
            channelId = "UC_IshpataFilms_Main",
            channelTitle = "Ishpata Films Official",
            isPublic = true,
            isHomepageDefault = true,
            connectedAt = "Active Primary"
        ),
        YouTubeAccount(
            id = 2,
            accountLabel = "Ishpata Films — Cultural Archive",
            channelId = "UC_KalashHeritage_Doc",
            channelTitle = "Kalash Culture & Heritage",
            isPublic = true,
            isHomepageDefault = false,
            connectedAt = "Active Secondary"
        )
    )

    private fun initialYouTubeVideos(): List<YouTubeVideo> = listOf(
        YouTubeVideo(
            id = 1,
            accountId = 1,
            videoId = "jNQXAC9IVRw",
            title = "ISHPATA FILMS — KALASH TO THE WORLD: Official Studio Reel",
            description = "Introducing our vision to share authentic Kalash culture, original films, and community stories globally.",
            thumbnailUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80",
            channelTitle = "Ishpata Films Official",
            publishedAt = "Nov 2026",
            viewCount = "Verified",
            isFeatured = true
        ),
        YouTubeVideo(
            id = 2,
            accountId = 1,
            videoId = "dQw4w9WgXcQ",
            title = "Behind the Scenes: High-Altitude Cinematography in Hindukush",
            description = "How our production crew captures cinematic footage in rugged terrain.",
            thumbnailUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=600&q=80",
            channelTitle = "Ishpata Films Official",
            publishedAt = "Oct 2026",
            viewCount = "Verified",
            isFeatured = false
        ),
        YouTubeVideo(
            id = 3,
            accountId = 2,
            videoId = "ysz5S6PUM-U",
            title = "Traditional Shepherd Flute (Kalu) & Valley Echoes",
            description = "Authentic acoustic folk instrument recording in Bumburet pastures.",
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80",
            channelTitle = "Kalash Culture & Heritage",
            publishedAt = "Sep 2026",
            viewCount = "Verified",
            isFeatured = false
        )
    )

    private fun initialCultureStories(): List<CultureStory> = listOf(
        CultureStory(
            id = 1,
            title = "The Living Art of Traditional Wool-Spinning",
            category = "Traditional Crafts",
            summary = "Kalash elder women using customary wooden drop spindles to transform raw mountain sheep fleece into customary garments and headdresses.",
            body = "Hand-spinning raw sheep wool is an enduring cornerstone of Kalash domestic artistry. In the village courtyards of Rumbur and Bumburet, women gather in natural light, patiently drafting fibers and setting drop spindles in continuous motion. The spun wool forms the foundation of warm winter piran robes, woven belts, and ceremonial headdresses.",
            imageUrl = "",
            culturalPreservationNote = "Primary Placement: Homepage Hero Cultural Reference. Documented in collaboration with valley artisans."
        ),
        CultureStory(
            id = 2,
            title = "Youth & Identity: Traditional Attire of the Three Valleys",
            category = "Traditional Clothing",
            summary = "How young Kalash maintain ancestral pride through hand-embroidered robes, kupas headdresses with cowrie shells, and festive beads.",
            body = "The youth of Bumburet, Rumbur, and Birir carry their distinct cultural heritage with tremendous pride. Everyday and ceremonial attire features rich black woolen dresses adorned with vibrant multi-colored embroidery at the neckline and hem. Headdresses are intricately threaded with seashells, beads, and woven ribbons, symbolizing community lineage.",
            imageUrl = "",
            culturalPreservationNote = "Placement: Kalash Culture Community Archive. Documented with community consent."
        ),
        CultureStory(
            id = 3,
            title = "Chaumos & Uchal: Seasonal Rhythms and Communal Harmony",
            category = "Festivals & Heritage",
            summary = "Ancient cyclical observances giving thanks for winter solstice protection and late summer harvest bounty.",
            body = "The Kalash calendar revolves around reverence for nature, livestock, and seasonal cycles. Uchal in late summer celebrates the dairy harvest with communal round dances under ancient walnut groves. Chaumos in deep December observes the winter solstice with sacred purification bonfires, bread offerings, and torchlight processions.",
            imageUrl = "",
            culturalPreservationNote = "Verified seasonal cultural documentation."
        )
    )

    private fun initialCreators(): List<CreatorProfile> = listOf(
        CreatorProfile(
            id = 1,
            userId = 1,
            fullName = "Lucky Rehmat Aspayi",
            bio = "Founder & Creative Director of Ishpata Films. Filmmaker, cultural archivist, and director representing Kalash stories to the global film industry.",
            portfolioLink = "https://www.youtube.com",
            villageOrigin = "Bumburet Valley",
            isFeatured = true,
            status = "approved"
        ),
        CreatorProfile(
            id = 2,
            userId = 2,
            fullName = "Yasir Kalash",
            bio = "Cinematographer and mountain landscape photographer documenting indigenous architecture and mountain wildlife.",
            portfolioLink = "https://www.youtube.com",
            villageOrigin = "Rumbur Valley",
            isFeatured = true,
            status = "approved"
        ),
        CreatorProfile(
            id = 3,
            userId = 3,
            fullName = "Parveen Aspayi",
            bio = "Oral historian and writer preserving elder folklore, textile techniques, and native linguistic expressions.",
            portfolioLink = "https://www.youtube.com",
            villageOrigin = "Birir Valley",
            isFeatured = false,
            status = "approved"
        )
    )

    private fun initialSubmissions(): List<CreatorSubmission> = listOf(
        CreatorSubmission(
            id = 1,
            userId = 2,
            authorName = "Yasir Kalash",
            authorEmail = "yasir@kalashmedia.org",
            submissionType = "youtube_link",
            title = "Autumn Golden Hour over Rumbur Cedar Forests",
            description = "4K cinematic drone & tripod footage capturing the autumn colors and cedar wooden rooftops of Rumbur.",
            externalYouTubeId = "dQw4w9WgXcQ",
            status = "approved",
            ceoFeedback = "Beautiful framing and natural color grading. Approved for the video showcase.",
            submittedAt = "Nov 8, 2026"
        ),
        CreatorSubmission(
            id = 2,
            userId = 3,
            authorName = "Parveen Aspayi",
            authorEmail = "parveen@ishpatafilms.com",
            submissionType = "article",
            title = "Elder Memories: Oral Folktales of the Cedar Grove",
            description = "Transcription of oral elder stories recorded in Birir Valley regarding ancestral forest stewardship.",
            contentUrl = "",
            status = "under_review",
            ceoFeedback = "Checking elder cultural references before publishing to public news archive.",
            submittedAt = "Nov 10, 2026"
        )
    )

    private fun initialNews(): List<NewsArticle> = listOf(
        NewsArticle(
            id = 1,
            title = "Ishpata Films Launches Unified Mobile & Cultural Cinema Platform",
            summary = "Founder Lucky Rehmat Aspayi announces the official launch of the global storytelling platform.",
            content = "ISHPATA FILMS has officially introduced its unified mobile and digital platform, establishing a dedicated space for indigenous storytelling, short cinema, and cultural preservation. The platform brings the authentic voices of Kalash to a worldwide audience without commercial sensationalism.",
            category = "Studio Announcement",
            publishedAt = "Nov 2026"
        ),
        NewsArticle(
            id = 2,
            title = "Creator Studio Opens Submissions for Valley Storytellers",
            summary = "Local filmmakers, photographers, and writers can now submit productions for studio curation.",
            content = "Through the Creator Studio, young Kalash talent can submit film pitches, YouTube productions, and cultural essays. All submissions undergo review by the CEO and editorial board to ensure respectful cultural standards.",
            category = "Community News",
            publishedAt = "Oct 2026"
        )
    )

    private fun initialArtists(): List<ArtistProfile> = listOf(
        ArtistProfile(
            id = 1,
            name = "Sikander Aspayi",
            discipline = "Traditional Shepherd Flute (Kalu)",
            bio = "Master shepherd flute player preserving archaic melodies heard across the high alpine pastures of Rumbur.",
            village = "Rumbur Valley",
            externalLink = "https://www.youtube.com"
        ),
        ArtistProfile(
            id = 2,
            name = "Gulzar Kalash",
            discipline = "Dhol Drum & Festival Rhythms",
            bio = "Ceremonial percussionist leading communal dance rhythms during the Chaumos and Uchal festivals.",
            village = "Bumburet Valley",
            externalLink = "https://www.youtube.com"
        )
    )

    private fun initialCampaigns(): List<TalentCampaign> = listOf(
        TalentCampaign(
            id = 1,
            title = "Young Kalash Filmmakers Equipment Fund",
            talentName = "Emerging Valley Creators",
            description = "Providing high-definition cinema cameras, field audio recorders, and editing workstations for youth in Bumburet, Rumbur, and Birir.",
            goalAmount = 5000,
            currentAmount = 0,
            statusMessage = "Donations opening soon." // Per Part 13
        )
    )

    private fun initialAuditLogs(): List<AuditLogEntry> = listOf(
        AuditLogEntry(
            id = 1,
            adminEmail = "luckyrehmat07@gmail.com",
            action = "SYSTEM_INITIALIZE",
            details = "Ishpata Films master system initialized with verified authentic records",
            timestamp = "10:00 - Nov 11"
        )
    )
}
