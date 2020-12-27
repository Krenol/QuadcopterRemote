package com.krenol.rpi.quadcopterremote.ui.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.krenol.rpi.quadcopterremote.R
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
    ): View {
        setHasOptionsMenu(true)

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

        viewModel.btnClick.observe(viewLifecycleOwner, { start ->
            if (start) start()
        })
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.start_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.start_menu_settings){
            val action = StartPageDirections.actionStartPageToSettingsActivity()
            findNavController(this).navigate(action)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun start(){
        val action = StartPageDirections.actionStartPageToCockpit()
        findNavController(this).navigate(action)
    }

}
