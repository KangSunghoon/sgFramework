package com.ksfams.sgframework.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import com.bumptech.glide.Glide
import com.ksfams.sgframework.R
import com.ksfams.sgframework.databinding.DialogLoadingBinding
import com.ksfams.sgframework.extensions.visible

/**
 *
 * Loading Progress Dialog
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

internal class LoadingDialog(context: Context,
                             val message: CharSequence?,
                             private val isBackKey: Boolean = false) : Dialog(context, R.style.AppTheme_Dialog) {

    // 바인딩
    private lateinit var binding: DialogLoadingBinding

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (isBackKey) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> dismiss()
            }
            super.onKeyDown(keyCode, event)
        } else {
            true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    private fun initLayout() {
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setDimAmount(0.2f)

        setCancelable(false)

        setContentView(binding.root)

        // gif 애니메이션 사용
        Glide.with(context)
                .load(R.drawable.loading_progress)
                .into(binding.spinnerImageView)

        message?.let {
            binding.progressMessage.text = it
            binding.progressMessage.visible()
        }
    }

    init {
        this.initLayout()
    }
}
