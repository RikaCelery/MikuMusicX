package github.rikacelery.mikumusicx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import github.rikacelery.mikumusicx.component.AppBottomNavBar
import github.rikacelery.mikumusicx.component.AppTopBar
import github.rikacelery.mikumusicx.screen.HomeScreen
import github.rikacelery.mikumusicx.screen.MusicListScreen
import github.rikacelery.mikumusicx.screen.PlayerScreen
import github.rikacelery.mikumusicx.screen.SettingsScreen
import github.rikacelery.mikumusicx.service.MusicService
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val dataModel by viewModels<VM>()

    override fun onStop() {
        dataModel.save(dataStore)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MusicService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            dataModel.load(dataStore)
        }
        enableEdgeToEdge()
//        enableEdgeToEdge(SystemBarStyle.auto(Color.CYAN, Color.BLACK), SystemBarStyle.dark(TRANSPARENT))
        setContent {
            MikuMusicXTheme {
                App()
            }
        }
    }
}

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("ktlint:standard:function-naming")
@Composable
fun App() {
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    val bottom = @Composable {
        AppBottomNavBar(selectedItem) {
            selectedItem = it
            when (it) {
                0 -> {
                    navController.navigate(Home)
                }

                1 -> {
                    navController.navigate(MusicList)
                }

                2 -> {
                    navController.navigate(Settings)
                }
            }
        }
    }
    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            bottom()
        },
    ) { p ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(p),
            enterTransition = {
                slideIn(tween(200)) {
                    IntOffset(it.width, 0)
                }
            },
            exitTransition = {
                slideOut(tween(200)) {
                    IntOffset(-it.width, 0)
                }
            },
        ) {
            composable<Home> {
                HomeScreen(navController, bottomBar = bottom)
            }
            composable<MusicList> {
                MusicListScreen(bottomBar = bottom) {
                    navController.navigate(Player(it.id))
                }
            }
            composable<Settings> {
                SettingsScreen(bottomBar = bottom)
            }
            composable<Player> {
                PlayerScreen {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MikuMusicXTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            App()
        }
    }
}
