package com.example.inoutshoppers.database

data class Store(
    val id: String,
    val name: String,
    val address: String?,
    val contactNumber: String?,
    val emailAddress: String?,
    val sections: List<StoreSection> = emptyList()
)

data class StoreSection(
    val name: String,
    val items: List<StoreItem>
)

data class StoreItem (
    val name: String,
    val price: Double?,
    val longitude: Double,
    val latitude: Double,
)