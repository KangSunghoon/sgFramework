package com.ksfams.sgframework.utils

import com.ksfams.sgframework.models.enums.ContentOrder

/**
 *
 * Collection 관련 유틸
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
 * Return an immutable list containing vararg parameter
 * <T> the class of the objects in the list
 *
 * @param items Array<out T> the objects to be stored in the returned list
 * @return List<T>
 */
fun <T> listOf(vararg items: T): List<T> = items.toList()

/**
 * Return an ArrayLIit containing vararg parameter
 * <T> The class of the objects in the list
 *
 * @param items Array<out T> the objects to be stored in the returned list
 * @return ArrayList<(T..T?)>
 */
fun <T> arrayListOf(vararg items: T) = java.util.ArrayList(items.toList())

/**
 * Return an Array<T> containing vararg parameter
 * <T> The class of the objects in the list
 *
 * @param items Array<out T> the objects to be stored in the returned list
 * @return Array<T>
 */
inline fun <reified T> arrayOf(vararg items: T) = items.toList().toTypedArray()

/**
 * Test if given all of vararg parameter are match given predicate
 * <T> The class of the objects
 *
 * Usages
 * testWithParams({ it.length > 3 }, "Steve", "Bobs") -> true
 * testWithParams({ it.length > 3 }, "Steve", "Bob") -> false
 *
 * @param predicate Function1<T, Boolean> Higher-order function that given single <T> and return as Boolean
 * @param items Array<out T> the objects to be stored in the returned list
 * @return Boolean
 */
fun <T> matchAllPredicate(predicate: (T) -> Boolean, vararg items: T): Boolean {
    return items.map { predicate(it) }.all { true }
}

/**
 * Test if given any of vararg parameter are match given predicate
 * <T> The class of the objects
 *
 * Usages
 * testWithParams({ it.length > 3 }, "Steve", "Bobs") -> true
 * testWithParams({ it.length > 3 }, "Steve", "Bob") -> false
 *
 * @param predicate Function1<T, Boolean> Higher-order function that given single <T> and return as Boolean
 * @param items Array<out T> the objects to be stored in the returned list
 * @return Boolean
 */
fun <T> matchAnyPredicate(predicate: (T) -> Boolean, vararg items: T): Boolean {
    return items.map { predicate(it) }.any { true }
}

/**
 * 중복되는 데이터는 제거하고
 * 오름차순 / 내림차순 정렬함
 *
 * @receiver ArrayList<T>
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @return ArrayList<T>
 */
fun <T: Comparable<T>> ArrayList<T>.deduplication(orderBy: ContentOrder = ContentOrder.ASCENDING): ArrayList<T> {
    // Set 데이터 형태로 생성되면서 중복 제거됨
    val set = this.toSet()

    // 중복 제거된 데이터를 정렬함.
    val newArrayList = ArrayList(set)
    if (orderBy == ContentOrder.ASCENDING) {
        newArrayList.sort()
    } else {
        newArrayList.sortDescending()
    }
    return newArrayList
}

/**
 * 중복되는 데이터는 제거하고
 * 오름차순 / 내림차순 정렬함
 *
 * @receiver List<T>
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @return List<T>
 */
fun <T: Comparable<T>> List<T>.deduplication(orderBy: ContentOrder = ContentOrder.ASCENDING): List<T> {
    // Set 데이터 형태로 생성되면서 중복 제거됨
    val set = this.toSet()

    // 중복 제거된 데이터를 정렬함.
    val newArrayList = ArrayList(set)
    if (orderBy == ContentOrder.ASCENDING) {
        newArrayList.sort()
    } else {
        newArrayList.sortDescending()
    }
    return newArrayList
}

/**
 * 중복되는 데이터는 제거하고
 * 오름차순 / 내림차순 정렬함
 *
 * @receiver Array<T>
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @return Array<T>
 */
