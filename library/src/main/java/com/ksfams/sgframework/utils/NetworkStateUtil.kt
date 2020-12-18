package com.ksfams.sgframework.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import com.ksfams.sgframework.models.enums.MobileType
import com.ksfams.sgframework.models.enums.NetworkType
import com.ksfams.sgframework.modules.reference.ApplicationReference
import com.ksfams.sgframework.modules.system.connectivityManager
import com.ksfams.sgframework.modules.system.telephonyManager
import com.ksfams.sgframework.modules.system.wifiManager
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

/**
 *
 * 네트워크 상태 유틸
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
 * get state of network connected
 *
 * @receiver Context
 * @return true is network connected
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun Context.isConnected(): Boolean {
    val networkType = checkNetwork()
    return networkType != NetworkType.NONE && networkType != NetworkType.UNKNOWN
}

/**
 * wifi connections check
 *
 * @receiver Context
 * @return true is wifi connected
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun Context.isWifiConnected(): Boolean = checkNetwork() == NetworkType.WIFI

/**
 * mobile network connection check
 *
 * @receiver Context
 * @return true is mobile network connected
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun Context.isMobileConnected(): Boolean = checkNetwork() == NetworkType.MOBILE

/**
 * Return whether mobile data is enabled.
 *
 * @receiver Context
 * @return `true`: enabled<br></br>`false`: disabled
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE])
fun Context.getMobileDataEnabled(): Boolean {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return telephonyManager.isDataEnabled
        }

        @SuppressLint("PrivateApi")
        val getMobileDataEnabledMethod = telephonyManager.javaClass.getDeclaredMethod("getDataEnabled")
        return getMobileDataEnabledMethod.invoke(telephonyManager) as Boolean
    } catch (e: Exception) {
        LogUtil.e(e)
    }
    return false
}

/**
 * Set mobile data enabled.
 * Must hold `android:sharedUserId="android.uid.system"`,
 * `<uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />`
 *
 * @receiver Context
 * @param enabled True to enabled, false otherwise.
 */
@RequiresPermission(Manifest.permission.MODIFY_PHONE_STATE)
fun Context.setMobileDataEnabled(enabled: Boolean) {
    try {
        val setMobileDataEnabledMethod = telephonyManager.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
        setMobileDataEnabledMethod.invoke(telephonyManager, enabled)
    } catch (e: Exception) {
        LogUtil.e(e)
    }
}

/**
 * Return whether using mobile data.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
 *
 * @receiver Context
 * @return `true`: yes<br></br>`false`: no
 */
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isMobileData(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities?.let {
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
    }
    else {
        @Suppress("DEPRECATION")
        val info: android.net.NetworkInfo? = connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION")
        return (null != info && info.isAvailable
                && info.type == ConnectivityManager.TYPE_MOBILE)
    }
    return false
}

/**
 * Return whether wifi is enabled.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
 *
 * @receiver Context
 * @return `true`: enabled<br></br>`false`: disabled
 */
@RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
fun Context.getWifiEnabled(): Boolean = wifiManager.isWifiEnabled

