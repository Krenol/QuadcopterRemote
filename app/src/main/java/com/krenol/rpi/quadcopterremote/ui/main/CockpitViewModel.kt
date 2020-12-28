package com.krenol.rpi.quadcopterremote.ui.main


import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.krenol.rpi.quadcopterremote.Socket
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


class CockpitViewModel : ViewModel() {
    private val TAG = "CockpitViewModel"
    private var mSocket: Socket = Socket()
    private var mTryConnect = AtomicBoolean(false)
    private var mReadData = AtomicBoolean(false)
    private val mGson = Gson()
    private val mStpe = Executors.newSingleThreadExecutor()
    lateinit var prefs: Prefs

    var cancelBtnClick = MutableLiveData(false)
        private set

    var showLoadingScreen = MutableLiveData(View.VISIBLE)
        private set
    var showCockpitScreen = MutableLiveData(View.GONE)
        private set

    var altitude = MutableLiveData(0.0)
        private set

    var attitude = MutableLiveData(AttitudeIndicator.Attitude(0.0F, 0.0F))
        private set

    var progressString = MutableLiveData("0")
        private set

    var throttleProgress = MutableLiveData(0)
        private set
    var yawn = MutableLiveData(0)
        private set
    var joystickDegrees = MutableLiveData(0.0F)
        private set
    var joystickOffset = MutableLiveData(0.0F)
        private set

    val seekArcListener = MutableLiveData(SeekArcListener(yawn))
    val joystickListener = MutableLiveData(JoystickListener(joystickDegrees, joystickOffset))



    fun onThrottleChange(seekBar: SeekBar?, progressValue: Int, fromUser: Boolean) {
        throttleProgress.value = progressValue
        progressString.value = progressValue.toString()
    }

    fun disconnect() {
        mTryConnect.set(false)
        mReadData.set(false)
        mStpe.shutdownNow()
        mSocket.disconnect()
    }

    fun cancelConnection() {
        disconnect()
        cancelBtnClick.value = true
    }

    private fun send(msg: String) {
        if(mSocket.isConnected()){
            try {
                mSocket.socket.getOutputStream().write((msg + prefs.delimiter).toByteArray())
            } catch (e: Exception) {
                Log.e("$TAG-send", e.toString())
                mSocket.disconnect()
                this.connect()
            }
        }
    }

    fun createEnqueueMessage() {
        val out = DataClasses.Output(
            throttleProgress.value, DataClasses.Joystick(
                joystickOffset.value,
                joystickDegrees.value,
                yawn.value
            )
        )
        val json = mGson.toJson(out)
        mStpe.submit { send(json.toString()) }
    }

    fun connect() {
        if(mTryConnect.get()) return
        mReadData.set(false)
        showLoadingScreen.postValue(View.VISIBLE)
        showCockpitScreen.postValue(View.GONE)
        viewModelScope.launch(Dispatchers.IO) {
            mTryConnect.set(true)

            while(mTryConnect.get() && !mSocket.isConnected()){
                if(prefs.useHostname){
                    Log.i(TAG, "Connect via hostname")
                    mSocket.connectToHost(prefs.hostname, prefs.port)
                } else {
                    Log.i(TAG, "Connect via IP")
                    mSocket.connectToIp(prefs.ip, prefs.port)
                }
                delay(500)
            }
            if(mSocket.isConnected() && mTryConnect.get()) {
                Log.i(TAG, "connected successfully to RPi")
                showLoadingScreen.postValue(View.GONE)
                showCockpitScreen.postValue(View.VISIBLE)
                mReadData.set(true)
                read()
            }
            mTryConnect.set(false)
        }
    }

    val x = '\r'
    val y = '\n'
    val d = x.toString() + y.toString()
    private fun parseAndSet(buf: String){
        if(buf.contains(d)){
            val msg = buf
            msg.substringBefore(d)
            buf.substringAfter(d)
            val json = mGson.fromJson(msg, DataClasses.Input::class.java)
            altitude.postValue(json.sensors.barometric_height)
            attitude.postValue(AttitudeIndicator.Attitude(json.angles.pitch, json.angles.roll))
        }
    }

    private fun read(){
        viewModelScope.launch(Dispatchers.IO) {
            val stream = mSocket.socket.getInputStream()
            val buffer = ByteArray(prefs.bytes)
            var buf = ""
            while(mReadData.get() && mSocket.isConnected()){
                try{
                    stream.read(buffer)
                    buf += String(buffer)
                    parseAndSet(buf)
                } catch(e: Exception) {
                    Log.e("$TAG-read", e.toString())
                }
                delay(50)
            }

        }
    }
}