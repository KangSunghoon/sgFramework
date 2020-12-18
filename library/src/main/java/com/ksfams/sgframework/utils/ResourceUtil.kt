package com.ksfams.sgframework.utils

import android.content.Context
import android.content.res.AssetManager
import androidx.annotation.RawRes
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 *
 * Resource 관련 유틸
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
 * Assets 파일을 String으로 리턴
 *
 * @receiver AssetManager
 * @param subDirectory name of directory
 * @param fileName name of file
 * @return String
 */
fun AssetManager.fileToString(subDirectory: String,
                              fileName: String): String? {
    return open("${subDirectory}/${fileName}").use {
        it.readBytes().toString(Charset.defaultCharset())
    }
}

/**
 * Assets 폴더의 파일 리스트
 *
 * @receiver AssetManager
 * @param subDirectory name of directory
 * @return list of file name
 */
@JvmName("assetsList")
fun AssetManager.asList(subDirectory: String): List<String>?
        = list(subDirectory)?.toList()

/**
 * Copy the file from assets.
 *
 * @receiver Context
 * @param assetsFilePath The path of file in assets.
 * @param destFilePath   The path of destination file.
 * @return `true`: success<br></br>`false`: fail
 */
fun Context.copyFileFromAssets(assetsFilePath: String,
                               destFilePath: String): Boolean {
    var res = true
    try {
        val assetList: Array<String>? = this.assets.list(assetsFilePath)
        if (assetList != null && assetList.isNotEmpty()) {
            for (asset in assetList) {
                res = res and copyFileFromAssets(
                        "$assetsFilePath/$asset",
                        "$destFilePath/$asset"
                )
            }
        } else {
            res = this.assets.open(assetsFilePath).writeFile(destFilePath, false) ?: false
        }
    } catch (e: IOException) {
        LogUtil.e(e)
        res = false
    }
    return res
}

/**
 * Copy the file from assets.
 *
 * @receiver Context
 * @param path String
 */
fun Context.copyAssets(path: String) {
    this.assets.list(path).tryCatch { files ->
        if (files.isNullOrEmpty()) {
            copyFile(path)
            return
        }

        File("${this.getExternalFilesDir(null)}/$path").mkdirs()
        files.forEach {
            val dirPath = if (path.isEmpty()) "" else "$path/"
            copyAssets("$dirPath$it")
        }
    }
}


/**
 * Return the content of assets.
 *
 * @receiver Context
 * @param assetsFilePath The path of file in assets.
 * @param charset    charset.
 * @return the content of assets
 */
fun Context.readAssets2String(assetsFilePath: String,
                              charset: Charset? = null): String? {
    val inputStream = try {
        assets.open(assetsFilePath)
    } catch (e: IOException) {
        LogUtil.e(e)
        return null
    }

    val bytes: ByteArray = inputStream.getByteArray() ?: return null
    return if (charset == null) {
        String(bytes)
    } else {
        try {
            String(bytes, charset)
        } catch (e: UnsupportedEncodingException) {
            LogUtil.e(e)
            ""
        }
    }
}

/**
 * Return the content of file in assets.
 *
 * @receiver Context
 * @param assetsPath  The path of file in assets.
 * @param charset charset.
 * @return the content of file in assets
 */
fun Context.readAssets2List(assetsPath: String,
                            charset: Charset? = null): List<String>? {
    return try {
        assets.open(assetsPath).getStringList(charset)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

/**
 * Copy the file from raw.
 *
 * @receiver Context
 * @param resId        The resource id.
 * @param destFilePath The path of destination file.
 * @return `true`: success<br></br>`false`: fail
 */
fun Context.copyFileFromRaw(@RawRes resId: Int, destFilePath: String): Boolean {
    val res: Boolean
    res = try {
        resources.openRawResource(resId).writeFile(destFilePath, false) ?: false
    } catch (e: IOException) {
        LogUtil.e(e)
        false
    }
    return res
}

/**
 * Return the content of resource in raw.
 *
 * @receiver Context
 * @param resId       The resource id.
 * @param charset charset.
 * @return the content of resource in raw
 */
fun Context.readRaw2String(@RawRes resId: Int, charset: Charset? = null): String? {
    val inputStream = try {
        resources.openRawResource(resId)
    } catch (e: IOException) {
        LogUtil.e(e)
        return null
    }

    val bytes: ByteArray = inputStream.getByteArray() ?: return null
    return if (charset == null) {
        String(bytes)
    } else {
        try {
            String(bytes, charset)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }
    }
}

/**
 * Return the content of resource in raw.
 *
 * @receiver Context
 * @param resId       The resource id.
 * @param charset charset.
 * @return the content of file in assets
 */
fun Context.readRaw2List(@RawRes resId: Int,
                         charset: Charset? = null): List<String?>? {
    return resources.openRawResource(resId).getStringList(charset)
}


/**
 * Assets 파일을
 * 내장 메모리로 복사
 * TODO 파일 복사 처리 유틸 개발 필요한지 검토해야 함.
 *
 * @receiver Context
 * @param fileName
 */
private fun Context.copyFile(fileName: String) {
    this.assets.open(fileName).use { stream ->
        File("${this.getExternalFilesDir(null)}/$fileName").outputStream().use { stream.copyTo(it) }
    }
}