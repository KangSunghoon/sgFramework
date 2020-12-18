package com.ksfams.sgframework.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.ksfams.sgframework.constants.FILE_SEP
import com.ksfams.sgframework.constants.LINE_SEP
import com.ksfams.sgframework.extensions.toAllString
import com.ksfams.sgframework.modules.reference.ApplicationReference
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

/**
 *
 * Application 로그를 처리한다.
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

object LogUtil {

    /** 디버깅 태그 */
    private const val TAG = "LogUtil"

    private const val TOP_CORNER = "┌"
    private const val MIDDLE_CORNER = "├"
    private const val LEFT_BORDER = "│ "
    private const val BOTTOM_CORNER = "└"
    private const val SIDE_DIVIDER =
            "────────────────────────────────────────────────────────"
    private const val MIDDLE_DIVIDER =
            "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private const val TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private const val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
    private const val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER

    private const val DEFAULT_FILENAME = "log"
    private const val MAX_LEN = 3000
    private const val NOTHING = "log nothing"
    private const val ARGS = "args"

    private val EXECUTOR = Executors.newSingleThreadExecutor()

    private var config: Config

    init {
        config = Config()
    }


    /**
     * init
     *
     * @param config Config
     */
    @JvmStatic
    fun init(config: Config) {
        this.config = config
    }

    /**
     * get config data
     *
     * @return Config
     */
    fun getConfig(): Config = this.config

    /**
     * log level VERBOSE
     *
     * @param contents Any
     * @param tag String?
     */
    fun v(contents: Any, tag: String? = null) = log(Log.VERBOSE, tag ?: config.globalTag, contents)

    /**
     * log level DEBUG
     *
     * @param contents Any
     * @param tag String?
     */
    fun d(contents: Any, tag: String? = null) = log(Log.DEBUG, tag ?: config.globalTag, contents)

    /**
     * log level INFO
     *
     * @param contents Any
     * @param tag String?
     */
    fun i(contents: Any, tag: String? = null) = log(Log.INFO, tag ?: config.globalTag, contents)

    /**
     * log level WARN
     *
     * @param contents Any
     * @param tag String?
     */
    fun w(contents: Any, tag: String? = null) = log(Log.WARN, tag ?: config.globalTag, contents)

    /**
     * log level ERROR
     *
     * @param contents Any
     * @param tag String?
     */
    fun e(contents: Any, tag: String? = null) = log(Log.ERROR, tag ?: config.globalTag, contents)

    /**
     * log level ASSERT
     *
     * @param contents Any
     * @param tag String?
     */
    fun a(contents: Any, tag: String? = null) = log(Log.ASSERT, tag ?: config.globalTag, contents)


    /**
     *
     * 배열을 가변인자로 넘기고 싶으면
     * * 을 붙이면 배열을 가변인자로 취급
     * ex) *contentsList
     *
     * @param logType
     * @param tag
     * @param contents
     */
    private fun log(logType: Int, tag: String?, contents: Any) {
        if (!config.isLogCat && !config.isSaveLogFile) return
        if (logType < config.consoleFilter && logType < config.fileFilter) return

        val tagHead = processTagAndHead(tag)
        val body = processBody(contents)

        if (config.isLogCat && logType >= config.consoleFilter) {
            printConsole(logType, tagHead.tag, tagHead.consoleHead, body)
        }

        if (config.isSaveLogFile && logType >= config.fileFilter) {
            printFile(logType, tagHead.tag, tagHead.fileHead + body)
        }
    }


    /**
     * Tag, Head 생성
     *
     * @param tag
     * @return
     */
    private fun processTagAndHead(tag: String?): TagHead {
        var tagResult: String? = tag
        if (!config.globalTag.isEmpty()) {
            tagResult = config.globalTag!!
        }

        val stackTrace: Array<StackTraceElement> = Throwable().stackTrace
        val stackIndex = 4
        val targetElement = stackTrace[stackIndex]
        val fileName = targetElement.getClassFileName()

        if (tagResult.isSpace()) {
            val index = fileName.indexOf('.')
            tagResult = if (index == -1) fileName else fileName.substring(0, index)
        }

        if (stackIndex >= stackTrace.size) {
            return TagHead(tagResult, null, ": ")
        }

        val tName = Thread.currentThread().name
        val head = Formatter()
                .format("%s, %s.%s(%s:%d)",
                        tName,
                        targetElement.className,
                        targetElement.methodName,
                        fileName,
                        targetElement.lineNumber)
                .toString()
        val fileHead = " [$head]: "
        return TagHead(tagResult, arrayOf(head).toList(), fileHead)
    }

    /**
     * 로그 body 생성
     *
     * @param contents
     * @return
     */
    private fun processBody(contents: Any?): String {
        var body: String? = null

        contents?.let {
            when (it) {
                is Array<*> -> {
                    val sb = StringBuilder()
                    for ((index, content) in it.withIndex()) {
                        content?.let { item ->
                            sb.append(ARGS)
                                    .append("[")
                                    .append(index)
                                    .append("]")
                                    .append(" = ")
                                    .append(formaToString(item))
                                    .append(LINE_SEP)
                        }
                    }
                    body = sb.toString()
                }
                else -> body = formaToString(it)
            }
        }

        return body ?: NOTHING
    }


    /**
     * border 라인 출력
     *
     * @param logType
     * @param tag
     * @param isTop
     */
    private fun printBorder(logType: Int, tag: String?, isTop: Boolean) {
        Log.println(logType, tag, if (isTop) TOP_BORDER else BOTTOM_BORDER)
    }

    /**
     * header 출력
     *
     * @param logType
     * @param tag
     * @param head
     */
    private fun printHead(logType: Int, tag: String?, head: List<String>? = null) {
        head?.let {
            it.forEach { aHead ->
                if (config.isBorder) {
                    Log.println(logType, tag, LEFT_BORDER + aHead)
                } else {
                    Log.println(logType, tag, aHead)
                }
            }

            if (config.isBorder) {
                Log.println(logType, tag, MIDDLE_BORDER)
            }
        }
    }

    /**
     * sub log message 출력
     *
     * @param logType
     * @param tag
     * @param msg
     */
    private fun printSubMsg(logType: Int, tag: String?, msg: String) {
        msg.split(LINE_SEP).forEach {
            if (config.isBorder) {
                Log.println(logType, tag, LEFT_BORDER + it)
            } else {
                Log.println(logType, tag, it)
            }
        }
    }

    private fun printMsg(logType: Int, tag: String?, msg: String) {
        val len = msg.length
        val countOfSub = len / MAX_LEN
        if (countOfSub > 0) {
            var index = 0
            for (i in 0 until countOfSub) {
                printSubMsg(logType, tag, msg.substring(index, index + MAX_LEN))
                index += MAX_LEN
            }

            if (index != MAX_LEN) {
                printSubMsg(logType, tag, msg.substring(index, len))
            }
        } else {
            printSubMsg(logType, tag, msg)
        }
    }

    /**
     * console log 출력
     *
     * @param logType
     * @param tag
     * @param head
     * @param msg
     */
    private fun printConsole(logType: Int, tag: String?,
                             head: List<String>? = null, msg: String) {
        if (config.isBorder) {
            printBorder(logType, tag, true)
        }

        printHead(logType, tag, head)
        printMsg(logType, tag, msg)

        if (config.isBorder) {
            printBorder(logType, tag, false)
        }
    }


    /**
     * file log
     *
     * @param logType
     * @param tag
     * @param msg
     */
    private fun printFile(logType: Int, tag: String?, msg: String) {
        val dateFormat = nowDateString("yyyy-MM-dd HH:mm:ss.SSS")
        val date = dateFormat.substring(0, 10)
        val time = dateFormat.substring(11) + " "
        var dir: String = ApplicationReference.getApp().getLogDirecory().absolutePath
        config.dir?.let { dir = it }
        val fullPath = "${dir}${FILE_SEP}${config.logFileName}-${date}.txt"

        try {
            val isPrintDeviceInfo: Boolean = !fullPath.isFileExist()

            if (!fullPath.createOrExistFile()) {
                printConsole(logType = Log.ERROR, tag = TAG, msg = "create failed: $fullPath")
                return
            }

            deletDueLogs(fullPath)
            if (isPrintDeviceInfo) printDeviceInfo(fullPath)

            val sb = StringBuilder()
            sb.append(time)
                    .append(getLogFilterName(logType))
                    .append(FILE_SEP)
                    .append(tag)
                    .append(msg)
                    .append(LINE_SEP)

            val content = sb.toString()
            inputToFile(content, fullPath)
        } catch (e: IOException) {
            e.printStackTrace()
            printConsole(logType = Log.ERROR, tag = TAG, msg = "create failed: $fullPath")
        }
    }


    /**
     * Config.saveDays 시간이
     * 지난 로그파일 삭제
     *
     * @param filePath
     */
    private fun deletDueLogs(filePath: String) {
        val files = filePath.toFile()?.parentFile?.listFiles { _, name ->
            name.matches("^${config.logFileName}-[0-9]{4}-[0-9]{2}-[0-9]{2}.txt$".toRegex())
        }

        files?.let { list ->
            if (list.isNotEmpty()) {
                val length = filePath.length
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tryCatch {
                    val curDay = filePath.substring(length - 14, length - 4)
                    val dueMillis = sdf.parse(curDay)!!.time - config.saveDays * 86400000L

                    list.forEach { file ->
                        val name = file.name
                        val nameLength = name.length
                        val logDay = name.substring(nameLength - 14, nameLength - 4)
                        if (sdf.parse(logDay)!!.time <= dueMillis) {
                            EXECUTOR.execute {
                                if (!file.delete()) {
                                    printConsole(logType = Log.ERROR, tag = TAG, msg = "delete failed: $file")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * input String to file
     *
     * @param input
     * @param filePath
     */
    private fun inputToFile(input: String, filePath: String) {
        EXECUTOR.execute {
            var bw: BufferedWriter? = null
            tryAndCatch(block = {
                bw = BufferedWriter(FileWriter(filePath, true))
                bw?.write(input)
            }, catch = {
                it?.printStackTrace()
                printConsole(logType = Log.ERROR, tag = TAG, msg = "log to $filePath failed")
            }, finally = {
                bw?.let { closeSafely(it) }
            })
        }
    }


    /**
     * Device Info
     *
     * @param filePath
     */
    @SuppressLint("MissingPermission")
    private fun printDeviceInfo(filePath: String) {
        val time = filePath.substring(filePath.length - 14, filePath.length - 4)
        val head = "************* Log Head ****************" +
                LINE_SEP + "Date of Log        : " + time +
                LINE_SEP + "Device Manufacturer: " + Build.MANUFACTURER +
                LINE_SEP + "Device Model       : " + Build.MODEL +
                LINE_SEP + "Android Version    : " + Build.VERSION.RELEASE +
                LINE_SEP + "Android SDK        : " + Build.VERSION.SDK_INT +
                LINE_SEP + "App VersionName    : " + ApplicationReference.getApp().getAppVersionName() +
                LINE_SEP + "App VersionCode    : " + ApplicationReference.getApp().getAppVersionCode() +
                LINE_SEP + "App UUID           : " + ApplicationReference.getApp().getUuid() +
                LINE_SEP + "************* Log Head ****************" + LINE_SEP + LINE_SEP
        inputToFile(head, filePath)
    }


    /**
     * 객체 정보를
     * String 으로 변환
     *
     * @param any
     * @return
     */
    private fun formaToString(any: Any): String? {
        return when (any) {
            is Array<*> -> any.contentToString()
            is List<*> -> any.toString()
            is Throwable -> throwableToString(any)
            is Bundle -> bundleToString(any)
            is Intent -> intentToString(any)
            else -> any.toString().jsonToString()
        }
    }

    /**
     * Throwable to String
     *
     * @return
     */
    private fun throwableToString(e: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        var cause = e.cause
        while (cause != null) {
            cause.printStackTrace(pw)
            cause = cause.cause
        }
        pw.flush()
        return sw.toString()
    }

    /**
     * Bundle 정보를
     * String 으로 변환
     *
     * @param bundle
     * @return
     */
    private fun bundleToString(bundle: Bundle): String {
        val iterator = bundle.keySet().iterator()
        if (!iterator.hasNext()) {
            return "Bundle {}"
        }

        val sb = StringBuilder(128)
        sb.append("Bundle { ")
        iterator.forEach { key ->
            val value = bundle.get(key)
            sb.append(key).append('=')
            value?.let {
                when (it) {
                    is Bundle -> sb.append(if (it == bundle) "(this Bundle)" else bundleToString(it))
                    else -> sb.append(formaToString(it))
                }
            }
            sb.append(',').append(' ')
        }
        return sb.append(" }").toString()
    }

    /**
     * Intent to String
     *
     * @param intent
     * @return
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun intentToString(intent: Intent): String? {
        val sb = java.lang.StringBuilder(128)
        sb.append("Intent { ")
        var first = true

        intent.action?.let {
            sb.append("act=").append(it)
            first = false
        }

        intent.categories?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cat=[")
            var firstCategory = true
            it.forEach { c ->
                if (!firstCategory) {
                    sb.append(',')
                }
                sb.append(c)
                firstCategory = false
            }
            sb.append("]")
        }

        intent.data?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("dat=").append(it)
        }

        intent.type?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("typ=").append(it)
        }

        val flags = intent.flags
        if (flags != 0) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("flg=0x").append(Integer.toHexString(flags))
        }

        intent.`package`?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("pkg=").append(it)
        }

        intent.component?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cmp=").append(it.flattenToShortString())
        }

        intent.sourceBounds?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("bnds=").append(it.toShortString())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.clipData?.let {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append(it.toAllString())
            }
        }

        intent.extras?.let {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("extras={")
            sb.append(bundleToString(it))
            sb.append('}')
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            intent.selector?.let {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("sel={")
                sb.append(if (it === intent) "(this Intent)" else intentToString(it))
                sb.append("}")
            }
        }
        sb.append(" }")
        return sb.toString()
    }

    /**
     * log filter 에 대한
     * description
     *
     * @param logFilter
     * @return
     */
    private fun getLogFilterName(logFilter: Int): String {
        return when (logFilter) {
            Log.VERBOSE -> LogFilter.VERBOSE.name
            Log.DEBUG -> LogFilter.DEBUG.name
            Log.INFO -> LogFilter.INFO.name
            Log.WARN -> LogFilter.WARN.name
            Log.ERROR -> LogFilter.ERROR.name
            Log.ASSERT -> LogFilter.ASSERT.name
            else -> ""
        }
    }

    /**
     * Kotlin Builder Pattern
     * 예) val config = LogUtil.ConfigBuilder().setIsLogCat(true).build()
     *
     * @property isLogCat Logcat에 로그를 출력할지 여부 설정
     * @property isSaveLogFile 파일로그를 생성할지 여부 설정
     * @property dir 로그 파일을 저장할 위치
     * @property logFileName 로그 파일명
     * @property globalTag null 값이 아니면 TAG 값을 global 값으로 사용
     * @property isBorder 로그 출력 시, 보더라인 표시 여부
     * @property consoleFilter 해당 filter 값 이상만 logcat 에 표시
     * @property fileFilter 해당 filter 값 이상만 로그파일에 표시
     * @property saveDays saveDays 만큼만 로그파일 유지
     */
    class Config internal constructor(val isLogCat: Boolean = false, val isSaveLogFile: Boolean = false,
                                      val dir: String? = null, val logFileName: String = DEFAULT_FILENAME,
                                      val globalTag: String? = null, val isBorder: Boolean = true,
                                      val consoleFilter: Int = Log.VERBOSE, val fileFilter: Int = Log.VERBOSE,
                                      val saveDays: Int = 1) {

        /**
         * 설정된 Config Builder 값
         *
         * @return ConfigBuilder
         */
        fun getBuilder(): ConfigBuilder {
            return ConfigBuilder(isLogCat, isSaveLogFile, dir, logFileName, globalTag, isBorder, consoleFilter, fileFilter, saveDays)
        }

        override fun toString(): String {
            return ("isLogCat: " + isLogCat.toString()
                    + LINE_SEP + "isSaveLogFile: " + isSaveLogFile.toString()
                    + LINE_SEP + "globalTag: " + globalTag
                    + LINE_SEP + "isBorder: " + isBorder.toString()
                    + LINE_SEP + "dir: " + dir
                    + LINE_SEP + "logFileName: " + logFileName
                    + LINE_SEP + "consoleFilter: " + getLogFilterName(consoleFilter)
                    + LINE_SEP + "fileFilter: " + getLogFilterName(fileFilter)
                    + LINE_SEP + "saveDays: " + saveDays.toString())
        }
    }

    /**
     * Config Builder
     *
     * @property isLogCat Logcat에 로그를 출력할지 여부 설정
     * @property isSaveLogFile 파일로그를 생성할지 여부 설정
     * @property dir 로그 파일을 저장할 위치
     * @property logFileName 로그 파일명
     * @property globalTag null 값이 아니면 TAG 값을 global 값으로 사용
     * @property isBorder 로그 출력 시, 보더라인 표시 여부
     * @property consoleFilter 해당 filter 값 이상만 logcat 에 표시
     * @property fileFilter 해당 filter 값 이상만 로그파일에 표시
     * @property saveDays saveDays 만큼만 로그파일 유지
     */
    data class ConfigBuilder(var isLogCat: Boolean = false, var isSaveLogFile: Boolean = false,
                             var dir: String? = null, var logFileName: String = DEFAULT_FILENAME,
                             var globalTag: String? = null, var isBorder: Boolean = true,
                             var consoleFilter: Int = Log.VERBOSE, var fileFilter: Int = Log.VERBOSE,
                             var saveDays: Int = 1) {
        fun setIsLogCat(isLogCat: Boolean) = apply { this.isLogCat = isLogCat }
        fun setIsSaveLogFile(isSaveLogFile: Boolean) = apply { this.isSaveLogFile = isSaveLogFile }
        fun setDir(dir: String?) = apply { this.dir = dir }
        fun setLogFileName(logFileName: String) = apply { this.logFileName = logFileName }
        fun setGlobalTag(globalTag: String?) = apply { this.globalTag = globalTag }
        fun setIsBorder(isBorder: Boolean) = apply { this.isBorder = isBorder }
        fun setConsoleFilter(consoleFilter: Int) = apply { this.consoleFilter = consoleFilter }
        fun setFileFilter(fileFilter: Int) = apply { this.fileFilter = fileFilter }
        fun setSaveDays(saveDays: Int) = apply { this.saveDays = saveDays }

        fun build() = Config(isLogCat, isSaveLogFile, dir, logFileName, globalTag, isBorder, consoleFilter, fileFilter, saveDays)
    }

    data class TagHead(val tag: String? = null, val consoleHead: List<String>? = null, val fileHead: String)
}


enum class LogFilter {
    VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT
}