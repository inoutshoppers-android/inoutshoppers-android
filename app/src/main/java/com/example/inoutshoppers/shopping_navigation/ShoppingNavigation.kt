package com.example.inoutshoppers.shopping_navigation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.inoutshoppers.R
import com.example.inoutshoppers.database.UserLocation
import com.example.inoutshoppers.permissions.Permissions
import com.example.inoutshoppers.databinding.ShoppingNavigationBinding
import com.example.inoutshoppers.shopping_navigation.models.PolylineData
import com.example.inoutshoppers.shopping_navigation.viewmodel.ShoppingNavigationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ShoppingNavigation : Fragment(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    companion object {
        const val SHOPPING_ITEMS = "SHOPPING_ITEMS"
        const val STORE = "STORE"
        const val TAG = "ShoppingNavigation"
    }

    private val fusedProviderClient by lazy {
        FusedLocationProviderClient(requireContext())
    }

    private lateinit var binding: ShoppingNavigationBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var geoApiContext: GeoApiContext
    private var userLocation: UserLocation? = null
    private var permissionGranted = false
    private var savedInstanceState: Bundle? = null
    private val polylineDataList = mutableListOf<PolylineData>()
    private var currentMarker: Marker? = null

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
        } else {
            permissionGranted = true
            initGoogleMaps(savedInstanceState)
            initViewModel()
            mapView.onStart()
            mapView.onResume()
        }
        configureButtons()
        return binding.root
    }

    private fun configureButtons() {
        binding.navigationFrame.setOnClickListener {
            val marker = currentMarker
            val location = userLocation
            if (marker != null && location!=null) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=${location.latitude},${location.longitude}&daddr=${marker.position.latitude},${marker.position.longitude}")
                )
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please select a marker for navigation", Toast.LENGTH_LONG).show()
            }
        }
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
        googleMap.setOnMarkerClickListener {
            if (it.isInfoWindowShown) {
                it.hideInfoWindow()
            } else {
                it.showInfoWindow()
            }
            if (currentMarker != it) {
                currentMarker = it
                fusedProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        userLocation = UserLocation(latitude = location.latitude, longitude = location.longitude)
                        userLocation?.let { userLocation ->
                            calculateDirections(it, userLocation)
                        }
                    }
            }
            true
        }
        setCameraView()
        if (!permissionGranted || !Permissions.checkLocationPermissionGranted(requireActivity())) {
            return
        }
        googleMap.isMyLocationEnabled = true
        googleMap.setOnPolylineClickListener(this)
    }

    private fun initGoogleMaps(bundle: Bundle?) {
        val mapViewBundle = bundle?.getBundle(MapsObject.MAPVIEW_BUNDLE_KEY)
        mapView = binding.mapView
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        geoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.api_key))
            .build()
    }

    private fun initViewModel() {
        val args: ShoppingNavigationArgs by navArgs()
        val viewModel: ShoppingNavigationViewModel by viewModels()
        val store = args.storeBundle.getParcelable<Place>(STORE)
        val items = args.storeBundle.getStringArrayList(SHOPPING_ITEMS).orEmpty()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemsLocations.collect { storeItems ->
                    var locationMarker: Marker? = null
                    storeItems.forEach { storeItem ->
                        val latlng = LatLng(storeItem.latitude, storeItem.longitude)
                        locationMarker = googleMap.addMarker(MarkerOptions().position(latlng))
                        locationMarker?.title = storeItem.name
                    }
                    locationMarker?.let { marker ->
                        if (permissionGranted) {
                            fusedProviderClient.lastLocation
                                .addOnSuccessListener { location ->
                                    userLocation = UserLocation(latitude = location.latitude, longitude = location.longitude)
                                    userLocation?.let { userLocation ->
                                        calculateDirections(marker, userLocation)
                                    }
                                }
                        }
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

    private fun calculateDirections(marker: Marker, userLocation: UserLocation) {
        val destination = com.google.maps.model.LatLng(marker.position.latitude, marker.position.longitude)
        val directions = DirectionsApiRequest(geoApiContext)
        directions.alternatives(true)
        directions.origin(com.google.maps.model.LatLng(userLocation.latitude, userLocation.longitude))
        directions.destination(destination).setCallback(object: PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult?) {
                if (result != null) {
                    lifecycleScope.launch {
                        addPolylines(result)
                    }
                }
            }

            override fun onFailure(e: Throwable?) {
                Log.e(TAG, "Failed to get directions")
            }

        })
    }

    private suspend fun addPolylines(directionResult: DirectionsResult) {
        withContext(Dispatchers.Main) {
            for (polyline in polylineDataList) {
                polyline.polyline.remove()
            }
            polylineDataList.clear()
            for (directionRoute in directionResult.routes) {
                val decodedPath = PolylineEncoding.decode(directionRoute.overviewPolyline.encodedPath)
                val newDecodedPath = mutableListOf<LatLng>()
                for (latlng in decodedPath) {
                    newDecodedPath.add(LatLng(latlng.lat, latlng.lng))
                }
                val polyline = googleMap.addPolyline(PolylineOptions().addAll(newDecodedPath))
                polyline.color = ContextCompat.getColor(requireContext(), R.color.quantum_grey500)
                polyline.isClickable = true
                val directionLeg = directionRoute.legs.firstOrNull()
                if (directionLeg != null) {
                    polylineDataList.add(PolylineData(polyline = polyline, directionsLeg = directionLeg))
                }
            }
        }
    }

    override fun onPolylineClick(polyline: Polyline) {
        for (polylineData in polylineDataList) {
            if (polylineData.polyline.id.equals(polyline.id)) {
                polylineData.polyline.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                polylineData.polyline.zIndex = 1f
                currentMarker?.snippet = "Duration: " + polylineData.directionsLeg.duration
            } else {
                polylineData.polyline.color = ContextCompat.getColor(requireContext(), R.color.quantum_grey500)
                polylineData.polyline.zIndex = 0f
            }
        }
    }
}