/**
 * Set wifi enabled.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
 *
 * @receiver Context
 * @param enabled True to enabled, false otherwise.
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.CHANGE_WIFI_STATE)
fun Context.setWifiEnabled(enabled: Boolean) {
    if (enabled == wifiManager.isWifiEnabled) return
    wifiManager.isWifiEnabled = enabled
}

/**
 * Return whether wifi is available.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
 * `<uses-permission android:name="android.permission.INTERNET" />`
 *
 * @receiver Context
 * @return `true`: available<br></br>`false`: unavailable
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun Context.isWifiAvailable(): Boolean {
    return getWifiEnabled() && isAvailableByPing()
}

/**
 * 네트워크 연결 여부 및 연결 종류
 *
 * @receiver Context
 * @return NetworkType
 * <ul>
 * <li>{@link NetworkStateUtil.NetworkType#ETHERNET} </li>
 * <li>{@link NetworkStateUtil.NetworkType#WIFI} </li>
 * <li>{@link NetworkStateUtil.NetworkType#MOBILE} </li>
 * <li>{@link NetworkStateUtil.NetworkType#UNKNOWN} </li>
 * <li>{@link NetworkStateUtil.NetworkType#NONE} </li>
 * </ul>
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun Context.checkNetwork(): NetworkType {
    connectivityManager.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getNetworkCapabilities(activeNetwork)?.run {
                return when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
                    hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NetworkType.UNKNOWN
                    else -> NetworkType.NONE
                }
            }
        }
        else {
            @Suppress("DEPRECATION")
            activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                    ConnectivityManager.TYPE_MOBILE -> NetworkType.MOBILE
                    ConnectivityManager.TYPE_ETHERNET -> NetworkType.ETHERNET
                    ConnectivityManager.TYPE_BLUETOOTH -> NetworkType.UNKNOWN
                    else -> NetworkType.NONE
                }
            }
        }
    }

    return NetworkType.NONE
}

/**
 * 모바일 네트워크 연결 여부 및 연결 종류
 *
 * @receiver Context
 * @return MobileType
 * <ul>
 * <li>{@link NetworkStateUtil.MobileType#MOBILE_5G} </li>
 * <li>{@link NetworkStateUtil.MobileType#MOBILE_4G} </li>
 * <li>{@link NetworkStateUtil.MobileType#MOBILE_3G} </li>
 * <li>{@link NetworkStateUtil.MobileType#MOBILE_2G} </li>
 * <li>{@link NetworkStateUtil.MobileType#UNKNOWN} </li>
 * <li>{@link NetworkStateUtil.MobileType#NONE} </li>
 * </ul>
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE])
fun Context.checkMobile(): MobileType {
    if (checkNetwork() == NetworkType.MOBILE) {
        val type: Int? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            telephonyManager.dataNetworkType
        } else {
            @Suppress("DEPRECATION")
            val info: android.net.NetworkInfo? = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            info?.subtype
        }

        return when (type) {
            TelephonyManager.NETWORK_TYPE_GSM -> MobileType.MOBILE_2G
            TelephonyManager.NETWORK_TYPE_GPRS -> MobileType.MOBILE_2G
            TelephonyManager.NETWORK_TYPE_CDMA -> MobileType.MOBILE_2G
            TelephonyManager.NETWORK_TYPE_EDGE -> MobileType.MOBILE_2G
            TelephonyManager.NETWORK_TYPE_1xRTT -> MobileType.MOBILE_2G
            TelephonyManager.NETWORK_TYPE_IDEN -> MobileType.MOBILE_2G
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_EVDO_A -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_UMTS -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_HSDPA -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_HSUPA -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_HSPA -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_EVDO_B -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_EHRPD -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_HSPAP -> MobileType.MOBILE_3G
            TelephonyManager.NETWORK_TYPE_IWLAN -> MobileType.MOBILE_4G
            TelephonyManager.NETWORK_TYPE_LTE -> MobileType.MOBILE_4G
            else -> MobileType.UNKNOWN
        }
    }
    return MobileType.NONE
}

/**
 * Return the ip address.
 *
 * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
 *
 * @return the ip address
 */
@RequiresPermission(Manifest.permission.INTERNET)
fun getIPAddress(): String? {
    val inetAddress: InetAddress = getInetAddress() ?: return null
    return inetAddress.toString()
}

/**
 * Return the MAC address.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
 * `<uses-permission android:name="android.permission.INTERNET" />`
 *
 * @return the MAC address
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun getMacAddress(): String? {
    return getMacAddress(*(null as Array<String?>?)!!)
}

/**
 * Return the MAC address.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
 * `<uses-permission android:name="android.permission.INTERNET" />`
 *
 * @return the MAC address
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET])
fun getMacAddress(vararg excepts: String?): String? {
    var macAddress = ApplicationReference.getApp().getMacAddressByWifiInfo()
    if (isAddressNotInExcepts(macAddress, *excepts)) {
        return macAddress
    }
    macAddress = getMacAddressByNetworkInterface()
    if (isAddressNotInExcepts(macAddress, *excepts)) {
        return macAddress
    }
    macAddress = getMacAddressByInetAddress()
    if (isAddressNotInExcepts(macAddress, *excepts)) {
        return macAddress
    }
    macAddress = getMacAddressByFile()
    return if (isAddressNotInExcepts(macAddress, *excepts)) {
        macAddress
    } else ""
}

/**
 * Return whether network is available using ping.
 *
 * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
 *
 * The default ping ip: 223.5.5.5
 *
 * @return `true`: yes<br></br>`false`: no
 */
@RequiresPermission(Manifest.permission.INTERNET)
fun isAvailableByPing(): Boolean {
    return isAvailableByPing(null)
}

