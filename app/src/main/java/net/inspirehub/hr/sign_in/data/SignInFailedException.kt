package net.inspirehub.hr.sign_in.data

/**
 * The sign in server answered, and the answer was a refusal.
 *
 * [errorCode] is the server's short name for the reason, for example
 * `DEVICE_LIMIT_REACHED`. It is empty when the server sent no code.
 *
 * Note: this is a normal answer, not a crash. Do not report it to Crashlytics.
 */
class SignInFailedException(
    message: String,
    val errorCode: String = ""
) : Exception(message) {

    companion object {
        /** The account already uses the highest number of phones it is allowed. */
        const val DEVICE_LIMIT_REACHED = "DEVICE_LIMIT_REACHED"
    }
}
