package com.example.inoutshoppers.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.inoutshoppers.databinding.LoginBinding

class Login : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = LoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener { view : View ->
            view.findNavController().navigate(LoginDirections.actionLoginToHome())
        }

        binding.signUpButton.setOnClickListener { view : View ->
            view.findNavController().navigate(LoginDirections.actionLoginToSignUp(binding.emailAddress.text.toString()))
        }

        return binding.root
    }

}