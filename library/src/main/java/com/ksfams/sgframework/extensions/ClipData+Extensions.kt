package com.ksfams.sgframework.extensions

import android.content.ClipData
import android.os.Build
import androidx.annotation.RequiresApi

/**
 *
 * ClipData Extensions
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
 * ClipData to String
 *
 * @receiver ClipData
 * @return String
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
fun ClipData.toAllString(): String {

    this.getItemAt(0)?.let { item ->
        val result = "ClipData.Item { "

        item.htmlText?.let {
            return result + "H:$it }"
        }

        item.text?.let {
            return result + "T:$it }"
        }

        item.uri?.let {
            return result + "U:$it }"
        }

        item.intent?.let {
            return result + "I:$it }"
        }

        return result + "NULL }"
    }

    return "ClipData.Item {}"
}