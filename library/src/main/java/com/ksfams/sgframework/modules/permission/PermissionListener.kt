package com.ksfams.sgframework.modules.permission

/**
 *
 * 퍼미션 체크 Listener
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

interface PermissionListener {

    /**
     * 파미션 체크 승인
     */
    fun onPermissionGranted()

    /**
     * 퍼미션 체크 거부
     * @param deniedPermissions
     */
    fun onPermissionDenied(deniedPermissions: Array<String>)
}