package com.ksfams.sgframework.constants

/**
 *
 * Application 공통 상수 정의
 *
 * Create Date 2020-01-10
 * @version    1.00 2020-01-10
 * @since   1.00
 * @see
 * @author    강성훈(ssogaree@gmail.com)
 * Revision History
 * who			when				what
 * ssogaree		2020-01-10		    신규 개발.
 */

const val INVALID_ID = 0

/** 현재 운영체제의 기본 file path '/' or '\' 처리   */
/** 현재 운영체제의 기본 file path '/' or '\' 처리   */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline val FILE_SEP: String
    get() = System.getProperty("file.separator").toString()

/** 현재 운영체제의 줄바꿈 문자  */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline val LINE_SEP: String
    get() = System.getProperty("line.separator").toString()

/** Y/N 정의 : Y  */
const val IS_TRUE: String = "Y"
/** Y/N 정의 : N  */
const val IS_FALSE: String = "N"

/** InputStream Buffer  */
const val BUFFER_SIZE: Int = 8192

