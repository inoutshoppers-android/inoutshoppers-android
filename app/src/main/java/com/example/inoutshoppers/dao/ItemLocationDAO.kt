package com.example.inoutshoppers.dao

import android.location.Location
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItemLocationDAO {
    private val db = Firebase.firestore

    fun addItem(store: Place, itemName: String, location: Location, successCallback: () -> Unit, failureCallback: () -> Unit) {
        // Check if store already added

        val itemData = hashMapOf(
            "name" to itemName,
            "latitude" to "${location.latitude}",
            "longitude" to "${location.longitude}"
        )

        val storeData = hashMapOf(
            "name" to store.name,
            "address" to store.address
        )

        db.collection("stores").document(store.id!!).set(storeData)
            .addOnSuccessListener {
                db.collection("stores").document(store.id!!)
                    .collection("items").document(itemName).set(itemData)
                    .addOnSuccessListener { successCallback() }
                    .addOnFailureListener { failureCallback() }
            }
            .addOnFailureListener { failureCallback() }


    }
}