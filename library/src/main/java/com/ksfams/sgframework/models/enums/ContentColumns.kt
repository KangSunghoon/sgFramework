package com.ksfams.sgframework.models.enums

/**
 *
 * Enum to choose between multiple column type
 * Content Provider
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

enum class ContentColumns(internal val s: String) {

    /** the size Column */
    SIZE ("_size"),

    /** the displayName Column */
    DISPLAY_NAME ("_display_name"),

    /** the title Column */
    TITLE ("title"),

    /** the date Added Column */
    DATE_ADDED ("date_added"),

    /** the date Modified Column */
    DATE_MODIFIED ("date_modified"),

    /** the width Column */
    WIDTH ("width"),

    /** the Height Column */
    HEIGHT ("height")
}