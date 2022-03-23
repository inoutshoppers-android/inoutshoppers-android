package com.example.inoutshoppers.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.example.inoutshoppers.InOutShoppersApplication
import com.example.inoutshoppers.R
import com.example.inoutshoppers.databinding.SignUpBinding
import kotlinx.coroutines.launch

class SignUp : Fragment() {

    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: SignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = SignUpBinding.inflate(inflater, container, false)
        setupViewModel()
        configureButtons()
        return binding.root
    }

    private fun configureButtons() {
        with(binding) {
            signUpButton2.setOnClickListener {
                if (password.text.toString() != passwordVerify.text.toString()) {
                    showToast("Passwords do not match")
                } else if (password.text.isNotEmpty() && emailAddress.text.isNotEmpty()) {
                    viewModel.signUpUser(email = password.text.toString(), emailAddress.text.toString())
                }
            }
        }
    }

    private fun setupViewModel() {
        val firebaseAuth = (requireActivity().application as InOutShoppersApplication).firebaseAuth
        viewModel = ViewModelProvider(requireActivity(), AuthViewModelFactory(firebaseAuth = firebaseAuth)).get(SignupViewModel::class.java)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signUpState.collect { signUpState ->
                    if (signUpState.signupSuccess) {
                        showToast("Success")
                        navigateToHome()
                    } else if (!signUpState.signupSuccess && signUpState.signupFailure){
                        showToast("Error signing up")
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        binding.root.findNavController().navigate(R.id.action_signUp_to_home)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

}