package com.ksfams.sgframework.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.ksfams.sgframework.modules.reference.ApplicationReference

/**
 *
 * View Extensions
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
 * get parent ViewGroup
 */
inline val View.parentViewGroup: ViewGroup
    get() = parent as ViewGroup


/**
 * View to Bitmap
 *
 * @receiver View
 * @return Bitmap Object
 */
fun View.viewToBitmap(): Bitmap {
    val ret = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(ret)
    val bgDrawable = this.background
    bgDrawable?.draw(canvas) ?: canvas.drawColor(Color.WHITE)
    this.draw(canvas)
    return ret
}


/**
 * View rotate
 *
 * @receiver View
 * @param angle
 * @param resetToZero
 * @param duration
 */
fun View.rotate(angle: Float = 360f, resetToZero: Boolean = true, duration: Long = 400) {
    if (resetToZero) rotation = 0f
    animate().rotation(angle).setDuration(duration).start()
}


/**
 * Start The FadeIn Animation on This View
 *
 * @receiver View
 * @param duration
 */
fun View.fadeIn(duration: Int = 400) {
    clearAnimation()
    val alphaAnimation = AlphaAnimation(this.alpha, 1.0f)
    alphaAnimation.duration = duration.toLong()
    startAnimation(alphaAnimation)
}

/**
 * Start The FadeOut Animation on This View
 *
 * @receiver View
 * @param duration
 */
fun View.fadeOut(duration: Int = 400) {
    clearAnimation()
    val alphaAnimation = AlphaAnimation(this.alpha, 0.0f)
    alphaAnimation.duration = duration.toLong()
    startAnimation(alphaAnimation)
}


/**
 * set Visible
 *
 * @receiver T
 * @return T
 */
fun <T: View> T.visible(): T {
    visibility = View.VISIBLE
    return this
}

/**
 * set InVisible
 *
 * @receiver T
 * @return T
 */
fun <T: View> T.invisible(): T {
    visibility = View.INVISIBLE
    return this
}

/**
 * set visible gone
 *
 * @receiver T
 * @return T
 */
fun <T: View> T.gone(): T {
    visibility = View.GONE
    return this
}


/**
 * visible 여부
 */
inline val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

/**
 * Check if View is Invisible to User
 */
inline val View.isInVisible: Boolean
    get() = visibility == View.INVISIBLE

/**
 * Check if View Visiblity == GONE
 */
inline val View.isGone: Boolean
    get() = visibility == View.GONE

/**
 * View Background
 *
 * @receiver View
 * @param drawable Drawable?
 */
@SuppressLint("ObsoleteSdkInt")
fun View.setBgDrawable(drawable: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = drawable
    } else {
        @Suppress("DEPRECATION")
        setBackgroundDrawable(drawable)
    }
}

/**
 * View Background
 *
 * @receiver View
 * @param drawableRes Int Drawable Resource
 */
@SuppressLint("ObsoleteSdkInt")
fun View.setBgDrawable(@DrawableRes drawableRes: Int) {
    val drawable = ApplicationReference.getApp().drawable(drawableRes)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = drawable
    } else {
        @Suppress("DEPRECATION")
        setBackgroundDrawable(drawable)
    }
}

/**
 * background color 설정
 *
 * @param color
 */
fun View.setBackgroundColorRes(@ColorRes color: Int)
        = setBackgroundColor(context.color(color))


/**
 * get Activity On Which View is inflated to
 */
fun View.getActivity(): Activity? {
    if (context is Activity)
        return context as Activity
    return null
}


/**
 * show KeyBoard
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

/**
 * hide Keyboard
 */
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}


/**
 * width get / set
 */
var View.width: Int
    get(): Int {
        return (layoutParams as ViewGroup.LayoutParams).width
    }
    set(value) {
        (layoutParams as ViewGroup.LayoutParams).width = value
    }

/**
 * height get / set
 */
var View.height: Int
    get(): Int {
        return (layoutParams as ViewGroup.LayoutParams).height
    }
    set(value) {
        (layoutParams as ViewGroup.LayoutParams).height = value
    }

/**
 * resize the View Width Height
 *
 * @param width
 * @param height
 */
fun View.resize(width: Int, height: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}


/**
 * bottom margin get / set
 */
var View.bottomMargin: Int
    get(): Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value
    }

/**
 * top margin get / set
 */
var View.topMargin: Int
    get(): Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).topMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).topMargin = value
    }

/**
 * right margin get / set
 */
var View.rightMargin: Int
    get(): Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).rightMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).rightMargin = value
    }

/**
 * left margin get / set
 */
var View.leftMargin: Int
    get(): Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).leftMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = value
    }

/**
 * left padding get / set
 */
var View.paddingLeft: Int
    get(): Int {
        return paddingLeft
    }
    set(value) {
        setPadding(value, paddingTop, paddingRight, paddingBottom)
    }

/**
 * right padding get / set
 */
var View.paddingRight: Int
    get(): Int {
        return paddingRight
    }
    set(value) {
        setPadding(paddingLeft, paddingTop, value, paddingBottom)
    }

/**
 * top padding get / set
 */
var View.paddingTop: Int
    get(): Int {
        return paddingTop
    }
    set(value) {
        setPadding(paddingLeft, value, paddingRight, paddingBottom)
    }

/**
 * bottom padding get / set
 */
var View.paddingBottom: Int
    get(): Int {
        return paddingBottom
    }
    set(value) {
        setPadding(paddingLeft, paddingTop, paddingRight, value)
    }

/**
 * set View Padding From Start
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.setPaddingStart(value: Int) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)

/**
 * set View Padding From End
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.setPaddingEnd(value: Int) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)

/**
 * set View Padding On Horizontal Edges
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.setPaddingHorizontal(value: Int) = setPaddingRelative(value, paddingTop, value, paddingBottom)

/**
 * set View Padding From Vertical Edges
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.setPaddingVertical(value: Int) = setPaddingRelative(paddingStart, value, paddingEnd, value)