package com.ksfams.sgframework.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.ksfams.sgframework.constants.LINE_SEP
import com.ksfams.sgframework.modules.reference.ApplicationReference
import com.ksfams.sgframework.modules.system.activityManager
import com.ksfams.sgframework.modules.system.appOpsManager
import com.ksfams.sgframework.modules.system.usageStatsManager
import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 *
 * 앱관련 유틸
 * 앱 정보 및 설치/삭제
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
 * Check if an App is enabled on the user device.
 *
 * @receiver Context
 * @param pName String
 * @return Boolean
 */
fun Context.isAppEnabled(pName: String = packageName): Boolean = guardRun {
    packageManager.getApplicationInfo(pName, PackageManager.GET_META_DATA).enabled
}

/**
 * Check if an App is Installed on the user device.
 *
 * @receiver Context
 * @param pName String
 * @return Boolean
 */
fun Context.isInstalled(pName: String = packageName): Boolean = guardRun {
    packageManager.getApplicationInfo(pName, PackageManager.GET_META_DATA)
}

/**
 * launch app.
 *
 * @receiver Context
 * @param packageName The name of the package.
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @param extra optional, Extra Data for Intent
 * @param url optional, URL Data for Inten
 * @return
 */
fun Context.launchApp(packageName: String, isNewTask: Boolean,
                      extra: Bundle? = null, url: String? = null) {
    if (packageName.isEmpty()) return

    if (isInstalled(packageName)) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        extra.isNotNull {
            intent?.putExtras(it)
        }

        url.isNotNull {
            intent?.data = url!!.toUri()
        }

        startActivity(intent?.getIntent(isNewTask))
    } else {
        startActivity(getGooglePlayIntent(packageName))
    }
}

/**
 * launch app.
 *
 * @receiver Activity
 * @param packageName
 * @param isNewTask
 * @param resultCode startActivityForResult call
 * @param extra optional, Extra Data for Intent
 * @param url optional, URL Data for Intent
 */
fun Activity.launchApp(packageName: String, isNewTask: Boolean,
                       resultCode: Int? = null, extra: Bundle? = null,
                       url: String? = null) {
    if (packageName.isEmpty()) return

    if (isInstalled(packageName)) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        extra.isNotNull {
            intent?.putExtras(it)
        }

        url.isNotNull {
            intent?.data = url!!.toUri()
        }

        if (resultCode != null) {
            startActivityForResult(intent?.getIntent(isNewTask), resultCode)
        } else {
            startActivity(intent?.getIntent(isNewTask))
        }
    } else {
        startActivity(getGooglePlayIntent(packageName))
    }
}

/**
 * launch app.
 *
 * @receiver Activity
 * @param cls Activity class
 * @param isNewTask
 * @param resultCode startActivityForResult call
 * @param extra optional, Extra Data for Intent
 * @param url optional, URL Data for Intent
 */
fun Activity.launchApp(cls: Class<out Activity>, isNewTask: Boolean,
                       resultCode: Int? = null, extra: Bundle? = null,
                       url: String? = null) {
    val intent = Intent(this, cls)
    extra.isNotNull {
        intent.putExtras(it)
    }

    url.isNotNull {
        intent.data = url!!.toUri()
    }

    if (resultCode != null) {
        startActivityForResult(intent.getIntent(isNewTask), resultCode)
    } else {
        startActivity(intent.getIntent(isNewTask))
    }
}

/**
 * launch app.
 *
 * @receiver Context
 * @param cls Activity class
 * @param isNewTask
 * @param extra optional, Extra Data for Intent
 * @param url optional, URL Data for Intent
 */
fun Context.launchApp(cls: Class<out Activity>, isNewTask: Boolean,
                      extra: Bundle? = null, url: String? = null) {
    val intent = Intent(this, cls)
    extra.isNotNull {
        intent.putExtras(it)
    }

    url.isNotNull {
        intent.data = url!!.toUri()
    }
    startActivity(intent.getIntent(isNewTask))
}

/**
 * Reboot Application
 * getLaunchIntentForPackage(): 외부앱 실행
 *
 * @receiver Context
 * @param restartIntent optional, desire activity for reboot
 */
@JvmOverloads
fun Context.rebootApp(restartIntent: Intent = packageManager.getLaunchIntentForPackage(packageName)!!) {
    restartIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    if (this is Activity) {
        this.startActivity(restartIntent)
        finishAllActivity(this)
    }
    else {
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(restartIntent)
    }
}

