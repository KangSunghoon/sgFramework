package com.ksfams.sgframework.models.enums

/**
 *
 * Enum to choose between Ascending or Descending order
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

enum class ContentOrder(internal val s: String) {
    /** sort by DESC Order */
    DESCENDING("DESC"),

    /** Sort by ASC Order */
    ASCENDING("ASC")
}