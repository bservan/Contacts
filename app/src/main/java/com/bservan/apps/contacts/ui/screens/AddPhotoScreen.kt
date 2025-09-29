package com.bservan.apps.contacts.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Camera
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bservan.apps.contacts.R
import com.bservan.apps.contacts.ui.state.ContactViewModel
import com.bservan.apps.contacts.ui.theme.ContactsBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddPhotoScreen(modifier: Modifier = Modifier, viewModel: ContactViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            photoBitmap = bitmap
            viewModel.onImageChanged(bitmap)
            onDismiss()
        }
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUriOrNull(context)
            if (uri != null) {
                takePictureLauncher.launch()
            }
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = createImageUriOrNull(context)
            if (uri != null) {
                takePictureLauncher.launch()
            }
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            CoroutineScope(Dispatchers.Default).launch {
                val bmp = loadBitmapFromUri(context, uri, maxDimPx = 2048)
                if (bmp != null) {
                    photoBitmap = bmp
                    viewModel.onImageChanged(bmp)
                    onDismiss()
                } else {
                    Toast.makeText(context, "Couldn't load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val requestReadMediaImages = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    val requestReadExternalStorage = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(context, "Storage permission is required to pick an image", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchGalleryWithPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            val perm = Manifest.permission.READ_MEDIA_IMAGES
            val granted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestReadMediaImages.launch(perm)
            }
        } else {
            val perm = Manifest.permission.READ_EXTERNAL_STORAGE
            val granted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestReadExternalStorage.launch(perm)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (LocalContext.current.
            packageManager.
            hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) &&
            Camera.getNumberOfCameras() > 0
        ) {
            Button(
                onClick = {
                    launchCamera()
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(Dp(2.0F), Color.Black),
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    disabledContentColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cam),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(6.dp))
                Text(text = "Camera")
            }
        }

        Button(
            onClick = {
                launchGalleryWithPermission()
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            border = BorderStroke(Dp(2.0F), Color.Black),
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                disabledContentColor = MaterialTheme.colorScheme.inversePrimary
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_gallery),
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(6.dp))
            Text(text = "Gallery")
        }

        TextButton(
            onClick = {
                onDismiss()
            },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = ContactsBlue,
                disabledContentColor = Color.Gray
            )
        ) {
            Text(
                text = "Cancel",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun createImageUriOrNull(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile("Picture_${timeStamp}_", ".jpg", storageDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            image
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private suspend fun loadBitmapFromUri(
    context: Context,
    uri: Uri,
    maxDimPx: Int = 512
): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val bitmap = if (Build.VERSION.SDK_INT >= 28) {
            val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        bitmap
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

