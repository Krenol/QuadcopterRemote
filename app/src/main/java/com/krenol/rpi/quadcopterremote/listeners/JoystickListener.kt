package com.krenol.rpi.quadcopterremote.listeners

import androidx.lifecycle.MutableLiveData


class JoystickListener(degrees: MutableLiveData<Float>, offset: MutableLiveData<Float>) : com.jmedeisis.bugstick.JoystickListener {
    private var mDegrees = degrees
    private var mOffset = offset

    override fun onDown() {
        //nothing to do here
    }

    override fun onDrag(degrees: Float, offset: Float) {
        mDegrees.value = degrees
        mOffset.value = offset
    }

    override fun onUp() {
        mDegrees.value = 0.0F
        mOffset.value = 0.0F
    }
}