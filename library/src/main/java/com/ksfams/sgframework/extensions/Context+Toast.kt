package com.ksfams.sgframework.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ksfams.sgframework.utils.ToastUtil

/**
 *
 * Context Extensions
 * show Toast
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
 * 기본 Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toast(message: CharSequence,
                  icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                  duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.normal(this, message, icon, tintColor, duration).show()
}

/**
 * 기본 Toast 표시
 *
 * @receiver Context
 * @param message Int
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toast(@StringRes message: Int,
                  icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                  duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.normal(this, this.string(message), icon, tintColor, duration).show()
}

/**
 * Success Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastSuccess(message: CharSequence,
                         icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                         duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.success(this, message, icon, tintColor, duration).show()
}

/**
 * Success Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastSuccess(@StringRes message: Int,
                         icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                         duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.success(this, this.string(message), icon, tintColor, duration).show()
}


/**
 * Information Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastInfo(message: CharSequence,
                      icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                      duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.info(this, message, icon, tintColor, duration).show()
}

/**
 * Information Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastInfo(@StringRes message: Int,
                      icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                      duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.info(this, this.string(message), icon, tintColor, duration).show()
}


/**
 * Warning Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastWarning(message: CharSequence,
                         icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                         duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.warning(this, message, icon, tintColor, duration).show()
}

/**
 * Warning Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastWarning(@StringRes message: Int,
                         icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                         duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.warning(this, this.string(message), icon, tintColor, duration).show()
}


/**
 * Error Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastError(message: CharSequence,
                       icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                       duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.error(this, message, icon, tintColor, duration).show()
}

/**
 * Error Toast 표시
 *
 * @receiver Context
 * @param message CharSequence
 * @param icon Drawable?
 * @param tintColor Int?
 * @param duration Int
 */
fun Context.toastError(@StringRes message: Int,
                       icon: Drawable? = null, @ColorRes tintColor: Int? = null,
                       duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.error(this, this.string(message), icon, tintColor, duration).show()
}