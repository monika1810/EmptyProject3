package `in`.mercuryai.emptyproject.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String?,
    val email: String?,
    val username: String?,
    val avatar_url: String?,
    val provider: String = "Google"
)