/**
 * Return whether network is available using ping.
 *
 * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
 *
 * @param ip The ip address.
 * @return `true`: yes<br></br>`false`: no
 */
@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
@RequiresPermission(Manifest.permission.INTERNET)
fun isAvailableByPing(ip: String?): Boolean {
    var pingIp = ip
    if (pingIp == null || pingIp.length <= 0) {
        pingIp = "223.5.5.5" // default ping ip
    }
    val result: CommandResult? = execCmd(String.format("ping -c 1 %s", pingIp), false)
    val ret = result?.result === 0
    result?.errorMsg?.let { LogUtil.d("isAvailableByPing() called $it") }
    result?.successMsg?.let { LogUtil.d("isAvailableByPing() called $it") }
    return ret
}


/**
 * is address not in excepts
 *
 * @param address String
 * @param excepts String?
 * @return Boolean
 */
private fun isAddressNotInExcepts(address: String, vararg excepts: String?): Boolean {
    if (excepts.isEmpty()) {
        return "02:00:00:00:00:00" != address
    }

    for (filter in excepts) {
        if (filter != null && address == filter) {
            return false
        }
    }
    return true
}

/**
 * get mac address by wifi info
 *
 * @receiver Context
 * @return String
 */
@SuppressLint("HardwareIds", "MissingPermission", "WifiManagerLeak")
private fun Context.getMacAddressByWifiInfo(): String {
    try {
        val info = wifiManager.connectionInfo
        if (info != null) return info.macAddress
    } catch (e: Exception) {
        LogUtil.e(e)
    }
    return "02:00:00:00:00:00"
}

/**
 * get mac address by network interface
 *
 * @return String
 */
private fun getMacAddressByNetworkInterface(): String {
    try {
        val nis = NetworkInterface.getNetworkInterfaces()
        while (nis.hasMoreElements()) {
            val ni = nis.nextElement()
            if (ni == null || !ni.name.equals("wlan0", ignoreCase = true)) continue
            val macBytes = ni.hardwareAddress
            if (macBytes != null && macBytes.size > 0) {
                val sb = StringBuilder()
                for (b in macBytes) {
                    sb.append(String.format("%02x:", b))
                }
                return sb.substring(0, sb.length - 1)
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return "02:00:00:00:00:00"
}

/**
 * get mac address by InetAddress
 *
 * @return String
 */
private fun getMacAddressByInetAddress(): String {
    try {
        val inetAddress: InetAddress? = getInetAddress()
        if (inetAddress != null) {
            val ni =
                    NetworkInterface.getByInetAddress(inetAddress)
            if (ni != null) {
                val macBytes = ni.hardwareAddress
                if (macBytes != null && macBytes.size > 0) {
                    val sb = StringBuilder()
                    for (b in macBytes) {
                        sb.append(String.format("%02x:", b))
                    }
                    return sb.substring(0, sb.length - 1)
                }
            }
        }
    } catch (e: java.lang.Exception) {
        LogUtil.e(e)
    }
    return "02:00:00:00:00:00"
}

/**
 * get mac address by file
 *
 * @return String
 */
@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
private fun getMacAddressByFile(): String {
    var result: CommandResult? = execCmd("getprop wifi.interface", false)
    if (result?.result === 0) {
        val name = result.successMsg
        result = execCmd("cat /sys/class/net/$name/address", false)
        if (result?.result === 0) {
            val address = result.successMsg
            if (address.isNotEmpty()) {
                return address
            }
        }
    }
    return "02:00:00:00:00:00"
}

/**
 * get ip address
 *
 * @return InetAddress?
 */
private fun getInetAddress(): InetAddress? {
    try {
        val nis = NetworkInterface.getNetworkInterfaces()
        while (nis.hasMoreElements()) {
            val ni = nis.nextElement()
            // To prevent phone of xiaomi return "10.0.2.15"
            if (!ni.isUp) continue
            val addresses = ni.inetAddresses
            while (addresses.hasMoreElements()) {
                val inetAddress = addresses.nextElement()
                if (!inetAddress.isLoopbackAddress) {
                    val hostAddress = inetAddress.hostAddress
                    if (hostAddress.indexOf(':') < 0) return inetAddress
                }
            }
        }
    } catch (e: SocketException) {
        LogUtil.e(e)
    }
    return null
}