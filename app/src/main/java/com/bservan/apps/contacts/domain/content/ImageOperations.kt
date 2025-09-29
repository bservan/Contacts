package com.bservan.apps.contacts.domain.content

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

fun Bitmap.toMultipartPart(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 90,
): MultipartBody.Part {
    val fieldName = "image"
    val mime = when (format) {
        Bitmap.CompressFormat.PNG -> "image/png"
        else -> "image/jpeg"
    }.toMediaType()

    val safeFilename = when (format) {
        Bitmap.CompressFormat.PNG -> "image.png"
        else -> "image.jpg"
    }

    val bos = ByteArrayOutputStream()
    require(this.compress(format, quality, bos)) { "Bitmap.compress() failed" }
    val body = bos.toByteArray().toRequestBody(mime)
    return MultipartBody.Part.createFormData(fieldName, filename = safeFilename, body = body)
}


fun bytesToBitmap(
    data: ByteArray?,
    inPrefConfig: Bitmap.Config = Bitmap.Config.ARGB_8888,
    maxWidth: Int? = null,
    maxHeight: Int? = null,
    mutable: Boolean = false
): Bitmap? {
    if (data == null) return null
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(data, 0, data.size, bounds)

    val options = BitmapFactory.Options().apply {
        inPreferredConfig = inPrefConfig
        inMutable = mutable
        inSampleSize = computeInSampleSize(
            bounds.outWidth, bounds.outHeight, maxWidth, maxHeight
        )
    }

    return BitmapFactory.decodeByteArray(data, 0, data.size, options)
}

private fun computeInSampleSize(
    srcW: Int, srcH: Int, maxW: Int?, maxH: Int?
): Int {
    if (maxW == null && maxH == null) return 1
    val targetW = maxW ?: srcW
    val targetH = maxH ?: srcH
    var inSample = 1
    val halfW = srcW / 2
    val halfH = srcH / 2
    while (halfW / inSample >= targetW && halfH / inSample >= targetH) {
        inSample *= 2
    }
    return inSample.coerceAtLeast(1)
}

