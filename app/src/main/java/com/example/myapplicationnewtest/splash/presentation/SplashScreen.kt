package com.example.myapplicationnewtest.splash.presentation

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.myapplicationnewtest.R
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import androidx.vectordrawable.graphics.drawable.Animatable2Compat

@Composable
fun SplashScreen(
    navController: NavController,
    nextDestination: String,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
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
                                            navController.navigate(nextDestination) {
                                                popUpTo("SplashScreen") { inclusive = true }
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
}
