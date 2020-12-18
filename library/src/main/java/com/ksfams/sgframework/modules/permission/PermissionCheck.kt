package com.ksfams.sgframework.modules.permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker
import com.ksfams.sgframework.R
import com.ksfams.sgframework.constants.LINE_SEP
import com.ksfams.sgframework.extensions.string
import com.ksfams.sgframework.modules.reference.ApplicationReference
import com.ksfams.sgframework.utils.getIntent

/**
 *
 * Permission Check 모듈
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

object PermissionCheck {

    private val context: Context
    private var config: Config

    private var listener: PermissionListener? = null

    init {
        context = ApplicationReference.getApp()
        config = Config(context = context)
    }

    /**
     * init
     *
     * @param config Config
     */
    fun init(config: Config) {
        this.config = config
    }

    /**
     * get config data
     *
     * @return Config
     */
    fun getConfig(): Config = this.config


    /**
     * [permissions] 를 run time 으로 체크 처리함.
     *
     * @param permissions Array<out String>
     * @param listener 퍼미션 허용/거부 listener
     */
    @SuppressLint("NewApi")
    fun runtimeCheck(vararg permissions: String, listener: PermissionListener) {
        this.listener = listener

        if (!hasDeniedPermissions(*permissions)) {
            this.listener?.onPermissionGranted()
        } else {
            // 체크해야할 퍼미션이 있으므로 퍼미션 체크 로직을 처리함.
            checkPermissions(arrayOf(*permissions))
        }
    }

    /**
     * config 정의된 팔수 퍼미션을 체크함.
     *
     * @param listener 퍼미션 허용/거부 listener
     */
    @SuppressLint("NewApi")
    fun check(listener: PermissionListener) {
        this.listener = listener

        if (hasNecessaryPermissions()) {
            // 체크해야할 퍼미션이 있으므로 퍼미션 체크 로직을 처리함.
            checkPermissions(config.necessaryPermissions, config.optionalPermissions)
        } else {
            this.listener?.onPermissionGranted()
        }
    }

    /**
     * config.necessaryPermissions 에 정의된
     * 필수 퍼미션 중에 허가 받아야할 퍼미션이 있는지 여부
     *
     * @return Boolean true: 허가 받아야할 퍼미션이 존재함. (퍼미션 체크 필요)
     */
    fun hasNecessaryPermissions(): Boolean {
        if (config.necessaryPermissions == null || config.necessaryPermissions!!.isEmpty()) {
            return false
        }

        return hasDeniedPermissions(*config.necessaryPermissions!!)
    }


    /**
     * 퍼미션 처리 결과
     *
     * @param deniedPermissions ArrayList<String>?
     */
    fun onPermissionResult(deniedPermissions: Array<String>? = null) {
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            this.listener?.onPermissionGranted()
        } else {
            this.listener?.onPermissionDenied(deniedPermissions)
        }
    }

    /**
     * [permissions] 중에 거부된 퍼미션이 있는지 확인
     *
     * @param permissions Array<out String>
     * @return Boolean
     */
    private fun hasDeniedPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }

        permissions.forEach {
            if (!checkSelfPermission(it)) {
                return true
            }
        }
        return false
    }


    /**
     * Determine context has access to the given permission.
     *
     * @param permission permission
     * @return true if context has access to the given permission, false otherwise.
     * @see .hasSelfPermissions
     */
    private fun checkSelfPermission(permission: String): Boolean {
        return try {
            PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
        } catch (t: RuntimeException) {
            false
        }
    }

    /**
     * 퍼미션 체크 Activity 호출
     *
     * @param necessaryPermissions 필수 퍼미션 리스트
     * @param optionalPermissions 선택 퍼미션 목록: 퍼미션 거부 시에도 앱 진입 가능 (optional)
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions(necessaryPermissions: Array<String>?, optionalPermissions: Array<String>? = null) {
        val intent = Intent(context, PermissionActivity::class.java)
        intent.putExtra(PermissionActivity.EXTRA_NECESSARY_PERMISSIONS, necessaryPermissions)
        intent.putExtra(PermissionActivity.EXTRA_OPTIONAL_PERMISSIONS, optionalPermissions)
        intent.putExtra(PermissionActivity.EXTRA_HAS_LAUNCH_SETTING, config.hasLaunchSetting)
        intent.putExtra(PermissionActivity.EXTRA_SETTING_BUTTON_LABEL, config.launchSettingButtonLabel)
        intent.putExtra(PermissionActivity.EXTRA_DENIED_DIALOG_TITLE, config.deniedDialogTitle)
        intent.putExtra(PermissionActivity.EXTRA_DENIED_DIALOG_MESSAGE, config.deniedDialogMessage)
        intent.putExtra(PermissionActivity.EXTRA_DENIED_DIALOG_CLOSE_BUTTON_LABEL, config.deniedDialogCloseButtonLabel)
        intent.getIntent()
        context.startActivity(intent)
    }


    /**
     * Kotlin Builder Pattern
     * 예) val config = PermissionCheck.ConfigBuilder().setPermissions(Array<String>).build()
     *
     * 퍼미션 거부 시, 발생되는 Dialog는
     * hasLaunchSettingButton 이 true 이면, two button Dialog 가 표시되고
     * false 이면 one button Dialog 가 표시됨.
     *
     * @property context
     * @property necessaryPermissions Array<String> 필수 퍼미션 목록: 퍼미션 거부 시, 앱 종료 처리
     * @property optionalPermissions Array<String> 선택 퍼미션 목록: 퍼미션 거부 시에도 앱 진입 가능 (optional)
     * @property hasLaunchSetting Boolean 퍼미션 거부 시, 시스템 설정 화면으로 이동할 수 있도록 버튼을 제공할지 여부 (true: 버튼 제공)
     * @property launchSettingButtonLabel String hasLaunchSettingButton 가 true 일 때, 버튼 label
     * @property deniedDialogTitle String 퍼미션 거부 시, 발생되는 Dialog 에 표시될 타이틀 (optional)
     * @property deniedDialogMessage String 퍼미션 거부 시, 발생되는 Dialog 에 표시될 메시지
     * @property deniedDialogCloseButtonLabel String 퍼미션 거부 시, 발생되는 Dialog 의 버튼 레이블
     * @constructor
     */
    class Config internal constructor(val context: Context,
                                      val necessaryPermissions: Array<String>? = null,
                                      val optionalPermissions: Array<String>? = null,
                                      val hasLaunchSetting: Boolean = true,
                                      val launchSettingButtonLabel: String = context.string(R.string.setting),
                                      val deniedDialogTitle: String? = null,
                                      val deniedDialogMessage: String = PermissionCheck.context.string(R.string.deny_permission),
                                      val deniedDialogCloseButtonLabel: String = PermissionCheck.context.string(R.string.close)) {

        override fun toString(): String {
            return ("necessaryPermissions: " + necessaryPermissions?.toString()
                    + LINE_SEP + "optionalPermissions: " + optionalPermissions?.toString()
                    + LINE_SEP + "hasLaunchSetting: " + hasLaunchSetting
                    + LINE_SEP + "launchSettingButtonLabel: " + launchSettingButtonLabel
                    + LINE_SEP + "deniedDialogTitle: " + deniedDialogTitle
                    + LINE_SEP + "deniedDialogMessage: " + deniedDialogMessage
                    + LINE_SEP + "deniedDialogCloseButtonLabel: " + deniedDialogCloseButtonLabel)
        }
    }

    /**
     * Config Builder
     * 퍼미션 거부 시, 발생되는 Dialog는
     * hasLaunchSettingButton 이 true 이면, two button Dialog 가 표시되고
     * false 이면 one button Dialog 가 표시됨.
     *
     * @property necessaryPermissions Array<String> 필수 퍼미션 목록: 퍼미션 거부 시, 앱 종료 처리
     * @property optionalPermissions Array<String> 선택 퍼미션 목록: 퍼미션 거부 시에도 앱 진입 가능 (optional)
     * @property hasLaunchSetting Boolean 퍼미션 거부 시, 시스템 설정 화면으로 이동할 수 있도록 버튼을 제공할지 여부 (true: 버튼 제공)
     * @property launchSettingButtonLabel String hasLaunchSettingButton 가 true 일 때, 버튼 label
     * @property deniedDialogTitle String 퍼미션 거부 시, 발생되는 Dialog 에 표시될 타이틀 (optional)
     * @property deniedDialogMessage String 퍼미션 거부 시, 발생되는 Dialog 에 표시될 메시지
     * @property deniedDialogCloseButtonLabel String 퍼미션 거부 시, 발생되는 Dialog 의 버튼 레이블
     * @constructor
     */
    data class ConfigBuilder(var necessaryPermissions: Array<String>? = null,
                             var optionalPermissions: Array<String>? = null,
                             var hasLaunchSetting: Boolean = true,
                             var launchSettingButtonLabel: String = context.string(R.string.setting),
                             var deniedDialogTitle: String? = null,
                             var deniedDialogMessage: String = context.string(R.string.deny_permission),
                             var deniedDialogCloseButtonLabel: String = context.string(R.string.close)) {

        /**
         * 필수 퍼미션 목록 설정
         *
         * @param necessaryPermissions Array<out String>
         * @return ConfigBuilder
         */
        fun setNecessaryPermissions(vararg necessaryPermissions: String) = apply {
            this.necessaryPermissions = arrayOf(*necessaryPermissions)
        }

        /**
         * 선택 퍼미션 목록 설정
         *
         * @param optionalPermissions Array<out String>
         * @return ConfigBuilder
         */
        fun setOptionalPermissions(vararg optionalPermissions: String) = apply {
            this.optionalPermissions = arrayOf(*optionalPermissions)
        }

        /**
         * System 설정 화면으로 이동 설정
         *
         * @param hasLaunchSetting Boolean
         * @param launchSettingButtonLabel String
         * @return ConfigBuilder
         */
        fun setLaunchSetting(hasLaunchSetting: Boolean,
                             launchSettingButtonLabel: String) = apply {
            this.hasLaunchSetting = hasLaunchSetting
            this.launchSettingButtonLabel = launchSettingButtonLabel
        }

        /**
         * 퍼미션 거부 시, 발생될 Dialog 설정
         *
         * @param title String?
         * @param message String
         * @param closeButtonLabel String
         * @return ConfigBuilder
         */
        fun setDeniedDialog(title: String? = null, message: String,
                            closeButtonLabel: String) = apply {
            this.deniedDialogTitle = title
            this.deniedDialogMessage = message
            this.deniedDialogCloseButtonLabel = closeButtonLabel
        }

        fun build() = Config(context,
                necessaryPermissions, optionalPermissions,
                hasLaunchSetting, launchSettingButtonLabel,
                deniedDialogTitle, deniedDialogMessage, deniedDialogCloseButtonLabel)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ConfigBuilder

            if (necessaryPermissions != null) {
                if (other.necessaryPermissions == null) return false
                if (!necessaryPermissions!!.contentEquals(other.necessaryPermissions!!)) return false
            } else if (other.necessaryPermissions != null) return false

            if (optionalPermissions != null) {
                if (other.optionalPermissions == null) return false
                if (!optionalPermissions!!.contentEquals(other.optionalPermissions!!)) return false
            } else if (other.optionalPermissions != null) return false

            if (hasLaunchSetting != other.hasLaunchSetting) return false
            if (launchSettingButtonLabel != other.launchSettingButtonLabel) return false
            if (deniedDialogTitle != other.deniedDialogTitle) return false
            if (deniedDialogMessage != other.deniedDialogMessage) return false
            if (deniedDialogCloseButtonLabel != other.deniedDialogCloseButtonLabel) return false

            return true
        }

        override fun hashCode(): Int {
            var result = necessaryPermissions?.contentHashCode() ?: 0
            result = 31 * result + (optionalPermissions?.contentHashCode() ?: 0)
            result = 31 * result + hasLaunchSetting.hashCode()
            result = 31 * result + launchSettingButtonLabel.hashCode()
            result = 31 * result + (deniedDialogTitle?.hashCode() ?: 0)
            result = 31 * result + deniedDialogMessage.hashCode()
            result = 31 * result + deniedDialogCloseButtonLabel.hashCode()
            return result
        }
    }
}