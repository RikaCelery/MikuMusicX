package github.rikacelery.mikumusicx

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import com.example.musicplayer.domain.repository.MusicRepository
import dagger.hilt.android.AndroidEntryPoint
import github.rikacelery.mikumusicx.ui.MikuMusicApp
import github.rikacelery.mikumusicx.ui.Settings
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import github.rikacelery.mikumusicx.ui.viewmodels.SharedViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val sharedViewModel: SharedViewModel by viewModels()

    @Inject
    lateinit var musicRepository: MusicRepository
    override fun onPause() {
//        Settings.save(this)
//        musicRepository.
        super.onPause()
    }

    override fun onStop() {
        Settings.save(this)

        super.onStop()
    }

    //    private val sharedViewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.load(this)
        enableEdgeToEdge()
        setContent {

            MikuMusicXTheme() {

                val conf = LocalConfiguration.current

                when (Settings.darkMode) {
                    1 -> conf.uiMode =
                        conf.uiMode and (Configuration.UI_MODE_NIGHT_MASK.inv() or Configuration.UI_MODE_NIGHT_NO)

                    2 -> conf.uiMode =
                        conf.uiMode and (Configuration.UI_MODE_NIGHT_MASK.inv() or Configuration.UI_MODE_NIGHT_YES)
                }

                val values: MutableList<ProvidedValue<out Any>> = mutableListOf()
                val providedConfiguration =
                    LocalConfiguration provides conf
                values.add(providedConfiguration)

                CompositionLocalProvider(*values.toTypedArray()) {
                    MikuMusicApp(sharedViewModel)
                }
            }
        }
    }
}