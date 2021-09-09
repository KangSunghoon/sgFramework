package com.ksfams.sgframework.modules.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ksfams.sgframework.modules.holder.SingletonHolder
import com.ksfams.sgframework.utils.copy
import com.ksfams.sgframework.utils.createOrExistsDir
import com.ksfams.sgframework.utils.getDumpDirectory
import java.io.File

/**
 *
 * SharedPreferences 처리
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

class Preference private constructor(private val context: Context) {

    /**
     * 싱글톤 정의
     */
    companion object : SingletonHolder<Preference, Context>(::Preference)

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor: SharedPreferences.Editor = preferences.edit()

    init {
        editor.apply()
    }

    /**
     * delete key-value from SharedPreference
     *
     * @param key key to delete
     */
    fun delete(key: String) = editor.remove(key).commit()

    /**
     * clear all of preferences
     *
     */
    fun clear() = editor.clear().commit()

    /**
     * put data to SharedPreferences
     *
     * @param key String key of preference
     * @param value T value to input
     * @return Boolean
     */
    fun <T> put(key: String, value: T) = with(editor) {
        when (value) {
            is String -> putString(key, value)
            is CharSequence -> putString(key, value.toString())
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Double -> putString(key, value.toString())
            is Char -> putString(key, value.toString())
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.commit()
    }

    /**
     * get data from SharedPreferences
     *
     * @param T data type
     * @param key key of preference
     * @param default optional, if key is not presented or some unexpected problem happened, it will be return
     * @return data type value
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T): T = with(preferences) {
        val result: Any = when (default) {
            is String -> getString(key, default) ?: ""
            is CharSequence -> getString(key, default.toString()) ?: ""
            is Boolean -> getBoolean(key, default)
            is Int -> getInt(key, default)
            is Long -> getLong(key, default)
            is Float -> getFloat(key, default)
            is Double -> getString(key, default.toString())?.toDouble() ?: 0
            is Char -> getString(key, default.toString())?.get(0) ?: ""
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        result as T
    }


    /**
     * SharedPreferences dump 처리
     * destPath 정보가 없으면 App Temp 디렉토레에 복사
     *
     * @param destPath 복사할 directory
     */
    @SuppressLint("SdCardPath")
    fun dumpFile(destPath: String? = null) {
        val fileName = "${this.context.packageName}_preferences.xml"
        val srcFile =
                File("/data/data/${this.context.packageName}/shared_prefs/${fileName}")

        if (destPath == null) {
            srcFile.copy(File(this.context.getDumpDirectory(), fileName))
        } else {
            destPath.createOrExistsDir()
            srcFile.copy(File(destPath, fileName))
        }
    }
}