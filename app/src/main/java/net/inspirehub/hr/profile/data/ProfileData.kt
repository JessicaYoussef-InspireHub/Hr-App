package net.inspirehub.hr.profile.data


data class ProfileUiData(
    val name: String,
    val email: String,
    val jobTitle: String,
    val department: String,
    val company: String,
    val birthday: String,
    val locations: List<ProfileWorkLocation>
)

data class ProfileWorkLocation(
    val name: String,
    val address: String,
    val allowedDistance: Int
)