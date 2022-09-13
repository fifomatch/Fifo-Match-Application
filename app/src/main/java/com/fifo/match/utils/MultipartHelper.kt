package com.fifo.match.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MultipartHelper {
    companion object {
        /*
        * Create Multipart Image File Object
        * */
        fun prepareFilePart(context: Context, partName: String, fileUri: Uri, imageFile: File): MultipartBody.Part {
            val extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
            val requestFile = imageFile.asRequestBody("image/$extension".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, imageFile.name, requestFile)
        }

        /*
        * Create Part Data From String
        * */

        fun prepareDataPart(data: String): RequestBody {
            return data.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        /*
              * Create Part json From String
              * */

        fun prepareJsonPart(data : String) : RequestBody{
            return data.toRequestBody("application/json".toMediaTypeOrNull())
        }

    }
}