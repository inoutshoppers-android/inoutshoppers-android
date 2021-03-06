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
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val name: String = "",
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

data class UserProfile(
    val userId: String = "",
    val totalContribution: Int = 0,
    val username: String = ""
)