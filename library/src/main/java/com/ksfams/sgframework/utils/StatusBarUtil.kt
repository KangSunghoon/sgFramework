package com.ksfams.sgframework.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.drawerlayout.widget.DrawerLayout
import com.ksfams.sgframework.modules.reference.ApplicationReference
import com.ksfams.sgframework.modules.system.windowManager

/**
 *
 * Status Bar 유틸
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

///////////////////////////////////////////////////////////////////////////
// status bar
///////////////////////////////////////////////////////////////////////////
private const val DEFAULT_ALPHA: Int = 112
private const val TAG_COLOR = "TAG_COLOR"
private const val TAG_ALPHA = "TAG_ALPHA"
private const val TAG_OFFSET = "TAG_OFFSET"
private const val KEY_OFFSET = -123

//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
/**
 * Set the status bar's visibility.
 *
 * @receiver Activity
 * @param isVisible True to set status bar visible, false otherwise.
 */
fun Activity.setStatusbarVisibility(isVisible: Boolean) {
    window.setStatusbarVisibility(isVisible)
}

//TODO Android 11 버전에 대한 Full Screen 관련 확인 필요
/**
 * Set the status bar's visibility.
 *
 * @receiver Window
 * @param isVisible True to set status bar visible, false otherwise.
 */
fun Window.setStatusbarVisibility(isVisible: Boolean) {
    if (isVisible) {
        @Suppress("DEPRECATION")
        clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        showColorView()
        showAlphaView()
        addMarginTopEqualHeight()
    } else {
        @Suppress("DEPRECATION")
        addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideColorView()
        hideAlphaView()
        subtractMarginTopEqualHeight()
    }
}

/**
 * Return whether the status bar is visible.
 *
 * @receiver Activity The activity.
 * @return `true`: yes<br></br>`false`: no
 */
fun Activity.isStatusbarVisible(): Boolean {
    val flags = window.attributes.flags
    @Suppress("DEPRECATION")
    return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
}

//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
/**
 * Set the status bar's light mode.
 *
 * @receiver Activity    The activity.
 * @param isLightMode True to set status bar light mode, false otherwise.
 */
fun Activity.setLightMode(isLightMode: Boolean) {
    window.setLightMode(isLightMode)
}

/**
 * Set the status bar's light mode.
 *
 * @receiver Window      The window.
 * @param isLightMode True to set status bar light mode, false otherwise.
 */
fun Window.setLightMode(isLightMode: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        var vis = decorView.systemUiVisibility
        vis = if (isLightMode) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            @Suppress("DEPRECATION")
            vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            @Suppress("DEPRECATION")
            vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = vis
    }
}

/**
 * Add the top margin size equals status bar's height for view.
 *
 * @receiver View The view.
 */
@SuppressLint("ObsoleteSdkInt")
fun View.addMarginTopEqualHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    tag = TAG_OFFSET
    val haveSetOffset = getTag(KEY_OFFSET)
    if (haveSetOffset != null && haveSetOffset as Boolean) return
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin + ApplicationReference.getApp().statusBarHeight,
            layoutParams.rightMargin,
            layoutParams.bottomMargin
    )
    setTag(KEY_OFFSET, true)
}

/**
 * Subtract the top margin size equals status bar's height for view.
 *
 * @receiver View The view.
 */
@SuppressLint("ObsoleteSdkInt")
fun View.subtractMarginTopEqualHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    val haveSetOffset = getTag(KEY_OFFSET)
    if (haveSetOffset == null || !(haveSetOffset as Boolean)) return
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
            layoutParams.leftMargin,
            layoutParams.topMargin - ApplicationReference.getApp().statusBarHeight,
            layoutParams.rightMargin,
            layoutParams.bottomMargin
    )
    setTag(KEY_OFFSET, false)
}

/**
 * add marginTop equal height
 *
 * @receiver Window
 */
@SuppressLint("ObsoleteSdkInt")
private fun Window.addMarginTopEqualHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    val withTag = decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
    withTag.addMarginTopEqualHeight()
}

/**
 * subtract marginTop equal height
 *
 * @receiver Window
 */
@SuppressLint("ObsoleteSdkInt")
private fun Window.subtractMarginTopEqualHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    val withTag = decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
    withTag.subtractMarginTopEqualHeight()
}

