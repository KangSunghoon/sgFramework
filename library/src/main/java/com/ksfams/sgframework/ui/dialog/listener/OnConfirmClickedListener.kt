package com.ksfams.sgframework.ui.dialog.listener

/**
 *
 * 입력 문자에 대한 클릭이벤트 처리
 *
 * Create Date 2020-02-03
 * @version    1.00 2020-02-03
 * @since   1.00
 * @see
 * @author    강성훈(ssogaree@gmail.com)
 * Revision History
 * who			when				what
 * ssogaree		2020-02-03		    신규 개발.
 */

interface OnConfirmClickedListener {
    /**
     * 클릭이벤트 처리
     * @param input
     */
    fun onClicked(input: String)
}