/**
 * Exit the application.
 */
fun exitApp() {
    val activityList: List<Activity> = ApplicationReference.getActivityList()
    for (i in activityList.indices.reversed()) { // remove from top
        val activity = activityList[i]
        // sActivityList remove the index activity at onActivityDestroyed
        activity.finish()
    }
    exitProcess(0)
}

/**
 * 앱 전체 종료
 *
 * @param activity Activity
 */
@SuppressLint("ObsoleteSdkInt")
fun finishAllActivity(activity: Activity) {
    activity.setResult(Activity.RESULT_CANCELED)
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> activity.finishAffinity()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> activity.runOnUiThread {
            activity.finishAffinity()
        }
        else -> ActivityCompat.finishAffinity(activity)
    }
}


/**
 * Return the application's icon.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's icon
 */
fun Context.getAppIcon(pName: String = packageName): Drawable? {
    if (pName.isEmpty()) return null

    return tryCatch {
        val pi = packageManager.getPackageInfo(pName, 0)
        pi?.applicationInfo?.loadIcon(packageManager)
    }
}

/**
 * Return the application's name.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's name
 */
fun Context.getAppName(pName: String = packageName): String? {
    if (pName.isEmpty()) return null

    return tryCatch {
        val pi = packageManager.getPackageInfo(pName, 0)
        pi?.applicationInfo?.loadLabel(packageManager)?.toString()
    }
}

/**
 * Return the application's path.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's path
 */
fun Context.getAppPath(pName: String = packageName): String? {
    if (pName.isEmpty()) return null

    return tryCatch {
        val pi = packageManager.getPackageInfo(pName, 0)
        pi?.applicationInfo?.sourceDir
    }
}

/**
 * 앱버전 정보를 가져온다
 *
 * @receiver Context
 * @param pName
 * @return Pair versionName, versionCode
 */
fun Context.getAppVersionInfo(pName: String = packageName): Pair<String, Long> {
    var versionName: String = ""
    var versionCode: Long = 0

    if (pName.isNotEmpty()) {
        tryAndCatch({
            val pi = packageManager.getPackageInfo(pName, 0)
            versionName = pi.versionName
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pi.versionCode.toLong()
            }
        })
    }
    return Pair(versionName, versionCode)
}

/**
 * get Application Version Name
 *
 * @receiver Context
 * @param pName the Package Name of the Target Application, Default is Current.
 * Provide Package or will provide the current App Detail
 */
fun Context.getAppVersionName(pName: String = packageName): String = getAppVersionInfo(pName).first

/**
 * get Application Version Code
 *
 * @receiver Context
 * @param pName the Package Name of the Target Application, Default is Current.
 * Provide Package or will provide the current App Detail
 */
fun Context.getAppVersionCode(pName: String = packageName): Long = getAppVersionInfo(pName).second

/**
 * get Application Size in Bytes
 *
 * @receiver Context
 * @param pName the Package Name of the Target Application, Default is Current.
 * Provide Package or will provide the current App Detail
 */
fun Context.getAppSize(pName: String = packageName): Long {
    return packageManager.getApplicationInfo(pName, 0).sourceDir.toFile()?.length() ?: 0
}

/**
 * get Application Apk File
 *
 * @receiver Context
 * @param pName the Package Name of the Target Application, Default is Current.
 * Provide Package or will provide the current App Detail
 */
fun Context.getAppApk(pName: String = packageName): File? {
    return packageManager.getApplicationInfo(pName, 0).sourceDir.toFile()
}

/**
 * Return the application's signature.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's signature
 */
fun Context.getAppSignature(pName: String = packageName): Array<Signature>? {
    if (pName.isEmpty()) return null

    return tryCatch {
        val pi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.packageManager.getPackageInfo(pName, PackageManager.GET_SIGNING_CERTIFICATES)
        } else {
            @Suppress("DEPRECATION")
            this.packageManager.getPackageInfo(pName, PackageManager.GET_SIGNATURES)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.signingInfo.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            pi.signatures
        }
    }
}

/**
 * Return the application's signature for SHA1 value.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's signature for SHA1 value
 */
fun Context.getAppSignatureSHA1(pName: String = packageName): String?
        = getAppSignatureHash(pName, "SHA1")

/**
 * Return the application's signature for SHA256 value.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's signature for SHA256 value
 */
