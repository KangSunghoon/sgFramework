package com.ksfams.sgframework.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ksfams.sgframework.databinding.DialogBinding
import com.ksfams.sgframework.extensions.*
import com.ksfams.sgframework.ui.dialog.adapter.DialogAdapter
import com.ksfams.sgframework.ui.dialog.builder.DialogBuilder
import com.ksfams.sgframework.ui.dialog.listener.OnConfirmClickedListener
import com.ksfams.sgframework.ui.dialog.listener.OnItemClickedListener
import com.ksfams.sgframework.utils.dpToPixel

/**
 *
 * Dialog 처리
 *
 * @property title: optional Dialog Title Text
 * @property message: optional Dialog Message Text.
 * @property adapter: 리스트에 표시될 Text List Adaptor (DialogAdapter 상속 처리)
 * @property itemClickedListener: 선택된 리스트에 대한 리턴 처리 listener
 * @property isEditText: 입력폼의 존재여부 (true: 입력폼 존재함.)  TODO dynamic custom 처리 필요함.
 * @property confirmListener: EditText 입력폼에 대한 입력값 리턴 처리 listener
 * @property isButtonLayout: 버튼 레이아웃 표시 여부 (false: 모든 버튼을 숨김)
 * @property primaryLabel: 좌측 기본 버튼에 대한 label Text
 * @property primaryListener: 좌측 기본 버튼에 대한 listener
 * @property secondaryLabel: 우측 옵션 버튼에 대한 label Text
 * @property secondaryListener: 우측 옵션 버튼에 대한 listener
 * @property isBackKey: Back key 처리 시, Dialog dismiss 처리 여부 (false: Back key 시에도 Dialog 닫히지 않도록 함.)
 *
 * Create Date  12/21/20
 * @version 1.00    12/21/20
 * @since   1.00
 * @see
 * @author  강성훈(ssogaree@gmail.com)
 * Revision History
 * who      when            what
 * 강성훈      12/21/20     신규 개발.
 */

