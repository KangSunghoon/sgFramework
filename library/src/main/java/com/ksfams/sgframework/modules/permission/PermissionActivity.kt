package com.ksfams.sgframework.modules.permission

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.ksfams.sgframework.ui.AbstractBaseActivity
import com.ksfams.sgframework.utils.isNotEmpty
import com.ksfams.sgframework.utils.launchAppDetailsSettings

/**
 *
 * 퍼미션 체크 처리 및
 * Rationale Dialog 처리
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

@RequiresApi(api = Build.VERSION_CODES.M)
class PermissionActivity : AbstractBaseActivity() {

    private val REQ_CODE_PERMISSION_REQUEST = 10
    private val REQ_CODE_REQUEST_SETTING = 20
    private val REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST = 30
    private val REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING = 31

    companion object {
        const val EXTRA_NECESSARY_PERMISSIONS = "necessary_permissions"
        const val EXTRA_OPTIONAL_PERMISSIONS = "optional_permissions"

        const val EXTRA_HAS_LAUNCH_SETTING = "has_launch_setting"
        const val EXTRA_SETTING_BUTTON_LABEL = "setting_button_label"

        const val EXTRA_DENIED_DIALOG_TITLE = "denied_dialog_title"
        const val EXTRA_DENIED_DIALOG_MESSAGE = "denied_dialog_message"
        const val EXTRA_DENIED_DIALOG_CLOSE_BUTTON_LABEL = "denied_dialog_close_button_label"
    }

    // 필수 퍼미션 리스트
    private var necessaryPermissions: Array<String>? = null
    // 선택 퍼미션 리스트
    private var optionalPermissions: Array<String>? = null

    // 시스템 설정 화면 이동 여부
    private var hasLaunchSetting = true
    // 시스템 설정 화면으로 이동할 경우, dialog 버튼 레이블
    private var settingButtonLabel: String? = null

    // 퍼미션 거부 시, 발생될 Dialog 타이틀
    private var deniedDialogTitle: String? = null
    // 퍼미션 거부 시, 발생될 Dialog 메시지
    private var deniedDialogMessage: String? = null
    // 퍼미션 거부 시, 발생될 Dialog 닫기 버튼 레이블
    private var deniedDialogCloseButtonLabel: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        init(savedInstanceState)

        // SYSTEM_ALERT_WINDOW 퍼미션이 필요한 경우, 시스템 설정 화면으로 이동 부터 한다.
        // SYSTEM_ALERT_WINDOW 퍼미션이 필요한 경우, 시스템 설정 화면으로 이동 부터 한다.
        if (needWindowPermission()) {
            startActivityWindowPermission(REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST)
        } else {
            checkPermissions(false)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            necessaryPermissions = savedInstanceState.getStringArray(EXTRA_NECESSARY_PERMISSIONS)
            optionalPermissions = savedInstanceState.getStringArray(EXTRA_OPTIONAL_PERMISSIONS)
            hasLaunchSetting = savedInstanceState.getBoolean(EXTRA_HAS_LAUNCH_SETTING)
            settingButtonLabel = savedInstanceState.getString(EXTRA_SETTING_BUTTON_LABEL)
            deniedDialogTitle = savedInstanceState.getString(EXTRA_DENIED_DIALOG_TITLE)
            deniedDialogMessage = savedInstanceState.getString(EXTRA_DENIED_DIALOG_MESSAGE)
            deniedDialogCloseButtonLabel = savedInstanceState.getString(EXTRA_DENIED_DIALOG_CLOSE_BUTTON_LABEL)
        } else {
            necessaryPermissions = intent.getStringArrayExtra(EXTRA_NECESSARY_PERMISSIONS)
            optionalPermissions = intent.getStringArrayExtra(EXTRA_OPTIONAL_PERMISSIONS)
            hasLaunchSetting = intent.getBooleanExtra(EXTRA_HAS_LAUNCH_SETTING, true)
            settingButtonLabel = intent.getStringExtra(EXTRA_SETTING_BUTTON_LABEL)
            deniedDialogTitle = intent.getStringExtra(EXTRA_DENIED_DIALOG_TITLE)
            deniedDialogMessage = intent.getStringExtra(EXTRA_DENIED_DIALOG_MESSAGE)
            deniedDialogCloseButtonLabel = intent.getStringExtra(EXTRA_DENIED_DIALOG_CLOSE_BUTTON_LABEL)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putStringArray(EXTRA_NECESSARY_PERMISSIONS, necessaryPermissions)
        outState.putStringArray(EXTRA_OPTIONAL_PERMISSIONS, optionalPermissions)
        outState.putBoolean(EXTRA_HAS_LAUNCH_SETTING, hasLaunchSetting)
        outState.putString(EXTRA_SETTING_BUTTON_LABEL, settingButtonLabel)
        outState.putString(EXTRA_DENIED_DIALOG_TITLE, deniedDialogTitle)
        outState.putString(EXTRA_DENIED_DIALOG_MESSAGE, deniedDialogMessage)
        outState.putString(EXTRA_DENIED_DIALOG_CLOSE_BUTTON_LABEL, deniedDialogCloseButtonLabel)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_CODE_REQUEST_SETTING -> checkPermissions(true)
            // 최초 SYSTEM_ALERT_WINDOW 퍼미션 요청에 대한 결과
            REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST -> {
                if (!hasWindowPermission() && deniedDialogMessage.isNotEmpty()) { // SYSTEM_ALERT_WINDOW 퍼미션 권한이 거부되고 denyMessage 가 있으면 경고 팝업이 발생한다.
                    showWindowPermissionDenyDialog()
                } else { // SYSTEM_ALERT_WINDOW 퍼미션 권한을 허용 했거나 denyMessage가 없는 경우는 남아있는 permission 들을 확인한다.
                    checkPermissions(false)
                }
            }
            // SYSTEM_ALERT_WINDOW 퍼미션 권한 설정 실패후 재 요청에 대한 결과 (showWindowPermissionDenyDialog 호출에 대한 결과)
            REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING -> checkPermissions(false)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        val deniedPermissions = ArrayList<String>()
        for (i in permissions.indices) {
            val permission = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                necessaryPermissions?.forEach {
                    if (it == permission) {
                        deniedPermissions.add(permission)
                        return@forEach
                    }
                }
            }
        }

        if (deniedPermissions.isEmpty()) {
            permissionGranted()
        } else {
            showPermissionDenyDialog(deniedPermissions.toTypedArray())
        }
    }


    /**
     * SYSTEM_ALERT_WINDOW 퍼미션이 필요한지 여부 확인
     *
     * @return
     */
    private fun needWindowPermission(): Boolean {
        necessaryPermissions?.forEach {
            if (it == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                return !hasWindowPermission()
            }
        }

        optionalPermissions?.forEach {
            if (it == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                return !hasWindowPermission()
            }
        }
        return false
    }

    /**
     * SYSTEM_ALERT_WINDOW 퍼미션이 필요한지 여부 확인
     *
     * @return Boolean
     */
    private fun hasWindowPermission(): Boolean {
        return Settings.canDrawOverlays(applicationContext)
    }

    /**
     * 퍼미션 체크 처
     * @param fromOnActivityResult
     */
    private fun checkPermissions(fromOnActivityResult: Boolean) {
        val needPermissions: Array<String> = needPermissions()
        if (needPermissions.isEmpty()) {
            permissionGranted()
        } else if (fromOnActivityResult) {
            permissionDenied(needPermissions)
        } else if (needPermissions.size == 1 && needPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            permissionDenied(needPermissions)
        } else {
            requestPermissions(needPermissions)
        }
    }

    /**
     * 필수 퍼미션 중에서
     * 확인할 퍼미션이 남아 있는지 여부
     *
     * @return
     */
    private fun needPermissions(): Array<String> {
        val needPermissions: ArrayList<String> = ArrayList()
        necessaryPermissions?.forEach {
            if (it == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                if (!hasWindowPermission()) {
                    needPermissions.add(it)
                }
            } else {
                if (PermissionChecker.checkSelfPermission(this, it) != PermissionChecker.PERMISSION_GRANTED) {
                    needPermissions.add(it)
                }
            }
        }
        return needPermissions.toTypedArray()
    }

    /**
     * ACTION_MANAGE_OVERLAY_PERMISSION 설정을 위한
     * 시스템 설정 화면 호출
     *
     * @param requestCode
     */
    private fun startActivityWindowPermission(requestCode: Int) {
        val uri = Uri.fromParts("package", baseContext.packageName, null)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        startActivityForResult(intent, requestCode)
    }

    /**
     * 퍼미션 체크
     * 선택 퍼미션이 존재할 경우, 같이 체크한다.
     *
     * @param needPermissions
     */
    private fun requestPermissions(needPermissions: Array<String>) {
        var permissions = needPermissions
        optionalPermissions?.let {
            permissions = needPermissions.plus(it)
        }

        ActivityCompat.requestPermissions(this, permissions, REQ_CODE_PERMISSION_REQUEST)
    }

    private fun permissionGranted() {
        PermissionCheck.onPermissionResult()
        finish()
        // 퍼미션 체크 Activity 는 빈 화면이므로 닫을 때, animation 을 제거함.
        overridePendingTransition(0, 0)
    }

    private fun permissionDenied(deniedPermissions: Array<String>) {
        PermissionCheck.onPermissionResult(deniedPermissions)
        finish()
        // 퍼미션 체크 Activity 는 빈 화면이므로 닫을 때, animation 을 제거함.
        overridePendingTransition(0, 0)
    }


    /**
     * SYSTEM_ALERT_WINDOW 퍼미션을 거부한 경우,
     * 해당 팝업 발생
     */
    private fun showWindowPermissionDenyDialog() {
        if (hasLaunchSetting) {
            //TODO
//            mDialogManager.showTwoBtnDialog(title = deniedDialogTitle,
//                message = deniedDialogMessage!!,
//                confirmLabel = settingButtonLabel!!,
//                confirmListener = DialogInterface.OnDismissListener {
//                    startActivityWindowPermission(REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING)
//                },
//                cancelLabel = deniedDialogCloseButtonLabel!!,
//                cancelListener = DialogInterface.OnDismissListener {
//                    checkPermissions(false)
//                })
        } else {
//            mDialogManager.showOneBtnDialog(title = deniedDialogTitle,
//                message = deniedDialogMessage!!,
//                confirmLabel = deniedDialogCloseButtonLabel!!,
//                confirmListener = DialogInterface.OnDismissListener {
//                    checkPermissions(false)
//                })
        }
    }

    private fun showPermissionDenyDialog(deniedPermissions: Array<String>) {
        if (deniedDialogMessage?.isEmpty()!!) { // denyMessage 설정 안함
            permissionDenied(deniedPermissions)
            return
        }

        if (hasLaunchSetting) {
            //TODO
//            mDialogManager.showTwoBtnDialog(title = deniedDialogTitle,
//                message = deniedDialogMessage!!,
//                confirmLabel = settingButtonLabel!!,
//                confirmListener = DialogInterface.OnDismissListener {
//                    launchAppDetailsSettings(requestCode = REQ_CODE_REQUEST_SETTING)
//                },
//                cancelLabel = deniedDialogCloseButtonLabel!!,
//                cancelListener = DialogInterface.OnDismissListener {
//                    permissionDenied(deniedPermissions)
//                })
        } else {
//            mDialogManager.showOneBtnDialog(title = deniedDialogTitle,
//                message = deniedDialogMessage!!,
//                confirmLabel = deniedDialogCloseButtonLabel!!,
//                confirmListener = DialogInterface.OnDismissListener {
//                    permissionDenied(deniedPermissions)
//                })
        }
    }
}