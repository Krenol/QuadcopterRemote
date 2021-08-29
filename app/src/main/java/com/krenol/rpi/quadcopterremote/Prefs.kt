package com.krenol.rpi.quadcopterremote

import android.content.SharedPreferences

class Prefs(pref: SharedPreferences) {
    val hostname: String = pref.getString("hostname", "raspberrypi").toString()
    val port: String = pref.getString("port", "8889").toString()
    val context: String = pref.getString("context", "/").toString()
    val wsSocket: Boolean = pref.getString("socket_type", "1").toString() == "1"
}