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

    private var _cancelBtnClick = MutableLiveData(false)
    val cancelBtnClick: LiveData<Boolean>
        get() = _cancelBtnClick

    private var _connected = MutableLiveData(View.VISIBLE)
    val connected: LiveData<Int>
        get() = _connected

    fun cancelConnection() {
        tryConnect.set(false)
        mSocket.disconnect()
        _connected.value = View.VISIBLE
        _cancelBtnClick.value = true
    }

    fun connect(hostname: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if(mSocket.connected) {
                mSocket.disconnect()
            }
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