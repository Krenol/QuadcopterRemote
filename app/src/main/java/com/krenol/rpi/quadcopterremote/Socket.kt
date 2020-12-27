package com.krenol.rpi.quadcopterremote

import android.util.Log
import java.lang.Exception
import java.net.InetAddress

class Socket {
    var mSocket = java.net.Socket()
        private set

    val connected = mSocket.isConnected && !mSocket.isClosed



    fun connect(hostname: String, port: Int) : Boolean {
        if(connected) {
            disconnect()
        }

        try {
            val address = InetAddress.getByName(hostname)
            mSocket = java.net.Socket(address, port)

        }
        catch (e: Exception) {
            Log.e("Socket", e.toString())
        }
        return connected
    }

    fun disconnect() {
        if(!connected) return
        mSocket.close()
    }
}