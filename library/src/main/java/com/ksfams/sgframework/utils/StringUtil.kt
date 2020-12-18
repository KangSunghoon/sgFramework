package com.ksfams.sgframework.utils

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.text.Html
import android.util.Base64
import androidx.annotation.RequiresApi
import com.ksfams.sgframework.constants.BUFFER_SIZE
import com.ksfams.sgframework.constants.IS_TRUE
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.ArrayList
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 *
 * String 관련 유틸
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

/** hex digit */
private val HEX_DIGITS: CharArray =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')


/**
 * String 을 전달받아
 * Bold 처리하여 리턴한다.
 *
 * @receiver String
 * @return
 */
fun String.getBold(): String {
    val source = "<b>$this</b>"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(source).toString()
    }
}

/**
 * True if String is a Phone Number
 *
 * @return
 */
fun String.isPhoneNumber(): Boolean
        = matches("([0+]{1})([0-9]{2})([0-9]{3,4})([0-9]{4})".toRegex())

/**
 * 이메일 주소 여부 확인
 *
 * @return
 */
fun String.isEmail(): Boolean
        = matches("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+\\.?([A-Za-z0-9]{2,3})?\\.[A-Za-z]{2,3}".toRegex())

/**
 * URL 인지 여부
 *
 * @return
 */
fun String.isUrl(): Boolean {
    val pattern = "(([Hh][Tt][Tt][Pp][Ss]?)://|(([Ww][Ww][Ww][\\.]?)|([Mm]{1}[\\.])))?(?:[A-Za-z0-9\\-]+[\\.])" +
            "+(?:(?:aero|arpa|asia|[Aa][CcDdEeFfGgIiLlMmNnOoQqRrSsTtUuWwXxZz])|(?:biz|[Bb][AaBbDdEeFfGgHhIiJjMmNnOoRrSsTtVvWwYyZz])|(?:cat|com|coop|[Cc][AaCcDdFfGgHhIiKkLlMmNnOoRrUuVvXxYyZz])|[Dd][EeJjKkMmOoZz]|" +
            "(?:edu|[Ee][CcEeGgRrSsTtUu])|[Ff][IiJjKkMmOoRr]|(?:gov|[Gg][AaBbDdEeFfGgHhIiLlMmNnPpQqRrSsTtUuWwYy])|[Hh][KkMmNnRrTtUu]|(?:info|int|[Ii][DdEeLlMmNnOoQqRrSsTt])|(?:jobs|[Jj][EeMmOoPp])|[Kk][EeGgHhIiMmNnPpRrWwYyZz]|[Ll][AaBbCcIiKkTrSsTtUuVvYy]|" +
            "(?:mil|mobi|museum|[Mm][AaCcDdEeGgHhKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz])|(?:name|net|[Nn][AaCcEeFfGgIiLlOoPpRrUuZz])|(?:org|om)|(?:pro|[Pp][AaEeFfGgHhKkLlMmNnRrSsTtWwYy])|qa|[Rr][EeOoSsUuWw]|[Ss][AaBbCcDdEeGgHhIiJjKkLlMmNnOoRrTtUuVvYyZz]|" +
            "(?:tel|travel|[Tt][CcDdFfGgHhJjKkLlMmNnOoPpRrTtVvWwZz])|[Uu][AaGgKkSsYyZz]|[Vv][AaCcEeGgIiNnUu]|[Ww][FfSs])[0-9A-Za-z\\(\\)\\.:,\\-_/~#=%\\&\\?]{1,}"
    return matches(pattern.toRegex())
}

/**
 * 숫자인지 여부
 *
 * @return
 */
fun String.isNumeric(): Boolean
        = matches("^[0-9]+$".toRegex())

/**
 * 아이디가
 * 영문, 숫자 조합되어
 * 4자 이상인지 여부
 *
 * @receiver String
 * @return Boolean
 */
fun String.isUserIdValidate(): Boolean
        = matches("^[A-Za-z[0-9]]{6,}$".toRegex())

/**
 * 비밀번호가
 * 영문, 숫자, 특수문자 중 2가지 이상 조합되어
 * 8자 이상인지 여부
 *
 * @receiver String
 * @return Boolean
 */
fun String.isPasswordValidate(): Boolean {
    return matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{8,}\$".toRegex())
}

