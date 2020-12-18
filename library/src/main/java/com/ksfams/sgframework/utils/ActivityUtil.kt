package com.ksfams.sgframework.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.ksfams.sgframework.extensions.hideKeyboard
import com.ksfams.sgframework.extensions.showKeyboard
import com.ksfams.sgframework.extensions.string
import com.ksfams.sgframework.modules.reference.ApplicationReference

/**
 *
 * Activity 관련 유틸
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
 * Check if given context is finishing.
 * This is a wrapper to check if it's both an activity and finishing
 * As of now, it is only checked when tied to an activity
 */
inline val Context.isFinishing: Boolean
    get() = (this as? Activity)?.isFinishing ?: false


/**
 * keyboard open
 *
 * @receiver Activity
 */
fun Activity.showKeyboard() {
    var view: View? = currentFocus
    if (view == null) {
        view = View(this)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }
    view.showKeyboard()
}

/**
 * keyboard hide
 *
 * @receiver Activity
 */
fun Activity.hideKeyboard() {
    var view: View? = currentFocus
    if (view == null) {
        view = View(this)
    }
    view.hideKeyboard()
}

/**
 * Return whether the activity exists.
 *
 * @receiver Context
 * @param packageName The name of the package.
 * @param className The name of the class.
 * @return {@code true}: yes<br>{@code false}: no
 */
fun Context.isActivityExists(packageName: String,
                             className: String): Boolean {
    val intent = Intent()
    intent.setClassName(packageName, className)
    return !(packageManager.resolveActivity(intent, 0) == null
            || intent.resolveActivity(packageManager) == null
            || packageManager.queryIntentActivities(intent, 0).size == 0)
}

/**
 * Start home activity.
 *
 * @receiver Context
 */
fun Context.startHomeActivity() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivity(intent)
}

/**
 * Return the name of launcher activity.
 *
 * @receiver Context
 * @return String the name of launcher activity
 */
fun Context.getLauncherActivity(): String {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    packageManager.queryIntentActivities(intent, 0).forEach {
        if (it.activityInfo.packageName == packageName) {
            return it.activityInfo.name
        }
    }
    return "no $packageName"
}

/**
 * Return whether the activity exists in activity's stack.
 *
 * @receiver Activity
 * @return Boolean {@code true}: yes<br>{@code false}: no
 */
fun Activity.isActivityExistsInStack(): Boolean {
    val activities = ApplicationReference.getActivityList()
    activities.forEach {
        if (it == this) {
            return true
        }
    }
    return false
}

/**
 * add an Callback to ViewTreeObserver
 * which let the developer know when contentView is inflated to the Activity Content
 *
 * @receiver Activity
 * @param onInflated
 */
fun Activity.onViewInflated(onInflated: () -> Unit) {
    window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            onInflated()
        }
    })
}

/**
 * get #rootVIew of the Activity
 *
 * @receiver Activity
 * @return
 */
fun Activity.getRootView(): View {
    var rootView = findViewById<View>(android.R.id.content)
    if (rootView == null) {
        rootView = window.decorView.findViewById(android.R.id.content)
    }
    return rootView
}

/**
 * Return whether the activity exists in activity's stack.
 *
 * @param cls The activity class.
 * @return {@code true}: yes<br>{@code false}: no
 */
fun isActivityExistsInStack(cls: Class<out Activity>): Boolean {
    val activities = ApplicationReference.getActivityList()
    activities.forEach {
        if (it.javaClass == cls) {
            return true
        }
    }
    return false
}

/**
 * Return the icon of activity.
 *
 * @receiver Activity
 * @return Drawable the icon of activity
 */
fun Activity.getActivityIcon(): Drawable
        = packageManager.getActivityIcon(this.componentName)

/**
 * Return the logo of activity.
 *
 * @receiver Activity
 * @return the logo of activity
 */
fun Activity.getActivityLogo(): Drawable?
        = packageManager.getActivityLogo(this.componentName)

/**
 * Launch the application's details settings.
 *
 * @receiver Context
 * @param packageName The name of the package.
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @return
 */
fun Context.launchAppDetailsSettings(packageName: String = ApplicationReference.getApp().packageName,
                                     isNewTask: Boolean = true): Boolean = guardRun {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = "package:${packageName}".toUri()
    startActivity(intent.getIntent(isNewTask))
}

/**
 * Launch the application's details settings.
 *
 * @receiver Context
 * @param packageName The name of the package.
 * @param requestCode
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @return
 */