//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
/**
 * Set the status bar's color.
 *
 * @receiver Activity The activity.
 * @param color    The status bar's color.
 * @param alpha    The status bar's alpha which isn't the same as alpha in the color.
 * @param isDecor  True to add fake status bar in DecorView,
 * false to add fake status bar in ContentView.
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.setColor(@ColorInt color: Int,
                      @IntRange(from = 0, to = 255) alpha: Int = DEFAULT_ALPHA,
                      isDecor: Boolean = false) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    hideAlphaView()
    transparent()
    addColor(color, alpha, isDecor)
}

/**
 * Set the status bar's color.
 *
 * @receiver Context
 * @param fakeStatusBar The fake status bar view.
 * @param color         The status bar's color.
 * @param alpha         The status bar's alpha which isn't the same as alpha in the color.
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.setColor(fakeStatusBar: View,
                     @ColorInt color: Int,
                     @IntRange(from = 0, to = 255) alpha: Int = DEFAULT_ALPHA) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    fakeStatusBar.visibility = View.VISIBLE
    (fakeStatusBar.context as Activity).transparent()
    val layoutParams = fakeStatusBar.layoutParams
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams.height = statusBarHeight
    fakeStatusBar.setBackgroundColor(getColor(color, alpha))
}

/**
 * Set the status bar's alpha.
 *
 * @receiver Activity The activity.
 * @param alpha    The status bar's alpha.
 * @param isDecor  True to add fake status bar in DecorView,
 * false to add fake status bar in ContentView.
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.setAlpha(@IntRange(from = 0, to = 255) alpha: Int = DEFAULT_ALPHA,
                      isDecor: Boolean = false) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    hideColorView()
    transparent()
    addAlpha(alpha, isDecor)
}

/**
 * Set the status bar's alpha.
 *
 * @receiver Context
 * @param fakeStatusBar The fake status bar view.
 * @param alpha         The status bar's alpha.
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.setAlpha(fakeStatusBar: View,
                     @IntRange(from = 0, to = 255) alpha: Int = DEFAULT_ALPHA) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    fakeStatusBar.visibility = View.VISIBLE
    (fakeStatusBar.context as Activity).transparent()
    val layoutParams = fakeStatusBar.layoutParams
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams.height = statusBarHeight
    fakeStatusBar.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
}

/**
 * Set the custom status bar.
 *
 * @receiver Context
 * @param fakeStatusBar The fake status bar view.
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.setCustom(fakeStatusBar: View) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    fakeStatusBar.visibility = View.VISIBLE
    (fakeStatusBar.context as Activity).transparent()
    val layoutParams = fakeStatusBar.layoutParams
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams.height = statusBarHeight
}

/**
 * Set the status bar's color for DrawerLayout.
 *
 * DrawLayout must add `android:fitsSystemWindows="true"`
 *
 * @receiver Activity      The activity.
 * @param drawer        The DrawLayout.
 * @param fakeStatusBar The fake status bar view.
 * @param color         The status bar's color.
 * @param alpha         The status bar's alpha which isn't the same as alpha in the color.
 * @param isTop         True to set DrawerLayout at the top layer, false otherwise.
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.setColor4Drawer(drawer: DrawerLayout,
                             fakeStatusBar: View,
                             @ColorInt color: Int,
                             @IntRange(from = 0, to = 255) alpha: Int = DEFAULT_ALPHA,
                             isTop: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    drawer.fitsSystemWindows = false
    transparent()
    setColor(fakeStatusBar, color, if (isTop) alpha else 0)
    var i = 0
    val len: Int = drawer.childCount
    while (i < len) {
        drawer.getChildAt(i).fitsSystemWindows = false
        i++
    }
    if (isTop) {
        hideAlphaView()
    } else {
        addAlpha(alpha, false)
    }
}

/**
 * Set the status bar's alpha for DrawerLayout.
 *
 * DrawLayout must add `android:fitsSystemWindows="true"`
 *
 * @receiver Activity.
 * @param drawer        drawerLayout
 * @param fakeStatusBar The fake status bar view.
 * @param alpha         The status bar's alpha.
 * @param isTop         True to set DrawerLayout at the top layer, false otherwise.
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.setAlpha4Drawer(drawer: DrawerLayout,
                             fakeStatusBar: View,
                             @IntRange(from = 0, to = 255) alpha: Int = DEFAULT_ALPHA,
                             isTop: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    drawer.fitsSystemWindows = false
    transparent()
    setAlpha(fakeStatusBar, if (isTop) alpha else 0)
    var i = 0
    val len: Int = drawer.getChildCount()
    while (i < len) {
        drawer.getChildAt(i).fitsSystemWindows = false
        i++
    }
    if (isTop) {
        hideAlphaView()
    } else {
        addAlpha(alpha, false)
    }
}

//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
/**
 * change theme of StatusBar
 * it will available android 23 and above.
 *
 * @receiver Activity
 * @param isDark
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Activity.setStatusBarTheme(isDark: Boolean) {
    @Suppress("DEPRECATION")
    val lFlags = window.decorView.systemUiVisibility
    @Suppress("DEPRECATION")
    window.decorView.systemUiVisibility = if (isDark) {
        lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    } else {
        lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}


/**
 * add color
 *
 * @receiver Activity
 * @param color Int
 * @param alpha Int
 * @param isDecor Boolean
 */
