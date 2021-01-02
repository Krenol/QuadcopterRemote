package com.krenol.rpi.quadcopterremote.ui.main

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.krenol.rpi.quadcopterremote.Prefs
import com.krenol.rpi.quadcopterremote.R
import com.krenol.rpi.quadcopterremote.databinding.CockpitFragmentBinding

class Cockpit : Fragment() {

    companion object {
        fun newInstance() = Cockpit()
    }
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    private val mFusedLocationProviderClient: FusedLocationProviderClient? = activity?.let { LocationServices.getFusedLocationProviderClient(
        it
    ) }
    private val mIntentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)

    private val viewModel: CockpitViewModel by navGraphViewModels(R.id.cockpit_nav) {
        CockpitViewModelFactory(Prefs(PreferenceManager.getDefaultSharedPreferences(context)))
    }
    private lateinit var binding: CockpitFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.cockpit_fragment,
            container,
            false
        )

        // Set the viewModel for databinding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.viewModel = viewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.cancelBtnClick.observe(viewLifecycleOwner, { cancel ->
            if (cancel) cancel()
        })
        viewModel.throttleProgress.observe(viewLifecycleOwner, {
            viewModel.enqueueMessage()
        })
        viewModel.yawn.observe(viewLifecycleOwner, {
            viewModel.enqueueMessage()
        })
        viewModel.joystickOffset.observe(viewLifecycleOwner, {
            viewModel.enqueueMessage()
        })
        viewModel.joystickDegrees.observe(viewLifecycleOwner, {
            viewModel.enqueueMessage()
        })
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cockpit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.cockpit_menu_disconnect){
            viewModel.cancelConnection()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.connect()
        activity?.let {
            it.registerReceiver(viewModel.receiver, mIntentFilter)
        }
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            // initialize location request
            mLocationRequest = LocationRequest.create()
            mLocationRequest!!.run {
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                interval = UPDATE_INTERVAL
                setFastestInterval(FASTEST_INTERVAL)
            }

            // initialize location setting request builder object
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)
            val locationSettingsRequest = builder.build()

            // initialize location service object
            val settingsClient = activity?.let { LocationServices.getSettingsClient(it) }
            settingsClient!!.checkLocationSettings(locationSettingsRequest)

            mFusedLocationProviderClient?.requestLocationUpdates(
                mLocationRequest,
                viewModel.locationCallback,
                Looper.myLooper()
            )
        }
    }

    private fun checkLocationPermission() : Boolean {
        return this.context?.let { ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) } == PackageManager.PERMISSION_GRANTED
    }

    private fun cancel(){
        val action = CockpitDirections.actionCockpitToStartPage()
        NavHostFragment.findNavController(this).navigate(action)
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationProviderClient?.removeLocationUpdates(viewModel.locationCallback)
        viewModel.disconnect()
        activity?.let {
            it.unregisterReceiver(viewModel.receiver)
        }
    }
}