package net.inspirehub.hr.settings.data


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import net.inspirehub.hr.SharedPrefManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPref = SharedPrefManager(application)

    fun changeCompany() {
        sharedPref.clearAll()
    }

    fun logout() {
        sharedPref.saveToken("")
    }
}