internal class CommonDialog(context: Context,
                            val title: CharSequence? = null,
                            val message: CharSequence? = null,
                            val adapter: RecyclerView.Adapter<*>? = null,
                            val itemClickedListener: OnItemClickedListener? = null,
                            val isEditText: Boolean = false,
                            val confirmListener: OnConfirmClickedListener? = null,
                            val isButtonLayout: Boolean,
                            val primaryLabel: CharSequence? = null,
                            val primaryListener: DialogInterface.OnDismissListener? = null,
                            val secondaryLabel: CharSequence? = null,
                            val secondaryListener: DialogInterface.OnDismissListener? = null,
                            val isBackKey: Boolean) : Dialog(context) {

    // 바인딩
    private lateinit var binding: DialogBinding

    // Dialog Manager config
    private val dialogConfig = DialogBuilder.getConfig()

    private fun initLayout() {
        binding = DialogBinding.inflate(LayoutInflater.from(context))

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setDimAmount(dialogConfig.systemDim)

        setCancelable(false)

        // 레이아웃 처리
        setContentView(binding.root)

        // 기본 레이아웃 정의
        val dialogLayout = binding.dialog
        val layoutParam = dialogLayout.layoutParams
        layoutParam.width = context.dpToPixel(dialogConfig.windowWidth).toInt()
        binding.dialog.setBgDrawable(dialogConfig.windowBackground)

        // 타이틀 처리
        title?.let {
            binding.title.text = it
            binding.title.textSize = context.dpToPixel(dialogConfig.titleTextSize)
            binding.title.setTextColor(context.color(dialogConfig.titleColor))
            binding.title.setTypeface(null, dialogConfig.titleTypeface)
            binding.title.letterSpacing = context.dpToPixel(dialogConfig.titleLetterSpacing)
            binding.title.gravity = dialogConfig.textAlignment
            binding.title.topMargin = context.dpToPixel(dialogConfig.titleTopSpacing).toInt()
            binding.title.leftMargin = context.dpToPixel(dialogConfig.titleHorizontalSpacing).toInt()
            binding.title.rightMargin = context.dpToPixel(dialogConfig.titleHorizontalSpacing).toInt()
            binding.title.bottomMargin = context.dpToPixel(dialogConfig.titleBottomSpacing).toInt()
            binding.titleBackground.visible()
        }

        // 타이틀 구분선 처리
        if (dialogConfig.titleLineHeight != null && dialogConfig.titleLineColor != null) {
            val titleDividerLayout = binding.titleDivider
            val titleDividerLayoutParam = titleDividerLayout.layoutParams
            titleDividerLayoutParam.height = context.dpToPixel(dialogConfig.titleLineHeight).toInt()
            titleDividerLayout.setBackgroundColor(context.color(dialogConfig.titleLineColor))
            titleDividerLayout.bottomMargin = context.dpToPixel(dialogConfig.titleLineBottomPadding).toInt()
            titleDividerLayout.leftMargin = context.dpToPixel(dialogConfig.titleLineHorizontalPadding ?: 0).toInt()
            titleDividerLayout.rightMargin = context.dpToPixel(dialogConfig.titleLineHorizontalPadding ?: 0).toInt()
            titleDividerLayout.visible()
        }

        // 메시지 처리
        message?.let {
            binding.message.text = it
            binding.message.textSize = context.dpToPixel(dialogConfig.messageTextSize)
            binding.message.setTextColor(context.color(dialogConfig.messageColor))
            binding.message.setTypeface(null, dialogConfig.messageTypeface)
            binding.message.letterSpacing = context.dpToPixel(dialogConfig.messageLetterSpacing)
            binding.message.maxLines = dialogConfig.messageLineLimit
            binding.message.gravity = dialogConfig.textAlignment
            binding.message.topMargin = context.dpToPixel(dialogConfig.messageTopSpacing).toInt()
            binding.message.leftMargin = context.dpToPixel(dialogConfig.messageHorizontalSpacing).toInt()
            binding.message.rightMargin = context.dpToPixel(dialogConfig.messageHorizontalSpacing).toInt()
            binding.message.visible()
        }

        // 리스트 처리
        adapter?.let {
            binding.contentListview.adapter = it
            binding.contentListview.layoutManager = LinearLayoutManager(context)
            binding.contentListview.visible()

            if (it is DialogAdapter) {
                (it as DialogAdapter).itemClickedListener = itemClickedListener
            }
        }

        // 입력폼 처리
        if (isEditText) {
            // 키패드가 가리지 않도록 함.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window!!.setDecorFitsSystemWindows(true)
            } else {
                val winLp = window!!.attributes
                @Suppress("DEPRECATION")
                winLp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                window!!.attributes = winLp
            }

            binding.input.addTextChangedListener(afterTextChanged = {
                if (binding.input.value.isNotEmpty()) {
                    binding.inputClear.visible()
                } else {
                    binding.inputClear.invisible()
                }
            })

            binding.input.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    this.onClick()
                }
                false
            }

            binding.inputClear.setOnClickListener {
                binding.input.setText("")
            }
        }

        // 버튼 레이아웃 처리
        if (isButtonLayout) {
            val buttonLayout = binding.buttonLayout
            val buttonLayoutParam = buttonLayout.layoutParams
            buttonLayoutParam.height = context.dpToPixel(dialogConfig.buttonHeight).toInt()
            buttonLayout.topMargin = context.dpToPixel(dialogConfig.buttonTopSpacing).toInt()
            buttonLayout.visible()

            // 버튼 레이아웃 구분선
            if (dialogConfig.buttonLineHeight != null && dialogConfig.buttonLineColor != null) {
                val buttonDividerLayout = binding.buttonDivider
                val buttonDividerLayoutParam = buttonDividerLayout.layoutParams
                buttonDividerLayoutParam.height = context.dpToPixel(dialogConfig.buttonLineHeight).toInt()
                buttonDividerLayout.setBackgroundColor(context.color(dialogConfig.buttonLineColor))
                buttonDividerLayout.visible()
            }

            // 좌측 버튼
            primaryLabel?.let { label ->
                binding.primary.text = label
                binding.primary.textSize = context.dpToPixel(dialogConfig.primaryButtonTextSize)
                binding.primary.setTextColor(context.color(dialogConfig.primaryButtonTextColor))
                binding.primary.setTypeface(null, dialogConfig.primaryButtonTextTypeface)
                binding.primary.letterSpacing = context.dpToPixel(dialogConfig.primaryButtonTextLetterSpacing)
                binding.primary.setBgDrawable(dialogConfig.primaryButtonSelector)

                // 1개의 버튼이고 리스트/입력폼 형식인 경우, listener 처리를 구분한다.
                if (secondaryLabel == null) {
                    if (isEditText) {
                        binding.primary.setOnClickListener {
                            onClick()
                        }
                    } else if (adapter != null && adapter is DialogAdapter) {
                        binding.primary.setOnClickListener {
                            itemClickedListener?.onClicked(it, -1, (adapter as DialogAdapter).getSelectedItemPositions())
                            dismiss()
                        }
                    } else {
                        primaryListener?.let { listener ->
                            binding.primary.setOnClickListener {
                                setOnDismissListener(listener)
                                dismiss()
                            }
                        }
                    }
                } else {
                    primaryListener?.let { listener ->
                        binding.primary.setOnClickListener {
                            setOnDismissListener(listener)
                            dismiss()
                        }
                    }
                }
            }

            // 버튼 간의 divider
            dialogConfig.buttonVerticalLineColor?.let {
                val buttonVerticalDivider = binding.buttonVerticalDivider
                val buttonVerticalDividerParam = buttonVerticalDivider.layoutParams
                buttonVerticalDividerParam.width = context.dpToPixel(dialogConfig.buttonVerticalLineWidth ?: 0).toInt()
                buttonVerticalDivider.setBackgroundColor(context.color(it))
                buttonVerticalDivider.topMargin = context.dpToPixel(dialogConfig.buttonVerticalLinePadding ?: 0).toInt()
                buttonVerticalDivider.bottomMargin = context.dpToPixel(dialogConfig.buttonVerticalLinePadding ?: 0).toInt()
                buttonVerticalDivider.visible()
            }

            // 우측 버튼
            secondaryLabel?.let { label ->
                binding.secondary.text = label
                binding.secondary.textSize = context.dpToPixel(dialogConfig.secondaryButtonTextSize)
                binding.secondary.setTextColor(context.color(dialogConfig.secondaryButtonTextColor))
                binding.secondary.setTypeface(null, dialogConfig.secondaryButtonTextTypeface)
                binding.secondary.letterSpacing = context.dpToPixel(dialogConfig.secondaryButtonTextLetterSpacing)
                binding.secondary.setBgDrawable(dialogConfig.secondaryButtonSelector)
                binding.secondary.visible()

                // 리스트/입력폼 형식인 경우, listener 처리를 구분한다.
                if (adapter != null && adapter is DialogAdapter) {
                    binding.secondary.setOnClickListener {
                        itemClickedListener?.onClicked(it, -1, (adapter as DialogAdapter).getSelectedItemPositions())
                        dismiss()
                    }
                }
                else if (isEditText) {
                    binding.secondary.setOnClickListener {
                        onClick()
                    }
                }
                else {
                    secondaryListener?.let { listener ->
                        binding.secondary.setOnClickListener {
                            setOnDismissListener(listener)
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (isBackKey) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    primaryListener?.let {
                        setOnDismissListener(it)
                    }
                    dismiss()
                }
            }
            super.onKeyDown(keyCode, event)
        } else {
            true
        }
    }

    /**
     * 입력값 전달.
     */
    private fun onClick() {
        // 비밀번호 입력값을 넘겨 준다.
        confirmListener?.let {
            it.onClicked(binding.input.value)
            dismiss()
        }
    }

    init {
        this.initLayout()
    }
}