package github.rikacelery.mikumusicx.ui.main.musiclist


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.ui.songscreen.component.MusicCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


@Suppress("ktlint:standard:function-naming")
@Composable
fun MusicListScreen(
    songs: List<Song>,
    modifier: Modifier = Modifier,
    onAddSongRequest: (Song) -> Unit = {},
    onRemoveSongRequest: (Song) -> Unit = {},
    onUpdateSongRequest: (Song) -> Unit = {},
    onAddPlayList:()-> Unit={},
    onClick: (Int) -> Unit = {},
) {
    Box(modifier = modifier) {
        var show by remember { mutableStateOf(false) }
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(songs,
                key = { idx, v -> v.mediaId }
            ) { idx, v ->
                var url by remember {
                    mutableStateOf("")
                }
                LaunchedEffect(Unit) {
                    launch {
                        url = API.fetchCover(v.mediaId)
                    }
                }
                var removeOnHold by remember {
                    mutableStateOf(false)
                }
                var showDesc by remember {
                    mutableStateOf(false)
                }

                ElevatedCard(
                    Modifier
                        .animateItem()
                        .height(IntrinsicSize.Min)
                ) {
                    Box {
                        val interactionSource = remember { MutableInteractionSource() }
                        MusicCard(
                            v.title,
                            v.subtitle,
                            url,
                            Modifier
                                .indication(interactionSource, LocalIndication.current)
                                .pointerInput(Unit) {
                                    detectTapGestures(onPress = {
                                        val press = PressInteraction.Press(it)
                                        interactionSource.emit(press)
                                        tryAwaitRelease()
                                        interactionSource.emit(PressInteraction.Release(press))
                                    }, onLongPress = {
                                        removeOnHold = true
                                    }, onDoubleTap = {
                                        showDesc = !showDesc
                                    }, onTap = {
                                        onClick(idx)
                                    })
                                },
                        )
                        this@ElevatedCard.AnimatedVisibility(
                            removeOnHold,
                            Modifier,
                            enter = slideIn {
                                IntOffset(-it.width, 0)
                            },
                            exit = slideOut {
                                IntOffset(-it.width, 0)
                            }
                        ) {
                            Box(
                                Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures {
                                            removeOnHold = false
                                        }
                                    }
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.errorContainer,
                                                MaterialTheme.colorScheme.errorContainer,
                                                Color.Transparent
                                            ),
                                            end = Offset(300.dp.value, 0f),
                                        )
                                    )
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                            ) {
                                IconButton(
                                    {
                                        onRemoveSongRequest(v)
                                    },
                                    Modifier.align(Alignment.CenterStart),
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        "remove song",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(
                        showDesc, Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Text(v.desc)
                        LaunchedEffect(Unit) {
                            if (v.desc.isBlank()) {
                                kotlin.runCatching {
                                    val info = API.fetchInfo(v.mediaId)
                                    onUpdateSongRequest(info)
                                }
                            }
                        }
                    }

                }
            }
            item {
                OutlinedButton(
                    {
                        show = true
                    }, Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Filled.Add, null)

                }

            }
        }
        if (show) {
            Dialog({ show = false }) {
                DialogBody(songs, onAddSong = onAddSongRequest) {
                    show = false
                }
            }
        }
        FloatingActionButton(onAddPlayList,Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Outlined.PlayArrow, null)
        }
    }
}

@Composable
fun DialogBody(songs: List<Song>, onAddSong: (Song) -> Unit, onDismissRequest: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    var enable by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }
    var info by remember { mutableStateOf<Song?>(null) }
    val regex by remember { mutableStateOf("(?:song\\?id=|^)(\\d+)".toRegex()) }
    val regexShortLink by remember { mutableStateOf("https?://163(\\.cn|cn\\.tv)/([a-zA-Z0-9]+)".toRegex()) }
    val current = LocalContext.current
    Card {
        Column(
            Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "添加歌曲(网易云)",
                Modifier.padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            OutlinedTextField(text, { text = it }, enabled = enable)
            val musicId by remember {
                derivedStateOf {
                    regex
                        .find(text)
                        ?.groups
                        ?.get(1)
                        ?.value
                }
            }
            val exists by remember {
                derivedStateOf {
                    songs.any { it.mediaId == musicId }
                }
            }
            if (musicId != null) {
                Text(
                    "解析到的歌曲id是$musicId${if (exists) "，已存在" else ""}",
                    color = if (exists) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            } else {
                Text("请将网易云分享链接粘贴到输入框")
            }
            var playable by remember { mutableStateOf<Boolean>(true) }
            val mutex by remember { mutableStateOf(Mutex()) }
            LaunchedEffect(text) {

                delay(500)
                println(text)
                val result = regexShortLink.find(text)
                println(result)
                if (result != null) {
                    mutex.withLock {
                        enable = false
                        try {
                            Log.i("Redirecting", result.value)
                            withContext(Dispatchers.IO) {
                                text = API.redirect(result.value)
                            }
                            Log.i("Redirect", result.value)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(current, "解析失败\n$e", Toast.LENGTH_SHORT).show()
                            }
                        }
                        enable = true
                    }
                }
            }
            LaunchedEffect(musicId) {
                mutex.withLock {
                    enable = false
                    musicId?.runCatching {
                        val fetchInfo = API.fetchInfo(this)
                        info = fetchInfo
                        playable = API.playable(fetchInfo.mediaId)
                    }
                    enable = true
                }
            }
            val music = info
            if (music != null) {
                if (!playable) {
                    Text(
                        "此歌曲不可播放(可能是会员歌曲或下架)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                MusicCard(music.title, music.subtitle, music.imageUrl)
            }
            val running by remember { mutableStateOf(false) }
            Button({
                if (info != null) {
                    onAddSong(info!!)
                    onDismissRequest()
                } else {
                    scope.launch {
                        Toast.makeText(current, "解析失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }, enabled = enable && !exists) {
                Text(if (enable) (if (running) "正在加载歌曲信息..." else (if (exists) "已存在" else "确认")) else "请稍后...")
                Icon(Icons.Outlined.Check, null)
            }
        }
    }
}
