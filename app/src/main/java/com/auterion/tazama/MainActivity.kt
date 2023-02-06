package com.auterion.tazama

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.navigation.Navigation
import com.auterion.tazama.navigation.navBarDestinations
import com.auterion.tazama.presentation.pages.main.MainViewModel
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.auterion.tazama.ui.theme.TazamaTheme
import com.auterion.tazama.util.Preferences
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
    val context = LocalContext.current
    val navController = rememberNavController()
    val measurementSystemFlow = Preferences.getMeasureSystemFlow(context)
    val vehicleViewModel = hiltViewModel<VehicleViewModel>(measurementSystemFlow)
    val mainViewModel = hiltViewModel<MainViewModel>()
    mainViewModel.setVideoStreamInfoFlow(vehicleViewModel.videoStreamInfo)
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = if (isLandscape) Modifier.height(0.dp) else Modifier,
                backgroundColor = colorResource(id = R.color.purple_500)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navBarDestinations.forEach { screen ->
                    BottomNavigationItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        label = { Text(screen.label) },
                        icon = {
                            Icon(
                                ImageVector.vectorResource(id = screen.iconSourceId),
                                contentDescription = null
                            )
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(0.4f),
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
        }) { innerPadding ->
        Navigation(
            navController = navController,
            mainViewModel,
            vehicleViewModel,
            settingsViewModel,
            modifier = Modifier.padding(innerPadding),
            mainViewModel.player
        )
    }
}
