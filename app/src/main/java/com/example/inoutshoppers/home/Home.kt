package com.example.inoutshoppers.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.inoutshoppers.InOutShoppersApplication
import com.example.inoutshoppers.dao.ItemLocationDAO
import com.example.inoutshoppers.dao.UserDao
import com.example.inoutshoppers.databinding.HomeBinding
import com.google.firebase.auth.FirebaseAuth

class Home : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: HomeBinding
    private val userDao = UserDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = HomeBinding.inflate(inflater, container, false)
        initViews()
        configureButtons();
        return binding.root
    }

    private fun initViews() {
        userDao.getUserProfileInfo(userProfile = {userProfile ->
            if (userProfile != null) {
                binding.welomeText.text = String.format(binding.welomeText.text.toString(), userProfile.username)
                binding.totalContributions.text = userProfile.totalContribution.toString()
            }
        }, onFailure = {})
    }

    private fun configureButtons() {
        binding.startShoppingCard.setOnClickListener { view : View ->
            view.findNavController().navigate(HomeDirections.actionHomeToStoreSearch())
        }

        binding.addLocationCard.setOnClickListener { view :View ->
            view.findNavController().navigate(HomeDirections.actionHomeToAddItem())
        }

        binding.logoutCard.setOnClickListener { view : View ->
            firebaseAuth = (requireActivity().application as InOutShoppersApplication).firebaseAuth
            firebaseAuth.signOut();
            view.findNavController().navigate(HomeDirections.actionHomeToLogin())
        }
    }
}