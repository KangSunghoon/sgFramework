package com.ksfams.sgframework.ui.dialog.manager

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.recyclerview.widget.RecyclerView
import com.ksfams.sgframework.R
import com.ksfams.sgframework.extensions.string
import com.ksfams.sgframework.modules.holder.SingletonHolder
import com.ksfams.sgframework.modules.reference.ApplicationReference
import com.ksfams.sgframework.ui.dialog.CommonDialog
import com.ksfams.sgframework.ui.dialog.LoadingDialog
import com.ksfams.sgframework.ui.dialog.listener.OnConfirmClickedListener
import com.ksfams.sgframework.ui.dialog.listener.OnItemClickedListener
import com.ksfams.sgframework.utils.LogUtil
import com.ksfams.sgframework.utils.finishAllActivity

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
    private var loadingDialog: LoadingDialog? = null

    /** 다이얼로그 */
    private var dialog: CommonDialog? = null

    private val mContext: Context = context
    private val mParentActivity: Activity = context as Activity


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

    /**
     * 다이얼로그 닫기
     */
    fun hideDialog() {
        dialog?.let {
            if (it.isShowing) it.dismiss()
            dialog = null
        }
    }

    /**
     * 버튼 없는
     * 일반 다이얼로그 보기
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param message
     * @param isBackKey back-key 종료 처리 여부
     */
    fun showNotButtonDialog(title: CharSequence? = null,
                            message: CharSequence,
                            isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, message = message,
                isButtonLayout = false, isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 버튼 없는
     * 리스트형 다이얼로그
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param adapter
     * @param itemClickedListener itemClickedListener 처리 시, dialog 닫기 처리도 추가해 주어야 함.
     * @param isBackKey
     */
    fun showNotButtonListDialog(title: CharSequence? = null,
                                adapter: RecyclerView.Adapter<*>,
                                itemClickedListener: OnItemClickedListener,
                                isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, adapter = adapter, itemClickedListener = itemClickedListener,
                isButtonLayout = false, isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 1개의 버튼을 가지는
     * 일반 다이얼로그 보기
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param message
     * @param buttonLabel
     * @param buttonListener
     * @param isBackKey
     */
    fun showOneButtonDialog(title: CharSequence? = null,
                            message: CharSequence,
                            buttonLabel: CharSequence,
                            buttonListener: DialogInterface.OnDismissListener,
                            isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, message = message,
                isButtonLayout = true, primaryLabel = buttonLabel, primaryListener = buttonListener,
                isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 1개의 버튼을 가지고
     * 앱을 종료 처리하는 dialog
     *
     * @param title
     * @param message
     * @param buttonLabel
     */
    fun showErrorAndFinishAffinity(title: CharSequence? = null,
                                   message: CharSequence,
                                   buttonLabel: CharSequence = mContext.string(R.string.finish)) {
        showOneButtonDialog(title = title, message = message,
                buttonLabel = buttonLabel,
                buttonListener = {
                    finishAllActivity(mParentActivity)
                })
    }

    /**
     * 확인 버튼을 가지고
     * Activity 종료 처리하는 dialog
     *
     * @param title
     * @param message
     * @param buttonLabel
     */
    fun showErrorAndFinish(title: CharSequence? = null,
                           message: CharSequence,
                           buttonLabel: CharSequence = mContext.string(R.string.finish)) {
        showOneButtonDialog(title = title, message = message,
                buttonLabel = buttonLabel,
                buttonListener = {
                    mParentActivity.finish()
                })
    }

    /**
     * 1개의 버튼, 입력폼을 가지는
     * 다이얼로그 보기
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param message
     * @param editTextListener 입력폼의 값을 전달
     * @param buttonLabel
     * @param isBackKey
     */
    fun showOneButtonInputDialog(title: CharSequence,
                                 message: CharSequence? = null,
                                 editTextListener: OnConfirmClickedListener,
                                 buttonLabel: CharSequence,
                                 isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, message = message,
                isEditText = true,
                confirmListener = editTextListener,
                isButtonLayout = true, primaryLabel = buttonLabel,
                isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 1개의 버튼을 가지는
     * 리스트형 다이얼로그
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param adapter
     * @param buttonClickedListener 리스트 선택 항목 리턴.
     * @param buttonLabel
     * @param isBackKey
     */
    fun showOneButtonListDialog(title: CharSequence? = null,
                                adapter: RecyclerView.Adapter<*>,
                                buttonClickedListener: OnItemClickedListener,
                                buttonLabel: CharSequence,
                                isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, adapter = adapter, itemClickedListener = buttonClickedListener,
                isButtonLayout = true, primaryLabel = buttonLabel, isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 2개의 버튼을 가지는 다이얼로그 보기
     * 2개의 버튼에 대한 레이블도 변경함.
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param message
     * @param primaryLabel 좌측버튼
     * @param primaryListener
     * @param secondaryLabel 우측버튼
     * @param secondaryListener
     * @param isBackKey
     */
    fun showTwoButtonDialog(title: CharSequence? = null,
                            message: CharSequence,
                            primaryLabel: CharSequence,
                            primaryListener: DialogInterface.OnDismissListener? = null,
                            secondaryLabel: CharSequence,
                            secondaryListener: DialogInterface.OnDismissListener,
                            isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, message = message,
                isButtonLayout = true,
                primaryLabel = primaryLabel, primaryListener = primaryListener,
                secondaryLabel = secondaryLabel, secondaryListener = secondaryListener,
                isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 2개의 버튼, 입력폼을 가지는
     * 2개의 버튼에 대한 레이블도 변경함.
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param message
     * @param primaryLabel 좌측버튼
     * @param primaryListener
     * @param secondaryLabel 우측버튼
     * @param secondaryListener 입력폼의 값을 전달함.
     * @param isBackKey
     */
    fun showTwoButtonInputDialog(title: CharSequence,
                                 message: CharSequence? = null,
                                 primaryLabel: CharSequence,
                                 primaryListener: DialogInterface.OnDismissListener? = null,
                                 secondaryLabel: CharSequence,
                                 secondaryListener: OnConfirmClickedListener,
                                 isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, message = message,
                isEditText = true, confirmListener = secondaryListener,
                isButtonLayout = true,
                primaryLabel = primaryLabel, primaryListener = primaryListener,
                secondaryLabel = secondaryLabel,
                isBackKey = isBackKey)
        dialog?.show()
    }

    /**
     * 2개의 버튼 리스트형 다이얼로그 보기
     * 2개의 버튼에 대한 레이블도 변경함.
     * BackKey 사용시, 다이얼로그 그냥 닫힘.
     *
     * @param title
     * @param adapter
     * @param primaryLabel 좌측버튼
     * @param primaryListener
     * @param secondaryLabel 우측버튼
     * @param secondaryListener 리스트 선택 항목 리턴.
     * @param isBackKey
     */
    fun showTwoButtonListDialog(title: CharSequence? = null,
                                adapter: RecyclerView.Adapter<*>,
                                primaryLabel: CharSequence,
                                primaryListener: DialogInterface.OnDismissListener? = null,
                                secondaryLabel: CharSequence,
                                secondaryListener: OnItemClickedListener,
                                isBackKey: Boolean = false) {
        hideAllDialog()
        if (mParentActivity.isFinishing) return

        dialog = CommonDialog(context = mContext,
                title = title, adapter = adapter, itemClickedListener = secondaryListener,
                isButtonLayout = true,
                primaryLabel = primaryLabel, primaryListener = primaryListener,
                secondaryLabel = secondaryLabel,
                isBackKey = isBackKey)
        dialog?.show()
    }
}