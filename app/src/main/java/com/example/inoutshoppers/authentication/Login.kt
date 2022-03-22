package com.example.inoutshoppers.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.example.inoutshoppers.InOutShoppersApplication
import com.example.inoutshoppers.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class Login : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: LoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = LoginBinding.inflate(inflater, container, false)
        setupViewModel()
        configureButtons()
        return binding.root
    }

    private fun setupViewModel() {
        firebaseAuth = (requireActivity().application as InOutShoppersApplication).firebaseAuth
        val loginViewModel: LoginViewModel by viewModels(factoryProducer = {
            AuthViewModelFactory(
                firebaseAuth
            )
        })
        viewModel = loginViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { signInState ->
                    if (signInState.signInSuccess) {
                        navigateToHomeScreen()
                    } else if (!signInState.signInSuccess && signInState.signInFailure){
                        showSignInFailure()
                    }
                }
            }
        }
    }

    private fun configureButtons() {
        with(binding) {
            loginButton.setOnClickListener {
                if (emailAddress.text.isNotEmpty() && password.text.isNotEmpty()) {
                    viewModel.signInUser(emailAddress.text.toString(), password.text.toString())
                } else {
                    showToast("Email address or password cannot be empty")
                }
            }
            signUpButton.setOnClickListener {
                navigateToSignUpScreen()
            }
        }

    }

    private fun showSignInFailure() {
        showToast("Incorrect login/password")
    }

    private fun navigateToHomeScreen() {
        binding.root.findNavController().navigate(LoginDirections.actionLoginToHome())
    }

    private fun navigateToSignUpScreen() {
        binding.root.findNavController()
            .navigate(LoginDirections.actionLoginToSignUp(binding.emailAddress.text.toString()))
    }

    private fun showToast(string: String) {
        Toast.makeText(requireActivity(), string, Toast.LENGTH_SHORT).show()
    }

}