inline fun <reified T: Comparable<T>> Array<T>.deduplication(orderBy: ContentOrder = ContentOrder.ASCENDING): Array<T> {
    // Set 데이터 형태로 생성되면서 중복 제거됨
    val set = this.toSet()

    // 중복 제거된 데이터를 정렬함.
    val newArray = set.toTypedArray()
    if (orderBy == ContentOrder.ASCENDING) {
        newArray.sortedArray()
    } else {
        newArray.sortedArrayDescending()
    }
    return newArray
}


/**
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver ArrayList<String>
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems ArrayList<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun ArrayList<String>.toString(expression: String = ",",
                               orderBy: ContentOrder,
                               exceptString: String? = null,
                               addItems: ArrayList<String>? = null): String? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    addItems?.let {
        this.addAll(it)
    }

    val sortList = deduplication(orderBy)

    // expression 을 포함한 문자열로 제조합.
    val sb = StringBuilder()
    sortList.forEach {
        // 빈값과 exceptString 은 제외
        if (it.isNotEmpty() && it != exceptString) {
            sb.append(it)
            sb.append(expression)
        }
    }

    // 맨마지막 expression 문자는 제거함.
    val result = sb.toString()
    return result.substring(0, result.length - expression.length)
}

/**
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver MutableList<String>
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems List<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun MutableList<String>.toString(expression: String = ",",
                                 orderBy: ContentOrder,
                                 exceptString: String? = null,
                                 addItems: List<String>? = null): String? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    addItems?.let {
        this.addAll(it)
    }

    val sortList = deduplication(orderBy)

    // expression 을 포함한 문자열로 제조합.
    val sb = StringBuilder()
    sortList.forEach {
        // 빈값과 exceptString 은 제외
        if (it.isNotEmpty() && it != exceptString) {
            sb.append(it)
            sb.append(expression)
        }
    }

    // 맨마지막 expression 문자는 제거함.
    val result = sb.toString()
    return result.substring(0, result.length - expression.length)
}

/**
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver Array<String>
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems Array<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun Array<String>.toString(expression: String = ",",
                           orderBy: ContentOrder,
                           exceptString: String? = null,
                           addItems: Array<String>? = null): String? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    val mutableList = this.toMutableList()
    addItems?.let {
        mutableList.addAll(it)
    }

    var sortList = mutableList.toTypedArray()
    sortList = sortList.deduplication(orderBy)

    // expression 을 포함한 문자열로 제조합.
    val sb = StringBuilder()
    sortList.forEach {
        // 빈값과 exceptString 은 제외
        if (it.isNotEmpty() && it != exceptString) {
            sb.append(it)
            sb.append(expression)
        }
    }

    // 맨마지막 expression 문자는 제거함.
    val result = sb.toString()
    return result.substring(0, result.length - expression.length)
}

/**
 * expression(,) 구분자로 문자열을 분리하여
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver String
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems Array<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun String.addArrayToString(expression: String = ",",
                            orderBy: ContentOrder,
                            exceptString: String? = null,
                            addItems: Array<String>? = null): String? {
    if (this.isEmpty()) return null

    val list = this.split(expression).toTypedArray()
    return list.toString(expression, orderBy, exceptString, addItems)
}

/**
 * expression(,) 구분자로 문자열을 분리하여
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver String
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems ArrayList<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun String.addArrayListToString(expression: String = ",",
                                orderBy: ContentOrder,
                                exceptString: String? = null,
                                addItems: ArrayList<String>? = null): String? {
    if (this.isEmpty()) return null

    val list = this.split(expression)
    val arrayList = ArrayList(list)
    return arrayList.toString(expression, orderBy, exceptString, addItems)
}

/**
 * expression(,) 구분자로 문자열을 분리하여
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver String
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems List<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun String.addListToString(expression: String = ",",
                           orderBy: ContentOrder,
                           exceptString: String? = null,
                           addItems: List<String>? = null): String? {
    if (this.isEmpty()) return null

    val list = this.split(expression)
    val arrayList = ArrayList(list)
    return arrayList.toString(expression, orderBy, exceptString, addItems)
}

/**
 * expression(,) 구분자로 문자열을 분리하여
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 배열을 리턴
 *
 * @receiver String
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems Array<String>? 리스트에 새롭게 추가할 items
 * @return Array<String>?
 */
