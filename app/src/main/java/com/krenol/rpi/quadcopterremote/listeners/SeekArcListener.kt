package com.krenol.rpi.quadcopterremote.listeners

import androidx.lifecycle.MutableLiveData
import com.savantech.seekarc.SeekArc

class SeekArcListener(progress: MutableLiveData<Int>) : SeekArc.OnSeekArcChangeListener {
    private var mProgress = progress

    override fun onStartTrackingTouch(seekArc: SeekArc) {}
    override fun onStopTrackingTouch(seekArc: SeekArc) {
        seekArc.progress = seekArc.maxProgress / 2;
        mProgress.value = 0
    }

    override fun onProgressChanged(seekArc: SeekArc, progress: Float) {
        val max = seekArc.maxProgress.toInt()
        val mid = if (max % 2 == 0) max / 2 else max / 2 + 1

        mProgress.value = mid - progress.toInt();
    }


}