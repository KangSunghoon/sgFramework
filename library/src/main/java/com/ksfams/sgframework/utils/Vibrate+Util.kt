package com.ksfams.sgframework.utils

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import androidx.annotation.RequiresPermission
import com.ksfams.sgframework.modules.system.vibrator

/**
 *
 * Vibrate Util
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
 * Vibrate
 * need VIBRATE permission
 *
 * @receiver Context
 * @param millSec duration of vibrate
 */
@RequiresPermission(Manifest.permission.VIBRATE)
fun Context.vibrate(millSec: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(millSec, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(millSec)
    }
}

/**
 * Vibrate
 * need VIBRATE permission
 *
 * @receiver Context
 * @param pattern vibrate pattern
 * @param repeat count of repeat, if once, give repeat as -1
 */
@JvmOverloads
@RequiresPermission(Manifest.permission.VIBRATE)
fun Context.vibrate(pattern: LongArray, repeat: Int = -1) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(pattern, repeat)
    }
}