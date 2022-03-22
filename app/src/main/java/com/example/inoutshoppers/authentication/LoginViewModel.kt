package com.example.inoutshoppers.authentication

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _signInState = MutableStateFlow(SignInState())
    val signInState: StateFlow<SignInState> = _signInState

    init {
        checkIfTheUserIsLoggedIn()
    }

    private fun checkIfTheUserIsLoggedIn() {
        if (firebaseAuth.currentUser != null) {
            _signInState.value = _signInState.value.copy(signInSuccess = true, signInFailure = false)
        }
    }

    fun signInUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInState.value = _signInState.value.copy(signInSuccess = true, signInFailure = false)
                } else {
                    _signInState.value = _signInState.value.copy(signInFailure = true, signInSuccess = false)
                }
            }
    }
}

data class SignInState(
    val signInSuccess: Boolean = false,
    val signInFailure: Boolean = false,
)