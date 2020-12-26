package com.krenol.rpi.quadcopterremote.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel

class StartPageViewModel : ViewModel() {
    // Create a LiveData with a String
    private var _btnClick = MutableLiveData(false)
    val btnClick: LiveData<Boolean>
        get() = _btnClick

    fun start() {
        _btnClick.value = true
    }
}