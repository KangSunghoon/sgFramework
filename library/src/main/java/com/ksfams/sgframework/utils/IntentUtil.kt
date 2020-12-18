package com.ksfams.sgframework.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File
import java.util.*

/**
 *
 * Intent 관련 유틸
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

/**
 * Wanna check if you can resolve the intent?
 * Call [isIntentResolvable] with your intent and check it with the ease.
 *
 * @receiver Context
 * @param intent Intent
 * @return Boolean
 */
fun Context.isIntentResolvable(intent: Intent): Boolean
        = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()

/**
 * Return the intent of install app.
 * <p>Target APIs greater than 25 must hold
 * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
 *
 * @receiver String apk file path
 * @param isNewTask Boolean True to add flag of new task, false otherwise.
 * @return the intent of install app.
 */
fun String.getInstallAppIntent(isNewTask: Boolean = false): Intent? {
    return toFile()?.let {
        getInstallAppIntent(isNewTask)
    }
}

/**
 * Return the intent of install app.
 * <p>Target APIs greater than 25 must hold
 * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
 *
 * @receiver File apk file
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of install app.
 */
fun File.getInstallAppIntent(isNewTask: Boolean = false): Intent? {
    if (!isFileExist()) return null

    val intent = Intent(Intent.ACTION_VIEW)
    val data: Uri = fileToUri()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    intent.setDataAndType(data, "application/vnd.android.package-archive")
    return intent.getIntent(isNewTask)
}

/**
 * Return the intent of uninstall app.
 *
 * @receiver package name
 * @return the intent of uninstall app.
 */
fun String.getUninstallAppIntent(isNewTask: Boolean = false): Intent {
    val intent = Intent(Intent.ACTION_DELETE)
    intent.data = "package:$this".toUri()
    return intent.getIntent(isNewTask)
}

/**
 * NewTask 처리 여부
 *
 * @receiver Intent
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return
 */
fun Intent.getIntent(isNewTask: Boolean = true): Intent
        = if (isNewTask) this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else this

/**
 * 새로운 Activity를 수행하고
 * 현재 Activity를 스텍에서 제거
 *
 * @receiver Intent
 * @param additionalFlags Int
 */
fun Intent.clearStack(additionalFlags: Int = 0) {
    flags = additionalFlags or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}

/**
 * 새로운 Activity 를
 * 최상위로 호출
 *
 * @receiver Intent
 */
fun Intent.topStack() {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
}

/**
 * Return the intent of share image.
 *
 * @param content   The content.
 * @param imagePath The path of image.
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of share image
 */
fun getShareImageIntent(content: String,
                        imagePath: String,
                        isNewTask: Boolean): Intent? {
    return getShareImageIntent(content, imagePath.toFile(), isNewTask)
}

/**
 * Return the intent of share image.
 *
 * @param content   The content.
 * @param image     The file of image.
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of share image
 */
fun getShareImageIntent(content: String,
                        image: File?,
                        isNewTask: Boolean): Intent? {
    return if (image == null || !image.isFile) {
        null
    } else {
        getShareImageIntent(content, image.fileToUri(), isNewTask)
    }
}

/**
 * Return the intent of share image.
 *
 * @param content   The content.
 * @param uri       The uri of image.
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of share image
 */
fun getShareImageIntent(content: String,
                        uri: Uri,
                        isNewTask: Boolean): Intent {
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.type = "image/*"
    return intent.getIntent(isNewTask)
}

/**
 * Return the intent of share images.
 *
 * @param content    The content.
 * @param imagePaths The paths of images.
 * @param isNewTask  True to add flag of new task, false otherwise.
 * @return the intent of share images
 */
fun getShareImageIntent(content: String,
                        imagePaths: LinkedList<String>,
                        isNewTask: Boolean): Intent? {
    if (imagePaths.isEmpty()) return null
    val files: MutableList<File?> = ArrayList()
    imagePaths.forEach {
        files.add(it.toFile())
    }
    return getShareImageIntent(content, files, isNewTask)
}

/**
 * Return the intent of share images.
 *
 * @param content   The content.
 * @param images    The files of images.
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of share images
 */
fun getShareImageIntent(content: String,
                        images: List<File?>,
                        isNewTask: Boolean): Intent? {
    if (images.isEmpty()) return null
    val uris = ArrayList<Uri>()
    images.forEach {
        if (it == null || !it.isFile) return@forEach
        uris.add(it.fileToUri())
    }

    if (uris.isEmpty()) return null
    return getShareImageIntent(content, uris, isNewTask)
}

/**
 * Return the intent of share images.
 *
 * @param content   The content.
 * @param uris      The uris of image.
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of share image
 */
fun getShareImageIntent(content: String,
                        uris: ArrayList<Uri>,
                        isNewTask: Boolean): Intent {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
    intent.type = "image/*"
    return intent.getIntent(isNewTask)
}

/**
 * Return the intent of component.
 *
 * @param packageName The name of the package.
 * @param className   The name of class.
 * @param extra      The Bundle of extras to add to this intent.
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @return the intent of component
 */
fun getComponentIntent(packageName: String,
                       className: String,
                       extra: Bundle? = null,
                       isNewTask: Boolean): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    extra.isNotNull {
        intent.putExtras(it)
    }
    intent.component = ComponentName(packageName, className)
    return intent.getIntent(isNewTask)
}

/**
 * Return the intent of capture.
 *
 * @param outUri    The uri of output.
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return the intent of capture
 */
fun getCaptureIntent(outUri: Uri, isNewTask: Boolean): Intent {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return intent.getIntent(isNewTask)
}

/**
 * Return the intent of google play
 *
 * @param packageName
 * @return
 */
fun getGooglePlayIntent(packageName: String): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = "market://details?id=${packageName}".toUri()
    intent.getIntent()

    val chooserIntent = Intent.createChooser(intent, "Open With")
    return chooserIntent.getIntent()
}