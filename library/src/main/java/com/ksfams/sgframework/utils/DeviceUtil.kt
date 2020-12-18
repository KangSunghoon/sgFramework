package com.ksfams.sgframework.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Insets
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import android.view.PixelCopy
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.ksfams.sgframework.modules.system.keyguardManager
import com.ksfams.sgframework.modules.system.powerManager
import com.ksfams.sgframework.modules.system.windowManager
import kotlin.math.round

/**
 *
 * Device 관련 유틸
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
 * Return whether device is tablet.
 */
inline val Context.isTablet: Boolean
    get() = ((resources.getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK)
            >= Configuration.SCREENLAYOUT_SIZE_LARGE)

/**
 * Return the density of screen.
 */
inline val Context.displayDensity: Float
    get() = resources.displayMetrics.density

/**
 * Return the screen density expressed as dots-per-inch.
 */
inline val Context.displayDensityDpi: Int
    get() = resources.displayMetrics.densityDpi

/**
 * Screen 사이즈 (width)
 */
inline val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

/**
 * Screen 사이즈 (height)
 */
inline val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels


/**
 * get Height of status bar
 */
inline val Context.statusBarHeight: Int
    get() {
        val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
        return this.resources.getDimensionPixelSize(resourceId)
    }

/**
 * get Height of navigation bar
 */
inline val Context.navigationBarHeight: Int
    get() {
        var height = 0
        val navigationBarResourceId =
                resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if (navigationBarResourceId > 0 && resources.getBoolean(navigationBarResourceId)) {
            val navigationBarHeightResourceId =
                    resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (navigationBarHeightResourceId > 0) {
                height = resources.getDimensionPixelSize(navigationBarHeightResourceId)
            }
        }
        return height
    }


/**
 * dp --> pixel 변환
 *
 * @receiver Context
 * @param dp Int
 * @return Int
 */
fun Context.dpToPixel(dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), resources.displayMetrics).toInt()
}

/**
 * dp --> pixel 변환
 *
 * @receiver Context
 * @param dp Float
 * @return Int
 */
fun Context.dpToPixel(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp, resources.displayMetrics).toInt()
}

/**
 * pixel --> dp 변환
 *
 * @receiver Context
 * @param px Int
 * @return Float
 */
fun Context.pixelToDp(px: Int): Float = px.toFloat() / displayDensity

/**
 * sp --> pixel 변환
 *
 * @receiver Context
 * @param sp Int
 * @return Int
 */
fun Context.spToPixel(sp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(), resources.displayMetrics).toInt()
}

/**
 * sp --> pixel 변환
 *
 * @receiver Context
 * @param sp Float
 * @return Int
 */
fun Context.spToPixel(sp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            sp, resources.displayMetrics).toInt()
}

/**
 * pixel --> sp 변환
 *
 * @receiver Context
 * @param px Int
 * @return Int
 */
fun Context.pixelsToSp(px: Int): Int
        = round(px.toFloat() / resources.displayMetrics.scaledDensity).toInt()

/**
 * pixel --> sp 변환
 *
 * @receiver Context
 * @param px Float
 * @return Int
 */
fun Context.pixelsToSp(px: Float): Int
        = round(px / resources.displayMetrics.scaledDensity).toInt()

/**
 * pixel --> pt 변환
 *
 * @receiver Context
 * @param px Float
 * @return Int value of pt
 */
fun Context.pixelsToPt(px: Float): Int {
    val metrics: DisplayMetrics = resources.displayMetrics
    return (px * 72 / metrics.xdpi + 0.5).toInt()
}

/**
 * pixel --> pt 변환
 *
 * @receiver Context
 * @param px Int
 * @return Int
 */
fun Context.pixelsToPt(px: Int): Int {
    val metrics: DisplayMetrics = resources.displayMetrics
    return (px.toFloat() * 72 / metrics.xdpi + 0.5).toInt()
}

/**
 * portrait 모드로 변환
 *
 * @receiver Activity
 */
fun Activity.setPortrait() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

/**
 * landscape 변환
 *
 * @receiver Activity
 */
fun Activity.setLandscape() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

/**
 * portrait 여부
 *
 * @receiver Activity
 * @return
 */
fun Activity.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

/**
 * landscape 여부
 *
 * @receiver Activity
 * @return
 */
fun Activity.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Return the rotation of screen.
 *
 * @receiver Activity
 * @return the rotation of screen
 */
