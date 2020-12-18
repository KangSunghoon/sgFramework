package com.ksfams.sgframework.modules.extras

import java.util.*

/**
 *
 * Memory Cache 처리/관리
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

object InMemoryCache {

    private val map = WeakHashMap<String, Any?>()


    /**
     * put [key] & [value] where
     *
     * @param key is the String to get the value from anywhere, If you have the key then you can get the value. So keep it safe.
     * @param value
     * @return
     */
    fun put(key: String, value: Any?): InMemoryCache {
        map[key] = value
        return this
    }

    /**
     * get the saved value addressed by the key
     *
     * @param key
     * @return
     */
    fun get(key: String): Any? = map[key]

    /**
     * check if have the value on the Given Key
     *
     * @param key
     */
    fun contains(key: String) = map.contains(key)

    /**
     * Clear all the InMemoryCache
     *
     */
    fun clear() = map.clear()

    /**
     * get All The InMemoryCache
     *
     */
    fun getAll() = map.toMap()

    /**
     * get All the InMemoryCache of an Specific Type.
     *
     * @param clazz
     */
    fun getAllByType(clazz: Class<*>) = getAll().filter {
        it.value != null && it.value!!::class.java == clazz
    }
}