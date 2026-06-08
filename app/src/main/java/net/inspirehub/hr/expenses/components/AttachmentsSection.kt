package net.inspirehub.hr.expenses.components

import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.expenses.data.ExpenseAttachmentResponse

@Composable
fun AttachmentsSection(
    selectedFiles: List<Uri>,
    onFilesChange: (List<Uri>) -> Unit,
    existingFiles: List<ExpenseAttachmentResponse>,
    onDeleteExisting: (Int) -> Unit
) {
    val hasAnyFiles = selectedFiles.isNotEmpty() || existingFiles.isNotEmpty()
    val colors = appColors()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<Int?>(null) }
    var uriToDelete by remember { mutableStateOf<Uri?>(null) }

    if (!hasAnyFiles) {
        UploadImageOrFileBox(
            onFilesSelected = { newFiles ->
                onFilesChange(newFiles)
            }
        )
        return
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

            items(existingFiles) { file ->

                Log.d(
                    "ATTACHMENT_DEBUG",
                    "id=${file.id}, name=${file.name}, mime=${file.mimetype}"
                )

                var showOverlay by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .border(2.dp, colors.tertiaryColor, RoundedCornerShape(12.dp))
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
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = file.name,
                            color = colors.onBackgroundColor,
                            textAlign = TextAlign.Center
                        )
                    }
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
                                    fileToDelete = file.id
                                    uriToDelete = null
                                    showConfirmDialog = true
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
                                    uriToDelete = uri
                                    fileToDelete = null
                                    showConfirmDialog = true
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

    if (showConfirmDialog) {
        MyDialog(
            title = stringResource(R.string.delete_confirmation),
            subtitle = stringResource(R.string.are_you_sure_you_want_to_delete_this_item),
            confirmButtonText = stringResource(R.string.delete),
            dismissButtonText = stringResource(R.string.cancel),
            onDismiss = {
                showConfirmDialog = false
                fileToDelete = null
                uriToDelete = null
            },
            onConfirm = {
                fileToDelete?.let { id ->
                    onDeleteExisting(id)
                }

                uriToDelete?.let { uri ->
                    onFilesChange(selectedFiles - uri)
                }

                showConfirmDialog = false
                fileToDelete = null
                uriToDelete = null
            }
        )
    }
}