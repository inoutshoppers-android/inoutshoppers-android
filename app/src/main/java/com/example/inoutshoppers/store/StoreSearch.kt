package com.example.inoutshoppers.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.inoutshoppers.databinding.StoreSearchBinding


class StoreSearch : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = StoreSearchBinding.inflate(inflater, container, false)

        binding.searchButton.setOnClickListener { view : View ->
            view.findNavController().navigate(StoreSearchDirections.actionStoreSearchToItemList())
        }

        return binding.root
    }

}