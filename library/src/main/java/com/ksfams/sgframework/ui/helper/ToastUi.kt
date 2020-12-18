package com.ksfams.sgframework.ui.helper

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import androidx.annotation.ColorRes
import com.ksfams.sgframework.R
import com.ksfams.sgframework.extensions.color
import com.ksfams.sgframework.extensions.drawable
import com.ksfams.sgframework.modules.reference.ApplicationReference

/**
 *
 * Toast UI 관련 처리
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

internal class ToastUi private constructor() {

    /**
     * static method 처리
     */
    companion object {

        fun tintIcon(drawable: Drawable, @ColorRes tintColor: Int): Drawable {
            val colorInt = ApplicationReference.getApp().color(tintColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable.colorFilter = BlendModeColorFilter(colorInt, BlendMode.SRC_IN)
            } else {
                @Suppress("DEPRECATION")
                drawable.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            }
            return drawable
        }

        fun tint9PatchDrawableFrame(context: Context, @ColorRes tintColor: Int): Drawable {
            val toastDrawable = context.drawable(R.drawable.toast_frame) as NinePatchDrawable
            return tintIcon(toastDrawable, tintColor)
        }
    }
}