package com.ksfams.sgframework.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.annotation.RequiresApi
import com.ksfams.sgframework.constants.EXTERNAL_SD_CARD
import com.ksfams.sgframework.constants.SD_CARD
import java.io.File
import java.util.*

/**
 *
 * Storage 관련 유틸
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
 * 외부저장소 상태 확인
 *
 * @return
 */
fun isExternalStorageMounted(): Boolean
        = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

/**
 * Return the path of /system.
 *
 * @return the path of /system
 */
fun getRootPath(): String {
    return Environment.getRootDirectory().absolutePath
}

/**
 * 외부 저장소 path
 * INFO 갤럭시 노트10 Q 버전에서 동작은 되고 있음.
 *
 * @return String the path of /storage/emulated/0
 */
@Suppress("DEPRECATION")
fun getExternalStoragePath(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val sdcards: Map<String, File> = getAllStorageLocations()
        val file = sdcards[SD_CARD]
        if (file != null) return file.absolutePath
    }
    return Environment.getExternalStorageDirectory().absolutePath
}

/**
 * 저장소 사용이 가능한지 여부
 *
 * @return True if the external storage is available. False otherwise.
 */
fun isAvailable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
}

/**
 * 메모리 path
 *
 * @return String
 */
@Suppress("DEPRECATION")
fun getSdCardPath(): String {
    return Environment.getExternalStorageDirectory().path + "/"
}

/**
 * 저장소 쓰기 여부
 *
 * @return True if the external storage is writable. False otherwise.
 */
fun isWritable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state
}

/**
 * 내부 메모리(앱 설치 공간)의 사용가능
 * 용량을 반환한다. (byte)
 *
 * @return
 */
fun getInternalStorageFreeSpace(): Long {
    val mountPath: String = Environment.getDataDirectory().absolutePath
    return if (mountPath.isNotEmpty()) {
        val stat = StatFs(mountPath)
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong // 남은 공간 확인용 (getFreeBlocks()를 사용할 경우 사용하지 못하는 공간도 포함됨
        LogUtil.i("InternalStorage Available Size(Byte) = $bytesAvailable")
        bytesAvailable
    } else {
        -1
    }
}

/**
 * 앱 저장소의 사용가능
 * 용량을 반환한다. (byte)
 *
 * @receiver Context
 * @return
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun Context.getExternalStorageFreeSpace(): Long {
    val dataDir = getExternalFilesDir(null)?.parentFile ?: return -1
    val stat = StatFs(dataDir.absolutePath)
    val bytesAvailable =
            stat.blockSizeLong * stat.availableBlocksLong // 남은 공간 확인용 (getFreeBlocks()를 사용할 경우 사용하지 못하는 공간도 포함됨
    LogUtil.i("ExternalStorage Available Size(Byte) = $bytesAvailable")
    return bytesAvailable
}

/**
 * 앱 저장소의 저장 공간으로
 * 저장할 내용보다 큰지를 판단
 *
 * @receiver Context
 * @param fileSize
 * @return
 */
fun Context.isAvailableSaveSpace(fileSize: Long): Boolean {
    return fileSize > 0 && getExternalStorageFreeSpace() > 0 && getExternalStorageFreeSpace() > fileSize
}

/**
 * /Android/data/[app_package_name] 정보 리턴
 * 앱의 저장소이므로 별다른 permission 이 필요 없음.
 *
 * @receiver Context
 * @param dirName
 * @return
 */
fun Context.getExternalAppDir(dirName: String): File? {
    val dataDir = getExternalFilesDir(null)?.parentFile
    val appDir = File(dataDir, dirName)
    if (!appDir.exists()) {
        tryAndCatch(block = {
            File(dataDir, "nomedia").createNewFile()
        }, catch = {
            it?.let { throwable ->
                LogUtil.e(throwable)
            }
        })

        if (!appDir.mkdirs()) {
            LogUtil.e("create failed: $appDir")
            return null
        }
    }
    return appDir
}

/**
 * 로그 디렉토리 정보
 * Returns application log directory. Log directory will be created on SD card
 * <i>("/Android/data/[app_package_name]/log")</i> if card is mounted. Else - Android defines files directory on
 * device's file system.
 *
 * @receiver Context
 * @return Log {@link File directory}
 */
fun Context.getLogDirecory(): File {
    var appLogDir = this.filesDir
    if (isExternalStorageMounted()) {
        appLogDir = getExternalAppDir("log")
    }
    return appLogDir
}

/**
 * cache 디렉토리 정보
 * Returns application cache directory. Cache directory will be created on SD card
 * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted. Else - Android defines files directory on
 * device's file system.
 *
 * @receiver Context
 * @return Cache {@link File directory}
 */
fun Context.getCacheDirectory(): File {
    var appCacheDir = cacheDir
    if (isExternalStorageMounted()) {
        appCacheDir = getExternalAppDir("cache")
    }
    return appCacheDir
}

