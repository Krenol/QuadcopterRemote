package com.krenol.rpi.quadcopterremote.ui.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krenol.rpi.quadcopterremote.Prefs

class CockpitViewModelFactory (private val prefs: Prefs) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CockpitViewModel::class.java)) {
            return CockpitViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}