package com.ksfams.sgframework.utils

import android.text.InputFilter
import android.text.Spanned
import java.io.UnsupportedEncodingException

/**
 *
 * EditText 등의 필드에 텍스트 입력/수정시
 * 입력문자열의 바이트 길이를 체크하여 입력을 제한하는 필터.
 *
 * @param maxbyte 입력가능한 최대 바이트 길이
 * @param charset 인코딩 문자셋
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

class ByteLengthFilter(private val maxbyte: Int, private val charset: String): InputFilter {


    /**
     * 입력/삭제 및 붙여넣기/잘라내기할 때마다 실행된다.
     *
     * - source : 새로 입력/붙여넣기 되는 문자열
     * - dest : 변경 전 문자열
     */
    override fun filter(source: CharSequence, start: Int, end: Int,
                        dest: Spanned, dstart: Int, dend: Int): CharSequence? {

        var byteExpected: ByteArray? = null
        var byteDest: ByteArray? = null

        // 변경 후 예상되는 문자열
        var expected = ""
        expected += dest.subSequence(0, dstart)
        expected += source.subSequence(start, end)
        expected += dest.subSequence(dend, dest.length)

        try {
            byteExpected = expected.toByteArray(charset(charset))
            byteDest = dest.toString().toByteArray(charset(charset))
        } catch (e: UnsupportedEncodingException) {
            LogUtil.e(e)
        }

        return if (byteExpected != null && byteDest != null && byteExpected.size > maxbyte) {
            if (byteDest.size >= maxbyte) {
                // 기존 입력된 문자열이 INPUT_MAX_LENGTH_BYTE 이상일 경우 입력된 문자열은 return하지 않는다.
                "" // source 입력 불가(원래 문자열 변경 없음)
            } else {
                // 기존문자열이 INPUT_MAX_LENGTH_BYTE 미만일 경우 manLengthByte에서 이미 입력된 문자열의 byte수 만큼을 제외한 문자열을 return 한다.
                subStringBytes(source.toString(), maxbyte - byteDest.size)
            }
        } else {
            null
        }
    }

    /**
     * 입력가능한 최대 문자 길이(최대 바이트 길이와 다름!).
     */
    fun calculateMaxLength(expected: String): Int {
        return maxbyte - (getByteLength(expected) - expected.length)
    }

    /**
     * 문자열의 바이트 길이.
     * 인코딩 문자셋에 따라 바이트 길이 달라짐.
     * @param str
     * @return
     */
    private fun getByteLength(str: String): Int {
        try {
            return str.toByteArray(charset(charset)).size
        } catch (e: UnsupportedEncodingException) {
            LogUtil.e(e)
        }
        return 0
    }

    /**
     * 입력된 string을 입력한 maxByte만큼 계산하여 return 한다.
     * @param str
     * @param maxByteLength
     * @return
     */
    private fun subStringBytes(str: String, maxByteLength: Int): CharSequence? {
        val length = str.length
        var retLength = 0
        var tempSize = 0
        var asc: Int
        for (count in 1..length) {
            asc = str[count - 1].toInt()
            if (asc > 127) {
                if (maxByteLength >= tempSize + 3) {
                    tempSize += 3
                    retLength++
                } else {
                    return str.substring(0, retLength)
                }
            } else {
                if (maxByteLength > tempSize) {
                    tempSize++
                    retLength++
                }
            }
        }
        return str.substring(0, retLength)
    }
}