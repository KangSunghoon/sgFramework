package com.ksfams.sgframework.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RemoteViews
import androidx.annotation.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ksfams.sgframework.constants.INVALID_ID
import com.ksfams.sgframework.modules.system.inputMethodManager
import com.ksfams.sgframework.modules.system.notificationManager
import com.ksfams.sgframework.utils.emptyToDefault
import kotlin.random.Random

/**
 *
 * Context Extensions
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

// Resource retrievers --------------------------------------------------------------------------

/**
 * String Resource
 *
 * @receiver Context
 * @param id Int
 * @return String
 */
fun Context.string(@StringRes id: Int): String = getString(id)

/**
 * String Resource
 *
 * @receiver Context
 * @param id Int
 * @param default String?
 * @return String?
 */
fun Context.string(@StringRes id: Int, default: String?): String?
        = if (id != INVALID_ID) string(id) else default

/**
 * String Resource
 *
 * @receiver Context
 * @param id Int
 * @param default Function String?
 * @return String?
 */
inline fun Context.string(@StringRes id: Int, default: () -> String?): String?
        = if (id != INVALID_ID) string(id) else default()

/**
 * String Array Resource
 *
 * @receiver Context
 * @param id Int
 * @return Array<String>
 */
fun Context.stringArray(@ArrayRes id: Int): Array<String>
        = resources.getStringArray(id)

/**
 * Color Resource
 *
 * @receiver Context
 * @param id Int ColorInt
 * @return Int
 */
fun Context.color(@ColorRes id: Int): Int
        = ContextCompat.getColor(this, id)

/**
 * Boolean Resource
 *
 * @receiver Context
 * @param id Int
 * @return Boolean
 */
fun Context.boolean(@BoolRes id: Int): Boolean = resources.getBoolean(id)

/**
 * Integer Resource
 *
 * @receiver Context
 * @param id Int
 * @return Int
 */
fun Context.integer(@IntegerRes id: Int): Int = resources.getInteger(id)

/**
 * dimension Resource
 *
 * @receiver Context
 * @param id Int
 * @return Float
 */
fun Context.dimen(@DimenRes id: Int): Float = resources.getDimension(id)

/**
 * dimension pixel size Resource
 *
 * @receiver Context
 * @param id Int
 * @return Int
 */
fun Context.dimenPixcelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

/**
 * Drawable Resource
 *
 * @receiver Context
 * @param id Int Drawable Resource
 * @return Drawable?
 */
fun Context.drawable(@DrawableRes id: Int): Drawable? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getDrawable(id)
    } else {
        @Suppress("DEPRECATION")
        resources.getDrawable(id)
    }
}

// Resource retrievers --------------------------------------------------------------------------

/**
 * toggle keyboard open / close
 *
 * @receiver Context
 */
fun Context.toggleKeyboard() {
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * inflate view
 *
 * @receiver Context
 * @param[layoutRes]    layout resource to inflate
 * @param[parent]       Optional view to be the parent of the generated hierarchy (if attachToRoot is true),
 *                      or else simply an object that provides a set of LayoutParams values for root of the returned hierarchy
 *                      (if attachToRoot is false.)
 *                      This value may be null.
 * @param[attachToRoot] Whether the inflated hierarchy should be attached to the root parameter?
 *                      If false, root is only used to create the correct subclass of LayoutParams for
 *                      the root view in the XML.
 * @return The root View of the inflated hierarchy.
 */
@JvmOverloads
fun Context.inflate(layoutRes: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View
        = LayoutInflater.from(this).inflate(layoutRes, parent, attachToRoot)

/**
 * create Notification Channel
 *
 * @receiver Context
 * @param id channel id, if this value is not present, it will be package name
 * @param name channel name, if this value is not present, it will be app name
 * @param description channel description, if this value is not present, it will be app name
 * @param importance importance of channel, if this value is not present, it will be IMPORTANCE_LOW
 * @return generated channel id
 */
@RequiresApi(Build.VERSION_CODES.O)
@JvmOverloads
fun Context.createNotificationChannel(id: String = "",
                                      name: String = "", description: String = "",
                                      importance: Int = NotificationManager.IMPORTANCE_HIGH): String {
    val newId = id.emptyToDefault(packageName)
    val appName = if (applicationInfo.labelRes != 0) getString(applicationInfo.labelRes) else applicationInfo.nonLocalizedLabel.toString()
    val newName = name.emptyToDefault(appName)
    val newDescription = description.emptyToDefault(appName)

    val notificationManager = notificationManager
    val channel = NotificationChannel(newId, newName, importance)
    channel.description = newDescription
    notificationManager.createNotificationChannel(channel)

    return newId
}


/**
 * Show Notification Easily, With Just a Single Method
 *
 * @receiver Context
 * @param contentTitle The title of the Notification
 * @param id the Id Of the Notification
 * @param contentText the ContentText Of the Notification
 * @param iconId The Small Icon of the Notification
 * @param channelId the ID of the Channel
 * @param channelName the Name of the Channel
 * @param contentInfo the ContentInfo For the Notification
 * @param pendingIntent the pending Intent for the Notification
 * @param contentView the ContentView for the Notification
 * @param bigContentView the BigContentView for the Notification
 * @param autoCancel set to True if want to autoCancel the Notification
 * @param ledColorInt sets the led argb Color
 * @param isColorized true if you want this Notification as a Colorized Notification
 * @param subText the SubText For the Notification
 * @param priority the priority sets param for Notification
 * @param style the Notification Style
 */
@JvmOverloads
fun Context.showNotification(contentTitle: String, id: Int = Random.nextInt(), contentText: String? = null,
                             @DrawableRes iconId: Int = android.R.drawable.stat_notify_more,
                             channelId: String = "default", channelName: String = "Default Notification",
                             contentInfo: String? = null,
                             pendingIntent: PendingIntent? = null,
                             contentView: RemoteViews? = null,
                             bigContentView: RemoteViews? = null,
                             autoCancel: Boolean = false,
                             @ColorInt ledColorInt: Int = Color.WHITE,
                             isColorized: Boolean = false,
                             subText: String? = null,
                             number: Int,
                             priority: Int = NotificationCompat.PRIORITY_DEFAULT,
                             style: NotificationCompat.Style? = null) {

    val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(contentTitle)
            .setSmallIcon(iconId)
            .setContentText(contentText)
            .setContentInfo(contentInfo)
            .setContentIntent(pendingIntent).setContent(contentView).setCustomBigContentView(bigContentView)
            .setAutoCancel(autoCancel)
            .setColor(ledColorInt)
            .setChannelId(channelId)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setColorized(isColorized)
            .setSubText(subText)
            .setNumber(number)
            .setPriority(priority)
            .setStyle(style)
            .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.createNotificationChannel(id = channelId, name = channelName)
    }

    notificationManager.cancel(id)
    notificationManager.notify(id, notification)
}
