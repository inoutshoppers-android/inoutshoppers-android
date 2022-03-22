package com.example.inoutshoppers.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class AuthViewModelFactory(
    private val firebaseAuth: FirebaseAuth
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(firebaseAuth = firebaseAuth) as T
        } else if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            SignupViewModel(firebaseAuth = firebaseAuth) as T
        } else {
            throw IllegalArgumentException("Incorrect viewmodel type")
        }
    }
}