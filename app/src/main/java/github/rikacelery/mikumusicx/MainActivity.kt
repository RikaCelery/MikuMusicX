package github.rikacelery.mikumusicx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import github.rikacelery.mikumusicx.component.AppBottomNavBar
import github.rikacelery.mikumusicx.component.AppTopBar
import github.rikacelery.mikumusicx.screen.HomeScreen
import github.rikacelery.mikumusicx.screen.MusicListScreen
import github.rikacelery.mikumusicx.screen.PlayerScreen
import github.rikacelery.mikumusicx.screen.SettingsScreen
import github.rikacelery.mikumusicx.service.MusicService
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import github.rikacelery.mikumusicx.ui.theme.roundedShape
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val vm by viewModels<VM>()
    override fun onStop() {
        vm.save(dataStore)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MusicService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            vm.load(dataStore)
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
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    val bottomBar = @Composable {
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
    val vm = viewModel<VM>()
    val state by vm.uiState.collectAsState()
    Scaffold(
        topBar = {
            AppTopBar {
                if (state.musicControllerUiState.currentSong != null) {
                    var rotate by remember { mutableFloatStateOf(0f) }
                    LaunchedEffect(state.musicControllerUiState.playerState) {
                        animate(
                            0f,
                            360f,
                            animationSpec = infiniteRepeatable(
                                tween(1000, easing = LinearEasing),
                                RepeatMode.Restart
                            )
                        ) { v, _ ->
                            rotate = v
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    ElevatedCard({
                        navController.navigate(Player(state.musicControllerUiState.currentSong!!.id))
                    }, Modifier.padding(10.dp), shape = roundedShape) {
                        AsyncImage(
                            state.musicControllerUiState.currentSong
                                ?.cover ?: "",
                            null,
                            Modifier.aspectRatio(1f),
                        )
                    }
                }
            }
        },
        bottomBar = {
            bottomBar()
        },
    ) { p ->
        ConstraintLayout(
            modifier =
            Modifier
                .padding(p)
                .fillMaxSize(),
        ) {
            val (host, plays) = createRefs()
            val current = LocalViewModelStoreOwner.current!!
            NavHost(
                navController = navController,
                startDestination = Home,

                modifier =
                Modifier.constrainAs(host) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
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
                    HomeScreen(navController, bottomBar = bottomBar)
                }
                composable<MusicList> {
                    MusicListScreen(bottomBar = bottomBar, vm = vm, modifier = Modifier.fillMaxSize()) {
                        navController.navigate(Player(it.id))
                    }
                }
                composable<Settings> {
                    SettingsScreen(viewModel = vm, bottomBar = bottomBar)
                }
                composable<Player> {
                    CompositionLocalProvider(LocalViewModelStoreOwner provides current) {
                        PlayerScreen(it.toRoute<Player>().id, vm) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

// @Suppress("ktlint:standard:function-naming")
// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//    MikuMusicXTheme {
//        Surface(color = MaterialTheme.colorScheme.surface) {
//            App()
//        }
//    }
// }
