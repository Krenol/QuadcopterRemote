package com.krenol.rpi.quadcopterremote.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CockpitViewModel : ViewModel() {
    private var _cancelBtnClick = MutableLiveData(false)
    val cancelBtnClick: LiveData<Boolean>
        get() = _cancelBtnClick

    fun cancelConnection() {
        //TODO stop connection
        _cancelBtnClick.value = true
    }
}