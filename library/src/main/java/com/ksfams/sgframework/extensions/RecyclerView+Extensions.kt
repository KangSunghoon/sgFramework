package com.ksfams.sgframework.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ksfams.sgframework.utils.guard

/**
 *
 * RecyclerView
 * Extensions
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
 * RecyclerView 의
 * 스크롤 가능 여부
 */
val RecyclerView.canScroll: Boolean
    get() {
        adapter?.let {
            val manager = guard(layoutManager as? LinearLayoutManager) {
                return false
            }

            return when (manager.orientation) {
                RecyclerView.HORIZONTAL -> (computeHorizontalScrollRange() + paddingLeft + paddingRight) > width
                else -> (computeVerticalScrollRange() + paddingTop + paddingBottom) > height
            }
        } ?: return false
    }