fun Context.getAppSignatureSHA256(pName: String = packageName): String?
        = getAppSignatureHash(pName, "SHA256")

/**
 * Return the application's signature for MD5 value.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return the application's signature for MD5 value
 */
fun Context.getAppSignatureMD5(pName: String = packageName): String?
        = getAppSignatureHash(pName, "MD5")


/**
 * Return the application's information.
 *
 *  * name of package
 *  * icon
 *  * name
 *  * path of package
 *  * version name
 *  * version code
 *  * is system
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return AppInfo
 */
fun Context.getAppInfo(pName: String = packageName): AppInfo? {
    if (pName.isEmpty()) return null

    return tryCatch {
        val pi = packageManager.getPackageInfo(pName, 0)
        getBean(pi)
    }
}

/**
 * Return the applications' information.
 *
 * @receiver Context
 * @return the applications' information
 */
fun Context.getAppsInfo(): List<AppInfo> {
    val list: MutableList<AppInfo> = ArrayList()

    val installedPackages = packageManager.getInstalledPackages(0)
    installedPackages.forEach { pi ->
        val ai: AppInfo? = getBean(pi)
        ai?.let { list.add(it) }
    }
    return list
}

/**
 * Launch app. install
 * <p>Target APIs greater than 25 must hold
 * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
 *
 * @receiver Context
 * @param apkFilePath apk file path
 * @return
 */
fun Context.installApp(apkFilePath: String): Boolean
        = installApp(apkFilePath.toFile())

/**
 * Launch app. install
 * <p>Target APIs greater than 25 must hold
 * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
 *
 * @receiver Context
 * @param apkFilePath apk file path
 * @return
 */
fun Context.installApp(apkFile: File?): Boolean {
    if (!apkFile.isFileExist()) return false

    return guardRun {
        startActivity(apkFile?.getInstallAppIntent())
    }
}

/**
 * Install the app.
 *
 * Target APIs greater than 25 must hold
 * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
 *
 * @receiver Activity
 * @param apkFilePath    The path of file.
 * @param requestCode If &gt;= 0, this code will be returned in
 * onActivityResult() when the activity exits.
 * @return
 */
fun Activity.installApp(apkFilePath: String, requestCode: Int): Boolean
        = installApp(apkFilePath.toFile(), requestCode)

/**
 * Install the app.
 *
 * Target APIs greater than 25 must hold
 * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
 *
 * @receiver Activity
 * @param apkFile        The file.
 * @param requestCode If &gt;= 0, this code will be returned in
 * onActivityResult() when the activity exits.
 * @return
 */
fun Activity.installApp(apkFile: File?, requestCode: Int): Boolean {
    if (!apkFile.isFileExist()) return false
    return guardRun {
        startActivityForResult(apkFile?.getInstallAppIntent(false), requestCode)
    }
}


/**
 * Install the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
 *
 * @receiver String file path
 * @return `true`: success<br></br>`false`: fail
 */
fun String.installAppSilent(): Boolean {
    toFile()?.let {
        return installAppSilent(null)
    }
    return false
}

/**
 * Install the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
 *
 * @receiver File
 * @return `true`: success<br></br>`false`: fail
 */
fun File.installAppSilent(): Boolean
        = installAppSilent(null)

/**
 * Install the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
 *
 * @receiver String The path of file.
 * @param params   The params of installation(e.g.,`-r`, `-s`).
 * @return `true`: success<br></br>`false`: fail
 */
fun String.installAppSilent(params: String?): Boolean {
    toFile()?.let {
        return installAppSilent(params)
    }
    return false
}

/**
 * Install the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
 *
 * @receiver File   The file.
 * @param params The params of installation(e.g.,`-r`, `-s`).
 * @return `true`: success<br></br>`false`: fail
 */
fun File.installAppSilent(params: String?): Boolean
        = installAppSilent(params, isDeviceRooted())

/**
 * Install the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
 *
 * @receiver File     The file.
 * @param params   The params of installation(e.g.,`-r`, `-s`).
 * @param isRooted True to use root, false otherwise.
 * @return `true`: success<br></br>`false`: fail
 */
@SuppressLint("DefaultLocale")
fun File.installAppSilent(params: String?, isRooted: Boolean): Boolean {
    if (!isFileExist()) return false

    val filePath = '"'.toString() + absolutePath + '"'
    val command = ("LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " +
            (if (params == null) "" else "$params ")
            + filePath)
    val commandResult: CommandResult? = execCmd(command, isRooted)
    return if (commandResult?.successMsg != null
            && commandResult.successMsg.toLowerCase().contains("success")) {
        true
    } else {
        LogUtil.e("installAppSilent successMsg: ${commandResult?.successMsg}, errorMsg: ${commandResult?.errorMsg}")
        false
    }
}

