package com.ekremkocak.alzheimer.ui.home


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ekremkocak.alzheimer.R
import com.ekremkocak.alzheimer.data.model.LocationEntity
import com.ekremkocak.alzheimer.data.sealed.FlowState
import com.ekremkocak.alzheimer.databinding.FragmentHomeBinding
import com.ekremkocak.alzheimer.ui.bottomsheet.AddressBottomSheetFragment
import com.ekremkocak.alzheimer.util.Constants
import com.ekremkocak.alzheimer.viewmodel.home.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private var locations: List<LocationEntity>? = null
    private var job: Job? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        viewModel.fetchLocations()
        observeLocations()
        initListeners()
    }

    private fun initListeners() {
        binding.buttonDeleteMarkers.setOnClickListener {
            viewModel.clearMarks()
        }
    }

    private fun setupMap(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeLocations(){
        lifecycleScope.launch {
                viewModel.locationsState.collect { state ->

                    when (state) {
                        is FlowState.Loading -> {

                        }
                        is FlowState.Success -> {
                            locations = state.data
                            if (::googleMap.isInitialized && isFirstLoad) {
                                drawMarkersOnMap()
                            }
                        }
                        is FlowState.Error -> {
                            Toast.makeText(requireContext(), state.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            viewModel.address.collect { addresses ->
                addresses?.forEach { address -> // only 1 adress ok
                    val adressline = address.getAddressLine(0)
                    val bottomSheetFragment =  AddressBottomSheetFragment.newInstance(adressline)
                    bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.isMyLocationEnabled = true

        setMarkerClickListener()

        locations?.let { drawMarkersOnMap() }
    }
    private fun centerMapWithMarkers(markerCoordinates: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        markerCoordinates.forEach { latLng ->
            googleMap.addMarker(MarkerOptions().position(latLng))
            builder.include(latLng)
        }

        if (markerCoordinates.isNotEmpty()){
            val bounds = builder.build()
            val padding = resources.getDimensionPixelSize(R.dimen.map_padding)
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap.animateCamera(cameraUpdate,Constants.CAMERA_SLIDE_EFFECT_DURATION,null)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(Constants.CAMERA_SLIDE_EFFECT_DURATION.toLong())
            _binding?.textHello?.visibility = View.GONE
        }

    }

    private fun setMarkerClickListener() {
        googleMap.setOnMarkerClickListener { marker ->
            false
        }

        googleMap.setOnInfoWindowClickListener { marker ->
            val position = marker.position
            viewModel.getAddressFromGeocoder(position.latitude, position.longitude)
        }
    }
    private fun drawMarkersOnMap() {
        googleMap.clear()
        locations?.forEach { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title("location.title")
                //.snippet("location.description")
            googleMap.addMarker(markerOptions)
        }

        val markerCoordinates = locations?.map { entity ->
            LatLng(entity.latitude, entity.longitude)
        }

        if (isFirstLoad) {
            markerCoordinates?.let {
                centerMapWithMarkers(markerCoordinates)
                isFirstLoad = false
            }
        }else{
            binding.textHello.visibility = View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
    fun confilict4(){

    }
}