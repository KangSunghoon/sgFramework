package com.ksfams.sgframework.ui.dialog.listener

import android.view.View

/**
 *
 * 리스트 item 클릭이벤트
 *
 * Create Date  12/21/20
 * @version 1.00    12/21/20
 * @since   1.00
 * @see
 * @author  강성훈(ssogaree@gmail.com)
 * Revision History
 * who      when            what
 * 강성훈      12/21/20     신규 개발.
 */

interface OnItemClickedListener {
    /**
     * 클릭이벤트 처리
     * @param view
     * @param selectedItemPositions
     */
    fun onClicked(view: View, position: Int, selectedItemPositions: List<Int>?)
}