package com.ksfams.sgframework.ui.helper

import android.view.KeyEvent

/**
 *
 * Hidden Menu를 열기 위한 key 동작 확인
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

object HiddenMenuKey {

    /** Hidden 메뉴 호출을 위한 키 조합  */
    private val HIDDEN_KEY_SEQ = intArrayOf(
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN
    )

    private var mStart = false
    private var mKeyList: ArrayList<Int> = ArrayList()

    fun isSecret(key: Int): Boolean {
        if (mStart) {
            val secCnt = HIDDEN_KEY_SEQ.size
            mKeyList.add(key)
            if (isSecret()) {
                if (mKeyList.size >= secCnt) {
                    mKeyList.clear()
                    mStart = false
                    return true
                }
            } else {
                mKeyList.clear()
                if (key == HIDDEN_KEY_SEQ[0]) {
                    mStart = true
                    mKeyList.add(key)
                } else {
                    mStart = false
                }
            }
        } else {
            if (key == HIDDEN_KEY_SEQ[0]) {
                mStart = true
                mKeyList.add(key)
            }
        }
        return false
    }

    private fun isSecret(): Boolean {
        val cnt = mKeyList.size
        for (i in 0 until cnt) {
            val key = mKeyList[i]
            if (key != HIDDEN_KEY_SEQ[i]) {
                return false
            }
        }
        return true
    }
}