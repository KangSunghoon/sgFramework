package com.ksfams.sgframework.extensions

import com.ksfams.sgframework.constants.IS_FALSE
import com.ksfams.sgframework.constants.IS_TRUE

/**
 *
 * Boolean Extensions
 *
 * Create Date 2020-01-28
 * @version    1.00 2020-01-28
 * @since   1.00
 * @see
 * @author    강성훈(ssogaree@gmail.com)
 * Revision History
 * who			when				what
 * ssogaree		2020-01-28		    신규 개발.
 */

/**
 * Converts Boolean to Int
 *
 * @return true then 1 else 0
 */
fun Boolean.toInt(): Int = if (this) 1 else 0

/**
 * Converts Boolean to Int
 *
 * @return true then 1 else 0
 */
fun Boolean.toYN(): String = if (this) IS_TRUE else IS_FALSE


/**
 * Toggle the Boolean Value,
 * if it's true then it will become false else vice versa.
 *
 */
fun Boolean.toggle() = !this