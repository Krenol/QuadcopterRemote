package com.krenol.rpi.quadcopterremote

import android.util.Log
import java.lang.Exception
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.SSLSocketFactory

class Websocket(hostname: URI, onMsg: (msg: String?) -> Unit) : WebSocketClient(hostname) {
    private val TAG = "Websocket"
    var onMsgCb = onMsg
    var connected = AtomicBoolean(false)
        private set

    fun useSSLFactory() {
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        setSocketFactory(socketFactory)
    }
    
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i("$TAG-onOpen", "connecting to: ${handshakedata.toString()}")
        connected.set(true)
    }

    override fun onMessage(message: String?) {
        Log.d("$TAG-onMessage", "received message: $message")
        onMsgCb(message)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i("$TAG-onClose", "disconnected with reason $reason and code $code")
        connected.set(false)
    }

    override fun onError(ex: Exception?) {
        Log.e("$TAG-onError", ex.toString())
    }
}