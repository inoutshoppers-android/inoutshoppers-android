package com.example.inoutshoppers.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.inoutshoppers.R
import com.example.inoutshoppers.databinding.SignUpBinding

class SignUp : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = SignUpBinding.inflate(inflater, container, false)
        val args = SignUpArgs.fromBundle(requireArguments())

        // Copy email address over from login page if user already entered it
        binding.emailAddress.setText(args.emailAddress)

        return binding.root
    }

}