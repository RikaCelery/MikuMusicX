package github.rikacelery.mikumusicx.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.songscreen.SongViewModel
import github.rikacelery.mikumusicx.ui.main.MainScreen
import github.rikacelery.mikumusicx.ui.songscreen.SongScreen
import github.rikacelery.mikumusicx.ui.viewmodels.SharedViewModel


@Composable
fun MikuMusicApp(sharedViewModel: SharedViewModel) {

    val nav = rememberNavController()
    NavHost(
        navController = nav,
        startDestination = "home",
    ) {
        composable(route = "home") {
            MainScreen(sharedViewModel){
                nav.navigate("player")
            }
        }
        composable(route = "player") {
            val songViewModel: SongViewModel = hiltViewModel()
            SongScreen(songViewModel::onEvent, sharedViewModel.musicControllerUiState) {
                nav.popBackStack()
            }
        }
    }
}