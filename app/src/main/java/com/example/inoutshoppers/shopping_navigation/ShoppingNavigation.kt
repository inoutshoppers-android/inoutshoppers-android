package com.example.inoutshoppers.shopping_navigation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.inoutshoppers.permissions.Permissions
import com.example.inoutshoppers.databinding.ShoppingNavigationBinding
import com.example.inoutshoppers.shopping_navigation.viewmodel.ShoppingNavigationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch


class ShoppingNavigation : Fragment(), OnMapReadyCallback {

    companion object {
        const val SHOPPING_ITEMS = "SHOPPING_ITEMS"
        const val STORE = "STORE"
    }

    private val fusedProviderClient by lazy {
        FusedLocationProviderClient(requireContext())
    }
    private lateinit var binding: ShoppingNavigationBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var permissionGranted = false
    private var savedInstanceState: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.savedInstanceState = savedInstanceState
        binding = ShoppingNavigationBinding.inflate(inflater, container, false)
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionGranted = true
                initGoogleMaps(savedInstanceState)
                initViewModel()
                mapView.onStart()
                mapView.onResume()
            }
        }
        if (!Permissions.checkLocationPermissionGranted(requireContext())) {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (permissionGranted) {
            mapView.onResume()
        }
    }

    override fun onStart() {
        super.onStart()
        if (permissionGranted) {
            mapView.onStart()
        }
    }

    override fun onPause() {
        super.onPause()
        if (permissionGranted) {
            mapView.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (permissionGranted) {
            mapView.onStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (permissionGranted) {
            mapView.onDestroy()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (permissionGranted) {
            mapView.onLowMemory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MapsObject.MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MapsObject.MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setCameraView()
        if (!permissionGranted || !Permissions.checkLocationPermissionGranted(requireActivity())) {
            return
        }
        googleMap.isMyLocationEnabled = true
    }

    private fun initGoogleMaps(bundle: Bundle?) {
        val mapViewBundle = bundle?.getBundle(MapsObject.MAPVIEW_BUNDLE_KEY)
        mapView = binding.mapView
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    private fun initViewModel() {
        val args: ShoppingNavigationArgs by navArgs()
        val viewModel: ShoppingNavigationViewModel by viewModels()
        val store = args.storeBundle.getParcelable<Place>(STORE)
        val items = args.storeBundle.getStringArrayList(SHOPPING_ITEMS).orEmpty()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemsLocations.collect { storeItems ->
                    storeItems.forEach { storeItem ->
                        val latlng = LatLng(storeItem.latitude, storeItem.longitude)
                        googleMap.addMarker(MarkerOptions().position(latlng))?.title = storeItem.name
                    }
                }

            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.errorMessage.collect{ errorMessage ->
                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        viewModel.fetchItemLocations(store, items)
    }

    private fun setCameraView() {
        if (permissionGranted) {
            fusedProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), googleMap.maxZoomLevel-2))
                }
        }
    }

}