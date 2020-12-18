package com.ksfams.sgframework.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.ksfams.sgframework.R
import com.ksfams.sgframework.extensions.color
import com.ksfams.sgframework.extensions.drawable
import com.ksfams.sgframework.extensions.setBgDrawable
import com.ksfams.sgframework.modules.system.layoutInflater
import com.ksfams.sgframework.ui.helper.ToastUi

/**
 *
 * 커스텀 Toast 처리
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

object ToastUtil {

    private var config: Config
    private var toast: Toast? = null

    init {
        config = Config()
    }


    /**
     * init
     *
     * @param config Config
     */
    @JvmStatic
    fun init(config: Config) {
        this.config = config
    }

    /**
     * get config data
     *
     * @return Config
     */
    fun getConfig(): Config = this.config


    /**
     * 일반 토스트
     *
     * @param context Context
     * @param message CharSequence
     * @param icon Drawable?
     * @param tintColor Int? default R.color.RGB_FF353A3E
     * @param duration Int
     * @return Toast
     */
    fun normal(context: Context, message: CharSequence,
               icon: Drawable? = null,
               @ColorRes tintColor: Int? = null,
               duration: Int = Toast.LENGTH_SHORT): Toast {

        val colorTint: Int = tintColor ?: R.color.RGB_FF353A3E
        return custom(context, message, icon, colorTint, duration)
    }

    /**
     * Warning 토스트
     *
     * @param context Context
     * @param message CharSequence
     * @param icon Drawable? default R.drawable.ic_error_outline_white_48dp
     * @param tintColor Int? default R.color.RGB_FFFFA900
     * @param duration Int
     * @return Toast
     */
    fun warning(context: Context, message: CharSequence,
                icon: Drawable? = null,
                @ColorRes tintColor: Int? = null,
                duration: Int = Toast.LENGTH_SHORT): Toast {

        val iconDrawable = icon ?: context.drawable(R.drawable.ic_error_outline_white_48dp)
        val colorTint = tintColor ?: R.color.RGB_FFFFA900
        return custom(context, message, iconDrawable, colorTint, duration)
    }

    /**
     * Information 토스트
     *
     * @param context Context
     * @param message CharSequence
     * @param icon Drawable? default R.drawable.ic_info_outline_white_48dp
     * @param tintColor Int? default R.color.RGB_FF3F51B5
     * @param duration Int
     * @return Toast
     */
    fun info(context: Context, message: CharSequence,
             icon: Drawable? = null,
             @ColorRes tintColor: Int? = null,
             duration: Int = Toast.LENGTH_SHORT): Toast {

        val iconDrawable = icon ?: context.drawable(R.drawable.ic_info_outline_white_48dp)
        val colorTint = tintColor ?: R.color.RGB_FF3F51B5
        return custom(context, message, iconDrawable, colorTint, duration)
    }

    /**
     * Success 토스트
     *
     * @param context Context
     * @param message CharSequence
     * @param icon Drawable? default R.drawable.ic_check_white_48dp
     * @param tintColor Int? default R.color.RGB_FF388E3C
     * @param duration Int
     * @return Toast
     */
    fun success(context: Context, message: CharSequence,
                icon: Drawable? = null,
                @ColorRes tintColor: Int? = null,
                duration: Int = Toast.LENGTH_SHORT): Toast {

        val iconDrawable = icon ?: context.drawable(R.drawable.ic_check_white_48dp)
        val colorTint = tintColor ?: R.color.RGB_FF388E3C
        return custom(context, message, iconDrawable, colorTint, duration)
    }

    /**
     * Error 토스트
     *
     * @param context Context
     * @param message CharSequence
     * @param icon Drawable? default R.drawable.ic_check_white_48dp
     * @param tintColor Int? default R.color.RGB_FF388E3C
     * @param duration Int
     * @return Toast
     */
    fun error(context: Context, message: CharSequence,
              icon: Drawable? = null,
              @ColorRes tintColor: Int? = null,
              duration: Int = Toast.LENGTH_SHORT): Toast {

        val iconDrawable = icon ?: context.drawable(R.drawable.ic_clear_white_48dp)
        val colorTint = tintColor ?: R.color.RGB_FFD50000
        return custom(context, message, iconDrawable, colorTint, duration)
    }


    /**
     * 커스텀 토스트
     *
     * @param context Context
     * @param message CharSequence
     * @param icon Drawable?
     * @param tintColor Int?
     * @param duration Int
     * @return Toast
     */
    @SuppressLint("ShowToast", "InflateParams")
    fun custom(context: Context, message: CharSequence,
               icon: Drawable? = null, @ColorRes tintColor: Int? = null,
               duration: Int = Toast.LENGTH_SHORT): Toast {

        if (toast == null) toast = Toast.makeText(context, message, duration)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val toastLayout = context.layoutInflater.inflate(R.layout.toast_layout, null)
            val toastIconView = toastLayout.findViewById<ImageView>(R.id.toast_icon)
            val toastTextView = toastLayout.findViewById<TextView>(R.id.toast_text)

            val drawableFrame = if (tintColor != null) {
                ToastUi.tint9PatchDrawableFrame(context, tintColor)
            } else {
                context.drawable(R.drawable.toast_frame)
            }
            toastLayout.setBgDrawable(drawableFrame)

            if (icon != null) {
                if (config.isTintIcon) {
                    toastIconView.setBgDrawable(ToastUi.tintIcon(icon, config.textColor))
                } else {
                    toastIconView.setBgDrawable(icon)
                }
            } else {
                toastIconView.visibility = View.GONE
            }

            toastTextView.text = message
            toastTextView.typeface = config.typeface
            toastTextView.setTextColor(context.color(config.textColor))
            toastTextView.setTextSize(config.textSizeUnit, config.textSize.toFloat())

            @Suppress("DEPRECATION")
            toast!!.view = toastLayout
        }
        return toast as Toast
    }


    /**
     * Kotlin Builder Pattern
     * 예) val config = ToastUtil.ConfigBuilder().setTextColor(R.color.RGB_FFFFFFFF).build()
     *
     * @property textColor Int
     * @property errorColor Int
     * @property infoColor Int
     * @property successColor Int
     * @property warningColor Int
     * @property typeface Typeface
     * @property textSizeUnit Int TypedValue dp or sp
     * @property textSize Int default 15dp
     * @property isTintIcon Boolean
     * @constructor
     */
    class Config internal constructor(@ColorRes val textColor: Int = R.color.RGB_FFFFFFFF,
                                      @ColorRes val errorColor: Int = R.color.RGB_FFD50000,
                                      @ColorRes val infoColor: Int = R.color.RGB_FF3F51B5,
                                      @ColorRes val successColor: Int = R.color.RGB_FF388E3C,
                                      @ColorRes val warningColor: Int = R.color.RGB_FFFFA900,
                                      val typeface: Typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL),
                                      val textSizeUnit: Int = TypedValue.COMPLEX_UNIT_DIP,
                                      val textSize: Int = 15,
                                      val isTintIcon: Boolean = true)

    /**
     * Config Builder
     *
     * @property textColor Int
     * @property errorColor Int
     * @property infoColor Int
     * @property successColor Int
     * @property warningColor Int
     * @property typeface Typeface
     * @property textSizeUnit Int TypedValue dp or sp
     * @property textSize Int default 15dp
     * @property isTintIcon Boolean
     * @constructor
     */
    data class ConfigBuilder(@ColorRes var textColor: Int = R.color.RGB_FFFFFFFF,
                             @ColorRes var errorColor: Int = R.color.RGB_FFD50000,
                             @ColorRes var infoColor: Int = R.color.RGB_FF3F51B5,
                             @ColorRes var successColor: Int = R.color.RGB_FF388E3C,
                             @ColorRes var warningColor: Int = R.color.RGB_FFFFA900,
                             var typeface: Typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL),
                             var textSizeUnit: Int = TypedValue.COMPLEX_UNIT_DIP,
                             var textSize: Int = 15,
                             var isTintIcon: Boolean = true) {

        fun setTextColor(@ColorRes textColor: Int) = apply { this.textColor = textColor }
        fun setErrorColor(@ColorRes errorColor: Int) = apply { this.errorColor = errorColor }
        fun setInfoColor(@ColorInt infoColor: Int) = apply { this.infoColor = infoColor }
        fun setSuccessColor(@ColorInt successColor: Int) = apply { this.successColor = successColor }
        fun setWarningColor(@ColorInt warningColor: Int) = apply { this.warningColor = warningColor }
        fun setTypeface(typeface: Typeface) = apply { this.typeface = typeface }
        fun setTextSizeUnit(textSizeUnit: Int) = apply { this.textSizeUnit = textSizeUnit }
        fun setTextSize(textSize: Int) = apply { this.textSize = textSize }
        fun setTintIcon(isTintIcon: Boolean) = apply { this.isTintIcon = isTintIcon }

        fun build() = Config(textColor, errorColor, infoColor, successColor, warningColor, typeface, textSizeUnit, textSize, isTintIcon)
    }
}