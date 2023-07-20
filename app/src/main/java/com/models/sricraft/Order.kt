package com.models.sricraft

import java.util.*

data class Order(
    val id: String,
    val title: String,
    val items: MutableMap<String, String>,
    val total: String,
    val userID: String,
    val address: String,
    val orderImage: String,
)
