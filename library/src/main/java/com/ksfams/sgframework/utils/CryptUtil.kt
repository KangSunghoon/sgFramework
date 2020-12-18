package com.ksfams.sgframework.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 *
 * 암호화/복호화 처리 유틸
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

///////////////////////////////////////////////////////////////////////////
// hash encryption
///////////////////////////////////////////////////////////////////////////
/**
 * Return the hex string of MD2 encryption.
 *
 * @receiver String?
 * @return the hex string of MD2 encryption
 */
fun String?.encryptMD2ToString(): String {
    return if (isEmpty()) "" else this?.toByteArray().encryptMD2ToString()
}

/**
 * Return the hex string of MD2 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of MD2 encryption
 */
fun ByteArray?.encryptMD2ToString(): String {
    return encryptMD2()?.bytes2HexString() ?: ""
}

/**
 * Return the bytes of MD2 encryption.
 *
 * @receiver ByteArray?
 * @return ByteArray? the bytes of MD2 encryption
 */
fun ByteArray?.encryptMD2(): ByteArray? {
    return this?.hashTemplate("MD2")
}

/**
 * Return the hex string of MD5 encryption.
 *
 * @receiver String
 * @return the hex string of MD5 encryption
 */
fun String.encryptMD5ToString(): String {
    return if (isEmpty()) "" else toByteArray().encryptMD5ToString()
}

/**
 * Return the hex string of MD5 encryption.
 *
 * @receiver String?
 * @param salt The salt.
 * @return the hex string of MD5 encryption
 */
fun String?.encryptMD5ToString(salt: String?): String? {
    if (this == null && salt == null) return ""
    if (salt == null) return this!!.toByteArray().encryptMD5().bytes2HexString()
    return if (this == null) {
        salt.toByteArray().encryptMD5().bytes2HexString()
    }  else {
        (this + salt).toByteArray().encryptMD5().bytes2HexString()
    }
}

/**
 * Return the hex string of MD5 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of MD5 encryption
 */
fun ByteArray?.encryptMD5ToString(): String {
    return encryptMD5().bytes2HexString() ?: ""
}

/**
 * Return the hex string of MD5 encryption.
 *
 * @receiver ByteArray?
 * @param salt The salt.
 * @return the hex string of MD5 encryption
 */
fun ByteArray?.encryptMD5ToString(salt: ByteArray?): String? {
    if (this == null && salt == null) return ""
    if (salt == null) return encryptMD5().bytes2HexString()
    if (this == null) return salt.encryptMD5().bytes2HexString()
    val dataSalt = ByteArray(size + salt.size)
    System.arraycopy(this, 0, dataSalt, 0, size)
    System.arraycopy(salt, 0, dataSalt, size, salt.size)
    return dataSalt.encryptMD5().bytes2HexString()
}

/**
 * Return the bytes of MD5 encryption.
 *
 * @receiver ByteArray?
 * @return the bytes of MD5 encryption
 */
fun ByteArray?.encryptMD5(): ByteArray? {
    return this?.hashTemplate("MD5")
}

/**
 * Return the hex string of file's MD5 encryption.
 *
 * @receiver String
 * @return the hex string of file's MD5 encryption
 */
fun String.encryptMD5File2String(): String? {
    val file = if (this.isEmpty()) null else File(this)
    return file.encryptMD5File2String()
}

/**
 * Return the bytes of file's MD5 encryption.
 *
 * @receiver String?
 * @return the bytes of file's MD5 encryption
 */
fun String.encryptMD5File(): ByteArray? {
    val file = if (this.isEmpty()) null else File(this)
    return file.encryptMD5File()
}

/**
 * Return the hex string of file's MD5 encryption.
 *
 * @receiver File?
 * @return the hex string of file's MD5 encryption
 */
fun File?.encryptMD5File2String(): String? {
    return encryptMD5File().bytes2HexString()
}

/**
 * Return the bytes of file's MD5 encryption.
 *
 * @receiver File?
 * @return the bytes of file's MD5 encryption
 */
