package com.ksfams.sgframework.utils

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Date 관련 유틸
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
 * Converts current date to Calendar
 *
 * @receiver Date
 * @return Calendar
 */
fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

/**
 * Date 가 현재보다 미래인지 여부
 *
 * @receiver Date
 * @return Boolean
 */
fun Date.isFuture(): Boolean {
    return !Date().before(this)
}

/**
 * Date 가 현재보다 과거인지 여부
 *
 * @receiver Date
 * @return Boolean
 */
fun Date.isPast(): Boolean {
    return Date().before(this)
}

/**
 * Date 가 현재인지 여부
 *
 * @receiver Date
 * @return Boolean
 */
fun Date.isToday(): Boolean {
    return DateUtils.isToday(this.time)
}

/**
 * Date 가 현재를 기준으로
 * 어제인지 여부
 *
 * @receiver Date
 * @return Boolean
 */
fun Date.isYesterday(): Boolean {
    return DateUtils.isToday(this.time + DateUtils.DAY_IN_MILLIS)
}

/**
 * Date 가 현재를 기준으로
 * 내일인지 여부
 *
 * @receiver Date
 * @return Boolean
 */
fun Date.isTomorrow(): Boolean {
    return DateUtils.isToday(this.time - DateUtils.DAY_IN_MILLIS)
}

/**
 * Date 의 어제 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun yesterday(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    return cal.time
}

/**
 * Date 의 엊그제 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun beforeYesterday(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -2)
    return cal.time
}

/**
 * Date 의 7일전 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun before7days(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -7)
    return cal.time
}

/**
 * Date 의 30일전 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun before30days(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -30)
    return cal.time
}

/**
 * 현재의 이전 달
 *
 * @return Date
 */
fun lastMonth(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.MONTH, -1)
    return cal.time
}


/**
 * 현재의 3개월 전
 *
 * @return Date
 */
fun last3Month(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.MONTH, -3)
    cal.add(Calendar.DATE, +1)
    return cal.time
}


/**
 * 이전 월에 대한 마지막 일자를 구한다.
 *
 * @return
 */
fun lastMonthLastDay(): Int {
    val cal = lastMonth().toCalendar()
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

/**
 * Date 의 내일 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun tomorrow(): Date {
    val cal = getCurrentDate().toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, 1)
    return cal.time
}

/**
 * Date 의 어제 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun Date.yesterday(): Date {
    val cal = this.toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    return cal.time
}

/**
 * Date 의 엊그제 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun Date.beforeYesterday(): Date {
    val cal = this.toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -2)
    return cal.time
}

/**
 * Date 의 내일 날짜(Date)
 *
 * @receiver Date
 * @return Date
 */
fun Date.tomorrow(): Date {
    val cal = this.toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, 1)
    return cal.time
}

/**
 * Date 의 시간
 *
 * @receiver Date
 * @return Int
 */
fun Date.hour(): Int {
    return this.toCalendar().get(Calendar.HOUR)
}

/**
 * Date 의 분
 *
 * @receiver Date
 * @return Int
 */
fun Date.minute(): Int {
    return this.toCalendar().get(Calendar.MINUTE)
}

/**
 * Date 의 초
 *
 * @receiver Date
 * @return Int
 */
fun Date.second(): Int {
    return this.toCalendar().get(Calendar.SECOND)
}

/**
 * Date 의 월
 *
 * @receiver Date
 * @return Int
 */
fun Date.month(): Int {
    return this.toCalendar().get(Calendar.MONTH) + 1
}

/**
 * Date 의 월 명칭
 *
 * @receiver Date
 * @param locale Locale
 * @return String
 */
fun Date.monthName(locale: Locale = Locale.getDefault()): String? {
    return this.toCalendar().getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
}

/**
 * Date 의 연도
 *
 * @receiver Date
 * @return Int
 */
fun Date.year(): Int {
    return this.toCalendar().get(Calendar.YEAR)
}

/**
 * Date 의 일자
 *
 * @receiver Date
 * @return Int
 */
fun Date.day(): Int {
    return this.toCalendar().get(Calendar.DAY_OF_MONTH)
}

/**
 * Date 의 몇번째 주
 *
 * @receiver Date
 * @return Int
 */
fun Date.dayOfWeek(): Int {
    return this.toCalendar().get(Calendar.DAY_OF_WEEK)
}

/**
 * Date 의 주 명칭
 *
 * @receiver Date
 * @param locale Locale
 * @return String?
 */
fun Date.dayOfWeekName(locale: Locale = Locale.getDefault()): String? {
    return this.toCalendar().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale)
}

/**
 * Date 의 해당연도의 몇번째 일자
 *
 * @receiver Date
 * @return Int
 */