fun String.toArray(expression: String = ",",
                   orderBy: ContentOrder,
                   exceptString: String? = null,
                   addItems: Array<String>? = null): Array<String>? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    val mutableList = this.split(expression).toMutableList()
    addItems?.let {
        mutableList.addAll(it)
    }

    // 빈값과 exceptString 은 제외
    for (i in mutableList.indices) {
        val item = mutableList[i]
        if (item.isEmpty() || item == exceptString) {
            mutableList.removeAt(i)
        }
    }

    val sortList = mutableList.toTypedArray()
    return sortList.deduplication(orderBy)
}

/**
 * expression(,) 구분자로 문자열을 분리하여
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 리스트를 리턴
 *
 * @receiver String
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems ArrayList<String>? 리스트에 새롭게 추가할 items
 * @return ArrayList<String>?
 */
fun String.toArrayList(expression: String = ",",
                       orderBy: ContentOrder,
                       exceptString: String? = null,
                       addItems: ArrayList<String>? = null): ArrayList<String>? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    val list = this.split(expression)
    val arrayList = ArrayList(list)
    addItems?.let {
        arrayList.addAll(it)
    }

    // 빈값과 exceptString 은 제외
    for (i in arrayList.indices) {
        val item = arrayList[i]
        if (item.isEmpty() || item == exceptString) {
            arrayList.removeAt(i)
        }
    }

    return arrayList.deduplication(orderBy)
}

/**
 * expression(,) 구분자로 문자열을 분리하여
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * 오름차순 / 내림차순 정렬한 다음
 * expression(,) 구분자로 나열된 리스트를 리턴
 *
 * @receiver String
 * @param expression String 구분자
 * @param orderBy ContentOrder 정렬방법: ContentOrder.ASCENDING (오름차순), ContentOrder.DESCENDING (내림차순)
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems List<String>? 리스트에 새롭게 추가할 items
 * @return List<String>?
 */
fun String.toArrayList(expression: String = ",",
                       orderBy: ContentOrder,
                       exceptString: String? = null,
                       addItems: List<String>? = null): List<String>? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    val list = this.split(expression)
    val arrayList = ArrayList(list)
    addItems?.let {
        arrayList.addAll(it)
    }

    // 빈값과 exceptString 은 제외
    for (i in arrayList.indices) {
        val item = arrayList[i]
        if (item.isEmpty() || item == exceptString) {
            arrayList.removeAt(i)
        }
    }

    return arrayList.deduplication(orderBy)
}


/**
 * 중복되는 데이터 제거,
 * exceptString 문자열 제거,
 * expression(,) 구분자로 나열된 문자열을 리턴
 *
 * @receiver ArrayList<String>
 * @param expression String 구분자
 * @param exceptString String? 리스트에서 제외할 문자열
 * @param addItems ArrayList<String>? 리스트에 새롭게 추가할 items
 * @return String?
 */
fun ArrayList<String>.toString(expression: String = ",",
                               exceptString: String? = null,
                               addItems: ArrayList<String>? = null): String? {
    if ((addItems == null || addItems.isEmpty())
            && this.isEmpty()) {
        return null
    }

    addItems?.let {
        this.addAll(it)
    }

    // expression 을 포함한 문자열로 제조합.
    val sb = StringBuilder()
    this.forEach {
        // 빈값과 exceptString 은 제외
        if (it.isNotEmpty() && it != exceptString) {
            sb.append(it)
            sb.append(expression)
        }
    }

    // 맨마지막 expression 문자는 제거함.
    val result = sb.toString()
    return result.substring(0, result.length - expression.length)
}