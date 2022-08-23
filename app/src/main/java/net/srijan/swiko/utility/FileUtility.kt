package net.srijan.swiko.utility

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.*

class FileUtility {
    companion object {
        val instance = FileUtility()
    }

    fun getFile(context: Context, fileUri: Uri): File {
        val file = File(context.cacheDir, context.contentResolver.getFileName(fileUri))
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(fileUri, "r", null)
        parcelFileDescriptor?.let {
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            copyInputStreamToFile(inputStream, file)
        }
        return file
    }

    private fun copyInputStreamToFile(`in`: InputStream, file: File) {
        try {
            val out: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {

        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }

        return name
    }

    /*fun isSizePermitted(file: File): Boolean {
        val fileSize = if (!file.exists()) return false else file.length().toInt()
        val fileSizeInKb = fileSize / 1024
        val fileSizeInMb = fileSizeInKb / 1024
        val allowedSize = MainApplication.userObject?.CompanyInfo?.FileUploadSize
        if (allowedSize != null && fileSizeInMb <= allowedSize) {
            return true
        }
        return false
    }*/
}