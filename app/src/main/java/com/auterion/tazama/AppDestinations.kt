package com.auterion.tazama

sealed class AppDestinations {
    object HomeDestination : AppDestinations() {
        val route = "home"
    }
}