/**
 * String 로 넘겨받은 String 을
 * 정수형으로 반환
 * String 이 null 이거나, 없는 경우는
 * 0 을 반환한다.
 *
 * @receiver String
 * @return
 */
fun String.getInt(): Int {
    var result = 0
    if (isNumeric()) result = toInt()
    return result
}

/**
 * String 로 넘겨받은 String 을
 * 정수형으로 반환
 * String 이 null 이거나, 없는 경우는
 * 0 을 반환한다.
 *
 * @receiver String
 * @return
 */
fun String.getLong(): Long {
    var result: Long = 0
    if (isNumeric()) {
        result = toLong()
    }
    return result
}

/**
 * String 로 넘겨받은 String 을
 * Float 형으로 반환
 * String 이 null 이거나, 없는 경우는
 * 0.0 을 반환한다.
 *
 * @receiver String
 * @return
 */
fun String.getFloat(): Float {
    var result = 0f
    if (isNotEmpty()) {
        result = try {
            toFloat()
        } catch (e: Exception) {
            return 0f
        }
    }
    return result
}

/**
 * String 로 넘겨받은 String 을
 * Float 형으로 반환
 * String 이 null 이거나, 없는 경우는
 * 0.0 을 반환한다.
 *
 * @receiver String
 * @return
 */
fun String.getDouble(): Double {
    var result = 0.0
    if (isNotEmpty()) {
        result = try {
            toDouble()
        } catch (e: Exception) {
            return 0.0
        }
    }
    return result
}

/**
 * 넘겨받은 String 을
 * Boolean 형으로 반환
 * String 이 null 이거나, 없는 경우는
 * false 을 반환한다.
 *
 * @receiver String
 * @return the boolean
 */
fun String.getBoolean(): Boolean {
    return "1" == this || IS_TRUE == this
}

/**
 * 문자열이 없거나,
 * null 일 경우 "" 의 값을 반환한다.
 *
 * @receiver String?
 * @return
 */
@SuppressLint("DefaultLocale")
fun String?.nullToBlank(): String
        = if (this == null || trim().toLowerCase() == "null") "" else trim()

/**
 * 문자열이 빈 문자열인지 검사한다.
 * 빈문자열 일 경우 : true
 *
 * @receiver String?
 * @return
 */
fun String?.isEmpty(): Boolean {
    val temp = this.nullToBlank()
    temp.replace("\r".toRegex(), "")
    temp.replace("\n".toRegex(), "")
    temp.replace("&nbsp;".toRegex(), "")
    return temp == ""
}

/**
 * 문자열이 빈 문자열인지 검사한다.
 * 빈문자열이 아닐 경우 : true
 *
 * @receiver String?
 * @return
 */
fun String?.isNotEmpty(): Boolean = !isEmpty()

/**
 * 문자열이 빈 문자열인지 검사한다.
 * 빈문자열 일 경우 : true
 *
 * @receiver CharSequence?
 * @return
 */
fun CharSequence?.isEmpty(): Boolean {
    val temp = this.toString().nullToBlank()
    temp.replace("\r".toRegex(), "")
    temp.replace("\n".toRegex(), "")
    temp.replace("&nbsp;".toRegex(), "")
    return temp == ""
}

/**
 * 문자열이 빈 문자열인지 검사한다.
 * 빈문자열이 아닐 경우 : true
 *
 * @receiver CharSequence?
 * @return
 */
fun CharSequence?.isNotEmpty(): Boolean = !isEmpty()

/**
 * 문자열 전체가 공백문자인지 여부
 *
 * @receiver String?
 * @return
 */
fun String?.isSpace(): Boolean {
    this?.let {
        it.forEach { c ->
            if (!c.isWhitespace()) return false
        }
    }
    return true
}

/**
 * String 의 [len] 부터
 * 남은 문자 개수만큼 [maskingChar] = '*' 표기로 마스킹 처리 한다.
 *
 * @receiver String
 * @param len Int
 * @param maskingChar
 * @return String
 */
fun String.toMasking(len: Int, maskingChar: String = "*"): String {
    var ret = this
    if (length > len) {
        val sb = StringBuilder()
        sb.append(substring(0, len))
        for (i in 0 until length - len) {
            sb.append(maskingChar)
        }
        ret = sb.toString()
    }
    return ret
}

/**
 * String 의 [len] 까지 자르고
 * [masking] = "..." 표기 처리한다.
 *
 * @receiver String
 * @param len Int
 * @param masking String
 * @return String
 */
