package com.krenol.rpi.quadcopterremote.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel

class StartPageViewModel : ViewModel() {

    private var _btnClick = MutableLiveData(false)
    val btnClick: LiveData<Boolean>
        get() = _btnClick

    fun start() {
        _btnClick.value = true
    }
}