package com.ksfams.sgframework.extensions

/**
 *
 * Long Extensions
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
 * [this] Null is Empty
 *
 * @receiver Long?
 * @return Boolean
 */
fun Long?.isEmpty(): Boolean = this == null

/**
 * [this] Not null 이거나
 * '0' 보다 크면 NotEmpty
 *
 * @receiver Long?
 * @return Boolean
 */
fun Long?.isNotEmpty() = !isEmpty() || this!! > 0