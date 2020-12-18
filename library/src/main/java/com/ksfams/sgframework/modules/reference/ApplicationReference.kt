package com.ksfams.sgframework.modules.reference

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ksfams.sgframework.utils.LogUtil
import java.lang.NullPointerException
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * Application Reference Class
 * @JvmStatic : Java 에서 static 메소드로 사용할 수 있도록 처리됨.
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

object ApplicationReference {

    private var mApplication: Application? = null

    private val mActivityList: LinkedList<Activity> = LinkedList()
    private val mStatusListenerMap: MutableList<OnAppStatusChangedListener> = CopyOnWriteArrayList()

    private var mIsForeground: Boolean = false
    private var mPaused: Boolean = true

    private val mHandler = Handler(Looper.getMainLooper())
    private var checkRunnable: Runnable? = null


    /**
     * 초기화
     *
     * @param application
     */
    private fun init(application: Application?) {
        if (mApplication == null) {
            mApplication = application ?: getApplicationByReflect()
            mApplication?.registerActivityLifecycleCallbacks(mCallbacks)
        } else {
            application?.let { app ->
                if (app.javaClass != mApplication?.javaClass) {
                    mApplication?.unregisterActivityLifecycleCallbacks(mCallbacks)
                    mActivityList.clear()
                    mApplication = app
                    mApplication?.registerActivityLifecycleCallbacks(mCallbacks)
                }
            }
        }
    }

    /**
     * get Application
     *
     * @return
     */
    fun getApp(): Application {
        mApplication?.let {
            return it
        }

        val app = getApplicationByReflect()
        init(app)
        return app
    }


    fun getTopActivityOrApp(): Context {
        if (isForground()) {
            getTopActivity()?.let { return it }
        }
        return getApp()
    }


    fun addListener(listener: OnAppStatusChangedListener) {
        mStatusListenerMap.add(listener)
    }

    fun removeListener(listener: OnAppStatusChangedListener) {
        mStatusListenerMap.remove(listener)
    }

    fun isForground(): Boolean = mIsForeground

    /**
     * Top Activity 정보 추가
     * (Activity 리스트 추가)
     *
     * @param activity
     */
    private fun setTopActivity(activity: Activity) {
        if (mActivityList.contains(activity)) {
            if (mActivityList.last != activity) {
                mActivityList.remove(activity)
                mActivityList.addLast(activity)
            }
        } else {
            mActivityList.addLast(activity)
        }
    }

    fun getActivityList(): LinkedList<Activity> = mActivityList

    fun getTopActivity(): Activity? {
        if (mActivityList.isNotEmpty()) {
            mActivityList.last?.let { return it }
        }

        val topActivityByReflect = getTopActivityByReflect()
        topActivityByReflect?.let {
            setTopActivity(it)
        }
        return topActivityByReflect
    }


    @SuppressLint("PrivateApi")
    private fun getTopActivityByReflect(): Activity? {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField = activityThreadClass.getDeclaredField("mActivityList")
            activitiesField.isAccessible = true
            val activities: Map<*, *>? = activitiesField.get(activityThread) as Map<*, *>

            activities?.values?.forEach { activityRecode ->
                activityRecode?.let {
                    val activityRecordClass = it.javaClass
                    val pausedField = activityRecordClass.getDeclaredField("paused")
                    pausedField.isAccessible = true
                    if (!pausedField.getBoolean(it)) {
                        val activityField = activityRecordClass.getDeclaredField("activity")
                        activityField.isAccessible = true
                        return activityField.get(it) as Activity
                    }
                }
            }
        } catch (e: NoSuchMethodException) {
            LogUtil.e(contents = e)
        } catch (e: IllegalAccessException) {
            LogUtil.e(contents = e)
        } catch (e: InvocationTargetException) {
            LogUtil.e(contents = e)
        } catch (e: ClassNotFoundException) {
            LogUtil.e(contents = e)
        } catch (e: NoSuchFieldException) {
            LogUtil.e(contents = e)
        }

        return null
    }


    @SuppressLint("PrivateApi")
    private fun getApplicationByReflect(): Application {
        try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
            app?.let {
                return it as Application
            }
        } catch (e: NoSuchMethodException) {
            LogUtil.e(contents = e)
        } catch (e: IllegalAccessException) {
            LogUtil.e(contents = e)
        } catch (e: InvocationTargetException) {
            LogUtil.e(contents = e)
        } catch (e: ClassNotFoundException) {
            LogUtil.e(contents = e)
        }

        throw NullPointerException("You should init first")
    }


    /**
     * Lifecycle call back
     */
    private val mCallbacks = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            LogUtil.d(contents = "onActivityCreated: ${activity.javaClass.name}")
            setTopActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            LogUtil.d(contents = "onActivityStarted: ${activity.javaClass.name}")
            setTopActivity(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            LogUtil.d(contents = "onActivityResumed: ${activity.javaClass.name}")
            setTopActivity(activity)

            mPaused = false
            val wasBackground = !mIsForeground
            mIsForeground = true

            checkRunnable?.let { mHandler.removeCallbacks(it) }

            if (wasBackground) {
                LogUtil.d(contents = "${activity.javaClass.name} went foreground")
                if (mStatusListenerMap.isNotEmpty()) {
                    mStatusListenerMap.forEach {
                        it.onForeground(activity)
                    }
                }
            } else {
                LogUtil.d(contents = "${activity.javaClass.name} still foreground")
            }
        }

        override fun onActivityPaused(activity: Activity) {
            LogUtil.d(contents = "onActivityPaused: ${activity.javaClass.name}")

            mPaused = true
            checkRunnable?.let { mHandler.removeCallbacks(it) }

            checkRunnable = Runnable {
                if (mIsForeground && mPaused) {
                    mIsForeground = false
                    LogUtil.d(contents = "${activity.javaClass.name} went background")

                    if (mStatusListenerMap.isNotEmpty()) {
                        mStatusListenerMap.forEach {
                            it.onBackground(activity)
                        }
                    }
                } else {
                    LogUtil.d(contents = "${activity.javaClass.name} still foreground")
                }
            }
            mHandler.post(checkRunnable!!)
        }

        override fun onActivityStopped(activity: Activity) {
            LogUtil.d(contents = "onActivityStopped: ${activity.javaClass.name}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            LogUtil.d(contents = "onActivityDestroyed: ${activity.javaClass.name}")
            mActivityList.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }
    }



    /**
     * Application 에서
     * Foreground / Background 되는 Activity 체크
     *
     */
    interface OnAppStatusChangedListener {

        /**
         * Foreground 되는 Activity
         *
         * @param activity
         */
        fun onForeground(activity: Activity)

        /**
         * Background 되는 Activity
         *
         * @param activity
         */
        fun onBackground(activity: Activity)
    }
}