fun Activity.launchAppDetailsSettings(packageName: String = ApplicationReference.getApp().packageName,
                                      requestCode: Int,
                                      isNewTask: Boolean = true): Boolean = guardRun {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = "package:${packageName}".toUri()
    intent.getIntent(isNewTask)
    startActivityForResult(intent, requestCode)
}

/**
 * wifi 설정 화면 open
 *
 * @receiver Context
 * @param isNewTask Boolean
 * @return Boolean
 */
fun Context.launchWirelessSettings(isNewTask: Boolean): Boolean = guardRun {
    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent.getIntent(isNewTask))
}

/**
 * Browse given url with internal browser
 *
 * @receiver Context
 * @param [url] url to browse
 * @param isNewTask   True to add flag of new task, false otherwise.
 */
@JvmOverloads
fun Context.browse(url: String, isNewTask: Boolean = false): Boolean = guardRun {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = url.toUri()
    startActivity(intent.getIntent(isNewTask))
}

/**
 * [packageId] play store 호출
 *
 * @receiver Context
 * @param packageId
 * @param isNewTask   True to add flag of new task, false otherwise.
 */
fun Context.launchPlayStoreLink(packageId: String, isNewTask: Boolean = false): Boolean
        = browse("https://play.google.com/store/apps/details?id=$packageId", isNewTask)

/**
 * [packageId] play store 호출
 *
 * @receiver Context
 * @param packageId
 * @param isNewTask   True to add flag of new task, false otherwise.
 */
fun Context.launchPlayStoreLink(@StringRes packageId: Int, isNewTask: Boolean = false): Boolean
        = launchPlayStoreLink(string(packageId), isNewTask)

/**
 * Make phone call with given phone number
 * this feature need CALL_PHONE permission.
 *
 * @receiver Context
 * @param[number] Phone number to call
 * @param isNewTask   True to add flag of new task, false otherwise.
 */
@RequiresPermission(Manifest.permission.CALL_PHONE)
fun Context.makeCall(number: String, isNewTask: Boolean = false): Boolean = guardRun {
    val intent = Intent(Intent.ACTION_CALL, "tel:$number".toUri())
    startActivity(intent.getIntent(isNewTask))
}

/**
 * Make phone dial with given phone number
 *
 * @receiver Context
 * @param[number] Phone number to dial
 * @param isNewTask   True to add flag of new task, false otherwise.
 */
fun Context.dialCall(number: String, isNewTask: Boolean = false): Boolean = guardRun {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
    startActivity(intent.getIntent(isNewTask))
}

/**
 * Send Sms Easily, Opens Default SMS App.
 *
 * @receiver Context
 * @param to contact number
 * @param body sms body
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @return
 */
fun Context.sendSMS(to: String = "", body: String, isNewTask: Boolean = false): Boolean = guardRun {
    val intent = Intent(Intent.ACTION_SENDTO, "smsto:$to".toUri()).putExtra("sms_body", body)
    startActivity(intent.getIntent(isNewTask))
}

/**
 * Open Default Email Client
 *
 * @receiver Context
 * @param mailTo
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @return
 */
fun Context.sendEmail(mailTo: String, isNewTask: Boolean = false): Boolean = guardRun {
    val intent = Intent(Intent.ACTION_SENDTO, "mailto:$mailTo".toUri())
    startActivity(intent.getIntent(isNewTask))
}


/**
 * shutdown.
 * <p>Requires root permission
 * or hold {@code android:sharedUserId="android.uid.system"},
 * {@code <uses-permission android:name="android.permission.SHUTDOWN/>}
 * in manifest.</p>
 *
 * @receiver Context
 * @param isNewTask True to add flag of new task, false otherwise.
 * @return
 */
fun Context.shutdown(isNewTask: Boolean): Boolean = guardRun {
    val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
    intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
    startActivity(intent.getIntent(isNewTask))
}

/**
 * share text
 *
 * @receiver Context
 * @param title : createChooser Title
 * @param text
 * @param isNewTask   True to add flag of new task, false otherwise.
 * @return
 */
fun Context.sharedText(title: String, text: String, isNewTask: Boolean): Boolean {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)
    val chooserIntent = Intent.createChooser(intent, title)
    return if (chooserIntent.resolveActivity(packageManager) != null) {
        startActivity(chooserIntent.getIntent(isNewTask))
        true
    } else {
        false
    }
}

/**
 * Activity 배경 투영 처리
 *
 * @receiver Context
 */
fun Activity.transparent() {
    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}
