package com.ksfams.sgframework.models.media

import android.net.Uri

/**
 *
 * 미디어 정보
 * Image(Photo), Video, Auio
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

data class Media(val id: Long, val uri: Uri)