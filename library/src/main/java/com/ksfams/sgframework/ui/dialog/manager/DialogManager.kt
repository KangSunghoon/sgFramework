package com.ksfams.sgframework.ui.dialog.manager

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.recyclerview.widget.RecyclerView
import com.ksfams.sgframework.R
import com.ksfams.sgframework.extensions.string
import com.ksfams.sgframework.modules.reference.ApplicationReference
import com.ksfams.sgframework.ui.dialog.CommonDialog
import com.ksfams.sgframework.ui.dialog.LoadingDialog
import com.ksfams.sgframework.ui.dialog.listener.OnConfirmClickedListener
import com.ksfams.sgframework.ui.dialog.listener.OnItemClickedListener
import com.ksfams.sgframework.utils.LogUtil

/**
 *
 * Dialog 처리
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

object DialogManager {

    /** 로딩 프로그래스 바 다이얼로그  */
    private var loadingDialog: LoadingDialog? = null

    /** 다이얼로그 */
    private var dialog: CommonDialog? = null

    private val mContext: Context
    private val mParentActivity: Activity

    init {
        mContext = ApplicationReference.getApp()
        mParentActivity = mContext as Activity
    }


    /**
     * Dialog가 열려있는지 여부
     * @return
     */
    fun isDialogShowing(): Boolean {
        loadingDialog?.let { if (it.isShowing) return true }
        dialog?.let { if (it.isShowing) return true }
        return false
    }

    /**
     * 모든 다이얼로그 닫기
     */
    fun hideAllDialog() {
        try {
            hideLoadingDialog()
            hideDialog()
        } catch (e: IllegalArgumentException) {
            LogUtil.e(e)
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }

    /**
     * 로딩 다이얼로그 닫기
     */
    fun hideLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) it.dismiss()
            loadingDialog = null
        }
    }

    /**
     * 로딩 다이얼로그 보기
     *
     * @param message CharSequence default 로딩중...
     * @param isBackKey Boolean back-key 종료 처리 여부
     */
    fun showLoadingDialog(message: CharSequence = mContext.string(R.string.loading),
                          isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        loadingDialog = LoadingDialog(mContext, message, isBackKey)
        loadingDialog?.setCanceledOnTouchOutside(false)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    fun hideDialog() {
        dialog?.let {
            if (it.isShowing) it.dismiss()
            dialog = null
        }
    }

    fun showNotButtonDialog(title: CharSequence? = null,
                            message: CharSequence,
                            isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return
//
//        dialog = CommonDialog(context = mContext,
//                title = title, message = message, )
//
//        val message: CharSequence? = null,
//        val adapter: RecyclerView.Adapter<*>? = null,
//        val itemClickedListener: OnItemClickedListener? = null,
//        val isEditText: Boolean = false,
//        val confirmListener: OnConfirmClickedListener? = null,
//        val isButtonLayout: Boolean = true,
//        val primaryLabel: CharSequence? = null,
//        val primaryListener: DialogInterface.OnDismissListener? = null,
//        val secondaryLabel: CharSequence? = null,
//        val secondaryListener: DialogInterface.OnDismissListener? = null,
//        val isBackKey: Boolean = false
    }
}