package com.example.inoutshoppers.authentication

import androidx.lifecycle.ViewModel
import com.example.inoutshoppers.dao.UserDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignupViewModel(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private var _signUpState = MutableStateFlow(SignUpState())
    val signUpState: StateFlow<SignUpState> = _signUpState
    private val userDao = UserDao()

    fun signUpUser(email: String, password: String, username: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    userDao.createUserProfile(username = username, onSuccess = {
                        _signUpState.value = _signUpState.value.copy(signupSuccess = true, signupFailure = false)
                    }, onFailure = {
                        _signUpState.value = _signUpState.value.copy(signupSuccess = false, signupFailure = true)
                    })
                } else {
                    _signUpState.value = _signUpState.value.copy(signupSuccess = false, signupFailure = true)
                }
            }
    }
}

data class SignUpState(
    val signupSuccess: Boolean = false,
    val signupFailure: Boolean = false
)