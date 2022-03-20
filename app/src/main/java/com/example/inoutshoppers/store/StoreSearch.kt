package com.example.inoutshoppers.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.inoutshoppers.R
import com.example.inoutshoppers.databinding.StoreSearchBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class StoreSearch : Fragment() {

    val TAG = "StoreSearch"
    var selectedStore : Place? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = StoreSearchBinding.inflate(inflater, container, false)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.api_key), Locale.US);
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Autocomplete fragment: Selected store: ${place.toString()}")
                selectedStore = place
            }

            override fun onError(p0: Status) {
                Log.e(TAG,"Place selection error ${p0.statusMessage}")
            }

        })

        binding.selectStoreButton.setOnClickListener { view : View ->
            if (selectedStore == null) {
                Toast.makeText(context, "Please select a store.", Toast.LENGTH_LONG).show()
            }
            else {
                view.findNavController()
                    .navigate(StoreSearchDirections.actionStoreSearchToItemList(selectedStore!!))
                Log.i(TAG, "Completed store store selection: $selectedStore")
            }
        }

        binding.debugStoreButton.setOnClickListener { view :View ->
            view.findNavController().navigate(StoreSearchDirections.actionStoreSearchToItemList(null))
            Log.d(TAG, "Continuing to item list without Place for testing purposes")
        }


        return binding.root
    }

}