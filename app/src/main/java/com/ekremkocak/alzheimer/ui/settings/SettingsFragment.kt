package com.ekremkocak.alzheimer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ekremkocak.alzheimer.MainActivity
import com.ekremkocak.alzheimer.databinding.FragmentSettingsBinding
import com.ekremkocak.alzheimer.util.PrefHelper
import com.ekremkocak.alzheimer.util.isLocationServiceRunning
import com.ekremkocak.alzheimer.viewmodel.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var prefHelper: PrefHelper
    private var _binding: FragmentSettingsBinding? = null
    val binding: FragmentSettingsBinding
        get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = settingsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    private fun initListeners() {
        settingsViewModel.trackingEnabled.observe(viewLifecycleOwner){isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                val mainActivity = requireActivity() as MainActivity
                if (isChecked && !requireContext().isLocationServiceRunning()) {
                    mainActivity.startLocationService()
                } else if(!isChecked && requireContext().isLocationServiceRunning()) {
                    mainActivity.stopLocationService()
                }
            }
        }

    }

    private fun initUI(){
        lifecycleScope.launch(Dispatchers.Main) {
            binding.switchTracking.isChecked = prefHelper.isTrackingEnabled()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}