fun Date.dayOfYear(): Int {
    return this.toCalendar().get(Calendar.DAY_OF_YEAR)
}

/**
 * 현재 날짜를
 * timeZone 에 맞춰
 * format String 으로 변환 한다.
 *
 * @param format optional. default is yyyyMMddHHmmssSSS (20170602192000000)
 * @param timeZone optional. default is Asia/Seoul
 * @return
 */
fun currentDateToString(format: String = "yyyyMMddHHmmssSSS", timeZone: String = "Asia/Seoul"): String {
    var currentDate = getCurrentDate()
    currentDate = currentDate.normalizeDate(timeZone)
    return currentDate.asDateString(format)!!
}

/**
 * 해당 날짜를
 * timeZone 에 맞춰
 * format String 으로 변환 한다.
 *
 * @receiver Date
 * @param format optional. default is yyyyMMddHHmmssSSS (20170602192000000)
 * @param timeZone optional. default is Asia/Seoul
 * @return
 */
fun Date.asDateString(format: String = "yyyyMMddHHmmssSSS", timeZone: String = "Asia/Seoul"): String? {
    var date = this
    date = date.normalizeDate(timeZone)
    return tryCatch { SimpleDateFormat(format, Locale.getDefault()).format(date) }
}

/**
 * parsing date from String
 *
 * @receiver String format 형식의 날짜
 * @param[format] optional. default is yyyyMMddHHmmssSSS (20170602192000000)
 * @param timeZone optional. default is Asia/Seoul
 * @return Date object, Nullable
 */
@JvmOverloads
fun String.parseDate(format: String = "yyyyMMddHHmmssSSS", timeZone: String = "Asia/Seoul"): Date? {
    return tryCatch {
        var date = SimpleDateFormat(format, Locale.getDefault()).parse(this)
        date = date!!.normalizeDate(timeZone)
        date
    }
}

/**
 * TimeMillis값을
 * format string 형식으로 리턴
 *
 * @receiver String TimeMillis
 * @param format optional, default is yyyyMMddHHmmssSSS (20191223161700000)
 * @param timeZone optional. default is Asia/Seoul
 * @return Formatted Date
 */
@JvmOverloads
fun String.asDateString(format: String = "yyyyMMddHHmmssSSS", timeZone: String = "Asia/Seoul"): String? {
    val date = guard(this.getGregorianCalendar()?.time) {
        return null
    }

    val normalizeDate = date.normalizeDate(timeZone)
    return tryCatch { SimpleDateFormat(format, Locale.getDefault()).format(normalizeDate) }
}

/**
 * TimeMillis 값을
 * timeZone 에 맞춰
 * format string 형식으로 리턴
 *
 * @receiver Long TimeMillis
 * @param format optional, default is yyyyMMddHHmmssSSS (20191223161700000)
 * @param timeZone optional. default is Asia/Seoul
 * @return Formatted Date
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@JvmOverloads
fun Long.asDateString(format: String = "yyyyMMddHHmmssSSS", timeZone: String = "Asia/Seoul"): String?
        = tryCatch { this.toString().asDateString(format, timeZone) }

/**
 * 특정 날짜로 그 달의 몇째 주인지 확인한다.
 *
 * @receiver Date
 * @return String
 */
fun Date.getWeekOfMonth(): String {
    asDateString("yyyy년 MM월 ")?.let {
        return "${it}${dayOfWeek()}주"
    }
    return ""
}

/**
 * TimeMillis 값에
 * [addDate] 만큼 더한 날짜를
 * format string 형식으로 리턴
 *
 * @receiver String
 * @param addDate Int
 * @param format String
 * @param timeZone String
 */
fun String.addDateString(addDate: Int,
                         format: String = "yyyyMMddHHmmssSSS",
                         timeZone: String = "Asia/Seoul"): String? {

    val calDate = guard(this.getGregorianCalendar()) {
        return null
    }

    calDate.add(GregorianCalendar.DATE, +addDate)
    val date = calDate.time
    val normalizeDate = date.normalizeDate(timeZone)
    return tryCatch { SimpleDateFormat(format, Locale.getDefault()).format(normalizeDate) }
}

/**
 * 대한민국 표준시 date을
 * format string 형식으로 리턴
 * UTC 타임존의 시간으로 변경한다
 *
 * @receiver String 날짜 패턴 (yyyyMMddHHmmssSS)
 * @param format
 * @return
 */
fun String.koreanToUTC(format: String = "yyyyMMddHHmmssSSS"): String? {
    val gregorian: GregorianCalendar = getGregorianCalendar() ?: return null

    gregorian.add(GregorianCalendar.HOUR, -9)
    gregorian.timeZone = TimeZone.getTimeZone("UTC")
    gregorian.add(
            GregorianCalendar.HOUR,
            (TimeZone.getDefault().getOffset(gregorian.timeInMillis) / (1000L * 60L * 60L)).toInt()
    )
    gregorian.timeZone = TimeZone.getDefault()
    return gregorian.time.asDateString(format)
}

