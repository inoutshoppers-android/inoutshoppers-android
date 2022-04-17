package com.example.inoutshoppers.shopping_navigation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.inoutshoppers.Permissions.Permissions
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShoppingNavigationBinding.inflate(inflater, container, false)
        checkLocationPermission()
        initGoogleMaps(savedInstanceState)
        initViewModel()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        checkLocationPermission()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            Permissions.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true
                }
            }
        }
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
        fusedProviderClient.lastLocation
            .addOnSuccessListener { location ->
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), googleMap.maxZoomLevel-2))
            }
    }

    private fun checkLocationPermission() {
        if (Permissions.checkLocationPermissionGranted(requireActivity())) {
            permissionGranted = true
        } else {
            Permissions.getLocationPermission(requireActivity())
        }
    }

}