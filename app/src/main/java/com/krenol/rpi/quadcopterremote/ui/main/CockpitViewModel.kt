package com.krenol.rpi.quadcopterremote.ui.main


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.wifi.WifiManager.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import com.krenol.rpi.quadcopterremote.listeners.JoystickListener
import com.krenol.rpi.quadcopterremote.listeners.SeekArcListener
import com.krenol.rpi.quadcopterremote.ui.components.AttitudeIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import com.krenol.rpi.quadcopterremote.DataClasses
import com.krenol.rpi.quadcopterremote.Prefs
import com.krenol.rpi.quadcopterremote.Websocket
import java.net.URI
import java.util.*


class CockpitViewModel(prefs: Prefs) : ViewModel() {
    private val TAG = "CockpitViewModel"
    private lateinit var mWsSocket: Websocket
    private var mTryConnect = AtomicBoolean(false)
    private var mDisconnect = AtomicBoolean(false)
    private val mGson = Gson()
    private var mWriter = Executors.newSingleThreadExecutor()
    var prefs: Prefs = prefs
        private set

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            location.value = locationResult!!.lastLocation
            Log.i("test", location.value.toString())
        }
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action: String? = intent.action
            val state: Int = intent.getIntExtra(
                EXTRA_WIFI_STATE,
                WIFI_STATE_DISABLED
            )
            if (action == WIFI_STATE_CHANGED_ACTION && state == WIFI_STATE_DISABLED) {
                onConnectionLost()
            } else if (action == WIFI_STATE_CHANGED_ACTION) {
                //TODO
            }
        }
    }

    val location = MutableLiveData<Location>()

    val cancelBtnClick = MutableLiveData(false)

    val showLoadingScreen = MutableLiveData(View.VISIBLE)

    val showCockpitScreen = MutableLiveData(View.GONE)

    val altitude = MutableLiveData(0.0)

    val attitude = MutableLiveData(AttitudeIndicator.Attitude(0.0F, 0.0F))

    val progressString = MutableLiveData("0")

    val throttleProgress = MutableLiveData(0)

    var yawn = MutableLiveData(0)

    val joystickDegrees = MutableLiveData(0.0F)

    val joystickOffset = MutableLiveData(0.0F)

    val seekArcListener = MutableLiveData(SeekArcListener(yawn))
    val joystickListener = MutableLiveData(JoystickListener(joystickDegrees, joystickOffset))



    fun onThrottleChange(seekBar: SeekBar?, progressValue: Int, fromUser: Boolean) {
        throttleProgress.value = progressValue
        progressString.value = progressValue.toString()
    }

    fun disconnect() {
        Log.i("$TAG-disconnect", "disconnecting from RPi")
        mDisconnect.set(true)
        mTryConnect.set(false)
        mWriter.shutdownNow()
        mWsSocket.close()
        Log.i("$TAG-disconnect", "successfully disconnected from RPi")
    }

    fun cancelConnection() {
        disconnect()
        cancelBtnClick.value = true
    }

    private fun send(msg: String) {
        if(mWsSocket.connected.get()) {
            mWsSocket.send(msg)
        } else {
            onConnectionLost()
        }
    }

    fun enqueueMessage() {
        val out = DataClasses.Output(
            throttleProgress.value,
            DataClasses.Joystick(
                joystickOffset.value,
                joystickDegrees.value,
                yawn.value
            ),
            DataClasses.GPS(
                location.value?.altitude,
                location.value?.latitude,
                location.value?.longitude
            )
        )
        val json = mGson.toJson(out)
        try{
            mWriter.submit { send(json.toString()) }
        } catch(e: Exception) {
            Log.e("$TAG-enqueueMessage", e.toString())
        }

    }

    fun connect() {
        if(mTryConnect.get()) return
        mDisconnect.set(false)
        prepareSocketConnection()
        viewModelScope.launch(Dispatchers.IO) {
            mTryConnect.set(true)
            val fullUrl = (if(prefs.wsSocket)  "ws" else  "wss") + "://${prefs.hostname}" +
                    (if(prefs.port.isBlank()) "" else ":${prefs.port}") +
                    (if(prefs.context.startsWith("/")) prefs.context else "/${prefs.context}")
            val url = URI(fullUrl)
            Log.i("$TAG-socketConnector", "Connect to $fullUrl")
            do {
                mWsSocket = Websocket(url, ::onMessageCb)
                if(!prefs.wsSocket) {
                    mWsSocket.useSSLFactory()
                }
                mWsSocket.connect()
                delay(500)
            } while(mTryConnect.get() && !mWsSocket.connected.get())
            if(mWsSocket.connected.get() && mTryConnect.get()) {
                onSocketConnection()
            }
            mTryConnect.set(false)
        }
    }
    private fun prepareSocketConnection() {
        showLoadingScreen.postValue(View.VISIBLE)
        showCockpitScreen.postValue(View.GONE)
    }

    private fun onSocketConnection(){
        Log.i("$TAG-onConnection", "connected successfully to RPi")
        if(mWriter.isShutdown || mWriter.isTerminated) {
            mWriter = Executors.newSingleThreadExecutor()
        }
        showLoadingScreen.postValue(View.GONE)
        showCockpitScreen.postValue(View.VISIBLE)
    }

    private fun onConnectionLost() {
        mWsSocket.close()
        if(!mDisconnect.get()) {
            this.connect()
        }
    }

    private fun onMessageCb(msg: String?) {
        val json = mGson.fromJson(msg, DataClasses.Dummy::class.java)
        altitude.postValue(json.throttle)
        //altitude.postValue(json.sensors.barometric_height)
        //attitude.postValue(AttitudeIndicator.Attitude(-json.angles.pitch, -json.angles.roll))
    }
}