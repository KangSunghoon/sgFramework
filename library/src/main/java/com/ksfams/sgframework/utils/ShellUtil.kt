package com.ksfams.sgframework.utils

import com.ksfams.sgframework.constants.LINE_SEP
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 *
 * Shell 명령어 실행
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
 * Execute the command.
 *
 * @param command  The command.
 * @param isRooted True to use root, false otherwise.
 * @return the single [CommandResult] instance
 */
fun execCmd(command: String, isRooted: Boolean): CommandResult?
        = execCmd(kotlin.arrayOf(command), isRooted, true)

/**
 * Execute the command.
 *
 * @param commands The commands.
 * @param isRooted True to use root, false otherwise.
 * @return the single [CommandResult] instance
 */
fun execCmd(commands: List<String>, isRooted: Boolean): CommandResult?
        = execCmd(commands, isRooted, true)

/**
 * Execute the command.
 *
 * @param commands The commands.
 * @param isRooted True to use root, false otherwise.
 * @return the single [CommandResult] instance
 */
fun execCmd(commands: Array<String>, isRooted: Boolean): CommandResult?
        = execCmd(commands, isRooted, true)

/**
 * Execute the command.
 *
 * @param command         The command.
 * @param isRooted        True to use root, false otherwise.
 * @param isNeedResultMsg True to return the message of result, false otherwise.
 * @return the single [CommandResult] instance
 */
fun execCmd(command: String, isRooted: Boolean, isNeedResultMsg: Boolean): CommandResult?
        = execCmd(kotlin.arrayOf(command), isRooted, isNeedResultMsg)

/**
 * Execute the command.
 *
 * @param commands        The commands.
 * @param isRooted        True to use root, false otherwise.
 * @param isNeedResultMsg True to return the message of result, false otherwise.
 * @return the single [CommandResult] instance
 */
fun execCmd(commands: List<String>, isRooted: Boolean, isNeedResultMsg: Boolean): CommandResult?
        = execCmd(commands.toTypedArray(), isRooted, isNeedResultMsg)

/**
 * Execute the command.
 *
 * @param commands        The commands.
 * @param isRooted        True to use root, false otherwise.
 * @param isNeedResultMsg True to return the message of result, false otherwise.
 * @return the single [CommandResult] instance
 */
fun execCmd(commands: Array<String>,
            isRooted: Boolean,
            isNeedResultMsg: Boolean): CommandResult? {
    var result = -1
    if (commands.isEmpty()) return CommandResult(result, "", "")

    var process: Process? = null
    var successResult: BufferedReader? = null
    var errorResult: BufferedReader? = null
    var successMsg: StringBuilder? = null
    var errorMsg: StringBuilder? = null
    var os: DataOutputStream? = null

    try {
        process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh")
        os = DataOutputStream(process.outputStream)
        commands.forEach {
            if (it.isEmpty()) return@forEach
            os.write(it.toByteArray())
            os.writeBytes(LINE_SEP)
            os.flush()
        }
        os.writeBytes("exit${LINE_SEP}")
        os.flush()
        result = process.waitFor()
        if (isNeedResultMsg) {
            successMsg = StringBuilder()
            errorMsg = StringBuilder()
            successResult = BufferedReader(
                    InputStreamReader(process.inputStream, "UTF-8")
            )
            errorResult = BufferedReader(
                    InputStreamReader(process.errorStream, "UTF-8")
            )
            var line: String?
            if (successResult.readLine().also { line = it } != null) {
                successMsg.append(line)
                while (successResult.readLine().also { line = it } != null) {
                    successMsg.append(LINE_SEP).append(line)
                }
            }
            if (errorResult.readLine().also { line = it } != null) {
                errorMsg.append(line)
                while (errorResult.readLine().also { line = it } != null) {
                    errorMsg.append(LINE_SEP).append(line)
                }
            }
        }
    } catch (e: Exception) {
        LogUtil.e(e)
    } finally {
        try {
            os?.close()
        } catch (e: IOException) {
            LogUtil.e(e)
        }
        try {
            successResult?.close()
        } catch (e: IOException) {
            LogUtil.e(e)
        }
        try {
            errorResult?.close()
        } catch (e: IOException) {
            LogUtil.e(e)
        }
        process?.destroy()
    }

    return CommandResult(result,
            successMsg?.toString() ?: "",
            errorMsg?.toString() ?: "")
}


/**
 * The result of command.
 */
class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {

    override fun toString(): String {
        return "result: ${result}${LINE_SEP}" +
                "successMsg: ${successMsg}${LINE_SEP}" +
                "errorMsg: ${errorMsg}${LINE_SEP}"
    }
}