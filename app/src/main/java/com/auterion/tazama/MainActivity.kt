package com.auterion.tazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.auterion.tazama.ui.theme.TazamaTheme
import com.auterion.tazama.data.VehicleViewModel
import com.auterion.tazama.navigation.Navigation
import com.auterion.tazama.navigation.navBarDestinations
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    val vehicleViewModel = hiltViewModel<VehicleViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()



    Scaffold(topBar = {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navBarDestinations.forEach { screen ->
                BottomNavigationItem(
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    label = {Text(screen.label)},
                    icon = { Icon(screen.icon, contentDescription = null)},
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }

    }
    }) { innerPadding->
                Navigation(
                    navController = navController,
                    vehicleViewModel,
                    settingsViewModel,
                    modifier=Modifier.padding(innerPadding))
    }

}
