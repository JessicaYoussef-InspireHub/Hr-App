package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.GeneralIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFilesClick: () -> Unit
) {
    val colors = appColors()
    val sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = colors.surfaceContainerHigh,
        contentWindowInsets = { WindowInsets(0) },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseIcon(
                    onClick = { onDismiss() })
            }
            Text(
                text = stringResource(R.string.choose_option),
                color = colors.tertiaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(40.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onDismiss()
                            onGalleryClick()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GeneralIcon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = stringResource(R.string.filter_by_suppliers),
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.gallery),
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onDismiss()
                            onCameraClick()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GeneralIcon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.camera),
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.camera),
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onDismiss()
                            onFilesClick()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GeneralIcon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = stringResource(R.string.files),
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.files),
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}