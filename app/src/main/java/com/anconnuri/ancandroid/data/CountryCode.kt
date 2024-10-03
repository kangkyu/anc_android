package com.anconnuri.ancandroid.data

data class CountryCode(val name: String, val code: String, val prefix: String)

val countryCodes = listOf(
    CountryCode("United States", "US", "+1")
    // Add more countries as needed
)
