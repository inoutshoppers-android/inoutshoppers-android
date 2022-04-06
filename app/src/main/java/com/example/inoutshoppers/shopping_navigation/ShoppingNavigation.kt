package com.example.inoutshoppers.shopping_navigation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.inoutshoppers.Permissions.Permissions
import com.example.inoutshoppers.databinding.ShoppingNavigationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class ShoppingNavigation : Fragment(), OnMapReadyCallback {

    companion object {
        const val SHOPPING_ITEMS = "SHOPPING_ITEMS"
        const val STORE = "STORE";
    }

    private lateinit var binding: ShoppingNavigationBinding
    private lateinit var mapView: MapView
    private var permissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShoppingNavigationBinding.inflate(inflater, container, false)
        val args: ShoppingNavigationArgs by navArgs()
        initGoogleMaps(savedInstanceState)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        if (Permissions.checkLocationPermissionGranted(requireActivity())) {
            permissionGranted = true
        } else {
            Permissions.getLocationPermission(requireActivity())
        }
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
        map.addMarker(MarkerOptions().position(LatLng(0.0,0.0)))?.title = "Marker"
        if (!permissionGranted || !Permissions.checkLocationPermissionGranted(requireActivity())) {
            return
        }
        map.isMyLocationEnabled = true
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

}