fun String.ellipsize(len: Int, masking: String = "..."): String {
    if (this.length > len) {
        return this.substring(0, len) + masking
    }
    return this
}

/**
 * 입력된 숫자에
 * 천단위 , 표기후 return 한다.
 *
 * @receiver Long
 * @return
 */
fun Long.numberFormat(format: String = "#,###"): String
        = this.toString().numberFormat(format)

/**
 * 입력된 숫자에
 * 천단위 , 표기후 return 한다.
 *
 * @receiver Int
 * @return
 */
fun Int.numberFormat(format: String = "#,###"): String
        = this.toString().numberFormat(format)

/**
 * 입력된 숫자에
 * 천단위 콤마, 소수점
 * 표기후 return 한다.
 *
 * @receiver Double
 * @param format default 천단위 콤마, 소수점 2째자리
 * @return
 */
fun Double.numberFormat(format: String = "#,###.##"): String
        = this.toString().numberFormat(format)

/**
 * formatting number like 1,000,000
 *
 * @receiver String
 * @param format String
 * @return String
 */
@JvmOverloads
fun String.numberFormat(format: String = "#,###", roundingMode: RoundingMode = RoundingMode.HALF_UP): String {
    val df = DecimalFormat(format)
    df.roundingMode = roundingMode
    return df.format(BigDecimal(this))
}

/**
 * Encode to Base64
 *
 * @receiver String
 * @return
 */
@RequiresApi(Build.VERSION_CODES.FROYO)
fun String.encodeToBase64(): String
        = Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)

/**
 * Encode to Base64
 *
 * @receiver String
 * @return
 */
@RequiresApi(Build.VERSION_CODES.FROYO)
fun ByteArray.encodeToBase64(): String
        = Base64.encodeToString(this, Base64.NO_WRAP)

/**
 * Decode to Base64
 *
 * @receiver String
 * @return
 */
@RequiresApi(Build.VERSION_CODES.FROYO)
fun String.decodeToBase64(): String
        = String(Base64.decode(this.toByteArray(), Base64.NO_WRAP), Charset.defaultCharset())

/**
 * returns the md5 of the String
 *
 * @receiver String
 * @return String?
 */
fun String.md5(): String? = encrypt(this, "MD5")

/**
 * returns the SHA1 of the String
 *
 * @receiver String
 * @return String?
 */
fun String.sha1(): String? = encrypt(this, "SHA-1")


/**
 * Encode String to URL
 *
 * @receiver String
 * @param charSet
 * @return
 */
fun String.encodeToUrl(charSet: String = "UTF-8"): String
        = URLEncoder.encode(this, charSet)

/**
 * Decode String to URL
 *
 * @receiver String
 * @param charSet
 * @return
 */
fun String.decodeToUrl(charSet: String = "UTF-8"): String
        = URLDecoder.decode(this, charSet)

/**
 * Encrypt String to AES with the specific Key
 *
 * @receiver String
 * @param key String
 * @return String
 */
fun String.encryptAES(key: String): String {
    var crypted: ByteArray? = null
    try {
        val skey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, skey)
        crypted = cipher.doFinal(toByteArray())
    } catch (e: Exception) {
        println(e.toString())
    }
    return String(Base64.encode(crypted, Base64.DEFAULT))
}

/**
 * Decrypt String to AES with the specific Key
 *
 * @receiver String
 * @param key String
 * @return String
 */
fun String.decryptAES(key: String): String {
    var output: ByteArray? = null
    try {
        val skey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, skey)
        output = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
    } catch (e: Exception) {
        println(e.toString())
    }
    return output?.let { String(it) } ?: ""
}


/**
 * encode The String to Binary
 */
fun String.encodeToBinary(): String {
    val stringBuilder = StringBuilder()
    toCharArray().forEach {
        stringBuilder.append(Integer.toBinaryString(it.toInt()))
        stringBuilder.append(" ")
    }
    return stringBuilder.toString()
}

/**
 * Decode the String from binary
 */
fun String.deCodeToBinary(): String {
    val stringBuilder = StringBuilder()
    split(" ").forEach {
        stringBuilder.append(Integer.parseInt(it.replace(" ", ""), 2))
    }
    return stringBuilder.toString()
}

/**
 * json 객체를
 * String 으로 변환
 *
 * @receiver String
 * @return
 */
