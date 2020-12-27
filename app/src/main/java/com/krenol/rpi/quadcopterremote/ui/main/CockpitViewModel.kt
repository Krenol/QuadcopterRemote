package com.krenol.rpi.quadcopterremote.ui.main


import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krenol.rpi.quadcopterremote.Socket
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
            while(tryConnect.get() && !mSocket.connected){
                mSocket.connect(hostname, port)
                delay(200)
            }
            if(mSocket.connected && !tryConnect.get()) _connected.postValue(View.GONE)
            tryConnect.set(false)
        }
    }
}