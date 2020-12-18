package com.ksfams.sgframework.extensions

import android.widget.EditText
import com.ksfams.sgframework.utils.nullToBlank

/**
 *
 * EditText Extensions
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
 * 입력값 리턴
 */
inline val EditText.value: String
    get() = text.toString().nullToBlank()