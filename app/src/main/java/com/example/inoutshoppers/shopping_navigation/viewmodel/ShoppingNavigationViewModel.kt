package com.example.inoutshoppers.shopping_navigation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inoutshoppers.database.StoreItem
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ShoppingNavigationViewModel : ViewModel() {

    private val firebaseDatabase by lazy {
        Firebase.firestore
    }
    private val storesCollectionReference by lazy {
        firebaseDatabase.collection("stores")
    }
    private var _itemsLocations = MutableStateFlow(emptyList<StoreItem>())
    val itemsLocations: StateFlow<List<StoreItem>> = _itemsLocations
    private var _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun fetchItemLocations(storeLocation: Place?, items: List<String>) {
        storesCollectionReference
            .whereEqualTo("address", storeLocation?.address)
            //.whereEqualTo("name", storeLocation?.name)
            .get()
            .addOnSuccessListener { stores ->
                if (stores.documents.size > 0) {
                    val store = stores.documents.first()
                    val itemsCollectionRef = storesCollectionReference.document(store.id).collection("items")
                    for (item in items) {
                        fetchItem(itemsCollectionRef, item)
                    }
                } else {
                    _errorMessage.value = "Store not found"
                }
            }
            .addOnFailureListener {
                _errorMessage.value = "Store not found"
            }
    }

    private fun fetchItem(items: CollectionReference, itemName: String) {
        items.document(itemName).get()
            .addOnSuccessListener { item ->
                val storeItem = item.toObject(StoreItem::class.java)
                if (storeItem != null) {
                    _itemsLocations.value += storeItem
                } else {
                    _errorMessage.value = "$itemName not found"
                }
            }
            .addOnFailureListener {
                _errorMessage.value = "$itemName not found"
            }
    }
}