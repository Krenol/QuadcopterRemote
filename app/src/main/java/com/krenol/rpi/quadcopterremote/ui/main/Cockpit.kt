package com.krenol.rpi.quadcopterremote.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels
import androidx.preference.PreferenceManager
import com.krenol.rpi.quadcopterremote.Prefs
import com.krenol.rpi.quadcopterremote.R
import com.krenol.rpi.quadcopterremote.databinding.CockpitFragmentBinding

class Cockpit : Fragment() {

    companion object {
        fun newInstance() = Cockpit()
    }

    private val viewModel: CockpitViewModel by navGraphViewModels(R.id.cockpit_nav)
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
            if(cancel) cancel()
        })
        viewModel.throttleProgress.observe(viewLifecycleOwner, {
            viewModel.createEnqueueMessage()
        })
        viewModel.yawn.observe(viewLifecycleOwner, {
            viewModel.createEnqueueMessage()
        })
        viewModel.joystickOffset.observe(viewLifecycleOwner, {
            viewModel.createEnqueueMessage()
        })
        viewModel.joystickDegrees.observe(viewLifecycleOwner, {
            viewModel.createEnqueueMessage()
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

    override fun onStart() {
        super.onStart()
        viewModel.prefs = Prefs(PreferenceManager.getDefaultSharedPreferences(context))
        viewModel.connect()
    }

    private fun cancel(){
        val action = CockpitDirections.actionCockpitToStartPage()
        NavHostFragment.findNavController(this).navigate(action)
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnect()
    }
}