fun String.jsonToString(): String? = tryCatch {
    when {
        it.startsWith("{") -> JSONObject(it).toString(4)
        it.startsWith("[") -> JSONArray(it).toString(4)
        else -> it
    }
}

/**
 * String List 의 element 비교
 * 대소문자 구분 없음.
 *
 * @receiver List<String>
 * @param item String?
 * @return Boolean
 */
fun List<String>.containsIgnoreCase(item: String?): Boolean {
    return this.map { it.equals(item, true) }.any { it }
}


/**
 * Hex 코드를 String으로 변환
 *
 * @receiver ByteArray?
 * @return String?
 */
fun ByteArray?.bytes2HexString(): String? {
    if (this == null) return ""

    val len = size
    if (len <= 0) return ""

    val ret = CharArray(len shl 1)
    var i = 0
    var j = 0
    while (i < len) {
        ret[j++] =
                HEX_DIGITS[this[i].toInt() shr 4 and 0x0f]
        ret[j++] =
                HEX_DIGITS[this[i].toInt() and 0x0f]
        i++
    }
    return String(ret)
}

/**
 * Hex String을 byte로 변환
 *
 * @receiver String hexString
 * @return
 */
@SuppressLint("DefaultLocale")
fun String.hexString2Bytes(): ByteArray? {
    var hexString = this
    if (hexString.isEmpty()) return null
    var len = hexString.length
    if (len % 2 != 0) {
        hexString = "0$hexString"
        len = len + 1
    }
    val hexBytes = hexString.toUpperCase().toCharArray()
    val ret = ByteArray(len shr 1)
    var i = 0
    while (i < len) {
        ret[i shr 1] = (hexBytes[i].hex2Dec() shl 4 or hexBytes[i + 1].hex2Dec()).toByte()
        i += 2
    }
    return ret
}

/**
 * Hex 코드를 Integer로 변환
 *
 * @receiver Char hexChar
 * @return
 */
fun Char.hex2Dec(): Int {
    return when (this) {
        in '0'..'9' -> this - '0'
        in 'A'..'F' -> this - 'A' + 10
        else -> throw IllegalArgumentException()
    }
}

/**
 * byte 합치기
 *
 * @param prefix
 * @param suffix
 * @return
 */
fun joins(prefix: ByteArray, suffix: ByteArray): ByteArray? {
    val ret = ByteArray(prefix.size + suffix.size)
    System.arraycopy(prefix, 0, ret, 0, prefix.size)
    System.arraycopy(suffix, 0, ret, prefix.size, suffix.size)
    return ret
}

/**
 * get response string from InputStream
 *
 * @receiver InputStream
 * @return response string
 */
fun InputStream.getString(): String? = this.bufferedReader().use {
    it.readText()
}

/**
 * InputStream to Byte
 *
 * @receiver InputStream
 * @return
 */
fun InputStream.getByteArray(): ByteArray? {
    return use {
        val os = ByteArrayOutputStream()
        os.use {
            val b = ByteArray(BUFFER_SIZE)
            var len: Int
            while (read(b, 0, BUFFER_SIZE).also { len = it } != -1) {
                os.write(b, 0, len)
            }
            os.toByteArray()
        }
    }
}

/**
 * InputStream 을 String List 로 변환
 *
 * @receiver InputStream
 * @param charset
 * @return
 */
fun InputStream.getStringList(charset: Charset?): List<String>? {
    return use {
        val list: MutableList<String> = ArrayList()
        val reader: BufferedReader = if (charset == null) {
            BufferedReader(InputStreamReader(this))
        } else {
            BufferedReader(InputStreamReader(this, charset))
        }

        reader.use {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                list.add(line)
            }
            list
        }
    }
}


/**
 * empty return
 *
 * @receiver String
 * @param default
 */
fun String?.emptyToDefault(default: String = ""): String
        = if (this.isEmpty()) default else this!!


/**
 * View editable 처리
 *
 * @receiver String
 * @return Editable
 */
fun String.toEditable(): Editable
        = Editable.Factory.getInstance().newEditable(this)


/**
 * 암호화 처리
 *
 * @param source String
 * @param type String
 * @return String?
 */
private fun encrypt(source: String, type: String): String? {
    val bytes = MessageDigest.getInstance(type).digest(source.toByteArray())
    return bytes.bytes2HexString()
}
