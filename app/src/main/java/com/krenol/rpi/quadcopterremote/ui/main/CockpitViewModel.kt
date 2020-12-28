package com.krenol.rpi.quadcopterremote.ui.main


import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
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


class CockpitViewModel : ViewModel() {
    private val TAG = "CockpitViewModel"
    private var mSocket: Socket = Socket()
    private var mTryConnect = AtomicBoolean(false)
    private var mReadData = AtomicBoolean(false)
    private val mGson = Gson()
    private val mStpe = Executors.newSingleThreadExecutor()
    val mPrefs = PreferenceManager.getDefaultSharedPreferences(null)
    val mHostname: String = mPrefs.getString("hostname", "raspberrypi").toString()
    val mPort: Int = mPrefs.getString("port", "8889").toString().toInt()
    val mIp: String = mPrefs.getString("ip", "192.168.1.1").toString()
    val mDelim: String = mPrefs.getString("delimiter", "\r\n").toString()
    val mBytes: Int = mPrefs.getString("bytes", "1024").toString().toInt()

    var connected = mSocket.connected

    var cancelBtnClick = MutableLiveData(false)
        private set

    var showLoadingScreen = MutableLiveData(View.VISIBLE)
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
        mStpe.shutdownNow()
        mSocket.disconnect()
    }

    fun cancelConnection() {
        disconnect()
        cancelBtnClick.value = true
    }

    private fun send(msg: String) {
        if(connected){
            try {
                mSocket.socket.getOutputStream().write((msg + mDelim).toByteArray())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                this.connect()
            }
        }
    }

    fun createEnqueueMessage() {
        val out = Output(
            throttleProgress.value, Joystick(
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
        viewModelScope.launch(Dispatchers.IO) {
            mTryConnect.set(true)
            /*while(mTryConnect.get() && !mSocket.connected){
                mSocket.connect(hostname, port)
                delay(200)
            }
            if(connected && !mTryConnect.get()) showLoadingScreen.postValue(View.GONE)*/
            showLoadingScreen.postValue(View.GONE)
            mTryConnect.set(false)
            delay(2000)
            attitude.postValue(AttitudeIndicator.Attitude(10.0F, 5.0F))
            altitude.postValue(200.0)
            mReadData.set(true)
        }
    }

    fun read(){
        viewModelScope.launch(Dispatchers.IO) {
            val stream = mSocket.socket.getInputStream()
            val buffer = ByteArray(mBytes)
            var buf: String
            while(mReadData.get() && connected){
                stream.read(buffer)
                buf = buffer.toString()

            }

        }
    }



    data class Joystick(
        var offset: Float?,
        var degrees: Float?,
        var rotation: Int?
    ) {

    }

    data class Output(
        var throttle: Int?,
        var joystick: Joystick
    ) {
    }
}