/**
 * Uninstall the app.
 *
 * @receiver Context
 * @param packageName The name of the package.
 * @return
 */
fun Context.uninstallApp(packageName: String): Boolean {
    if (packageName.isEmpty()) return false

    return guardRun {
        startActivity(packageName.getUninstallAppIntent())
    }
}

/**
 * Uninstall the app.
 *
 * @receiver Activity
 * @param packageName The name of the package.
 * @param requestCode If &gt;= 0, this code will be returned in
 * onActivityResult() when the activity exits.
 * @return
 */
fun Activity.uninstallApp(packageName: String, requestCode: Int): Boolean {
    if (packageName.isEmpty()) return false

    return guardRun {
        startActivityForResult(packageName.getUninstallAppIntent(false), requestCode)
    }
}

/**
 * Uninstall the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`
 *
 * @receiver String The name of the package.
 * @return `true`: success<br></br>`false`: fail
 */
fun String.uninstallAppSilent(): Boolean
        = uninstallAppSilent(false)

/**
 * Uninstall the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`
 *
 * @receiver String The name of the package.
 * @param isKeepData  Is keep the data.
 * @return `true`: success<br></br>`false`: fail
 */
fun String.uninstallAppSilent(isKeepData: Boolean): Boolean
        = uninstallAppSilent(isKeepData, isDeviceRooted())

/**
 * Uninstall the app silently.
 *
 * Without root permission must hold
 * `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`
 *
 * @receiver String The name of the package.
 * @param isKeepData  Is keep the data.
 * @param isRooted    True to use root, false otherwise.
 * @return `true`: success<br></br>`false`: fail
 */
@SuppressLint("DefaultLocale")
fun String.uninstallAppSilent(isKeepData: Boolean, isRooted: Boolean): Boolean {
    if (isEmpty()) return false

    val command = ("LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall "
            + (if (isKeepData) "-k " else "")
            + this)

    val commandResult: CommandResult? = execCmd(command, isRooted)
    return if (commandResult?.successMsg != null
            && commandResult.successMsg.toLowerCase().contains("success")) {
        true
    } else {
        LogUtil.e("uninstallAppSilent successMsg: ${commandResult?.successMsg}, errorMsg: ${commandResult?.errorMsg}")
        false
    }
}

/**
 * Return whether the application with root permission.
 *
 * @return `true`: yes<br></br>`false`: no
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
fun isAppRoot(): Boolean {
    val result: CommandResult? = execCmd("echo root", true)
    result?.let {
        if (it.result === 0) return true
        LogUtil.d("isAppRoot() called ${result.errorMsg}")
    }
    return false
}

/**
 * Return whether it is a debug application.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return `true`: yes<br></br>`false`: no
 */
