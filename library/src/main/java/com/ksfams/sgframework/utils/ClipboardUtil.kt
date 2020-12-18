package com.ksfams.sgframework.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import com.ksfams.sgframework.extensions.string
import com.ksfams.sgframework.modules.system.clipboardManager

/**
 *
 * Clipboard 관련 유틸
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

/**
 * copy text to clipboard
 *
 * @receiver Context
 * @param text String
 * @param label String
 */
fun Context.copyToClipboard(text: String, label: String = "Copied Text") {
    clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text))
}

/**
 * copy text to clipboard
 *
 * @receiver Context
 * @param[text] to copy
 * @param label
 */
fun Context.copyToClipboard(@StringRes text: Int, label: String = "Copied Text") {
    copyToClipboard(string(text), label)
}

/**
 * copy uri to clipboard
 *
 * @receiver Context
 * @param uri to copy
 * @param label
 */
fun Context.copyUriToClipboard(uri: Uri, label: String = "Copied URI") {
    clipboardManager.setPrimaryClip(ClipData.newUri(contentResolver, label, uri))
}

/**
 * copy intent to clipboard
 *
 * @receiver Context
 * @param intent
 * @param label
 */
fun Context.copyIntentToClipboard(intent: Intent, label: String = "Copied Intent") {
    clipboardManager.setPrimaryClip(ClipData.newIntent(label, intent))
}

/**
 * get text of clipboard list
 *
 * @receiver Context
 * @return first object of primaryClip list
 */
fun Context.getTextFromClipboard(): String {
    val clip = clipboardManager.primaryClip
    return if (clip != null && clip.itemCount > 0) clip.getItemAt(0).coerceToText(this).toString() else ""
}

/**
 * get uri of clipboard list
 *
 * @receiver Context
 * @return first object of primaryClip list
 */
fun Context.getUriFromClipboard(): Uri? {
    val clip = clipboardManager.primaryClip
    return if (clip != null && clip.itemCount > 0) clip.getItemAt(0).uri else null
}

/**
 * get intent of clipboard list
 *
 * @receiver Context
 * @return first object of primaryClip list
 */
fun Context.getIntentFromClipboard(): Intent? {
    val clip = clipboardManager.primaryClip
    return if (clip != null && clip.itemCount > 0) clip.getItemAt(0).intent else null
}