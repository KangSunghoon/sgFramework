package com.ksfams.sgframework.utils

import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.ksfams.sgframework.constants.FILE_SEP
import com.ksfams.sgframework.modules.reference.ApplicationReference
import java.io.*
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 *
 * File Util
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

/** InputStream Buffer  */
const val BUFFER_SIZE = 8192


/**
 * make String to File
 *
 * @receiver String
 * @return File?
 */
fun String.toFile(): File? {
    return if (this.isNotEmpty()) {
        File(this)
    } else {
        null
    }
}

/**
 * filePath로 File 객체
 *
 * @param path
 * @param fileName
 * @return
 */
fun getFileByPath(path: String, fileName: String): File? {
    var file: File? = null
    if (path.isNotEmpty() && fileName.isNotEmpty()) {
        file = File(path, fileName)
    }
    return file
}

/**
 * 파일의 존재 여부
 *
 * @receiver String
 * @return
 */
fun String.isFileExist(): Boolean
        = toFile() != null && toFile()!!.exists()

/**
 * 파일의 존재 여부
 *
 * @receiver File?
 * @return
 */
fun File?.isFileExist(): Boolean
        = this != null && this.exists()

/**
 * 파일의 존재 여부
 *
 * @param path
 * @param fileName
 * @return
 */
fun isFileExist(path: String, fileName: String): Boolean {
    val file = getFileByPath(path, fileName)
    return file.isFileExist()
}

/**
 * 파일 생성 처리
 *
 * @receiver String
 * @return
 */
fun String.createOrExistFile(): Boolean {
    val file = toFile()
    if (file.isFileExist()) return file!!.isFile
    if (!file!!.parentFile.createOrExistsDir()) return false
    return file.createNewFile()
}

/**
 * 파일 생성 처리
 *
 * @receiver File
 * @return
 */
@Throws(IOException::class)
fun File.createOrExistsFile(): Boolean {
    if (exists()) return isFile
    if (!parentFile.createOrExistsDir()) return false
    return createNewFile()
}

/**
 * 파일 생성 처리
 *
 * @param path
 * @param fileName
 * @return
 */
fun createOrExistFile(path: String, fileName: String): Boolean {
    val file = getFileByPath(path, fileName)
    if (file.isFileExist()) return file!!.isFile
    if (!file!!.parentFile.createOrExistsDir()) return false
    return file.createNewFile()
}

/**
 * directory 생성 처리
 *
 * @receiver String
 * @return
 */
fun String.createOrExistsDir(): Boolean {
    val directory = toFile()
    return directory.createOrExistsDir()
}

/**
 * directory 생성 처리
 *
 * @receiver File?
 * @return
 */
fun File?.createOrExistsDir(): Boolean
        = this != null && if (exists()) isDirectory else mkdirs()

/**
 * File Uri 리턴
 * M 버전 이상은 Manifest에 반드시 file provider 설정해야 함.
 *
 * @receiver File
 * @return
 */
fun File.fileToUri(): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val authority: String = ApplicationReference.getApp().packageName + ".provider"
        FileProvider.getUriForFile(ApplicationReference.getApp(), authority, this)
    } else {
        Uri.fromFile(this)
    }
}

/**
 * StackTrace에서 파일명을 가져온다
 *
 * @receiver StackTraceElement
 * @return
 */
fun StackTraceElement.getClassFileName(): String {
    val fileName = this.fileName
    if (fileName != null) {
        return fileName
    }
    else {
        var className = this.className
        val classNameInfo = className.split("\\.".toRegex())
        if (classNameInfo.isNotEmpty()) {
            className = classNameInfo[classNameInfo.size - 1]
        }

        val index = className.indexOf('$')
        if (index != -1) {
            className = className.substring(0, index)
        }

        return "${className}.kt"
    }
}

/**
 * save file with content
 *
 * @param fullPath String
 * @param content String
 * @return File?
 */
fun saveFile(fullPath: String, content: String): File? = fullPath.toFile()?.apply {
    writeText(content, Charset.defaultCharset())
}

/**
 * Save String to a Given File
 *
 * @receiver String
 * @param file
 */
fun String.saveFile(file: File)
        = FileOutputStream(file).bufferedWriter().use {
    it.write(this)
    it.flush()
    it.close()
}

/**
 * read file to string
 *
 * @receiver File
 * @return String
 */
fun File.readFile(): String = this.readText(Charset.defaultCharset())

/**
 * Convert File to ByteArray
 *
 * @receiver File
 * @return ByteArray
 */
fun File.toByteArray(): ByteArray {
    val bos = ByteArrayOutputStream(this.length().toInt())
    val input = FileInputStream(this)
    val size = 1024
    val buffer = ByteArray(size)
    var len = input.read(buffer, 0, size)
    while (len != -1) {
        bos.write(buffer, 0, len)
        len = input.read(buffer, 0, size)
    }
    input.close()
    bos.close()
    return bos.toByteArray()
}

/**
 * 파일 삭제 처리
 *
 * @param path
 * @param fileName
 * @return
 */
fun delFile(path: String, fileName: String): Boolean {
    val file: File? = getFileByPath(path, fileName)
    file?.let { return it.delete() }
    return false
}

/**
 * 파일 삭제 처리
 *
 * @receiver String
 * @return
 */
fun String.delFile(): Boolean {
    val file = toFile()
    file?.let { return it.delete() }
    return false
}

/**
 * Delete the File or if its a Directory the delete all the contents
 *
 * @receiver File
 */
fun File.deleteAll() {
    if (isFile && exists()) {
        delete()
        return
    }

    if (isDirectory) {
        val files = listFiles()
        if (files == null || files.isEmpty()) {
            delete()
            return
        }

        files.forEach {
            it.deleteAll()
        }
        delete()
    }
}

