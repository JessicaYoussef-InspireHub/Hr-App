package net.inspirehub.hr.settings.data


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import net.inspirehub.hr.SharedPrefManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefManager = SharedPrefManager(application)

    fun changeCompany() {
        prefManager.clearAll()
    }

    fun changeProtectionMethod() {
        prefManager.savePin("")
        prefManager.setFingerprintAuthSuccess(false)
    }

    fun logout() {
        prefManager.saveToken("")
    }
}
