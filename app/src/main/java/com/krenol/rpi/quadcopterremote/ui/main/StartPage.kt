package com.krenol.rpi.quadcopterremote.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.krenol.rpi.quadcopterremote.R
import androidx.databinding.DataBindingUtil
import com.krenol.rpi.quadcopterremote.databinding.StartPageFragmentBinding

class StartPage : Fragment() {

    companion object {
        fun newInstance() = StartPage()
    }

    private lateinit var viewModel: StartPageViewModel
    private lateinit var binding: StartPageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.start_page_fragment,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(StartPageViewModel::class.java)
        // Set the viewModel for databinding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.viewModel = viewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.btnClick.observe(viewLifecycleOwner, {
            start()
        })
        return binding.root
    }


    private fun start(){
        //val action = StartPageDirections.actionToCockpit()
        //findNavController(this).navigate(action)
    }

}