/**
 * copy file / directory
 *
 * @receiver File
 * @param dest
 */
fun File.copy(dest: File) {
    if (isDirectory) {
        copyDirectory(dest)
    } else {
        copyFile(dest)
    }
}

/**
 * move file / directory
 *
 * @receiver File
 * @param dest
 */
fun File.move(dest: File) {
    if (isFile) {
        renameTo(dest)
    } else {
        moveDirectory(dest)
    }
}


/**
 * Test given path is exists and can read
 *
 * @receiver String
 * @return Boolean
 */
fun String.isExistReadFile(): Boolean = isFileExist() && toFile()?.canRead()!!

/**
 * Full Path에서 파일명만
 * 가져온다.
 *
 * @receiver String?
 * @return
 */
fun String?.getFileName(): String {
    return this?.substring(lastIndexOf(FILE_SEP) + 1, length) ?: ""
}

/**
 * get extensions of given file path
 *
 * @receiver String
 * @return String
 */
fun String.getFileExtension(): String {
    val lastPoi = this.lastIndexOf('.')
    val lastSep = this.lastIndexOf(File.separator)
    return if (lastPoi == -1 || lastSep >= lastPoi) "" else this.substring(lastPoi + 1)
}

/**
 * File Path를 통하여
 * 해당 파일의 MIME-TYPE을 리턴한다.
 * 일단 문서 파일만 정의함.
 *
 * @receiver String
 * @return String
 */
fun String.getMimeType(): String {
    val fileExtension: String = getFileExtension()
    return if (fileExtension.isNotEmpty()) {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.substring(1))!!
    } else {
        "application/octet-stream"
    }
}

/**
 * 해당 파일의 MIME-TYPE을 리턴한다.
 * 일단 문서 파일만 정의함.
 *
 * @receiver File
 * @return String
 */
fun File.getMimeType(): String {
    return absolutePath.getMimeType()
}

/**
 * 파일의 fileSize 를
 * Byte, KB, MB, GB. TB 에 맞추어 리턴한다.
 *
 * @param path
 * @param fileName
 * @return
 */
fun getFileSize(path: String, fileName: String): String {
    val file = getFileByPath(path, fileName)
    return file.getFileSize()
}

/**
 * 파일의 fileSize 를
 * Byte, KB, MB, GB. TB 에 맞추어 리턴한다.
 *
 * @receiver File?
 * @return String
 */
fun File?.getFileSize(): String {
    val fileSize: Long = this?.length() ?: 0
    return fileSize.toNumInUnits()
}

/**
 * 파일 복사 처리
 *
 * @receiver File
 * @param dest File
 */
fun File.copyFile(dest: File) {
    var fi: FileInputStream? = null
    var fo: FileOutputStream? = null
    var ic: FileChannel? = null
    var oc: FileChannel? = null

    tryAndCatch(block = {
        if (!dest.exists()) {
            dest.createNewFile()
        }
        fi = FileInputStream(this)
        fo = FileOutputStream(dest)
        ic = fi?.channel
        oc = fo?.channel
        ic?.size()?.let {
            ic?.transferTo(0, it, oc)
        }
    }, catch = { e ->
        e?.let { LogUtil.e(it) }
    }, finally = {
        fi?.close()
        fo?.close()
        ic?.close()
        oc?.close()
    })
}

/**
 * 디렉토리 복사 처리
 *
 * @receiver File
 * @param dest File
 */
fun File.copyDirectory(dest: File) {
    if (!dest.exists()) {
        dest.mkdirs()
    }

    var files = listFiles()
    files?.forEach {
        if (it.isFile) {
            it.copyFile(File("${dest.absolutePath}/${it.name}"))
        }

        if (it.isDirectory) {
            val dirSrc = File("${absolutePath}/${it.name}")
            val dirDest = File("${dest.absolutePath}/${it.name}")
            dirSrc.copyDirectory(dirDest)
        }
    }
}

/**
 * InputStream 으로 부터 file
 *
 * @receiver InputStream
 * @param filePath
 * @param append
 * @throws IOException
 * @return
 */
@Throws(IOException::class)
fun InputStream.writeFile(filePath: String,
                          append: Boolean): Boolean? {
    filePath.toFile()?.let {
        return writeFile(it, append)
    }
    return false
}

/**
 * InputStream 으로 부터 file
 *
 * @receiver InputStream
 * @param file
 * @param append
 * @throws IOException
 * @return
 */
fun InputStream.writeFile(file: File, append: Boolean): Boolean? {
    if (!file.createOrExistsFile()) return false

    // safe close
    return use {
        val os: OutputStream = BufferedOutputStream(FileOutputStream(file, append))
        os.use {
            val data = ByteArray(BUFFER_SIZE)
            var len: Int
            while (read(data, 0, BUFFER_SIZE).also { len = it } != -1) {
                os.write(data, 0, len)
            }
            return true
        }
    }
}

/**
 * Calcuate size into human-readable size
 *
 * @receiver Long
 * @return String
 */
fun Long.toNumInUnits(): String {
    var bytes = this
    var u = 0

    while (bytes > 1024 * 1024) {
        u++
        bytes = bytes shr 10
    }

    if (bytes > 1024) {
        u++
    }

    return String.format("%.1f %cB", bytes / 1024f, " kMGTPE"[u])
}

/**
 * 디렉토리 이동 처리
 *
 * @receiver File
 * @param dest File
 */
private fun File.moveDirectory(dest: File) {
    copyDirectory(dest)
    deleteAll()
}