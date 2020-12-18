package com.ksfams.sgframework.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 *
 * Swift guard 역할
 *
 * ex)
 * guard(nullable != null) { return }
 * val foo = guard(nullable) { return }
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

@ExperimentalContracts
inline fun guard(predicate: Boolean, block: () -> Nothing) {
    contract {
        returns() implies predicate
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (!predicate) block()
}

inline fun <T> guard(receiver: T?, block: () -> Nothing): T {
    if (receiver == null) block()
    return receiver
}