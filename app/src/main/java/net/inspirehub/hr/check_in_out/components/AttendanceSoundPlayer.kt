package net.inspirehub.hr.check_in_out.components

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import androidx.annotation.RawRes
import net.inspirehub.hr.R

/**
 * Plays the short confirmation chime after a check in / check out the server accepted.
 *
 * SoundPool rather than MediaPlayer: the clip is tiny and has to start the moment the
 * response lands, and one decoded sample can be replayed without re-preparing anything.
 *
 * The owner must call [release] when done (the screen does it in onDispose), otherwise
 * the native SoundPool keeps its audio track and decoded sample alive.
 */
class AttendanceSoundPlayer(
    context: Context,
    @RawRes soundRes: Int = R.raw.attendance_success
) {

    private var soundPool: SoundPool? = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var soundId = 0
    private var isLoaded = false

    /*
     * Loading is asynchronous. A success that arrives before the sample is decoded
     * (first tap right after opening the screen) is remembered and played on load
     * instead of being lost.
     */
    private var playWhenLoaded = false

    init {
        val pool = soundPool!!

        pool.setOnLoadCompleteListener { loadedPool, sampleId, status ->

            if (status != 0 || sampleId != soundId) {
                Log.w(TAG, "Confirmation sound failed to load (status=$status)")
                return@setOnLoadCompleteListener
            }

            isLoaded = true

            if (playWhenLoaded) {
                playWhenLoaded = false
                loadedPool.play(soundId, VOLUME, VOLUME, 1, 0, 1f)
            }
        }

        soundId = pool.load(context.applicationContext, soundRes, 1)
    }

    fun playSuccess() {

        val pool = soundPool ?: return

        if (isLoaded) {
            pool.play(soundId, VOLUME, VOLUME, 1, 0, 1f)
        } else {
            playWhenLoaded = true
        }
    }

    fun release() {
        playWhenLoaded = false
        isLoaded = false
        soundPool?.setOnLoadCompleteListener(null)
        soundPool?.release()
        soundPool = null
    }

    private companion object {
        const val TAG = "AttendanceSound"
        const val VOLUME = 1f
    }
}