private fun Activity.addColor(color: Int,
                              alpha: Int,
                              isDecor: Boolean) {
    val parent =
            if (isDecor) window.decorView as ViewGroup else (findViewById<View>(android.R.id.content) as ViewGroup)
    val fakeStatusBarView = parent.findViewWithTag<View>(TAG_COLOR)
    if (fakeStatusBarView != null) {
        if (fakeStatusBarView.visibility == View.GONE) {
            fakeStatusBarView.visibility = View.VISIBLE
        }
        fakeStatusBarView.setBackgroundColor(getColor(color, alpha))
    } else {
        parent.addView(createColorView(color, alpha))
    }
}

/**
 * add alpha
 *
 * @receiver Activity
 * @param alpha Int
 * @param isDecor Boolean
 */
private fun Activity.addAlpha(alpha: Int, isDecor: Boolean) {
    val parent =
            if (isDecor) window.decorView as ViewGroup else (findViewById<View>(android.R.id.content) as ViewGroup)
    val fakeStatusBarView = parent.findViewWithTag<View>(TAG_ALPHA)
    if (fakeStatusBarView != null) {
        if (fakeStatusBarView.visibility == View.GONE) {
            fakeStatusBarView.visibility = View.VISIBLE
        }
        fakeStatusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
    } else {
        parent.addView(createAlphaView(alpha))
    }
}

/**
 * hide color view
 *
 * @receiver Activity
 */
private fun Activity.hideColorView() {
    window.hideColorView()
}

/**
 * hide alpha view
 *
 * @receiver Activity
 */
private fun Activity.hideAlphaView() {
    window.hideAlphaView()
}

/**
 * hide color view
 *
 * @receiver Window
 */
private fun Window.hideColorView() {
    val decorView = decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_COLOR) ?: return
    fakeStatusBarView.visibility = View.GONE
}

/**
 * hide alpha view
 *
 * @receiver Window
 */
private fun Window.hideAlphaView() {
    val decorView = decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_ALPHA) ?: return
    fakeStatusBarView.visibility = View.GONE
}

/**
 * show color view
 *
 * @receiver Window
 */
private fun Window.showColorView() {
    val decorView = decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_COLOR) ?: return
    fakeStatusBarView.visibility = View.VISIBLE
}

/**
 * show alpha view
 *
 * @receiver Window
 */
private fun Window.showAlphaView() {
    val decorView = decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_ALPHA) ?: return
    fakeStatusBarView.visibility = View.VISIBLE
}

/**
 * get color
 *
 * @param color Int
 * @param alpha Int
 * @return Int
 */
private fun getColor(color: Int, alpha: Int): Int {
    if (alpha == 0) return color
    val a = 1 - alpha / 255f
    var red = color shr 16 and 0xff
    var green = color shr 8 and 0xff
    var blue = color and 0xff
    red = (red * a + 0.5).toInt()
    green = (green * a + 0.5).toInt()
    blue = (blue * a + 0.5).toInt()
    return Color.argb(255, red, green, blue)
}

/**
 * create color view
 *
 * @receiver Context
 * @param color Int
 * @param alpha Int
 * @return View
 */
private fun Context.createColorView(color: Int,
                                    alpha: Int): View {
    val statusBarView = View(this)
    statusBarView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            statusBarHeight
    )
    statusBarView.setBackgroundColor(getColor(color, alpha))
    statusBarView.tag = TAG_COLOR
    return statusBarView
}

/**
 * create alpha view
 *
 * @receiver Context
 * @param alpha Int
 * @return View
 */
private fun Context.createAlphaView(alpha: Int): View {
    val statusBarView = View(this)
    statusBarView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            statusBarHeight
    )
    statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
    statusBarView.tag = TAG_ALPHA
    return statusBarView
}

//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
///**
// * transparent
// *
// * @receiver Activity
// */
//@SuppressLint("ObsoleteSdkInt")
//private fun Activity.transparent() {
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        val option =
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        window.decorView.systemUiVisibility = option
//        window.statusBarColor = Color.TRANSPARENT
//    } else {
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//    }
//}


///////////////////////////////////////////////////////////////////////////
// action bar
///////////////////////////////////////////////////////////////////////////
/**
 * Return the action bar's height.
 *
 * @receiver Context
 * @return the action bar's height
 */
