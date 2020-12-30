package com.krenol.rpi.quadcopterremote

import android.content.SharedPreferences

class Prefs(pref: SharedPreferences) {
    val hostname: String = pref.getString("hostname", "raspberrypi").toString()
    val port: Int = pref.getString("port", "8889").toString().toInt()
    val ip: String = pref.getString("ip", "192.168.1.1").toString()
    val delimiter: String = "\r\n" //pref.getString("delimiter", "\r\n").toString().replace("\\\\", "\\")
    val useHostname: Boolean = pref.getString("connection_type", "1").toString() == "1"
}