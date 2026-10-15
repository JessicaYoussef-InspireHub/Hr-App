package net.inspirehub.hr.time_off.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import net.inspirehub.hr.DeleteIcon
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

data class AttachmentPicker(
    val uri: Uri,
    val name: String
)

@Composable
fun AttachmentPicker(
    selectedAttachments: List<AttachmentPicker>,
    onPickAttachments: () -> Unit,
    onRemoveAttachment: (AttachmentPicker) -> Unit
) {
    val colors = appColors()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var attachmentToDelete by remember {
        mutableStateOf<AttachmentPicker?>(null)
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        // Upload button is always visible.
        item {
            val isEmpty = selectedAttachments.isEmpty()

            Box(
                modifier = Modifier
                    .then(
                        if (isEmpty) {
                            Modifier.fillParentMaxWidth()
                        } else {
                            Modifier.width(100.dp)
                        }
                    )
                    .height(100.dp)
                    .animateContentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onPickAttachments),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val cornerRadius = 12.dp.toPx()

                    drawRoundRect(
                        color = colors.surfaceColor,
                        topLeft = Offset(
                            strokeWidth / 2,
                            strokeWidth / 2
                        ),
                        size = Size(
                            size.width - strokeWidth,
                            size.height - strokeWidth
                        ),
                        cornerRadius = CornerRadius(
                            cornerRadius,
                            cornerRadius
                        ),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(12.dp.toPx(), 6.dp.toPx()),
                                0f
                            )
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CloudUpload,
                        contentDescription = stringResource(R.string.upload_file),
                        tint = colors.onBackgroundColor
                    )

                    Text(
                        text = stringResource(R.string.upload_file),
                        color = colors.onBackgroundColor,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    if (isEmpty) {
                        Text(
                            text = stringResource(R.string.pdf_jbg_png_max_5mb),
                            color = colors.onBackgroundColor.copy(0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Selected files appear horizontally.
        items(
            items = selectedAttachments,
            key = { it.uri.toString() }
        ) { attachment ->

            var showOverlay by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        width = 2.dp,
                        color = colors.tertiaryColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(attachment.uri) {
                        detectTapGestures(
                            onLongPress = {
                                showOverlay = true
                            },
                            onTap = {
                                showOverlay = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val isImage = remember(attachment.name) {
                    attachment.name.substringAfterLast(
                        ".",
                        ""
                    ).lowercase() in listOf(
                        "jpg",
                        "jpeg",
                        "png",
                        "webp",
                        "gif",
                        "bmp"
                    )
                }

                if (isImage) {
                    AsyncImage(
                        model = attachment.uri,
                        contentDescription = attachment.name,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = attachment.name,
                            color = colors.onBackgroundColor,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center

                        )
                    }
                }

                if (showOverlay) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                colors.onBackgroundColor.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        DeleteIcon(
                            onClick = {
                                attachmentToDelete = attachment
                                showConfirmDialog = true
                                showOverlay = false
                            },
                            tint = colors.onSecondaryColor
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        MyDialog(
            title = stringResource(R.string.delete_confirmation),
            subtitle = stringResource(
                R.string.are_you_sure_you_want_to_delete_this_item
            ),
            confirmButtonText = stringResource(R.string.delete),
            dismissButtonText = stringResource(R.string.cancel),
            onDismiss = {
                showConfirmDialog = false
                attachmentToDelete = null
            },
            onConfirm = {
                attachmentToDelete?.let { attachment ->
                    onRemoveAttachment(attachment)
                }

                showConfirmDialog = false
                attachmentToDelete = null
            }
        )
    }
}