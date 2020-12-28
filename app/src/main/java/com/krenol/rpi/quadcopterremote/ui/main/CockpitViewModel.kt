package com.krenol.rpi.quadcopterremote.ui.main


import android.view.View
import androidx.lifecycle.LiveData
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

    private var _cancelBtnClick = MutableLiveData(false)
    val cancelBtnClick: LiveData<Boolean>
        get() = _cancelBtnClick

    private var _connected = MutableLiveData(View.VISIBLE)
    val showLoadingScreen: LiveData<Int>
        get() = _connected

    private var _altitude = MutableLiveData(0.0)
    val altitude: LiveData<Double>
        get() = _altitude

    private var _attitude = MutableLiveData(AttitudeIndicator.Attitude(0.0F, 0.0F))
    val attitude: LiveData<AttitudeIndicator.Attitude>
        get() = _attitude


    fun disconnect() {
        tryConnect.set(false)
        mSocket.disconnect()
    }

    fun cancelConnection() {
        disconnect()
        _connected.value = View.VISIBLE
        _cancelBtnClick.value = true
    }

    fun connect(hostname: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tryConnect.set(true)
            /*while(tryConnect.get() && !mSocket.connected){
                mSocket.connect(hostname, port)
                delay(200)
            }
            if(mSocket.connected && !tryConnect.get()) _connected.postValue(View.GONE)*/
            _connected.postValue(View.GONE)
            tryConnect.set(false)
            delay(2000)
            _attitude.postValue(AttitudeIndicator.Attitude(10.0F, 5.0F))
            _altitude.postValue(200.0)
        }
    }
}