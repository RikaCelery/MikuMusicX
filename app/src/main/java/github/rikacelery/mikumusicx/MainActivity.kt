package github.rikacelery.mikumusicx

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import github.rikacelery.mikumusicx.component.AppBottomNavBar
import github.rikacelery.mikumusicx.component.AppTopBar
import github.rikacelery.mikumusicx.screen.HomeScreen
import github.rikacelery.mikumusicx.screen.MusicListScreen
import github.rikacelery.mikumusicx.screen.SettingsScreen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch {
            dataModel.load(dataStore)
        }
        enableEdgeToEdge()
//        enableEdgeToEdge(SystemBarStyle.auto(Color.CYAN, Color.BLACK), SystemBarStyle.dark(TRANSPARENT))
        super.onCreate(savedInstanceState)
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
    var currentPage by rememberSaveable { mutableIntStateOf(1) }
    Scaffold(
        modifier =
            Modifier
                .fillMaxSize(),
        topBar = { AppTopBar() },
        bottomBar = {
            AppBottomNavBar(onclick = { currentPage = it })
        },
    ) { innerPadding ->
        Surface(
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (currentPage) {
                0 -> HomeScreen()
                1 -> MusicListScreen()
                2 -> SettingsScreen()
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
