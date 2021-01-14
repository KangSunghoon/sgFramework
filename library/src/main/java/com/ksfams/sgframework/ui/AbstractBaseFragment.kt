package com.ksfams.sgframework.ui

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.ksfams.sgframework.modules.preference.Preference
import com.ksfams.sgframework.ui.dialog.manager.DialogManager
import com.ksfams.sgframework.utils.finishAllActivity

/**
 *
 * Base Fragment
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

abstract class AbstractBaseFragment : Fragment() {

    protected lateinit var mContext: Context
    protected var mParentActivity: Activity? = null

    /** 다이얼로그 매니져  */
    protected lateinit var mDialogManager: DialogManager

    protected lateinit var mPreference: Preference

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
        mParentActivity = activity
        mDialogManager = DialogManager(context)
        mPreference = Preference.getInstance(context)
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
        mParentActivity?.let {
            finishAllActivity(it)
        }
    }

    /**
     * Fragment initialize
     */
    protected abstract fun init()
}