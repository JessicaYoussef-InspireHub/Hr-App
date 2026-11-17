package net.inspirehub.hr.profile.data

import android.os.Build
import androidx.annotation.RequiresApi
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.profile.components.formatBirthday
import java.util.Locale
import kotlin.text.orEmpty


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

@RequiresApi(Build.VERSION_CODES.O)
fun loadProfileFromPrefs(prefs: SharedPrefManager): ProfileUiData {
    val allowedIds = prefs.getAllowedLocationsIds()
    val addresses = prefs.getCompanyAddresses()

    val locations = prefs.getCompaniesLatLng()
        .filter { allowedIds.isEmpty() || it.id in allowedIds }
        .map { site ->
            ProfileWorkLocation(
                name = site.name,
                address = addresses[site.id].orEmpty(),
                allowedDistance = site.allowedDistance.toInt()
            )
        }

    return ProfileUiData(
        name = prefs.getEmployeeName().orEmpty(),
        email = prefs.getEmployeeEmail() ?: "-",
        jobTitle = prefs.getEmployeeJobTitle().orEmpty(),
        department = prefs.getEmployeeDepartment() ?: "-",
        company = prefs.getEmployeeCompany() ?: "-",
        birthday = prefs.getEmployeeBirthday()
            ?.let { formatBirthday(it, Locale.forLanguageTag(prefs.getLanguage())) }
            ?: "-",
        locations = locations
    )
}