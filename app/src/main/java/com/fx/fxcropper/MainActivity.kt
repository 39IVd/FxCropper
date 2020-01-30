package com.fx.fxcropper

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    private var select_video_crop : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_video_crop = findViewById(R.id.select_video_crop) as Button
        select_video_crop?.setOnClickListener {
            if (checkStoragePermission()) {
                val intent = Intent()
                intent.type = "video/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Video"),
                    REQUEST_TAKE_GALLERY_VIDEO
                )
            }
            else {
                requestStoragePermission()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
            if (resultCode == RESULT_OK) {
                val selectedImageUri = data!!.data

                // MEDIA GALLERY
                val path = getPath(selectedImageUri)
                if (path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        Log.v("filepath", path.toString())
                        Log.v("filepath_uri", selectedImageUri.toString())
                        startActivityForResult(
                            Intent(
                                this@MainActivity,
                                VideoCropActivity::class.java
                            ).putExtra("EXTRA_PATH", path),
                            VIDEO_TRIM
                        )
                        overridePendingTransition(0, 0)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Please select proper video",
                            Toast.LENGTH_LONG
                        )
                    }
                }
            }
        } else if (requestCode == VIDEO_TRIM) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val videoPath = data.extras!!.getString("INTENT_VIDEO_FILE")
                    Toast.makeText(
                        this@MainActivity,
                        "Video stored at " + videoPath!!,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_ID_STORAGE_PERMISSIONS
        )
    }

    private fun checkStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) === PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_STORAGE_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission granted, Click again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getPath(uri: Uri?): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri!!)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(this, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(this, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri!!.scheme!!, ignoreCase = true)) {
            return getDataColumn(this, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return ""
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            val currentApiVersion = Build.VERSION.SDK_INT
            //TODO changes to solve gallery video issue
            if (currentApiVersion > Build.VERSION_CODES.M && uri!!.toString().contains(getString(R.string.app_provider))) {
                cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.getString(column_index) != null) {
                        val state = Environment.getExternalStorageState()
                        val file: File
                        if (Environment.MEDIA_MOUNTED == state) {
                            file = File(
                                Environment.getExternalStorageDirectory().toString() + "/DCIM/",
                                cursor.getString(column_index)
                            )
                        } else {
                            file = File(context.filesDir, cursor.getString(column_index))
                        }
                        return file.absolutePath
                    }
                    return ""
                }
            } else {
                cursor =
                    context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return if (cursor.getString(column_index) != null) {
                        cursor.getString(column_index)
                    } else ""
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    companion object {

        private val REQUEST_ID_STORAGE_PERMISSIONS = 1
        private val REQUEST_TAKE_GALLERY_VIDEO = 100
        private val VIDEO_TRIM = 101
    }
}
