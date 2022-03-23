package com.example.inoutshoppers.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.inoutshoppers.R
import com.example.inoutshoppers.databinding.HomeBinding
import com.example.inoutshoppers.databinding.SignUpBinding

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = HomeBinding.inflate(inflater, container, false)

        binding.findStoreButton.setOnClickListener { view : View ->
            view.findNavController().navigate(HomeDirections.actionHomeToStoreSearch())
        }

        binding.addItemButton.setOnClickListener { view :View ->
            view.findNavController().navigate(HomeDirections.actionHomeToAddItem())
        }

        return binding.root
    }

}