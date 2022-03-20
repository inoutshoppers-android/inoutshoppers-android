package com.example.inoutshoppers.item

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.inoutshoppers.R
import com.example.inoutshoppers.dao.ItemLocationDAO
import com.example.inoutshoppers.databinding.AddItemBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class AddItem : Fragment() {

    var selectedStore: Place? = null
    val TAG = "AddItem"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val dao = ItemLocationDAO()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = AddItemBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.api_key), Locale.US);
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS
            )
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Autocomplete fragment: Selected store: ${place.toString()}")
                selectedStore = place
            }

            override fun onError(p0: Status) {
                Log.e(TAG, "Place selection error ${p0.statusMessage}")
            }
        })

        // Ask for location permission if not already granted
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(TAG, "Access fine location permission granted")
            }
            else -> {
                val locationPermissionRequest = registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions.get(Manifest.permission.ACCESS_FINE_LOCATION) == true -> {
                            Log.i(TAG, "Fine location access granted")
                        }
                        permissions.get(Manifest.permission.ACCESS_COARSE_LOCATION) == true -> {
                            Log.i(TAG, "Course location access granted")
                        } else -> {
                            Log.i(TAG, "Location access not granted")
                        }
                    }
                }

                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }

        binding.addItemButton.setOnClickListener { view : View ->

            if (selectedStore == null) {
            Toast.makeText(requireContext(), "Must select store", Toast.LENGTH_LONG).show()
            }
            else if (binding.itemNameEdit.text.isEmpty()) {
                Toast.makeText(requireContext(), "Must enter item name", Toast.LENGTH_LONG).show()
            }
            else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        Log.i(TAG, "Current location: $location")
                        var item = binding.itemNameEdit.text.toString()
                        // Add to database
                        dao.addItem(selectedStore!!, item, location!!,
                            {
                                Toast.makeText(requireContext(), "$item added to ${selectedStore!!.name}", Toast.LENGTH_LONG).show()
                                view.findNavController()
                                    .navigateUp()
                            },
                            {
                                Toast.makeText(requireContext(), "Unable to add item. Please try again.", Toast.LENGTH_LONG).show()
                            })

                    }
            }
        }

        return binding.root
    }
}