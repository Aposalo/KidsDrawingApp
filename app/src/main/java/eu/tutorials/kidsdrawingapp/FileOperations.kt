package eu.tutorials.kidsdrawingapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import eu.tutorials.kidsdrawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileOperations(private val activity: AppCompatActivity, private val binding: ActivityMainBinding) {

    private val progressDialog = ProgressDialog(activity.applicationContext)

    fun getSaveOperationForBitmapFile() {
        if (isReadStorageAllowed()) {
            progressDialog.showProgressDialog()
            activity.lifecycleScope.launch{
                saveBitmapFile(getBitmapFromView(binding.flDrawingViewContainer))
            }
        }
    }

    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap) : String {
        var result: String
        withContext(Dispatchers.IO) {
            try {
                val bytes = ByteArrayOutputStream() // Creates a new byte array output stream.
                // The buffer capacity is initially 32 bytes, though its size increases if necessary.

                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

                val f = File(
                    activity.applicationContext.externalCacheDir?.absoluteFile.toString()
                            + File.separator + "KidDrawingApp_" + System.currentTimeMillis() / 1000 + ".jpg"
                )
                val fo =
                    FileOutputStream(f) // Creates a file output stream to write to the file represented by the specified object.
                fo.write(bytes.toByteArray()) // Writes bytes from the specified byte array to this file output stream.
                fo.close() // Closes this file output stream and releases any system resources associated with this stream. This file output stream may no longer be used for writing bytes.
                result = f.absolutePath // The file absolute path is return as a result.
                //We switch from io to ui thread to show a toast
                activity.runOnUiThread {
                    progressDialog.cancelProgressDialog()
                    if (result.isNotEmpty()) {
                        shareImage(result)
                    }
                }
            }
            catch (e: Exception) {
                result = ""
                Toast.makeText(
                    activity.applicationContext,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
        return result
    }

    private fun shareImage(result: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = FileProvider.getUriForFile(
                activity.applicationContext,
                "eu.kidsdrawingapp.fileprovider",
                File(result))
            sentImageThroughIntent(uri)
        }
        else{
            MediaScannerConnection.scanFile(activity.applicationContext, arrayOf(result), null) {
                _, uri ->
                sentImageThroughIntent(uri)
            }
        }
    }

    private fun sentImageThroughIntent(uri: Uri?) {
        val shareIntent = getShareIntent(uri)
        activity.applicationContext.startActivity(
            Intent.createChooser(
                shareIntent,
                "Share image using.."
            )
        )
    }

    private fun getShareIntent(uri: Uri?): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/png"
        return shareIntent
    }
}