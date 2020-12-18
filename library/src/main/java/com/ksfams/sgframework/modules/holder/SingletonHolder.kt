package com.ksfams.sgframework.modules.holder

/**
 *
 * 싱글톤을 lazy하게 생성하고 초기화하기 위해서
 * 인수와 함께 로직을 SingletonHolder 클래스 안에 캡슐화
 *
 * @volatile
 * volatile은 컴파일러에게 해당하는 변수에 대하여 최적화(Optimize)를 하지 않게 하는 키워드임.
 * volatile이 선언되어있지 않은 변수는 값이 변경되면 register에 cache된 상태에서 실질적인 메모리로의 반영이 되지 않는 경우가 생김.
 * 단일 쓰레드의 프로그램이라면 별 상관 없겠지만, 다중쓰레드 프로그램일때, 여러 쓰레드가 한 변수에 접근한다면, 문제가 발생 할 수 있음.
 *
 * volatile & synchronized
 * synchronized는 코드 블록을 한 번에 한 쓰레드만 실행하도록 제한함.
 * volatile은 변수를 접근할 때 캐시를 사용하지 않고 무조건 메모리에서 읽어오도록 함.
 * 값이 여기저기서 바뀌다 보면 쓰레드마다 캐시 내용이 달라서 불일치가 생길 수도 있으므로 보통 같이 사용됨.
 *
 * 싱글톤 정의
 * companion object : SingletonHolder<Preference, Context>(::Preference)
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

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}