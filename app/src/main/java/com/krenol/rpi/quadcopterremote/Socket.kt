package com.krenol.rpi.quadcopterremote

import android.util.Log
import java.lang.Exception
import java.net.InetAddress

class Socket {
    private val TAG = "Socket"
    var socket = java.net.Socket()
        private set

    val connected = socket.isConnected && !socket.isClosed



    fun connect(hostname: String, port: Int) : Boolean {
        if(connected) {
            disconnect()
        }

        try {
            val address = InetAddress.getByName(hostname)
            socket = java.net.Socket(address, port)

        }
        catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return connected
    }

    fun disconnect() {
        if(!connected) return
        socket.close()
    }
}