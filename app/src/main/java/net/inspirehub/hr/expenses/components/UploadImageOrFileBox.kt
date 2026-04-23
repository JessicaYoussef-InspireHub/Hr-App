package net.inspirehub.hr.expenses.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun UploadImageOrFileBox(){

    val colors = appColors()
    var showUploadSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val imageFile = remember {
        java.io.File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
    }

    val cameraUri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri = cameraUri
            println("Camera Image URI: $cameraImageUri")
        }
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(cameraUri)
            } else {
                println("Camera permission denied")
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            println("Selected Image: $it")
        }
    }

    val filesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            println("Selected File: $it")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showUploadSheet = true }
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = colors.tertiaryColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Photo,
            contentDescription = stringResource(R.string.upload),
            tint = colors.tertiaryColor,
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.tap_to_upload_image_or_file),
            color = colors.onBackgroundColor,
            fontSize = 16.sp
        )
    }

    if (showUploadSheet) {
        UploadBottomSheet(
            onDismiss = { showUploadSheet = false },
            onCameraClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
            onGalleryClick = { galleryLauncher.launch("image/*") },
            onFilesClick = { filesLauncher.launch(arrayOf("*/*")) }
        )
    }

}