package com.ksfams.sgframework.utils

import com.ksfams.sgframework.modules.extras.InMemoryCache
import java.io.Closeable
import java.lang.Exception

/**
 *
 * 공통 Util
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
 * [valueToCompare] 객체와 동일한 자료형(객체)이면
 * block 을 실행
 *
 * @param T
 * @param valueToCompare
 * @param block
 */
fun <T> T.ifIs(valueToCompare: T?, block: T.() -> Unit) {
    if (this == valueToCompare) block(this)
}

/**
 * [valueToCompare] 객체와 동일하지 않은 자료형(객체)이면
 * block 을 실행
 *
 * @param T
 * @param valueToCompare
 * @param block
 */
fun <T> T.ifIsNot(valueToCompare: T?, block: (T) -> Unit) {
    if (this != valueToCompare) block(this)
}


/**
 * put Something In Memory to use it later
 *
 * @param key
 * @param value
 */
fun putInMemory(key: String, value: Any?): InMemoryCache = InMemoryCache.put(key, value)

/**
 * get Saved Data from memory, null if it os not exists
 *
 * @param key
 * @return
 */
fun getFromMemory(key: String): Any? = InMemoryCache.get(key)

/**
 * try .. catch
 * 블럭 처리
 *
 * @receiver T
 * @param block Function1<T, R>
 * @return R?
 */
inline fun <T, R> T.tryCatch(block: (T) -> R): R? {
    return try {
        block(this)
    } catch (e: Exception) {
        LogUtil.e(e)
        null
    }
}


/**
 * Try Catch within a single line
 *
 * @param block Function0<Unit>
 * @param catch Function1<[@kotlin.ParameterName] Throwable?, Unit>?
 * @param finally Function0<Unit>?
 * @return Unit?
 */
@JvmOverloads
fun tryAndCatch(block: () -> Unit,
                catch: ((e: Throwable?) -> Unit)? = null,
                finally: (() -> Unit)? = null) =
        try {
            block.invoke()
        } catch (e: Throwable) {
            catch?.invoke(e)
        } finally {
            finally?.invoke()
        }

/**
 * Close All the CLosable safely.
 *
 * @param closeables Array<out Closeable>
 */
fun closeSafely(vararg closeables: Closeable) {
    closeables.forEach {
        tryAndCatch(block = { it.close() })
    }
}

/**
 * Use block for autocloseables
 *
 * @receiver T
 * @param block Function1<T, R>
 * @return R
 */
@Throws(Exception::class)
inline fun <T: AutoCloseable, R> T.use(block: (T) -> R): R? {
    var closed = false
    return try {
        block(this)
    } catch (e: Exception) {
        try {
            close()
            closed = true
        } catch (closeException: Exception) {
            e.addSuppressed(closeException)
        }
        LogUtil.e(e)
        null
    } finally {
        if (!closed) {
            close()
        }
    }
}

/**
 * guardRun will run your code and returns True if it executed Properly else false.
 *
 * @param runnable Function0<Unit>
 * @return Boolean
 */
fun guardRun(runnable: () -> Unit): Boolean = try {
    runnable()
    true
} catch (ignore: Exception) {
    LogUtil.e(ignore)
    false
}

/**
 * helper Function to Cast things
 *
 * @receiver Any
 * @param clazz Class<T>
 * @return T?
 */
fun <T> Any.cast(clazz: Class<T>): T? = clazz.cast(this)

/**
 * is not null run runnable
 *
 * @receiver T?
 * @param runnable Function1<[@kotlin.ParameterName] T, Unit>
 * @return Unit?
 */
fun <T: Any?> T?.isNotNull(runnable: (it: T) -> Unit) = this?.let {
    runnable(it)
}