/**
 * Temp 디렉토리 정보
 * Returns application temporary directory. Temporary directory will be created on SD card
 * <i>("/Android/data/[app_package_name]/temp")</i> if card is mounted. Else - Android defines files directory on
 * device's file system.
 *
 * @receiver Context
 * @return Temporary {@link File directory}
 */
fun Context.getTempDirectory(): File {
    var appTempDir = filesDir
    if (isExternalStorageMounted()) {
        appTempDir = getExternalAppDir("temp")
    }
    return appTempDir
}

/**
 * Dump 디렉토리 정보
 * Returns application dump directory. Dump directory will be created on SD card
 * <i>("/Android/data/[app_package_name]/dump")</i> if card is mounted. Else - Android defines files directory on
 * device's file system.
 *
 * @receiver Context
 * @return Dump {@link File directory}
 */
fun Context.getDumpDirectory(): File {
    var appDumpDir = filesDir
    if (isExternalStorageMounted()) {
        appDumpDir = getExternalAppDir("dump")
    }
    return appDumpDir
}

/**
 * Download 디렉토리 정보
 * Returns application download directory. Download directory will be created on SD card
 * <i>("/Android/data/[app_package_name]/download")</i> if card is mounted. Else - Android defines files directory on
 * device's file system.
 *
 * @receiver Context
 * @return Download {@link File directory}
 */
fun Context.getDownloadDirectory(): File {
    var appDownloadDir = filesDir
    if (isExternalStorageMounted()) {
        appDownloadDir = getExternalAppDir("download")
    }
    return appDownloadDir
}

/**
 * Download 디렉토리 정보
 * Returns application download directory. Download directory will be created on SD card
 * <i>("/Android/data/[app_package_name]/DCIM")</i> if card is mounted. Else - Android defines files directory on
 * device's file system.
 *
 * @receiver Context
 * @return Download {@link File directory}
 */
fun Context.getDCIMDirectory(): File {
    var appDownloadDir = filesDir
    if (isExternalStorageMounted()) {
        appDownloadDir = getExternalAppDir("DCIM")
    }
    return appDownloadDir
}

/**
 * get all storage location
 *
 * @return Map<String, File> A map of all storage locations available
 */
@Suppress("DEPRECATION")
private fun getAllStorageLocations(): Map<String, File> {
    val map: MutableMap<String, File> = HashMap(10)
    val mMounts: MutableList<String> = ArrayList(10)
    val mVold: MutableList<String> = ArrayList(10)

    mMounts.add("/mnt/sdcard")
    mVold.add("/mnt/sdcard")
    try {
        val mountFile = File("/proc/mounts")
        if (mountFile.exists()) {
            val scanner = Scanner(mountFile)
            while (scanner.hasNext()) {
                val line = scanner.nextLine()
                if (line.startsWith("/dev/block/vold/")) {
                    val lineElements = line.split(" ").toTypedArray()
                    val element = lineElements[1]
                    // don't add the default mount path
                    // it's already in the list.
                    if (element != "/mnt/sdcard") mMounts.add(element)
                }
            }
        }
    } catch (e: Exception) {
        LogUtil.e(e)
    }

    try {
        val voldFile = File("/system/etc/vold.fstab")
        if (voldFile.exists()) {
            val scanner = Scanner(voldFile)
            while (scanner.hasNext()) {
                val line = scanner.nextLine()
                if (line.startsWith("dev_mount")) {
                    val lineElements = line.split(" ").toTypedArray()
                    var element = lineElements[2]
                    if (element.contains(":")) element = element.substring(0, element.indexOf(":"))
                    if (element != "/mnt/sdcard") mVold.add(element)
                }
            }
        }
    } catch (e: Exception) {
        LogUtil.e(e)
    }

    var i = 0
    while (i < mMounts.size) {
        val mount = mMounts[i]
        if (!mVold.contains(mount)) mMounts.removeAt(i--)
        i++
    }

    mVold.clear()

    val mountHash: MutableList<String> = ArrayList(10)
    for (mount in mMounts) {
        val root = File(mount)
        if (root.exists() && root.isDirectory && root.canWrite()) {
            val list = root.listFiles()
            var hash = "["
            if (list != null) {
                for (f in list) {
                    hash += f.name.hashCode().toString() + ":" + f.length() + ", "
                }
            }
            hash += "]"
            if (!mountHash.contains(hash)) {
                var key: String = SD_CARD + "_" + map.size
                if (map.isEmpty()) {
                    key = SD_CARD
                } else if (map.size == 1) {
                    key = EXTERNAL_SD_CARD
                }
                mountHash.add(hash)
                map[key] = root
            }
        }
    }
    mMounts.clear()
    if (map.isEmpty()) {
        map[SD_CARD] = Environment.getExternalStorageDirectory()
    }
    return map
}