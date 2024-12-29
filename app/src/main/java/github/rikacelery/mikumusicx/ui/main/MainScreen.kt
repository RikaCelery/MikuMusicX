package github.rikacelery.mikumusicx.ui.main

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import github.rikacelery.mikumusicx.domain.other.PlayerState
import github.rikacelery.mikumusicx.ui.Settings
import github.rikacelery.mikumusicx.ui.main.musiclist.MusicListScreen
import github.rikacelery.mikumusicx.ui.main.setting.SettingsScreen
import github.rikacelery.mikumusicx.ui.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(shareVM: SharedViewModel, toSong: () -> Unit) {

    val vm: MainVM = hiltViewModel()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val state = rememberPagerState(0) { 4 }
    Scaffold(Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(snackBarHostState)
    }, bottomBar = {
        Column {
            if (shareVM.musicControllerUiState.currentSong != null)
                Box(
                    Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(8.dp)
                ) {
                    val scale by animateFloatAsState(
                        vm.fft,
                        tween(100, easing = FastOutSlowInEasing)
                    )
                    CurrentSongCard(
                        shareVM.musicControllerUiState.currentSong!!,
                        shareVM.musicControllerUiState.playerState == PlayerState.PLAYING,
                        Modifier.clickable(onClick = {
                            toSong()
                        }),
                        progress = shareVM.musicControllerUiState.currentPosition.toFloat()
                            .div(shareVM.musicControllerUiState.totalDuration.coerceAtLeast(1)),
                        albumScale = scale,
                        playOrToggleSong = {
                            when (shareVM.musicControllerUiState.playerState) {
                                PlayerState.PLAYING -> vm.pause()
                                PlayerState.PAUSED -> vm.resume()
                                PlayerState.STOPPED -> vm.play(0)
                                else -> {
                                    scope.launch {
                                        snackBarHostState.showSnackbar("playerState 为空")
                                    }
                                }
                            }
                        },
                        playNextSong = {
                            vm.next()
                        },
                        playPreviousSong = {
                            vm.previous()
                        }
                    )
                }
            BottomAppBar {
                NavigationBarItem(state.currentPage == 0, {
                    scope.launch { state.animateScrollToPage(0) }
                }, { Icon(Icons.Outlined.Home, null) })
                NavigationBarItem(state.currentPage == 1, {
                    scope.launch { state.animateScrollToPage(1) }
                }, { Icon(Icons.AutoMirrored.Outlined.QueueMusic, null) })
                NavigationBarItem(state.currentPage == 2, {
                    scope.launch { state.animateScrollToPage(2) }
                }, { Icon(Icons.Outlined.Settings, null) })
            }
        }
    }) { padding ->
        HorizontalPager(
            state,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalAlignment = Alignment.Top
        ) {
            val mainstate by vm.mainUiState.collectAsState()
            Box(Modifier.fillMaxSize()) {
                when (it) {
                    0 ->
                        HomeScreen()

                    1 -> {
                        MusicListScreen(mainstate.songs, Modifier.fillMaxSize(), onClick = {
                            vm.play(it)
//                        toSong()
                        }, onAddSongRequest = {
                            vm.addSong(it)
                        }, onRemoveSongRequest = {
                            vm.removeSong(it.mediaId)
                        }, onUpdateSongRequest = {
                            vm.updateSong(it.mediaId)
                        }, onAddPlayList = {
                            scope.launch() { vm.set() }
                        })
                    }

                    2 -> {
                        SettingsScreen(
                            Settings.seedColor,
                            { Settings.seedColor = it },
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) false else Settings.dynamicColor,
                            { Settings.dynamicColor = it },
                            Settings.darkMode,
                            { Settings.darkMode = it },
                        )
                    }

                    3 -> {

                        val width = LocalConfiguration.current.screenWidthDp.dp
                        val state = rememberScrollState()
                        Column(
                            Modifier.verticalScroll(state),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val scale by animateFloatAsState(vm.fft, tween(100, easing = FastOutSlowInEasing))
                            Box(
                                Modifier
                                    .scale(scale, 1f)
                                    .background(Color.Green)
                                    .fillMaxWidth()
                                    .height(20.dp)
//                            .align(Alignment.BottomCenter)
                            )
//                    Box(
//                        Modifier
//                            .scale(RmsPeak.peak,1f)
//                            .background(Color.Red)
//                            .fillMaxWidth()
//                            .height(20.dp)
////                            .align(Alignment.BottomCenter)
//                    )
                            Button({
                                scope.launch() { vm.set() }
                            }) {
                                Text("add playlist")
                            }
                            Button({
                                vm.play(0)
                            }) {
                                Text("play")
                            }
                            Button({
                                vm.pause()
                            }) {
                                Text("pause")
                            }
                            Button({
                                vm.resume()
                            }) {
                                Text("resume")
                            }
                            Button({
                                when (shareVM.musicControllerUiState.playerState) {
                                    PlayerState.PLAYING -> vm.pause()
                                    PlayerState.PAUSED -> vm.resume()
                                    PlayerState.STOPPED -> vm.play(0)
                                    else -> {
                                        scope.launch {
                                            snackBarHostState.showSnackbar("playerState 为空")
                                        }
                                    }
                                }
                            }) {
                                Text("toggle")
                            }
                            Button({
                                toSong()
                            }) {
                                Text("to player screen")
                            }
                            Slider(
                                shareVM.musicControllerUiState.currentPosition.toFloat(),
                                valueRange = 0f..shareVM.musicControllerUiState.totalDuration.toFloat(),
                                onValueChange = {
                                    vm.seek(it.toLong())
                                },
                            )
                            Text(
                                shareVM.musicControllerUiState.pretty().split(", ")
                                    .joinToString("\n")
                            )

                        }
                    }
                }
                if (mainstate.loading)
                    androidx.compose.ui.window.Dialog({}) {
                        Card {
                            Text(
                                "加载中...",
                                Modifier.padding(20.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

            }

        }


    }
}