fun Activity.getScreenRotation(): Int {
    val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this.display?.rotation
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.rotation
    }

    return when (rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }
}

//TODO Android 11 버전에 대한 Full Screen 관련 확인 필요
///**
// * Return whether screen is full.
// *
// * @receiver Activity
// * @return {@code true}: yes<br>{@code false}: no
// */
//fun Activity.isFullScreen(): Boolean {
//    val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
//    return window.getAttributes().flags and fullScreenFlag == fullScreenFlag
//}
//
///**
// * Set full screen.
// *
// * @receiver Activity
// */
//fun Activity.setFullScreen() {
//    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//}
//
///**
// * Set non full screen.
// *
// * @receiver Activity
// */
//fun Activity.setNonFullScreen() {
//    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//}
//
///**
// * Toggle full screen.
// *
// * @receiver Activity
// */
//fun Activity.toggleFullScreen() {
//    val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
//    if (window.attributes.flags and fullScreenFlag == fullScreenFlag) {
//        window.clearFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
//    } else {
//        window.addFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
//    }
//}


/**
 * Activity 화면 캡처
 * Oreo 버전까지 사용 가능함.
 *
 * @receiver Activity
 * @param isExceptStatusBar status bar 를 제외할 지 여부
 * @return Bitmap object
 */
@Suppress("DEPRECATION")
@Deprecated("This function is deprecated")
@JvmOverloads
fun Activity.screenShot(isExceptStatusBar: Boolean = true): Bitmap {

    // DecorView는 간단히 말해서 Top-Level View 중 하나라고 생각하시면 될 것 같습니다.
    // 윈도우의 Background Drawable View를 담고 있는 객체인 셈이죠.
    // 이 DecorView의 setSystemUiVisibility() 메소드를 통해서 최상위 View를 어느정도 제어할 수가 있습니다.
    val decorView = window.decorView
    decorView.isDrawingCacheEnabled = true
    decorView.buildDrawingCache()

    val bmp = decorView.drawingCache
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)

    val ret: Bitmap = if (isExceptStatusBar) {
        Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight)
    } else {
        Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
    }

    decorView.destroyDrawingCache()
    return ret
}


/**
 * View 를 이용한
 * Activity 화면 캡처
 * Oreo 버전 이상.
 *
 * @receiver Activity
 * @param view View
 * @param isExceptStatusBar Boolean
 * @param callback Function1<Bitmap, Unit> 캡처 결과
 */
@RequiresApi(Build.VERSION_CODES.O)
@JvmOverloads
fun Activity.screenShot(view: View, isExceptStatusBar: Boolean = true, callback: (Bitmap) -> Unit) {
    val bitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val locationOfViewWindow = IntArray(2)
    view.getLocationInWindow(locationOfViewWindow)

    var top = locationOfViewWindow[1]
    if (isExceptStatusBar) {
        top = locationOfViewWindow[1] + statusBarHeight
    }

    try {
        PixelCopy.request(window,
                Rect(locationOfViewWindow[0], top,
                        locationOfViewWindow[0] + view.width, top + view.height),
                bitmap, { copyResult: Int ->
            if (copyResult == PixelCopy.SUCCESS) {
                callback(bitmap)
            }
        },
                Handler(Looper.getMainLooper())
        )
    } catch (e: IllegalArgumentException) {
        // PixelCopy may throw IllegalArgumentException, make sure to handle it
        LogUtil.e(e)
    }
}

/**
 * Return whether screen is locked.
 *
 * @receiver Activity
 * @return {@code true}: yes<br>{@code false}: no
 */
fun Activity.isScreenLock(): Boolean {
    return keyguardManager.isKeyguardLocked
}

/**
 * Set the duration of sleep.
 * <p>Must hold {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
 *
 * @receiver Context
 * @param duration The duration.
 */
@RequiresPermission(Manifest.permission.WRITE_SETTINGS)
fun Context.setSleepDuration(duration: Int) {
    Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, duration)
}

/**
 * Return the duration of sleep.
 *
 * @receiver Context
 * @return the duration of sleep.
 */
fun Context.getSleepDuration(): Int {
    return try {
        Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
    } catch (e: Settings.SettingNotFoundException) {
        LogUtil.e(e)
        -123
    }
}

/**
 * 스마트폰 스크린 인지 여부
 *
 * @receiver Context
 * @return
 */
fun Context.isPhoneScreenType(): Boolean {
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this.display
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay
    }
    val rotation = display?.rotation

    val size = getSize()
    return if (Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation) {
        size.x <= size.y
    } else { // This is a Phone and it is in Landscape orientation
        // This is a Tablet and it is in Portrait orientation
        size.x > size.y
    }
}

