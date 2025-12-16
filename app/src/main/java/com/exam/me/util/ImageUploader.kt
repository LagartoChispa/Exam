package com.exam.me.util

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

// Changed from object to class for testability
class ImageUploader {

    fun bitmapToMultipart(bitmap: Bitmap, fieldName: String = "image"): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()

        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)

        return MultipartBody.Part.createFormData(fieldName, "profile.jpg", requestBody)
    }
}