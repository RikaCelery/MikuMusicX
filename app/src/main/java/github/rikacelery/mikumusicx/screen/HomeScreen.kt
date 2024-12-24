package github.rikacelery.mikumusicx.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import github.rikacelery.mikumusicx.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen() {
    Column(
        Modifier.Companion
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
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

        val cardModifier = Modifier.Companion.fillMaxWidth()
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
                    contentScale = ContentScale.Companion.Crop,
                    modifier =
                        Modifier.Companion
                            .height(220.dp),
                )
            }
        }
        // Topic
        ElevatedCard({}, cardModifier) {
            Column(Modifier.Companion.padding(20.dp)) {
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
            Column(Modifier.Companion.padding(20.dp)) {
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