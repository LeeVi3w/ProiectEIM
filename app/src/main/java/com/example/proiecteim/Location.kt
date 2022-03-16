package com.example.proiecteim

data class Location(
    val name: String,
    val currTemp: Float,
    val maxTemp: Float,
    val minTemp: Float,
    val pressure: Int,
    val humidity: Int
) {
}