fun Context.getActionBarHeight(): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(android.R.attr.actionBarSize, tv,true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    } else 0
}


///////////////////////////////////////////////////////////////////////////
// notification bar
///////////////////////////////////////////////////////////////////////////
/**
 * Set the notification bar's visibility.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />`
 *
 * @param isVisible True to set notification bar visible, false otherwise.
 */
@SuppressLint("ObsoleteSdkInt")
@RequiresPermission(Manifest.permission.EXPAND_STATUS_BAR)
fun setNotificationBarVisibility(isVisible: Boolean) {
    val methodName = if (isVisible) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) "expand" else "expandNotificationsPanel"
    } else {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) "collapse" else "collapsePanels"
    }
    ApplicationReference.getApp().invokePanels(methodName)
}

/**
 * invoke panels
 *
 * @receiver Context
 * @param methodName String
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
private fun Context.invokePanels(methodName: String) {
    try {
        @SuppressLint("WrongConstant")
        val service: Any = getSystemService("statusbar")
        @SuppressLint("PrivateApi")
        val statusBarManager = Class.forName("android.app.StatusBarManager")
        val expand = statusBarManager.getMethod(methodName)
        expand.invoke(service)
    } catch (e: Exception) {
        LogUtil.e(e)
    }
}


///////////////////////////////////////////////////////////////////////////
// navigation bar
///////////////////////////////////////////////////////////////////////////
//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
/**
 * Set the navigation bar's visibility.
 *
 * @receiver Activity
 * @param isVisible True to set navigation bar visible, false otherwise.
 */
fun Activity.setNavBarVisibility(isVisible: Boolean) {
    window.setNavBarVisibility(isVisible)
}

/**
 * Set the navigation bar's visibility.
 *
 * @receiver Window
 * @param isVisible True to set navigation bar visible, false otherwise.
 */
fun Window.setNavBarVisibility(isVisible: Boolean) {
    @Suppress("DEPRECATION")
    val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    val decorView = decorView
    if (isVisible) {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = decorView.systemUiVisibility and uiOptions.inv()
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = decorView.systemUiVisibility or uiOptions
    }
}

//TODO Android 11 버전에 대한 Flag 처리 확인 필요.
/**
 * Return whether the navigation bar visible.
 *
 * @receiver Activity
 * @return `true`: yes<br></br>`false`: no
 */
fun Activity.isNavBarVisible(): Boolean {
    return window.isNavBarVisible()
}

/**
 * Return whether the navigation bar visible.
 *
 * @receiver Window
 * @return `true`: yes<br></br>`false`: no
 */
fun Window.isNavBarVisible(): Boolean {
    val decorView = decorView
    @Suppress("DEPRECATION")
    val visibility = decorView.systemUiVisibility
    @Suppress("DEPRECATION")
    return visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
}

/**
 * Set the navigation bar's color.
 *
 * @receiver Activity The activity.
 * @param color    The navigation bar's color.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setNavBarColor(@ColorInt color: Int) {
    window.setNavBarColor(color)
}

/**
 * Set the navigation bar's color.
 *
 * @receiver Window The window.
 * @param color  The navigation bar's color.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.setNavBarColor(@ColorInt color: Int) {
    navigationBarColor = color
}

/**
 * Return the color of navigation bar.
 *
 * @receiver Activity The activity.
 * @return the color of navigation bar
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.getNavBarColor(): Int {
    return window.getNavBarColor()
}

/**
 * Return the color of navigation bar.
 *
 * @param window The window.
 * @return the color of navigation bar
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.getNavBarColor(): Int {
    return navigationBarColor
}

/**
 * Set Color of navigation bar divider
 *
 * @receiver Activity The activity.
 * @param color desire color of navigation bar divider
 */
@RequiresApi(Build.VERSION_CODES.P)
fun Activity.setNavBarDivderColor(@ColorInt color: Int) {
    window.setNavBarDivderColor(color)
}

/**
 * Set Color of navigation bar divider
 *
 * @receiver Window The window.
 * @param color desire color of navigation bar divider
 */
@RequiresApi(Build.VERSION_CODES.P)
fun Window.setNavBarDivderColor(@ColorInt color: Int) {
    navigationBarDividerColor = color
}

/**
 * Return whether the navigation bar visible.
 *
 * @receiver Context
 * @return `true`: yes<br></br>`false`: no
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.isSupportNavBar(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        val size = getSize()
        val realSize = Point()
        display?.getRealSize(realSize)
        return realSize.y != size.y || realSize.x != size.x
    }
    val menu = ViewConfiguration.get(this).hasPermanentMenuKey()
    val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    return !menu && !back
}