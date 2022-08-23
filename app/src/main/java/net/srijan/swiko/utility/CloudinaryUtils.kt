package net.srijan.swiko.utility

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import net.srijan.swiko.ui.CloudinaryConstants
import java.util.*

object CloudinaryUtils {

    var requestId: String? = null
    fun uploadImageToCloudinaryCloud(
        path: Uri, returnUrlFromCloudinary: (String) -> Unit,
    ) {
        val options: MutableMap<String, Any> = HashMap()
        options["resource_type"] = "image"
        options["folder"] = "Swiko_users"
        requestId = MediaManager.get().upload(path).options(options)
            .unsigned(CloudinaryConstants.UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.e("cloudinary", "started")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = bytes.toDouble() / totalBytes
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    returnUrlFromCloudinary.invoke(resultData["secure_url"].toString())
                    Log.e("cloudinary", resultData.toString())
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.e("cloudinary", error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }

}