/**
 * format formatted date to another formatted date
 *
 * @receiver String 날짜 패턴 (yyyyMMddHHmmssSS)
 * @param[fromFormat] original date format
 * @param[toFormat] to convert
 * @return new Formatted Date
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun String.toDateString(fromFormat: String, toFormat: String): String? {
    val result: String
    val df = SimpleDateFormat(fromFormat, Locale.getDefault())
    val df2 = SimpleDateFormat(toFormat, Locale.getDefault())
    try {
        result = df2.format(df.parse(this))
    } catch (e: ParseException) {
        return this
    }
    return result
}

/**
 * 현재 시스템 날짜를
 * format string 형식으로 리턴
 *
 * @param format optional, default is yyyyMMddHHmmssSSS (20191223161700000)
 * @return Formatted Date
 */
@JvmOverloads
fun nowDateString(format: String = "yyyyMMddHHmmssSSS"): String
        = getCurrentDate().asDateString(format)!!


/**
 * normalize the date to the beginning of the day with given timeZone
 * it equals to Time.getJulianDay()
 *
 * @receiver Date
 * @param timeZone optional, default is "UTC"
 */
@JvmOverloads
fun Date.normalizeDate(timeZone: String = "UTC"): Date
        = Date(this.time.normalizeDate(timeZone))

/**
 * get TimeZone on system
 */
fun getTimeZone(): TimeZone = TimeZone.getDefault()

/**
 * get id of TimeZone on system
 */
fun getTimeZoneId(): String = TimeZone.getDefault().id

/**
 * 현재 시스템 날짜
 *
 * @return current date
 */
fun getCurrentDate(): Date = Date(System.currentTimeMillis())

/**
 * normalize the date to the beginning of the day with given timeZone
 * it equals to Time.getJulianDay()
 *
 * @receiver String TimeMillis
 * @param timeZone optional, default is "UTC"
 */
@JvmOverloads
fun String.normalizeDate(timeZone: String = "UTC"): Long {
    val date = GregorianCalendar.getInstance(TimeZone.getTimeZone(timeZone)) as GregorianCalendar
    date.time = tryCatch { Date(this.toLong()) }!!
    return date.timeInMillis
}

/**
 * normalize the date to the beginning of the day with given timeZone
 * it equals to Time.getJulianDay()
 *
 * @receiver Long TimeMillis
 * @param timeZone optional, default is "UTC"
 */
@JvmOverloads
fun Long.normalizeDate(timeZone: String = "UTC"): Long {
    val date = GregorianCalendar.getInstance(TimeZone.getTimeZone(timeZone)) as GregorianCalendar
    date.time = Date(this)
    return date.timeInMillis
}


/**
 * since 와 until Date 의
 * 간격이 7일 이내인지 여부
 *
 * @receiver String : since Date (yyyyMMdd)
 * @return Boolean: true - 7일 이내, false - 7일 이상
 */
fun String.isGap7days(untilDate: String): Boolean {
    val since = this.toDateString("yyyy-MM-dd", "yyyyMMdd")?.getInt() ?: 0
    val until = untilDate.toDateString("yyyy-MM-dd", "yyyyMMdd")?.getInt() ?: 0
    if (since == 0 || until == 0) {
        return false
    }

    return until - since <= 7
}


/**
 * 특정 날짜 format 의 GregorianCalendar 객체를 얻는다.
 *
 * @receiver String 날짜 패턴 (yyyyMMddHHmmssSS)
 * @return GregorianCalendar
 */
fun String.getGregorianCalendar(): GregorianCalendar? {
    if (length < 8) {
        return null
    }
    val yyyy = substring(0, 4).toInt()
    val MM = substring(4, 6).toInt()
    val dd = substring(6, 8).toInt()
    var hh = 0
    if (length > 8) {
        hh = substring(8, 10).toInt()
    }
    var mm = 0
    if (length > 10) {
        mm = substring(10, 12).toInt()
    }
    var ss = 0
    if (length > 12) {
        ss = substring(12, 14).toInt()
    }
    var SSS = 0
    if (length > 14) {
        SSS = substring(14).toInt()
    }

    val calendar = GregorianCalendar(yyyy, MM - 1, dd)
    calendar[GregorianCalendar.HOUR] = hh
    calendar[GregorianCalendar.MINUTE] = mm
    calendar[GregorianCalendar.SECOND] = ss
    calendar[GregorianCalendar.MILLISECOND] = SSS
    return calendar
}