package com.ksfams.sgframework.utils

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import com.ksfams.sgframework.models.enums.ContentColumns
import com.ksfams.sgframework.models.enums.ContentOrder
import com.ksfams.sgframework.models.media.Media
import java.io.File

/**
 *
 * Media Store 유틸
 *
 * Create Date  12/18/20
 * @version 1.00    12/18/20
 * @since   1.00
 * @see
 * @author  강성훈(ssogaree@gmail.com)
 * Revision History
 * who      when            what
 * 강성훈      12/18/20     신규 개발.
 */

/** provider 정의 */
private val cloudAuthorityProvider = kotlin.collections.arrayListOf("com.google.android.apps.photos.content",
        "com.google.android.apps.photos.contentprovider",
        "com.sec.android.gallery3d.provider")


/**
 * check uri is media uri
 *
 * @receiver Uri
 * @return True if Uri is a MediaStore Uri.
 */
fun Uri.isMediaUri(): Boolean
        = "media".equals(authority, ignoreCase = true)

/**
 * check uri is media uri
 *
 * @receiver Uri
 * @return Whether the Uri authority is MediaProvider.
 */
fun Uri.isMediaDocument(): Boolean
        = "com.android.providers.media.documents" == authority

/**
 * check uri is media uri
 *
 * @receiver Uri
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun Uri.isDownloadsDocument(): Boolean
        = "com.android.providers.downloads.documents" == authority

/**
 * check uri is media uri
 *
 * @receiver Uri
 * @return Whether the Uri authority is External Storage
 */
fun Uri.isExternalStorageDocument(): Boolean
        = "com.android.externalstorage.documents" == authority


/**
 * uri 의 실제 경로
 *
 * @receiver Uri
 * @param context Context
 * @return String?
 */
fun Uri.getPath(context: Context): String? {
    if (DocumentsContract.isDocumentUri(context, this)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument()) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                @Suppress("DEPRECATION")
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument()) {
            val id = DocumentsContract.getDocumentId(this)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
            )
            return context.getDataColumn(contentUri, null, null)
        } else if (isMediaDocument()) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = kotlin.arrayOf(
                    split[1]
            )
            return context.getDataColumn(contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(scheme, ignoreCase = true)) {
        return context.getDataColumn(this, null, null)
    } else if ("file".equals(scheme, ignoreCase = true)) {
        return path
    }
    return null
}

/**
 * create empty Uri in MediaStore
 *
 * @receiver Context
 * @param videoUri Boolean flag on kind of class. true - MediaStore.Video false - MediaStore.Images
 * @param internalStorage Boolean flag on saved at internal storage
 * @param title String
 * @return Uri?
 */
@JvmOverloads
fun Context.createUri(videoUri: Boolean = false,
                      internalStorage: Boolean = false,
                      title: String = nowDateString()): Uri? {
    val uri = if (videoUri && internalStorage) MediaStore.Video.Media.INTERNAL_CONTENT_URI
    else if (videoUri) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    else if (!videoUri && internalStorage) MediaStore.Images.Media.INTERNAL_CONTENT_URI
    else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val cv = ContentValues()
    if (videoUri) {
        cv.put(MediaStore.Video.Media.TITLE, title)
    } else {
        cv.put(MediaStore.Images.Media.TITLE, title)
    }
    return contentResolver.insert(uri, cv)
}

/**
 * Insert and save Bitmap into MediaStore
 *
 * @receiver Context
 * @param bitmap Bitmap
 * @param format CompressFormat
 * @param quality Int
 * @return Uri?
 */
fun Context.insertImage(bitmap: Bitmap,
                        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                        quality: Int = 100): Uri? {

    val file = createImageFile(format = format)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        values.put(MediaStore.Images.Media.MIME_TYPE, file.getMimeType())
        values.put(MediaStore.Images.Media.IS_PENDING, 1)

        val item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        item?.let {
            contentResolver.openFileDescriptor(item, "w", null).use {
                file.outputStream().use { output ->
                    bitmap.compress(format, quality, output)
                    if (bitmap.isRecycled) bitmap.recycle()
                }
            }
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)

        item?.let {
            contentResolver.update(it, values, null, null)
        }

        return item
    }
    else {
        file.outputStream().use { output ->
            bitmap.compress(format, quality, output)
            if (bitmap.isRecycled) bitmap.recycle()
        }

        @Suppress("DEPRECATION") val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, nowDateString(), null)
        return Uri.parse(path)
    }
}

/**
 * Request MediaScanning
 * Android Q 버전 이하만 사용 가능
 *
 * @receiver Context
 * @param[url] to request
 */
@Suppress("DEPRECATION")
fun Context.requestMediaScanner(url: String) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val contentUri = Uri.fromFile(File(url))
    mediaScanIntent.data = contentUri
    this.sendBroadcast(mediaScanIntent)
}

/**
 * uri 이미지의 orientation
 *
 * @receiver Context
 * @param uri Uri
 * @return Int
 */
fun Context.getOrientation(uri: Uri): Int {
    var orientation = 0
    uri.tryCatch {
        val cursor = contentResolver.query(uri, kotlin.arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
                null, null, null)
        cursor?.use {
            orientation = if (!cursor.moveToFirst()) 0 else cursor.getInt(0)
        }
    }

    return orientation
}

