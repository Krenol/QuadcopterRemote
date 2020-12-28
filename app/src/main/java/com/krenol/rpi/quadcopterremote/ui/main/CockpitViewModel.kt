package com.krenol.rpi.quadcopterremote.ui.main


import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krenol.rpi.quadcopterremote.Socket
import com.krenol.rpi.quadcopterremote.ui.components.AttitudeIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class CockpitViewModel : ViewModel() {
    private var mSocket: Socket = Socket()
    private var tryConnect = AtomicBoolean(false)
    var connected = mSocket.connected

    var cancelBtnClick = MutableLiveData(false)
        private set

    var showLoadingScreen = MutableLiveData(View.VISIBLE)
        private set

    var altitude = MutableLiveData(0.0)
        private set

    var attitude = MutableLiveData(AttitudeIndicator.Attitude(0.0F, 0.0F))
        private set

    var progress = MutableLiveData(0)
        private set

    var progressString = MutableLiveData("0")
        private set


    fun onThrottleChange(seekBar: SeekBar?, progressValue: Int, fromUser: Boolean) {
        progress.value = progressValue
        progressString.value = progressValue.toString()
    }

    fun disconnect() {
        tryConnect.set(false)
        mSocket.disconnect()
    }

    fun cancelConnection() {
        disconnect()
        showLoadingScreen.value = View.VISIBLE
        cancelBtnClick.value = true
    }

    fun connect(hostname: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tryConnect.set(true)
            /*while(tryConnect.get() && !mSocket.connected){
                mSocket.connect(hostname, port)
                delay(200)
            }
            if(mSocket.connected && !tryConnect.get()) _connected.postValue(View.GONE)*/
            showLoadingScreen.postValue(View.GONE)
            tryConnect.set(false)
            delay(2000)
            attitude.postValue(AttitudeIndicator.Attitude(10.0F, 5.0F))
            altitude.postValue(200.0)
        }
    }
}