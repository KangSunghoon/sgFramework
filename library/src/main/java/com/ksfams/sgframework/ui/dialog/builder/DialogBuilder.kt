package com.ksfams.sgframework.ui.dialog.builder

import android.graphics.Typeface
import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.ksfams.sgframework.R
import com.ksfams.sgframework.constants.LINE_SEP

/**
 *
 * Dialog 설정 Builder
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

object DialogBuilder {

    private var config: DialogBuilder.Config

    init {
        config = DialogBuilder.Config()
    }

    /**
     * init
     *
     * @param config Config
     */
    @JvmStatic
    fun init(config: DialogBuilder.Config) {
        this.config = config
    }

    /**
     * get config data
     *
     * @return Config
     */
    fun getConfig(): DialogBuilder.Config = this.config


    /**
     * Kotlin Builder Pattern
     * 예) val config = DialogManager.ConfigBuilder().setSystemDim(0.5f).build()
     *
     * @property systemDim: Dialog popup 외부 background Dim 값
     * @property windowWidth: Dialog width size. Default is `280dp`
     * @property windowBackground: Dialog background Drawable resource
     * @property textAlignment: text 정렬. Default is `Gravity.CENTER`
     * @property titleBackgroundColor: title 영역 배경 색상. Default is `R.color.RGB_FFFFFFFF`
     * @property titleTopSpacing: title text top padding. title 이 없을 경우, message top padding 값으로 사용됨. Default is `13dp`
     * @property titleBottomSpacing: title text bottom padding. Default is `18dp`
     * @property titleHorizontalSpacing: title text horizontal padding. Default is `25dp`
     * @property titleTextSize: title text size. Default is `15dp`
     * @property titleColor: title text color resource. Default is `R.color.RGB_FF232323`
     * @property titleTypeface: title text Font Typeface. Default is `Typeface.BOLD`
     * @property titleLetterSpacing: optional title text 글자 간격
     * @property titleLineColor: optional title 구분 라인 색상
     * @property titleLineHeight: optional title 구분 라인 크기
     * @property titleLineBottomPadding: optional title 구분 라인과 하단 레이아웃 간의 padding 크기. Default is `5dp`
     * @property titleLineHorizontalPadding: optional title 구분 라인의 horizontal padding 크기
     * @property messageTopSpacing: message top padding. title 이 없을 경우, titleTopSpacing 값으로 사용됨. . Default is `0dp`
     * @property messageHorizontalSpacing: message horizontal padding. Default is `25dp`
     * @property messageLineLimit: message limit line. Default is `2`
     * @property messageTextSize: message Text size. Default is `14dp`
     * @property messageColor: message text Color resource. Default is `R.color.RGB_FF232323`
     * @property messageTypeface: message text Font Typeface. Default is `Typeface.BOLD`
     * @property messageLetterSpacing: optional message text 글자 간격
     * @property buttonHeight: 버튼 레이아웃 height
     * @property buttonTopSpacing: 버튼 레이아웃과 message text view 간의 top padding. Default is `15dp`
     * @property buttonLineColor: optional body와 button layout 구분 라인 색상
     * @property buttonLineHeight: optional body와 button layout 구분 라인 크기
     * @property primaryButtonSelector: primary Button background Selector resource
     * @property primaryButtonTextSize: primary Button text size. Default is `14dp`
     * @property primaryButtonTextColor: primary Button text Color resource
     * @property primaryButtonTextTypeface: primary Button text Font Typeface. Default is `Typeface.BOLD`
     * @property primaryButtonTextLetterSpacing: optional primary Button text 글자 간격
     * @property buttonVerticalLineColor: optional button 간의 구분 라인 색상
     * @property buttonVerticalLineWidth: optional button 간의 구분 라인 크기
     * @property buttonVerticalLinePadding: optional button 간의 구분 라인의 vertical padding 크기
     * @property secondaryButtonTextSize: secondary Button text size. Default is `15dp`
     * @property secondaryButtonSelector: secondary Button background Selector resource
     * @property secondaryButtonTextColor: secondary Button text Color resource
     * @property secondaryButtonTextTypeface: secondary Button text Font Typeface. Default is `Typeface.BOLD`
     * @property secondaryButtonTextLetterSpacing: secondary Button text 글자 간격
     */
    class Config internal constructor(val systemDim: Float = 0.7f,
                                      val windowWidth: Int = 280,
                                      @DrawableRes val windowBackground: Int = R.drawable.dialog_rounded,
                                      val textAlignment: Int = Gravity.CENTER,
                                      @ColorRes val titleBackgroundColor: Int = R.color.RGB_FFFFFFFF,
                                      val titleTopSpacing: Int = 13,
                                      val titleBottomSpacing: Int = 18,
                                      val titleHorizontalSpacing: Int = 25,
                                      val titleTextSize: Int = 15,
                                      @ColorRes val titleColor: Int = R.color.RGB_FF232323,
                                      val titleTypeface: Int = Typeface.BOLD,
                                      val titleLetterSpacing: Float = 0f,
                                      @ColorRes val titleLineColor: Int? = null,
                                      val titleLineHeight: Int? = null,
                                      val titleLineBottomPadding: Int = 5,
                                      val titleLineHorizontalPadding: Int? = null,
                                      val messageTopSpacing: Int = 0,
                                      val messageHorizontalSpacing: Int = 25,
                                      val messageLineLimit: Int = 2,
                                      val messageTextSize: Int = 14,
                                      @ColorRes val messageColor: Int = R.color.RGB_FF232323,
                                      val messageTypeface: Int = Typeface.BOLD,
                                      val messageLetterSpacing: Float = 0f,
                                      val buttonHeight: Int = 44,
                                      val buttonTopSpacing: Int = 14,
                                      @ColorRes val buttonLineColor: Int? = null,
                                      val buttonLineHeight: Int? = null,
                                      @DrawableRes val primaryButtonSelector: Int = R.drawable.btn_primary_selector,
                                      val primaryButtonTextSize: Int = 15,
                                      @ColorRes val primaryButtonTextColor: Int = R.color.RGB_FFFFFFFF,
                                      val primaryButtonTextTypeface: Int = Typeface.BOLD,
                                      val primaryButtonTextLetterSpacing: Float = 0f,
                                      @ColorRes val buttonVerticalLineColor: Int? = null,
                                      val buttonVerticalLineWidth: Int? = null,
                                      val buttonVerticalLinePadding: Int? = null,
                                      @DrawableRes val secondaryButtonSelector: Int = R.drawable.btn_secondary_selector,
                                      val secondaryButtonTextSize: Int = 15,
                                      @ColorRes val secondaryButtonTextColor: Int = R.color.RGB_FFFFFFFF,
                                      val secondaryButtonTextTypeface: Int = Typeface.BOLD,
                                      val secondaryButtonTextLetterSpacing: Float = 0f) {

        /**
         * 설정된 Config Builder 값
         *
         * @return ConfigBuilder
         */
        fun getBuilder(): ConfigBuilder {
            return ConfigBuilder(systemDim, windowWidth, windowBackground, textAlignment,
                    titleBackgroundColor, titleTopSpacing, titleBottomSpacing, titleHorizontalSpacing, titleTextSize, titleColor, titleTypeface, titleLetterSpacing, titleLineColor, titleLineHeight, titleLineBottomPadding, titleLineHorizontalPadding,
                    messageTopSpacing, messageHorizontalSpacing, messageLineLimit, messageTextSize, messageColor, messageTypeface, messageLetterSpacing,
                    buttonHeight, buttonTopSpacing, buttonLineColor, buttonLineHeight,
                    primaryButtonSelector, primaryButtonTextSize, primaryButtonTextColor, primaryButtonTextTypeface, primaryButtonTextLetterSpacing,
                    buttonVerticalLineColor, buttonVerticalLineWidth, buttonVerticalLinePadding,
                    secondaryButtonSelector, secondaryButtonTextSize, secondaryButtonTextColor, secondaryButtonTextTypeface, secondaryButtonTextLetterSpacing)
        }

        override fun toString(): String {
            return ("systemDim: " + systemDim.toString()
                    + LINE_SEP + "windowWidth: " + windowWidth.toString()
                    + LINE_SEP + "windowBackground: " + windowBackground.toString()
                    + LINE_SEP + "textAlignment: " + textAlignment.toString()
                    + LINE_SEP + "titleBackgroundColor: " + titleBackgroundColor.toString()
                    + LINE_SEP + "titleTopSpacing: " + titleTopSpacing.toString()
                    + LINE_SEP + "titleBottomSpacing: " + titleBottomSpacing.toString()
                    + LINE_SEP + "titleHorizontalSpacing: " + titleHorizontalSpacing.toString()
                    + LINE_SEP + "titleTextSize: " + titleTextSize.toString()
                    + LINE_SEP + "titleColor: " + titleColor.toString()
                    + LINE_SEP + "titleTypeface: " + titleTypeface.toString()
                    + LINE_SEP + "titleLetterSpacing: " + titleLetterSpacing.toString()
                    + LINE_SEP + "titleLineColor: " + titleLineColor.toString()
                    + LINE_SEP + "titleLineHeight: " + titleLineHeight.toString()
                    + LINE_SEP + "titleLineBottomPadding: " + titleLineBottomPadding.toString()
                    + LINE_SEP + "titleLineHorizontalPadding: " + titleLineHorizontalPadding.toString()
                    + LINE_SEP + "messageTopSpacing: " + messageTopSpacing.toString()
                    + LINE_SEP + "messageHorizontalSpacing: " + messageHorizontalSpacing.toString()
                    + LINE_SEP + "messageLineLimit: " + messageLineLimit.toString()
                    + LINE_SEP + "messageTextSize: " + messageTextSize.toString()
                    + LINE_SEP + "messageColor: " + messageColor.toString()
                    + LINE_SEP + "messageTypeface: " + messageTypeface.toString()
                    + LINE_SEP + "messageLetterSpacing: " + messageLetterSpacing.toString()
                    + LINE_SEP + "buttonHeight: " + buttonHeight.toString()
                    + LINE_SEP + "buttonTopSpacing: " + buttonTopSpacing.toString()
                    + LINE_SEP + "buttonLineColor: " + buttonLineColor.toString()
                    + LINE_SEP + "buttonLineHeight: " + buttonLineHeight.toString()
                    + LINE_SEP + "primaryButtonSelector: " + primaryButtonSelector.toString()
                    + LINE_SEP + "primaryButtonTextSize: " + primaryButtonTextSize.toString()
                    + LINE_SEP + "primaryButtonTextColor: " + primaryButtonTextColor.toString()
                    + LINE_SEP + "primaryButtonTextTypeface: " + primaryButtonTextTypeface.toString()
                    + LINE_SEP + "primaryButtonTextLetterSpacing: " + primaryButtonTextLetterSpacing.toString()
                    + LINE_SEP + "buttonVerticalLineColor: " + buttonVerticalLineColor.toString()
                    + LINE_SEP + "buttonVerticalLineWidth: " + buttonVerticalLineWidth.toString()
                    + LINE_SEP + "buttonVerticalLinePadding: " + buttonVerticalLinePadding.toString()
                    + LINE_SEP + "secondaryButtonSelector: " + secondaryButtonSelector.toString()
                    + LINE_SEP + "secondaryButtonTextSize: " + secondaryButtonTextSize.toString()
                    + LINE_SEP + "secondaryButtonTextColor: " + secondaryButtonTextColor.toString()
                    + LINE_SEP + "secondaryButtonTextTypeface: " + secondaryButtonTextTypeface.toString()
                    + LINE_SEP + "secondaryButtonTextLetterSpacing: " + secondaryButtonTextLetterSpacing.toString())
        }
    }

    /**
     * Config Builder
     *
     * @property systemDim: Dialog popup 외부 background Dim 값
     * @property windowWidth: Dialog width size. Default is `280dp`
     * @property windowBackground: Dialog background Drawable resource
     * @property textAlignment: text 정렬. Default is `Gravity.CENTER`
     * @property titleBackgroundColor: title 영역 배경 색상. Default is `R.color.RGB_FFFFFFFF`
     * @property titleTopSpacing: title text top padding. title 이 없을 경우, message top padding 값으로 사용됨. Default is `13dp`
     * @property titleBottomSpacing: title text bottom padding. Default is `18dp`
     * @property titleHorizontalSpacing: title text horizontal padding. Default is `25dp`
     * @property titleTextSize: title text size. Default is `15dp`
     * @property titleColor: title text color resource. Default is `R.color.RGB_FF232323`
     * @property titleTypeface: title text Font Typeface. Default is `Typeface.BOLD`
     * @property titleLetterSpacing: optional title text 글자 간격
     * @property titleLineColor: optional title 구분 라인 색상
     * @property titleLineHeight: optional title 구분 라인 크기
     * @property titleLineBottomPadding: optional title 구분 라인과 하단 레이아웃 간의 padding 크기. Default is `5dp`
     * @property titleLineHorizontalPadding: optional title 구분 라인의 horizontal padding 크기
     * @property messageTopSpacing: message top padding. title 이 없을 경우, titleTopSpacing 값으로 사용됨. . Default is `0dp`
     * @property messageHorizontalSpacing: message horizontal padding. Default is `25dp`
     * @property messageLineLimit: message limit line. Default is `2`
     * @property messageTextSize: message Text size. Default is `14dp`
     * @property messageColor: message text Color resource. Default is `R.color.RGB_FF232323`
     * @property messageTypeface: message text Font Typeface. Default is `Typeface.BOLD`
     * @property messageLetterSpacing: optional message text 글자 간격
     * @property buttonHeight: 버튼 레이아웃 height
     * @property buttonTopSpacing: 버튼 레이아웃과 message text view 간의 top padding. Default is `15dp`
     * @property buttonLineColor: optional body와 button layout 구분 라인 색상
     * @property buttonLineHeight: optional body와 button layout 구분 라인 크기
     * @property primaryButtonSelector: primary Button background Selector resource
     * @property primaryButtonTextSize: primary Button text size. Default is `14dp`
     * @property primaryButtonTextColor: primary Button text Color resource
     * @property primaryButtonTextTypeface: primary Button text Font Typeface. Default is `Typeface.BOLD`
     * @property primaryButtonTextLetterSpacing: optional primary Button text 글자 간격
     * @property buttonVerticalLineColor: optional button 간의 구분 라인 색상
     * @property buttonVerticalLineWidth: optional button 간의 구분 라인 크기
     * @property buttonVerticalLinePadding: optional button 간의 구분 라인의 vertical padding 크기
     * @property secondaryButtonTextSize: secondary Button text size. Default is `15dp`
     * @property secondaryButtonSelector: secondary Button background Selector resource
     * @property secondaryButtonTextColor: secondary Button text Color resource
     * @property secondaryButtonTextTypeface: secondary Button text Font Typeface. Default is `Typeface.BOLD`
     * @property secondaryButtonTextLetterSpacing: secondary Button text 글자 간격
     */
    data class ConfigBuilder(var systemDim: Float = 0.7f,
                             var windowWidth: Int = 280,
                             @DrawableRes var windowBackground: Int = R.drawable.dialog_rounded,
                             var textAlignment: Int = Gravity.CENTER,
                             @ColorRes var titleBackgroundColor: Int = R.color.RGB_FFFFFFFF,
                             var titleTopSpacing: Int = 13,
                             var titleBottomSpacing: Int = 18,
                             var titleHorizontalSpacing: Int = 25,
                             var titleTextSize: Int = 15,
                             @ColorRes var titleColor: Int = R.color.RGB_FF232323,
                             var titleTypeface: Int = Typeface.BOLD,
                             var titleLetterSpacing: Float = 0f,
                             @ColorRes var titleLineColor: Int? = null,
                             var titleLineHeight: Int? = null,
                             var titleLineBottomPadding: Int = 5,
                             var titleLineHorizontalPadding: Int? = null,
                             var messageTopSpacing: Int = 0,
                             var messageHorizontalSpacing: Int = 25,
                             var messageLineLimit: Int = 2,
                             var messageTextSize: Int = 14,
                             @ColorRes var messageColor: Int = R.color.RGB_FF232323,
                             var messageTypeface: Int = Typeface.BOLD,
                             var messageLetterSpacing: Float = 0f,
                             var buttonHeight: Int = 44,
                             var buttonTopSpacing: Int = 14,
                             @ColorRes var buttonLineColor: Int? = null,
                             var buttonLineHeight: Int? = null,
                             @DrawableRes var primaryButtonSelector: Int = R.drawable.btn_primary_selector,
                             var primaryButtonTextSize: Int = 15,
                             @ColorRes var primaryButtonTextColor: Int = R.color.RGB_FFFFFFFF,
                             var primaryButtonTextTypeface: Int = Typeface.BOLD,
                             var primaryButtonTextLetterSpacing: Float = 0f,
                             @ColorRes var buttonVerticalLineColor: Int? = null,
                             var buttonVerticalLineWidth: Int? = null,
                             var buttonVerticalLinePadding: Int? = null,
                             @DrawableRes var secondaryButtonSelector: Int = R.drawable.btn_secondary_selector,
                             var secondaryButtonTextSize: Int = 15,
                             @ColorRes var secondaryButtonTextColor: Int = R.color.RGB_FFFFFFFF,
                             var secondaryButtonTextTypeface: Int = Typeface.BOLD,
                             var secondaryButtonTextLetterSpacing: Float = 0f) {

        fun setSystemDim(systemDim: Float) = apply { this.systemDim = systemDim }
        fun setWindowWidth(windowWidth: Int) = apply { this.windowWidth = windowWidth }
        fun setWindowBackground(windowBackground: Int) = apply { this.windowBackground = windowBackground }
        fun setTextAlignment(textAlignment: Int) = apply { this.textAlignment = textAlignment }
        fun setTitleBackgroundColor(titleBackgroundColor: Int) = apply { this.titleBackgroundColor = titleBackgroundColor }
        fun setTitleTopSpacing(titleTopSpacing: Int) = apply { this.titleTopSpacing = titleTopSpacing }
        fun setTitleBottomSpacing(titleBottomSpacing: Int) = apply { this.titleBottomSpacing = titleBottomSpacing }
        fun setTitleHorizontalSpacing(titleHorizontalSpacing: Int) = apply { this.titleHorizontalSpacing = titleHorizontalSpacing }
        fun setTitleTextSize(titleTextSize: Int) = apply { this.titleTextSize = titleTextSize }
        fun setTitleColor(titleColor: Int) = apply { this.titleColor = titleColor }
        fun setTitleTypeface(titleTypeface: Int) = apply { this.titleTypeface = titleTypeface }
        fun setTitleLetterSpacing(titleLetterSpacing: Float) = apply { this.titleLetterSpacing = titleLetterSpacing }
        fun seTitleLineColor(titleLineColor: Int) = apply { this.titleLineColor = titleLineColor }
        fun setTitleLineHeight(titleLineHeight: Int) = apply { this.titleLineHeight = titleLineHeight }
        fun setTitleLineBottomPadding(titleLineBottomPadding: Int) = apply { this.titleLineBottomPadding = titleLineBottomPadding }
        fun setTitleLineHorizontalPadding(titleLineHorizontalPadding: Int) = apply { this.titleLineHorizontalPadding = titleLineHorizontalPadding }
        fun setMessageTopSpacing(messageTopSpacing: Int) = apply { this.messageTopSpacing = messageTopSpacing }
        fun setMessageHorizontalSpacing(messageHorizontalSpacing: Int) = apply { this.messageHorizontalSpacing = messageHorizontalSpacing }
        fun setMessageLineLimit(messageLineLimit: Int) = apply { this.messageLineLimit = messageLineLimit }
        fun setMessageTextSize(messageTextSize: Int) = apply { this.messageTextSize = messageTextSize }
        fun setMessageColor(messageColor: Int) = apply { this.messageColor = messageColor }
        fun setMessageTypeface(messageTypeface: Int) = apply { this.messageTypeface = messageTypeface }
        fun setMessageLetterSpacing(messageLetterSpacing: Float) = apply { this.messageLetterSpacing = messageLetterSpacing }
        fun setButtonHeight(buttonHeight: Int) = apply { this.buttonHeight = buttonHeight }
        fun setButtonTopSpacing(buttonTopSpacing: Int) = apply { this.buttonTopSpacing = buttonTopSpacing }
        fun setButtonLineColor(buttonLineColor: Int) = apply { this.buttonLineColor = buttonLineColor }
        fun setButtonLineHeight(buttonLineHeight: Int) = apply { this.buttonLineHeight = buttonLineHeight }
        fun setPrimaryButtonSelector(primaryButtonSelector: Int) = apply { this.primaryButtonSelector = primaryButtonSelector }
        fun setPrimaryButtonTextSize(primaryButtonTextSize: Int) = apply { this.primaryButtonTextSize = primaryButtonTextSize }
        fun setPrimaryButtonTextColor(primaryButtonTextColor: Int) = apply { this.primaryButtonTextColor = primaryButtonTextColor }
        fun setPrimaryButtonTextTypeface(primaryButtonTextTypeface: Int) = apply { this.primaryButtonTextTypeface = primaryButtonTextTypeface }
        fun setPrimaryButtonTextLetterSpacing(primaryButtonTextLetterSpacing: Float) = apply { this.primaryButtonTextLetterSpacing = primaryButtonTextLetterSpacing }
        fun setButtonVerticalLineColor(buttonVerticalLineColor: Int) = apply { this.buttonVerticalLineColor = buttonVerticalLineColor }
        fun setButtonVerticalLineWidth(buttonVerticalLineWidth: Int) = apply { this.buttonVerticalLineWidth = buttonVerticalLineWidth }
        fun setButtonVerticalLinePadding(buttonVerticalLinePadding: Int) = apply { this.buttonVerticalLinePadding = buttonVerticalLinePadding }
        fun setSecondaryButtonSelector(secondaryButtonSelector: Int) = apply { this.secondaryButtonSelector = secondaryButtonSelector }
        fun setSecondaryButtonTextSize(secondaryButtonTextSize: Int) = apply { this.secondaryButtonTextSize = secondaryButtonTextSize }
        fun setSecondaryButtonTextColor(secondaryButtonTextColor: Int) = apply { this.secondaryButtonTextColor = secondaryButtonTextColor }
        fun setSecondaryButtonTextTypeface(secondaryButtonTextTypeface: Int) = apply { this.secondaryButtonTextTypeface = secondaryButtonTextTypeface }
        fun setSecondaryButtonTextLetterSpacing(secondaryButtonTextLetterSpacing: Float) = apply { this.secondaryButtonTextLetterSpacing = secondaryButtonTextLetterSpacing }

        fun build() = Config(systemDim, windowWidth, windowBackground, textAlignment,
                titleBackgroundColor, titleTopSpacing, titleBottomSpacing, titleHorizontalSpacing, titleTextSize, titleColor, titleTypeface, titleLetterSpacing, titleLineColor, titleLineHeight, titleLineBottomPadding, titleLineHorizontalPadding,
                messageTopSpacing, messageHorizontalSpacing, messageLineLimit, messageTextSize, messageColor, messageTypeface, messageLetterSpacing,
                buttonHeight, buttonTopSpacing, buttonLineColor, buttonLineHeight,
                primaryButtonSelector, primaryButtonTextSize, primaryButtonTextColor, primaryButtonTextTypeface, primaryButtonTextLetterSpacing,
                buttonVerticalLineColor, buttonVerticalLineWidth, buttonVerticalLinePadding,
                secondaryButtonSelector, secondaryButtonTextSize, secondaryButtonTextColor, secondaryButtonTextTypeface, secondaryButtonTextLetterSpacing)
    }
}