package net.inspirehub.hr.sign_in.data

import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.inspirehub.hr.SharedPrefManager

/**
 * Signs the employee out when the server refuses to renew their key.
 *
 * It clears the same things as the Log out button: the employee key and the
 * "protection skipped" flag. The company from the QR code stays, so the next
 * screen is sign in, not the QR scan.
 *
 * [events] tells the open screen to move to sign in. If no screen is open, the
 * next app start goes to sign in by itself, because the key is empty.
 */
object SessionExpired {

    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun signOut(context: Context) {
        val sharedPref = SharedPrefManager(context)
        sharedPref.saveToken("")
        sharedPref.setProtectionSkipped(false)
        _events.tryEmit(Unit)
    }
}

/** The server answered the renewal and said no. */
class RenewTokenRefusedException(message: String) : Exception(message)
