package net.inspirehub.hr.expenses.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.inspirehub.hr.appColors
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R

@Composable
fun AttachmentsSection(
    selectedFiles: List<Uri>,
    onFilesChange: (List<Uri>) -> Unit
) {
    val hasFiles = selectedFiles.isNotEmpty()
    val colors = appColors()

    if (!hasFiles) {
        UploadImageOrFileBox(
            onFilesSelected = { newFiles ->
                onFilesChange(newFiles)
            }
        )

    } else {

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            item {
                Box {
                    UploadImageOrFileBox(
                        onFilesSelected = { newFiles ->
                            onFilesChange(selectedFiles + newFiles)
                        }
                    )
                }
            }

            items(selectedFiles) { uri ->

                var showOverlay by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .border(
                            2.dp,
                            colors.tertiaryColor,
                            RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    showOverlay = true
                                },
                                onTap = {
                                    showOverlay = false
                                }
                            )
                        }
                ) {

                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .matchParentSize()
                    )

                    // 🔥 OVERLAY
                    if (showOverlay) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    colors.onBackgroundColor.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            IconButton(
                                onClick = {
                                    onFilesChange(selectedFiles - uri)
                                    showOverlay = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = colors.onSecondaryColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}