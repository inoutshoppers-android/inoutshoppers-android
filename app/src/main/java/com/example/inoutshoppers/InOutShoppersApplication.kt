package com.example.inoutshoppers

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InOutShoppersApplication : Application() {
    val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
}