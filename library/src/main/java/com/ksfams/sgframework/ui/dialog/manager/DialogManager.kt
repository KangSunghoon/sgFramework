package com.ksfams.sgframework.ui.dialog.manager

import android.app.Activity
import android.content.Context
import com.ksfams.sgframework.R
import com.ksfams.sgframework.extensions.string
import com.ksfams.sgframework.modules.holder.SingletonHolder
import com.ksfams.sgframework.ui.dialog.LoadingDialog
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

class DialogManager(context: Context) {

    /** 싱글톤 정의 */
    companion object : SingletonHolder<DialogManager, Context>(::DialogManager)

    /** 로딩 프로그래스 바 다이얼로그  */
    private var mLoadingDialog: LoadingDialog? = null

    private val mContext: Context = context
    private val mParentActivity: Activity = context as Activity

    /**
     * Dialog가 열려있는지 여부
     * @return
     */
    fun isDilogShowing(): Boolean {
        mLoadingDialog?.let { if (it.isShowing) return true }
        //TODO
//        mNotBtnDialog?.let { if (it.isShowing) return true }
//        mNotBtnListDialog?.let { if (it.isShowing) return true }
//        mOneBtnDialog?.let { if (it.isShowing) return true }
//        mOneBtnPasswordDialog?.let { if (it.isShowing) return true }
//        mOneBtnListDialog?.let { if (it.isShowing) return true }
//        mTwoBtnDialog?.let { if (it.isShowing) return true }
//        mTwoBtnPasswordDialog?.let { if (it.isShowing) return true }
//        mTwoBtnListDialog?.let { if (it.isShowing) return true }
        return false
    }

    /**
     * 모든 다이얼로그 닫기
     */
    fun hideAllDialog() {
        try {
            hideLoadingDialog()
            //TODO
//            hideNotButtonDialog()
//            hideNotButtonListDialog()
//            hideOneBtnDialog()
//            hideOneBtnPasswordInputDialog()
//            hideOneBtnListDialog()
//            hideTwoBtnDialog()
//            hideTwoBtnPasswordInputDialog()
//            hideTwoBtnListDialog()
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
        mLoadingDialog?.let {
            if (it.isShowing) it.dismiss()
            mLoadingDialog = null
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

        mLoadingDialog = LoadingDialog(mContext, message, isBackKey)
        mLoadingDialog?.setCanceledOnTouchOutside(false)
        mLoadingDialog?.setCancelable(false)
        mLoadingDialog?.show()
    }
}