fun File?.encryptMD5File(): ByteArray? {
    if (this == null) return null
    var fis: FileInputStream? = null
    val digestInputStream: DigestInputStream
    return try {
        fis = FileInputStream(this)
        var md = MessageDigest.getInstance("MD5")
        digestInputStream = DigestInputStream(fis, md)
        val buffer = ByteArray(256 * 1024)
        while (true) {
            if (digestInputStream.read(buffer) <= 0) break
        }
        md = digestInputStream.messageDigest
        md.digest()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        try {
            fis?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * Return the hex string of SHA1 encryption.
 *
 * @receiver String?
 * @return the hex string of SHA1 encryption
 */
fun String?.encryptSHA1ToString(): String? {
    return if (isEmpty()) "" else this?.toByteArray().encryptSHA1ToString()
}

/**
 * Return the hex string of SHA1 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of SHA1 encryption
 */
fun ByteArray?.encryptSHA1ToString(): String? {
    return encryptSHA1().bytes2HexString()
}

/**
 * Return the bytes of SHA1 encryption.
 *
 * @receiver ByteArray?
 * @return the bytes of SHA1 encryption
 */
fun ByteArray?.encryptSHA1(): ByteArray? {
    return this?.hashTemplate("SHA-1")
}

/**
 * Return the hex string of SHA224 encryption.
 *
 * @receiver String?
 * @return the hex string of SHA224 encryption
 */
fun String?.encryptSHA224ToString(): String? {
    return if (isEmpty()) "" else this?.toByteArray().encryptSHA224ToString()
}

/**
 * Return the hex string of SHA224 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of SHA224 encryption
 */
fun ByteArray?.encryptSHA224ToString(): String? {
    return encryptSHA224().bytes2HexString()
}

/**
 * Return the bytes of SHA224 encryption.
 *
 * @receiver ByteArray?
 * @return the bytes of SHA224 encryption
 */
fun ByteArray?.encryptSHA224(): ByteArray? {
    return this?.hashTemplate("SHA224")
}

/**
 * Return the hex string of SHA256 encryption.
 *
 * @receiver String?
 * @return the hex string of SHA256 encryption
 */
fun String?.encryptSHA256ToString(): String? {
    return if (isEmpty()) "" else this?.toByteArray().encryptSHA256ToString()
}

/**
 * Return the hex string of SHA256 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of SHA256 encryption
 */
fun ByteArray?.encryptSHA256ToString(): String? {
    return encryptSHA256().bytes2HexString()
}

/**
 * Return the bytes of SHA256 encryption.
 *
 * @receiver ByteArray?
 * @return the bytes of SHA256 encryption
 */
fun ByteArray?.encryptSHA256(): ByteArray? {
    return this?.hashTemplate("SHA-256")
}

/**
 * Return the hex string of SHA384 encryption.
 *
 * @receiver String?
 * @return the hex string of SHA384 encryption
 */
fun String?.encryptSHA384ToString(): String? {
    return if (isEmpty()) "" else this?.toByteArray().encryptSHA384ToString()
}

/**
 * Return the hex string of SHA384 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of SHA384 encryption
 */
fun ByteArray?.encryptSHA384ToString(): String? {
    return encryptSHA384().bytes2HexString()
}

/**
 * Return the bytes of SHA384 encryption.
 *
 * @receiver ByteArray?
 * @return the bytes of SHA384 encryption
 */
fun ByteArray?.encryptSHA384(): ByteArray? {
    return this?.hashTemplate("SHA-384")
}

/**
 * Return the hex string of SHA512 encryption.
 *
 * @receiver String?
 * @return the hex string of SHA512 encryption
 */
fun String?.encryptSHA512ToString(): String? {
    return if (isEmpty()) "" else this?.toByteArray().encryptSHA512ToString()
}

/**
 * Return the hex string of SHA512 encryption.
 *
 * @receiver ByteArray?
 * @return the hex string of SHA512 encryption
 */
fun ByteArray?.encryptSHA512ToString(): String? {
    return encryptSHA512().bytes2HexString()
}

/**
 * Return the bytes of SHA512 encryption.
 *
 * @receiver ByteArray?
 * @return the bytes of SHA512 encryption
 */
fun ByteArray?.encryptSHA512(): ByteArray? {
    return this?.hashTemplate("SHA-512")
}

/**
 * Return the bytes of hash encryption.
 *
 * @receiver ByteArray
 * @param algorithm The name of hash encryption.
 * @return the bytes of hash encryption
 */
fun ByteArray.hashTemplate(algorithm: String): ByteArray? {
    if (isEmpty()) return null

    return try {
        val md = MessageDigest.getInstance(algorithm)
        md.update(this)
        md.digest()
    } catch (e: NoSuchAlgorithmException) {
        LogUtil.e(e)
        null
    }
}