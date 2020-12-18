package com.ksfams.sgframework.models.enums

/**
 *
 * Image 관련 구조체
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
 * Image Orientation 구조체
 *
 */
enum class ImageOrientation {
    PORTRAIT, LANDSCAPE;

    /**
     * companion object로 Java에서 사용할 수 있는 static 메소드, 객체 정의하기
     */
    companion object {
        fun getOrientation(width: Int, height: Int): ImageOrientation =
                if (width >= height) LANDSCAPE else PORTRAIT
    }
}


/**
 * Image Resize Mode 구조체
 *
 */
enum class ResizeMode {
    AUTOMATIC, FIT_TO_WIDTH, FIT_TO_HEIGHT, FIT_EXACT
}