package net.inspirehub.hr.attendance.components

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest


@Composable
 fun WorkEntryTypeImage(
    base64Image: String,
    backgroundColor: Color
) {
    val context = LocalContext.current

    val imageBytes = remember(base64Image) {
        try {
            Base64.decode(
                base64Image,
                Base64.DEFAULT
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .background(
                backgroundColor.copy(alpha = 0.12f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        if (imageBytes != null) {

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageBytes)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}