/**
 * [uri] 로부터 Bitmap 이미지를
 * 가져온다.
 *
 * @receiver Context
 * @param uri Uri
 * @return Bitmap?
 */
fun Context.getImageFromUri(uri: Uri?): Bitmap? {
    return tryCatch {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            uri?.let {
                val source = ImageDecoder.createSource(contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }
}

/**
 * Want All the Images from the User Phone?
 * Get them easily with the below method,
 * Make Sure You have READ_EXTERNAL_STORAGE Permission
 *
 * @receiver Context
 * @param sortBy
 * @param order
 * @return
 */
@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
fun Context.getAllImages(sortBy: ContentColumns = ContentColumns.DATE_ADDED,
                         order: ContentOrder = ContentOrder.DESCENDING): List<Media> {
    val data = ArrayList<Media>()
    val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            kotlin.arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.DATE_TAKEN
            ),
            null,
            null,
            sortBy.s + " " + order.s
    )

    cursor?.let {
        while (cursor.isClosed.not() && cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            val images = Media(id, uri)
            data.add(images)
        }
        cursor.close()
    }
    return data
}

/**
 * Want All the Videos from the User Phone?
 * Get them easily with the below method,
 * Make Sure You have READ_EXTERNAL_STORAGE Permission
 *
 * @receiver Context
 * @param sortBy
 * @param order
 * @return
 */
@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
fun Context.getAllVideos(sortBy: ContentColumns = ContentColumns.DATE_ADDED,
                         order: ContentOrder = ContentOrder.DESCENDING): List<Media> {
    val data = ArrayList<Media>()
    val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            kotlin.arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.DATE_TAKEN
            ),
            null,
            null,
            sortBy.s + " " + order.s
    )

    cursor?.let {
        while (cursor.isClosed.not() && cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
            val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            val videos = Media(id, uri)
            data.add(videos)
        }
        cursor.close()
    }
    return data
}

/**
 * Want All the Audios from the User Phone?
 * Get them easily with the below method,
 * Make Sure You have READ_EXTERNAL_STORAGE Permission
 *
 * @receiver Context
 * @param sortBy
 * @param order
 * @return
 */
@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
fun Context.getAllAudios(sortBy: ContentColumns = ContentColumns.DATE_ADDED,
                         order: ContentOrder = ContentOrder.DESCENDING): List<Media> {
    val data = ArrayList<Media>()
    val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            kotlin.arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.BUCKET_ID,
                    MediaStore.Audio.Media.DATE_TAKEN
            ),
            null,
            null,
            sortBy.s + " " + order.s
    )

    cursor?.let {
        while (cursor.isClosed.not() && cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
            val videos = Media(id, uri)
            data.add(videos)
        }
        cursor.close()
    }
    return data
}


/**
 * get Width of Video which given String
 *
 * @receiver String
 * @return width of video
 */
fun String.getVideoWidth(): Int {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this)

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    val width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
    retriever.release()
    return width
}

/**
 * get Height of Video which given String
 *
 * @receiver String
 * @return height of video
 */
fun String.getVideoHeight(): Int {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this)

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    val height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
    retriever.release()
    return height
}


/**
 * get id from MediaStore.Video with given
 *
 * @receiver Context
 * @param videoPath
 * @return
 */
fun Context.getVideoFileId(videoPath: String): String {
    var id = ""

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaScannerConnection.scanFile(this,
                kotlin.arrayOf(videoPath),
                null) { _, uri ->
            id = DocumentsContract.getDocumentId(uri)
        }
    }
    else {
        val projection = kotlin.arrayOf(MediaStore.Video.Media._ID)
        @Suppress("DEPRECATION")
        val where = MediaStore.Video.VideoColumns.DATA + "=?"
        val cursor: Cursor? = queryVideo(videoPath, projection, where)

        id = cursor?.use {
            it.getString(0) ?: ""
        }!!
    }
    return id
}

/**
 * get Duration of Video from MediaStore.Video with given [videoId]
 * you can get videoId from Context.getVideoFileId()
 *
 * @receiver Context
 * @param videoId Video ID
 */
fun Context.getVideoDuration(videoId: String): Long {
    val projection = kotlin.arrayOf(MediaStore.Video.VideoColumns.DURATION)
    val where = MediaStore.Video.VideoColumns._ID + "=?"
    val cursor = queryVideo(videoId, projection, where)

    return cursor?.use { it.getLong(0) } ?: 0
}


/**
 * _data colum infomation
 *
 * @receiver Context
 * @param uri Uri?
 * @param selection String?
 * @param selectionArgs Array<String>?
 * @return String?
 */
private fun Context.getDataColumn(uri: Uri?,
                                  selection: String?,
                                  selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = kotlin.arrayOf(column)
    try {
        cursor = contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * video query result
 *
 * @receiver Context
 * @param args
 * @param projection
 * @param where
 * @return
 */
private fun Context.queryVideo(args: String, projection: Array<String>, where: String): Cursor? {
    val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val whereArgs = kotlin.arrayOf(args)
    val cursor: Cursor? = contentResolver.query(uri, projection, where, whereArgs, null)

    if (cursor == null || !cursor.moveToFirst()) {
        return null
    }

    return cursor
}