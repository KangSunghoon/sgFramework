package com.ksfams.sgframework.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ksfams.sgframework.modules.preference.Preference
import com.ksfams.sgframework.ui.dialog.manager.DialogManager
import com.ksfams.sgframework.utils.finishAllActivity

/**
 *
 * Base Activity
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

abstract class AbstractBaseActivity : AppCompatActivity() {

    protected lateinit var mContext: Context

    /** 다이얼로그 매니져  */
    protected lateinit var mDialogManager: DialogManager

    protected lateinit var mPreference: Preference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this
        mDialogManager = DialogManager(this)
        mPreference = Preference.getInstance(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 열려있는 모든 Dialog를 닫는다. (leak window 발생)
        mDialogManager.hideAllDialog()
    }


    /**
     * 앱의 모든 화면 종료 처리
     */
    protected open fun finishApp() {
        finishAllActivity(this)
    }

    /**
     * Activity initialize
     */
    protected abstract fun init(savedInstanceState: Bundle?)
}