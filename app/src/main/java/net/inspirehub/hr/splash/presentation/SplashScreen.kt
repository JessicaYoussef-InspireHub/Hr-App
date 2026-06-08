package net.inspirehub.hr.splash.presentation

import android.content.Intent
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import net.inspirehub.hr.R
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.appColors

@Composable
fun SplashScreen(
    navController: NavController,
    nextDestination: String,
) {
    val colors = appColors()
    val context = LocalContext.current
    var showUpdateDialog by remember { mutableStateOf(false) }
    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        val isUpdateAvailable =
            appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

        if (isUpdateAvailable) {
            showUpdateDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.onSecondaryColor)
    ) {
        AndroidView(
            factory = { imageViewContext ->
                ImageView(imageViewContext).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER

                    val target = object : ImageViewTarget<GifDrawable>(this) {
                        override fun setResource(resource: GifDrawable?) {
                            resource?.let { gifDrawable ->
                                gifDrawable.setLoopCount(1)
                                setImageDrawable(gifDrawable)

                                // Callback is executed when the animation is finished.
                                gifDrawable.registerAnimationCallback(
                                    object : Animatable2Compat.AnimationCallback() {
                                        override fun onAnimationEnd(drawable: android.graphics.drawable.Drawable?) {
                                            super.onAnimationEnd(drawable)

                                            if (nextDestination != "NotificationsScreen") {
                                                navController.navigate(nextDestination) {
                                                    popUpTo("SplashScreen") { inclusive = true }
                                                }
                                            }
                                        }
                                    }
                                )

                                gifDrawable.start()
                            }
                        }
                    }

                    Glide.with(context)
                        .asGif()
                        .load(R.drawable.inspire_hub_logo)
                        .into(target)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    if (showUpdateDialog) {
        MyDialog(
            onConfirm = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=${context.packageName}".toUri()
                )
                context.startActivity(intent)
            },
            onDismiss = {
                showUpdateDialog = false
            },
            title = stringResource(R.string.update_available),
            subtitle = stringResource(R.string.a_new_version_is_available_You_can_update_now_or_continue_using_the_app),
            confirmButtonText = stringResource(R.string.update),
            dismissButtonText = stringResource(R.string.later)
        )
    }
}