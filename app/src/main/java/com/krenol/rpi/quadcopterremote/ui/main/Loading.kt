package com.krenol.rpi.quadcopterremote.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels
import com.krenol.rpi.quadcopterremote.R
import com.krenol.rpi.quadcopterremote.databinding.LoadingFragmentBinding

class Loading : Fragment() {

    companion object {
        fun newInstance() = Loading()
    }

    private val viewModel: CockpitViewModel by navGraphViewModels(R.id.cockpit_nav)
    private lateinit var binding: LoadingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.loading_fragment,
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
        return binding.root
    }

    private fun cancel(){
        val action = CockpitDirections.actionCockpitToStartPage()
        NavHostFragment.findNavController(this).navigate(action)
    }
}