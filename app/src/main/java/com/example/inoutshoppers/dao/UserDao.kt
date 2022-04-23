package com.example.inoutshoppers.dao

import com.example.inoutshoppers.database.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDao {

    private val db = Firebase.firestore

    fun addContributionPoint(onContributionAdded: () -> Unit, onFailure: () -> Unit) {
        getUserProfileInfo(
            userProfile = { userProfile ->
                if (userProfile != null) {
                    val userDocumentRef = db.collection("users").document(userProfile.userId)
                    incrementContributionPoint(
                        userDocumentRef,
                        userProfile,
                        onContributionAdded = onContributionAdded,
                        onFailure = onFailure
                    )
                }
            }, onFailure = {
                onFailure()
            }
        )
    }

    private fun incrementContributionPoint(
        userDocumentReference: DocumentReference,
        userProfile: UserProfile,
        onContributionAdded: () -> Unit,
        onFailure: () -> Unit
    ) {
        val user = userProfile.copy(totalContribution = userProfile.totalContribution + 1)
        userDocumentReference.set(user)
            .addOnSuccessListener {
                onContributionAdded()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun getUserProfileInfo(userProfile: (UserProfile?) -> Unit, onFailure: () -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: ""
        userId.takeIf { it.isNotEmpty() }?.let { id ->
            val userDocumentRef = db.collection("users").document(id)
            userDocumentRef
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userInfo = document.toObject(UserProfile::class.java)
                        userProfile(userInfo)
                    } else {
                        val userInfo = UserProfile(userId = id)
                        userProfile(userInfo)
                    }
                }
                .addOnFailureListener {
                    onFailure()
                }
        }
    }

    fun createUserProfile(username: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: ""
        userId.takeIf { it.isNotEmpty() }?.let { id ->
            db.collection("users").document(id)
                .set(UserProfile(userId = id, username = username))
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onFailure()
                }
        }
    }
}