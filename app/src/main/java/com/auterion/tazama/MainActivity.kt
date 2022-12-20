package com.auterion.tazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
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
    val mainViewModel = hiltViewModel<MainViewModel>()
    val vehicleViewModel = hiltViewModel<VehicleViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    val videoStreamUri = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"
    val context = LocalContext.current
    val customLoadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(0, 0, 0, 0)
        .build()
    val player = remember { ExoPlayer.Builder(context).setLoadControl(customLoadControl).build() }
    val mediaSource = RtspMediaSource.Factory()
        .setForceUseRtpTcp(videoStreamUri.contains("rtspt"))
        .createMediaSource(MediaItem.fromUri(videoStreamUri))

    player.setMediaSource(mediaSource)
    player.prepare()
    player.play()

    Scaffold(bottomBar = {
        BottomNavigation(backgroundColor = colorResource(id = R.color.purple_500)) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navBarDestinations.forEach { screen ->
                BottomNavigationItem(
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    label = { Text(screen.label) },
                    icon = { Icon(screen.icon, contentDescription = null) },
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
            player
        )
    }
}
