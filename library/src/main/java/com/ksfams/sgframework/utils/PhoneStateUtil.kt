package com.ksfams.sgframework.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.MediaDrm
import android.os.Build
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import com.ksfams.sgframework.constants.BasePreferenceKeyConst
import com.ksfams.sgframework.constants.LINE_SEP
import com.ksfams.sgframework.modules.preference.Preference
import com.ksfams.sgframework.modules.system.locationManager
import com.ksfams.sgframework.modules.system.telephonyManager
import java.util.*

/**
 *
 * 단말기 상태 및 정보
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
 * Checks if GPS is Enabled or Not.
 *
 * @receiver Context
 * @return
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun Context.isGpsEnabled(): Boolean
        = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

/**
 * 디바이스 고유키값 중에
 * AndroidId 값 가져오기
 *
 * @receiver Context
 * @return
 */
@SuppressLint("HardwareIds")
fun Context.getAndroidId(): String
        = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)


/**
 * 디바이스 고유키값
 * Q 버전 미만
 *  Android ID 를 이용하므로 SignKey 가 동일하면 동일한 uuid 가 발생됨.
 *  Factory Reset 또는 Device 초기화 시, 값이 변경됨.
 * Q 버전 이상
 *  DRM Widevine ID 를 이용하므 package name 이 동일하면 동일한 uuid 가 발생됨.
 *  Factory Reset 또는 Device 초기화 하더라도 값이 유지됨.
 *  4.3 미만 버전 또는 구글 서비스가 탑재되지 않으면 uuid 값이 불가
 *
 * @receiver Context
 * @return
 */
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.getUuid(): String? = tryCatch {

    // 저장된 UUID 값
    var uuid = Preference.getInstance(this).get(BasePreferenceKeyConst.DEVICE_UUID, "")
    if (uuid.isEmpty()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Oreo 버전 이상인 경우는 DRM(Widevine ID) 를 사용한다.
            val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)

            val wvDrm = tryCatch {
                MediaDrm(WIDEVINE_UUID)
            }

            wvDrm!!.apply {
                val widevineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                uuid = UUID.nameUUIDFromBytes(widevineId).toString()
                wvDrm.close()
            }
        }
        else {
            val androidId = getAndroidId()
            uuid = UUID.randomUUID().toString()
            if ("9774d56d682e549c" != androidId) {
                uuid = UUID.nameUUIDFromBytes(androidId.toByteArray(Charsets.UTF_8)).toString()
            }
        }
        // UUID 저장
        Preference.getInstance(this).put(BasePreferenceKeyConst.DEVICE_UUID, uuid)
    }
    uuid
}

/**
 * 현재 로밍중인가?
 *
 * @receiver Context
 * @return Boolean
 */
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.isRoaming(): Boolean {
    return try {
        telephonyManager.isNetworkRoaming
    } catch (e: Exception) {
        false
    }
}

/**
 * 비행모드로 설정되어있는가?
 *
 * @return
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.isAirplaneMode(): Boolean {
    val settingValue: Int
    return try {
        settingValue = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            @Suppress("DEPRECATION")
            Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 0)
        } else {
            Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
        }
        settingValue == 1
    } catch (e: Settings.SettingNotFoundException) {
        false
    }
}

/**
 * Return whether sim card state is ready.
 *
 * @receiver Context
 * @return `true`: yes<br></br>`false`: no
 */
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.isSimCardReady(): Boolean {
    return telephonyManager.simState == TelephonyManager.SIM_STATE_READY
}

/**
 * 전화번호를 가져오는 메소드.(국가번호가 있는경우 그대로 들고온다)
 *
 * @receiver Context
 * @return
 */
@SuppressLint("HardwareIds")
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.getMyPhoneNumber(): String? = tryCatch {
    telephonyManager.line1Number
}

/**
 * 국가번호를 제외하고 전화번호를 가져온다.
 *
 * @receiver Context
 * @return
 */
@SuppressLint("HardwareIds")
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.getMyDigitPhoneNumber(): String {
    var phoneNum: String = getMyPhoneNumber() ?: ""

    // 전화번호가 없거나, 앞네자리가 '0'인 경우 없는 번호로 넘긴다.
    if (phoneNum.isEmpty() || phoneNum.length < 4 || "0000" == phoneNum.substring(0, 4)) {
        return ""
    }

    phoneNum = phoneNum.replace("+82", "0")
    return phoneNum
}

/**
 * Gets IMSI
 * android.permission.READ_PHONE_STATE 퍼미션을 추가시켜야한다.
 *
 * @receiver Context
 * @return
 */
@SuppressLint("HardwareIds", "MissingPermission")
fun Context.getIMSI(): String? = tryCatch {
    telephonyManager.subscriberId
}

/**
 * Gets the brand
 *
 * @receiver Context
 * @return the brand
 */
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.getBrand(): String? = tryCatch {
    telephonyManager.simOperatorName
}

/**
 * Gets the device model
 *
 * @return the device model
 */
fun getDeviceModel(): String = Build.MODEL

/**
 * 현재 통화중인지 여부
 *
 * @receiver Context
 * @return
 */
fun Context.isPhoneCall(): Boolean {
    return try {
        telephonyManager.callState != TelephonyManager.CALL_STATE_IDLE
    } catch (e: Exception) {
        false
    }
}

/**
 * Send sms silently.
 *
 * Must hold `<uses-permission android:name="android.permission.SEND_SMS" />`
 *
 * @receiver Context
 * @param phoneNumber The phone number.
 * @param content     The content.
 */
@RequiresPermission(Manifest.permission.SEND_SMS)
fun Context.sendSmsSilent(phoneNumber: String, content: String) {
    if (content.isEmpty()) return

    val sentIntent =
            PendingIntent.getBroadcast(this, 0, Intent("send"), 0)
    val smsManager = SmsManager.getDefault()
    if (content.length >= 70) {
        val ms: List<String> = smsManager.divideMessage(content)
        for (str in ms) {
            smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null)
        }
    } else {
        smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null)
    }
}

/**
 * Return the phone status.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
 *
 * @receiver Context
 * @return
 */
@SuppressLint("HardwareIds")
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun Context.getPhoneStatus(): String {
    val tm = this.telephonyManager
    var str = ""
    str += "DeviceSoftwareVersion = " + tm.deviceSoftwareVersion + LINE_SEP
    str += "Line1Number = " + tm.line1Number + LINE_SEP
    str += "NetworkCountryIso = " + tm.networkCountryIso + LINE_SEP
    str += "NetworkOperator = " + tm.networkOperator + LINE_SEP
    str += "NetworkOperatorName = " + tm.networkOperatorName + LINE_SEP

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        str += "NetworkType = " + tm.dataNetworkType + LINE_SEP
    } else {
        @Suppress("DEPRECATION")
        str += "NetworkType = " + tm.networkType + LINE_SEP
    }

    str += "PhoneType = " + tm.phoneType + LINE_SEP
    str += "SimCountryIso = " + tm.simCountryIso + LINE_SEP
    str += "SimOperator = " + tm.simOperator + LINE_SEP
    str += "SimOperatorName = " + tm.simOperatorName + LINE_SEP
    str += "SimState = " + tm.simState + LINE_SEP
    str += "VoiceMailNumber = " + tm.voiceMailNumber + LINE_SEP
    return str
}