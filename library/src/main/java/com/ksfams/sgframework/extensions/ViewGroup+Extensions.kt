package com.ksfams.sgframework.extensions

import android.view.View
import android.view.ViewGroup

/**
 *
 * ViewGroup Extensions
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
 * Add View with the (+) Operator
 *
 * @receiver ViewGroup
 * @param child
 */
operator fun ViewGroup.plusAssign(child: View) = addView(child)

/**
 * Remove View with the (-) Operator
 *
 * @receiver ViewGroup
 * @param child
 */
operator fun ViewGroup.minusAssign(child: View) = removeView(child)


/**
 * [child] 가 ViewGroup 에 포함되어 있는지 여부
 *
 * @receiver ViewGroup
 * @param child
 * @return
 */
fun ViewGroup.contains(child: View): Boolean
        = indexOfChild(child) > -1


/**
 * get All the Children's as Iterator
 *
 * @receiver ViewGroup
 * @return Iterator<View>
 */
fun ViewGroup.children() = object : Iterator<View> {
    var index = 0

    override fun hasNext(): Boolean {
        return index < childCount
    }

    override fun next(): View {
        return getChildAt(index++)
    }
}