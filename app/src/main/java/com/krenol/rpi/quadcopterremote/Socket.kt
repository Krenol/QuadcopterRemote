package com.krenol.rpi.quadcopterremote

import android.util.Log
import java.lang.Exception
import java.net.InetAddress

class Socket {
    private val TAG = "Socket"
    var socket = java.net.Socket()
        private set

    fun isConnected() : Boolean {
        return socket.isConnected && !socket.isClosed
    }


    fun connectToHost(hostname: String, port: Int) : Boolean {
        if(isConnected()) {
            disconnect()
        }

        try {
            socket = java.net.Socket(hostname, port)
        }
        catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return isConnected()
    }

    fun connectToIp(ip: String, port: Int) : Boolean {
        if(isConnected()) {
            disconnect()
        }

        try {
            val address = InetAddress.getByName(ip)
            socket = java.net.Socket(address, port)

        }
        catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return isConnected()
    }

    fun disconnect() {
        if(!isConnected()) return
        socket.close()
    }
}