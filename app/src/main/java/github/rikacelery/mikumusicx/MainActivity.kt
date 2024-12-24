package github.rikacelery.mikumusicx

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import coil3.compose.rememberAsyncImagePainter
import github.rikacelery.mikumusicx.component.AppBottomNavBar
import github.rikacelery.mikumusicx.component.AppTopBar
import github.rikacelery.mikumusicx.component.SettingsScreen
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
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
@Composable
fun MusicListScreen(modifier: Modifier = Modifier) {
    LazyColumn(modifier.padding(20.dp)) {
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.padding(10.dp)) {
                    Text("aaa")
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Banner
        val pageCount: Int = 10
        val pagerState: PagerState = rememberPagerState { 4 }
        val autoScrollDuration: Long = 3000L
        val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
        if (isDragged.not()) {
            with(pagerState) {
                var currentPageKey by remember { mutableStateOf(0) }
                LaunchedEffect(key1 = currentPageKey) {
                    launch {
                        delay(timeMillis = autoScrollDuration)
                        val nextPage = (currentPage + 1).mod(pageCount)
                        animateScrollToPage(page = nextPage)
                        currentPageKey = nextPage
                    }
                }
            }
        }

        val cardModifier = Modifier.fillMaxWidth()
        ElevatedCard(cardModifier) {
            HorizontalPager(pagerState) { page ->
                val painter =
                    when (page) {
                        0 ->
                            rememberAsyncImagePainter(
                                R.mipmap.cover1,
                            )

                        1 ->
                            rememberAsyncImagePainter(
                                R.mipmap.cover2,
                            )

                        2 ->
                            rememberAsyncImagePainter(
                                R.mipmap.cover3,
                            )

                        3 ->
                            rememberAsyncImagePainter(
                                R.mipmap.cover4,
                            )

                        else -> throw IllegalStateException("Page $page not found")
                    }
                Image(
                    painter,
                    null,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .height(220.dp),
                )
            }
        }
        // Topic
        ElevatedCard({}, cardModifier) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "公告",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    "这里是MikuMusic哦!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        // Promote
        ElevatedCard({}, cardModifier) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "推广",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    """这里是MikuMusic哦!这里是MikuMusic哦!这里是MikuMusic哦!这里是MikuMusic哦!
这里是MikuMusic哦!这里是MikuMusic哦!
ssssss""",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
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
