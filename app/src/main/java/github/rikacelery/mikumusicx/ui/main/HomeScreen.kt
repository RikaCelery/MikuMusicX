package github.rikacelery.mikumusicx.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil3.compose.rememberAsyncImagePainter
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.R
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
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
                                R.drawable.cover1,
                            )

                        1 ->
                            rememberAsyncImagePainter(
                                R.drawable.cover2,
                            )

                        2 ->
                            rememberAsyncImagePainter(
                                R.drawable.cover3,
                            )

                        3 ->
                            rememberAsyncImagePainter(
                                R.drawable.cover4,
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
                var text by remember { mutableStateOf("加载中...") }
                LaunchedEffect(Unit) {
                    for (i in 1..10) {
                        try {
                            text =
                                API.client.get("http://lingrain.online/gonggao.html").bodyAsText()
                            break
                        } catch (e: Exception) {
                            text = e.toString()
                            delay(1000)
                        }
                    }
                }
                Text(
                    text,
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

                var text by remember { mutableStateOf("加载中...") }
                LaunchedEffect(Unit) {
                    for (i in 1..10) {
                        try {
                            text =
                                API.client.get("http://lingrain.online/tuiguang.html").bodyAsText()
                            break
                        } catch (e: Exception) {
                            text = e.toString()
                            delay(1000)
                        }
                    }
                }
                Text(
                    text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Spacer(Modifier.height(10.dp))
                val context = LocalContext.current
                Row(
                    Modifier.height(20.dp).clickable(onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("http://lingrain.online")
                            startActivity(context, intent, null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }),
                ) {
                    Icon(Icons.Outlined.Web, null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(10.dp))
                    Text("官网", color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.height(20.dp).clickable(onClick = {
                        try {
                            val qq =
                                Regex("官方QQ群(\\d+)")
                                    .find(text)
                                    ?.groups
                                    ?.get(1)
                                    ?.value ?: "798593085"
                            startActivity(
                                context,
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=$qq&card_type=group&source=qrcode",
                                    ),
                                ),
                                null,
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }),
                ) {
                    Image(
                        painterResource(R.drawable.qq),
                        null,
                        contentScale = ContentScale.Fit,
                        colorFilter =
                            ColorFilter.tint(
                                MaterialTheme.colorScheme.secondary,
                            ),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("加官方群", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
