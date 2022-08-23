package net.srijan.swiko.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import net.srijan.swiko.R
import net.srijan.swiko.utility.CloudinaryUtils
import net.srijan.swiko.utility.FileUtility
import net.srijan.swiko.utility.LoadingScreen
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class BaseActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var imageFile: File
    private lateinit var openCameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var mediaUrlFetcher: (String) -> Unit = {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openCameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    uploadImageToCloudinary(imageFile.absolutePath)
                }
            }
        openGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    uploadImageToCloudinary(result.data?.data?.let {
                        FileUtility.instance.getFile(
                            this,
                            it
                        ).absolutePath
                    })
                }
            }
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    showMediaSelectionDialog()
                } else {
                   // Toast.makeText(this, "Permission de de", Toast.LENGTH_SHORT).show()
                }

            }
    }


    fun uploadProfilePicture(urlFetcher: (String) -> Unit) {
        mediaUrlFetcher = urlFetcher
        if (checkPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            showMediaSelectionDialog()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            Log.e("permission", "p - $permission")
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            } else {
                continue
            }
        }
        return true
    }

    private fun showMediaSelectionDialog() {
        //Show Dialog for camera and Gallery options
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_upload_options)
        dialog.findViewById<LinearLayout>(R.id.camera_option_holder).setOnClickListener {
            openCamera()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.gallery_option_holder).setOnClickListener {
            openImageLibrary()
            dialog.dismiss()
        }
        dialog.show()

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val captureImage = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (captureImage.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "net.srijan.swiko.media.provider",
                    photoFile
                )
                captureImage.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    photoURI
                )
                openCameraLauncher.launch(captureImage)
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "Swiko_Time" + timeStamp + "_"
        val storageDir: File =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        imageFile = File.createTempFile(imageFileName, ".jpeg", storageDir)
        return imageFile
    }

    private fun openImageLibrary() {
        if (checkPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            openGalleryLauncher.launch(intent)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }


    //Image Upload on Cloudinary
    private fun uploadImageToCloudinary(absolutePath: String?) {
        absolutePath?.let { path ->
            LoadingScreen.displayLoadingWithText(this@BaseActivity, "Please Wait...", false)
            scope.launch {
                val result = suspendCoroutine<String> { cont ->
                    CloudinaryUtils.uploadImageToCloudinaryCloud(Uri.fromFile(File(path))) { imageUrl ->
                        cont.resume(imageUrl)
                    }
                }
                withContext(Dispatchers.Main) {
                    mediaUrlFetcher.invoke(result)
                    LoadingScreen.hideLoading()
                }

            }
        }


    }
}