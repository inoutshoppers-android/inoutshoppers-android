package com.example.inoutshoppers.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.inoutshoppers.InOutShoppersApplication
import com.example.inoutshoppers.R
import com.example.inoutshoppers.authentication.AuthViewModelFactory
import com.example.inoutshoppers.authentication.LoginViewModel
import com.example.inoutshoppers.databinding.HomeBinding
import com.example.inoutshoppers.databinding.SignUpBinding
import com.google.firebase.auth.FirebaseAuth

class Home : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: HomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = HomeBinding.inflate(inflater, container, false)

        configureButtons();

        return binding.root
    }

    private fun configureButtons() {
        binding.findStoreButton.setOnClickListener { view : View ->
            view.findNavController().navigate(HomeDirections.actionHomeToStoreSearch())
        }

        binding.addItemButton.setOnClickListener { view :View ->
            view.findNavController().navigate(HomeDirections.actionHomeToAddItem())
        }

        binding.signOutButton.setOnClickListener { view : View ->
            firebaseAuth = (requireActivity().application as InOutShoppersApplication).firebaseAuth
            firebaseAuth.signOut();
            view.findNavController().navigate(HomeDirections.actionHomeToLogin())
        }
    }
}