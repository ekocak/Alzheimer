package com.ekremkocak.alzheimer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekremkocak.alzheimer.databinding.FragmentAddressBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddressBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddressBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(address: String): AddressBottomSheetFragment {
            val fragment = AddressBottomSheetFragment()
            val args = Bundle()
            args.putString("ADDRESS", address)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = arguments?.getString("ADDRESS")
        address?.let {
            binding.addressTextView.text = address
        }


        // Butona tıklandığında yapılacak işlemleri tanımla
        binding.enterAddressButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}