fun Context.isAppDebug(pName: String = packageName): Boolean {
    if (pName.isEmpty()) return false

    return guardRun {
        val ai = packageManager.getApplicationInfo(pName, 0)
        ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}

/**
 * Return whether it is a system application.
 *
 * @receiver Context
 * @param pName The name of the package.
 * @return `true`: yes<br></br>`false`: no
 */
fun Context.isAppSystem(pName: String = packageName): Boolean {
    if (pName.isEmpty()) return false

    return guardRun {
        val ai = packageManager.getApplicationInfo(pName, 0)
        ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}

/**
 * Return whether application is foreground.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isAppForeground(): Boolean = ApplicationReference.isForground()

/**
 * Return whether application is foreground.
 *
 * Target APIs greater than 21 must hold
 * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
 *
 * @receiver Context
 * @param packageName The name of the package.
 * @return `true`: yes<br></br>`false`: no
 */
fun Context.isAppForeground(packageName: String): Boolean {
    return packageName.isNotEmpty() && packageName == getForegroundProcessName()
}


/**
 * Return ForegroundProcess PackageName
 *
 * Target APIs greater than 21 must hold
 * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
 *
 * @receiver Context
 * @return
 */
fun Context.getForegroundProcessName(): String? {
    val am = activityManager
    val pInfo = am.runningAppProcesses
    pInfo?.forEach {
        if (it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return it.processName
        }
    }

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        val pm: PackageManager = packageManager
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        val list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        LogUtil.i(list)
        if (list.isEmpty()) {
            LogUtil.i("getForegroundProcessName: noun of access to usage information.")
            return ""
        }

        try { // Access to usage information.
            val info = pm.getApplicationInfo(packageName, 0)
            val aom = appOpsManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (aom.unsafeCheckOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName
                        )
                        != AppOpsManager.MODE_ALLOWED
                ) {
                    startActivity(intent.getIntent())
                }

                if (aom.unsafeCheckOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName
                        )
                        != AppOpsManager.MODE_ALLOWED
                ) {
                    LogUtil.i("getForegroundProcessName: refuse to device usage stats.")
                    return ""
                }
            }
            else {
                @Suppress("DEPRECATION")
                if (aom.checkOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName
                        )
                        != AppOpsManager.MODE_ALLOWED
                ) {
                    startActivity(intent.getIntent())
                }

                @Suppress("DEPRECATION")
                if (aom.checkOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName
                        )
                        != AppOpsManager.MODE_ALLOWED
                ) {
                    LogUtil.i("getForegroundProcessName: refuse to device usage stats.")
                    return ""
                }
            }

            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 86400000 * 7
            val usageStatsList = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST,
                    beginTime, endTime)

            var recentStats: UsageStats? = null
            usageStatsList?.forEach {
                if (recentStats == null
                        || it.lastTimeUsed > recentStats?.lastTimeUsed!!) {
                    recentStats = it
                }
            }
            return recentStats?.packageName
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtil.e(e)
        }
    }
    return ""
}

/**
 * Creates App ShortCut to the launcher Screen
 *
 * @receiver Context
 * @param shortcutName The Name of the SHortCut
 * @param iconId the Resource Drawable for the Shortcut
 * @param presentIntent the Intent will be fire on Clicking the ShortCut.
 */
fun Context.createShortcut(shortcutName: String,
                           @DrawableRes iconId: Int,
                           presentIntent: Intent) {
    val shortcutInfo = ShortcutInfoCompat.Builder(this, UUID.randomUUID().toString())
            .setIntent(presentIntent)
            .setIcon(IconCompat.createWithResource(this, iconId))
            .setLongLabel(shortcutName)
            .build()
    ShortcutManagerCompat.createShortcutResultIntent(this, shortcutInfo)
}

/**
 * Badge count 처리
 *
 * @receiver Context
 * @param count Int
 */
fun Context.applyBadgeCount(count: Int = 0) {
    val launcherClassName = getLauncherActivity()
    val intent: Intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
    intent.putExtra("badge_count_package_name", packageName)
    intent.putExtra("badge_count_class_name", launcherClassName)
    intent.putExtra("badge_count", count)
    intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
    sendBroadcast(intent)
}

/**
 * get App Signature Hash
 *
 * @receiver Context
 * @param pName String
 * @param algorithm String
 * @return String?
 */
private fun Context.getAppSignatureHash(pName: String = packageName,
                                        algorithm: String): String? {
    if (pName.isEmpty()) return null

    val signingInfo = getAppSignature(pName)
    signingInfo?.let {
        it[0].toByteArray().hashTemplate(algorithm).bytes2HexString()?.replace("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0")
    }
    return null
}


/**
 * The application's information
 * bean setting
 *
 * @receiver Context
 * @param pi
 * @return
 */
private fun Context.getBean(pi: PackageInfo?): AppInfo? {
    if (pi == null) return null

    val ai = pi.applicationInfo
    val packageName = pi.packageName
    val name = ai.loadLabel(packageManager).toString()
    val icon = ai.loadIcon(packageManager)
    val packagePath = ai.sourceDir
    val versionName = pi.versionName
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pi.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        pi.versionCode.toLong()
    }
    val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
    return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
}


/**
 * The application's information.
 */
data class AppInfo(var packageName: String, var name: String,
                   var icon: Drawable, var packagePath: String,
                   var versionName: String, var versionCode: Long,
                   var isSystem: Boolean) {

    override fun toString(): String {
        return "pkg name: " + packageName +
                LINE_SEP + "app icon: " + icon +
                LINE_SEP + "app name: " + name +
                LINE_SEP + "app path: " + packagePath +
                LINE_SEP + "app v name: " + versionName +
                LINE_SEP + "app v code: " + versionCode +
                LINE_SEP + "is system: " + isSystem
    }
}