/**
 * Android 11 버전 부터 display.getSize Deprecated 되어
 * size 를 가져오는 Method 를 생성함.
 *
 * @receiver Context
 * @return Point
 */
@SuppressLint("NewApi")
fun Context.getSize(): Point {
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics = windowManager.currentWindowMetrics
        val windowInsets = metrics.windowInsets
        var insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        val cutout = windowInsets.displayCutout
        cutout?.let {
            val cutoutSafeInsets = Insets.of(it.safeInsetLeft, it.safeInsetTop, it.safeInsetRight, it.safeInsetBottom)
            insets = Insets.max(insets, cutoutSafeInsets)
        }

        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom

        @Suppress("NAME_SHADOWING")
        val size = Size(metrics.bounds.width() - insetsWidth, metrics.bounds.height() - insetsHeight)
        point.x = size.width
        point.y = size.height
    } else {
        @Suppress("DEPRECATION")
        display?.getSize(point)
    }

    return point
}


/**
 * <pre>OS 버전 정보를 가져온다.</pre>
 *
 * @return  * RELEASE Information ex)"2.1-update1"
 */
fun getOSRelease(): String?  = try {
    Build.VERSION.RELEASE
} catch (e: Exception) {
    LogUtil.e(e)
    null
}

/**
 * 단말 모델명
 *
 * @return String        모델명
 */
fun getModelName(): String? = try {
    Build.MODEL
} catch (e: Exception) {
    LogUtil.e(e)
    null
}

/**
 * Build 버전
 *
 * @return
 */
fun getBuildVersion(): String? = Build.DISPLAY


/**
 * Check if Device is Rooted.
 *
 * @return
 */
fun isDeviceRooted(): Boolean {
    var isRooted = false

    tryAndCatch(block = {
        Runtime.getRuntime().exec("su")
        isRooted = true
    })

    if (!isRooted) {
        val filePaths = kotlin.arrayOf(
                "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/xbin/su", "/data/local/bin/su", "/data/local/su",
                "/system/sbin/su", "/usr/bin/su", "/vendor/bin/su",
                "/system/app/SuperUser.apk", "/data/data/com.noshufou.android.su"
        )

        // forEach 구문에서 break 처리하기..
        // 참고 : https://soulduse.tistory.com/71
        run loop@ {
            filePaths.forEach {
                if (it.toFile().isFileExist()) {
                    isRooted = true
                    return@loop
                }
            }
        }
    }
    return isRooted
}

/**
 * Return whether ADB is enabled.
 *
 * @receiver Context
 * @return `true`: yes<br></br>`false`: no
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Context.isAdbEnabled(): Boolean
        = Settings.Secure.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) > 0

/**
 * Shutdown the device
 * Requires root permission
 * or hold `android:sharedUserId="android.uid.system"`,
 * `<uses-permission android:name="android.permission.SHUTDOWN/>`
 * in manifest.
 *
 * @receiver Context
 */
fun Context.shutdown() {
    execCmd("reboot -p", true)
    val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
    intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
    startActivity(intent.getIntent())
}

/**
 * Reboot the device.
 * Requires root permission
 * or hold `android:sharedUserId="android.uid.system"` in manifest.
 *
 * @receiver Context
 */
fun Context.reboot() {
    execCmd("reboot", true)
    val intent = Intent(Intent.ACTION_REBOOT)
    intent.putExtra("nowait", 1)
    intent.putExtra("interval", 1)
    intent.putExtra("window", 0)
    sendBroadcast(intent)
}

/**
 * Reboot the device.
 *
 * Requires root permission
 * or hold `android:sharedUserId="android.uid.system"`,
 * `<uses-permission android:name="android.permission.REBOOT" />`
 *
 * @receiver Context
 * @param reason code to pass to the kernel (e.g., "recovery") to
 * request special boot modes, or null.
 */
@RequiresPermission(Manifest.permission.REBOOT)
fun Context.reboot(reason: String?) {
    powerManager.reboot(reason)
}

/**
 * Reboot the device to recovery.
 *
 * Requires root permission.
 */
fun reboot2Recovery() {
    execCmd("reboot recovery", true)
}

/**
 * Reboot the device to bootloader.
 *
 * Requires root permission.
 */
fun reboot2Bootloader() {
    execCmd("reboot bootloader", true)
}
