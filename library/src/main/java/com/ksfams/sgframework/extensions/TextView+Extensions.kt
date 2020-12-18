package com.ksfams.sgframework.extensions

import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 *
 * TextView Extensions
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
 * 좌측 이미지 적용
 *
 * @receiver TextView
 * @param id Drawable Resource id
 */
fun TextView.setDrawableLeft(@DrawableRes id: Int) {
    val drawable = ContextCompat.getDrawable(this.context, id)
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(drawable, null, null, null)
}

/**
 * 우측 이미지 적용
 *
 * @receiver TextView
 * @param id Drawable Resource id
 */
fun TextView.setDrawableRight(@DrawableRes id: Int) {
    val drawable = ContextCompat.getDrawable(this.context, id)
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, null, drawable, null)
}

/**
 * 상단 이미지 적용
 *
 * @receiver TextView
 * @param id Drawable Resource id
 */
fun TextView.setDrawableTop(@DrawableRes id: Int) {
    val drawable = ContextCompat.getDrawable(this.context, id)
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, drawable, null, null)
}

/**
 * 하단 이미지 적용
 *
 * @receiver TextView
 * @param id Drawable Resource id
 */
fun TextView.setDrawableBottom(@DrawableRes id: Int) {
    val drawable = ContextCompat.getDrawable(this.context, id)
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, null, null, drawable)
}