package github.rikacelery.mikumusicx.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.VM
import github.rikacelery.mikumusicx.component.MusicCard
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class Music(
    val name: String,
    val artist: String = "",
    val id: Long = 0,
    val cover: String = "",
    val desc: String = "",
)

val musicData =
    mutableStateListOf(
        Music(
            "蜘蛛糸モノポリー",
            "sasakure.UK 初音ミク",
            26440351,
        ),
        Music(
            "ワールドイズマイン",
            "初音ミク ryo",
            22677570,
        ),
        Music(
            "初音ミクの消失",
            "初音ミク CosMo@暴走P",
            22686712,
        ),
        Music(
            "炉心融解",
            "鏡音リン",
            526473118,
        ),
        Music(
            "私は人間じゃないから",
            "初音ミク",
            22717499,
        ),
        Music(
            "くるみ☆ぽんちお",
            "まだ仔,初音ミク",
            4884801,
        ),
        Music(
            "アンノウン・マザーグース",
            "wowaka,初音ミク",
            502455381,
        ),
        Music(
            "ねぇねぇねぇ",
            "ピノキオピー,鏡音リン,初音ミク",
            1859603835,
        ),
        Music(
            "夢に咲く",
            "guesswhois45,初音ミク",
            1866017709,
        ),
        Music(
            "Blessing",
            "halyosy,初音ミク,鏡音リン,鏡音レン,巡音ルカ,KAITO,MEIKO",
            1966112474,
        ),
        Music(
            "ラビットホール",
            "DECO*27,初音ミク",
            2043178301,
        ),
        Music(
            "命に嫌われている",
            "カンザキイオリ,初音ミク",
            1911300549,
        ),
        Music(
            "メズマライザー",
            "サツキ,初音ミク,重音テト",
            2153390036,
        ),
        Music(
            "妄想感傷代償連盟",
            "DECO*27,初音ミク",
            432486474,
        ),
        Music(
            "Ievan Polkka",
            "初音ミク",
            22677558,
        ),
        Music(
            "ツギハギスタッカート",
            "とあ,初音ミク",
            30148963,
        ),
        Music(
            "しんかいしょうじょ",
            "初音ミク,ゆうゆ",
            29023577,
        ),
        Music(
            "おこちゃま戦争",
            "Giga,鏡音リン,鏡音レン",
            29827559,
        ),
        Music(
            "少女A",
            "椎名もた,鏡音リン",
            33516239,
        ),
        Music(
            "ごめんね ごめんね",
            "きくお,初音ミク",
            435004129,
        ),
        Music(
            "みんなみくみくにしてあげる",
            "MOSAIC.WAV,ika,初音ミク",
            29785926,
        ),
        Music(
            "ぺぽよ-めんへらーめん",
            "ぺぽよ,初音ミク",
            1922379483,
        ),
        Music(
            "死別",
            "シャノン,GUMI",
            2134872913,
        ),
        Music(
            "ヴァンパイア",
            "DECO*27,初音ミク",
            1840474281,
        ),
        Music(
            "すとれいきゃっと",
            "タケノコ少年,初音ミク",
            2157894445,
        ),
        Music(
            "匿名M",
            "ピノキオピー,初音ミク,ARuFa",
            2020725645,
        ),
        Music(
            "太陽系デスコ",
            "ナユタン星人,初音ミク",
            459717345,
        ),
    )

@Suppress("ktlint:standard:function-naming")
@Composable
fun MusicListScreen(
    navController: NavController = rememberNavController(),
    bottomBar: @Composable () -> Unit = {},
    vm: VM = viewModel(),
    modifier: Modifier = Modifier,
    onClick: (Music) -> Unit = {},
) {
    Box(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(vm.songs, key = { idx, v -> v.id }) { idx, v ->
                var url by remember {
                    mutableStateOf("")
                }
                LaunchedEffect(Unit) {
                    launch {
                        url = API.fetchCover(v.id)
                    }
                }
                var removeOnHold by remember {
                    mutableStateOf(false)
                }
                ElevatedCard(Modifier
                    .animateItem()
                    .height(IntrinsicSize.Min)
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    MusicCard(
                        v.name,
                        v.artist,
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
                            }, onTap = {
                                onClick(v)
                            })
                        },
                        viewModel = vm,

                        )
                    AnimatedVisibility(
                        removeOnHold,
                        Modifier
                        ,
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
                                    vm.removeSong(v.id)
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
            }
        }
        var show by remember { mutableStateOf(false) }
        if (show) {
            Dialog({ show = false }) {
                DialogBody(vm) {
                    show = false
                }
            }
        }
        FloatingActionButton(
            {
                show = true
            }, Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Outlined.Add, null)
        }
    }
}

@Composable
fun DialogBody(vm: VM = viewModel(), onDismissRequest: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    var enable by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }
    var info by remember { mutableStateOf<Music?>(null) }
    val regex by remember { mutableStateOf("(?:song\\?id=|^)(\\d+)".toRegex()) }
    val regexShortLink by remember { mutableStateOf("https?://163(\\.cn|cn\\.tv)/(\\w+)".toRegex()) }
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
                        ?.value?.toLong()
                }
            }
            val exists by remember {
                derivedStateOf {
                    vm.songs.any { it.id == musicId }
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
                println(regexShortLink.find(text))
                if (regexShortLink.find(text) != null) {
                    mutex.withLock {
                        enable = false
                        try {
                            Log.i("Redirecting", text)
                            withContext(Dispatchers.IO) {
                                text = API.redirect(text)
                            }
                            Log.i("Redirect", text)
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
                        playable = API.playable(fetchInfo.id)
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
                MusicCard(music.name, music.artist, music.cover)
            }
            val running by remember { mutableStateOf(false) }
            Button({
                if (info != null) {
                    vm.addSong(info!!)
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

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
fun DialogBodyPreview() {
    MikuMusicXTheme {
        DialogBody()
    }
}

// @Suppress("ktlint:standard:function-naming")
// @Preview(showBackground = true)
// @Composable
// fun MusicListScreenPreview() {
//    MikuMusicXTheme {
//        MusicListScreen()
//    }
// }
