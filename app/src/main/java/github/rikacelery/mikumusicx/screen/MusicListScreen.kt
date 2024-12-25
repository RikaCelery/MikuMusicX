package github.rikacelery.mikumusicx.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.VM
import github.rikacelery.mikumusicx.component.MusicCard
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.launch

data class Music(
    val name: String,
    val artist: String = "",
    val cover: String = "",
    val id: Long = 0,
)

val musicData =
    mutableStateListOf(
        Music(
            "蜘蛛糸モノポリー",
            "sasakure.UK 初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iFRN923lc13i&pwd=8888",
            26440351,
        ),
        Music(
            "ワールドイズマイン",
            "初音ミク ryo",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iFX5S27i6l2h&pwd=cltn",
            2636690582,
        ),
        Music(
            "初音ミクの消失",
            "初音ミク CosMo@暴走P",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iJBD6267qhmb&pwd=8888",
            22686712,
        ),
        Music(
            "炉心融解",
            "鏡音リン",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iRRJ4267rfle&pwd=8888",
            526473118,
        ),
        Music(
            "私は人間じゃないから",
            "初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iFRY026wzzfg&pwd=8888",
            22717499,
        ),
        Music(
            "くるみ☆ぽんちお",
            "まだ仔,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iB7EH26x0uyb&pwd=8888",
            4884801,
        ),
        Music(
            "アンノウン・マザーグース",
            "wowaka,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/i68EP26zh2ve&pwd=8888",
            502455381,
        ),
        Music(
            "ねぇねぇねぇ",
            "ピノキオピー,鏡音リン,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iA0IS278jgcf&pwd=fxtk",
            1859603835,
        ),
        Music(
            "夢に咲く",
            "guesswhois45,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iDTMF27i2ngj&pwd=ax3u",
            1866017709,
        ),
        Music(
            "Blessing",
            "halyosy,初音ミク,鏡音リン,鏡音レン,巡音ルカ,KAITO,MEIKO",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iOIXT27o505e&pwd=hlnj",
            1966112474,
        ),
        Music(
            "ラビットホール",
            "DECO*27,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iJ1VM27p3zad&pwd=6fb6",
            2043178301,
        ),
        Music(
            "命に嫌われている",
            "カンザキイオリ,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iFLBH27uehyf&pwd=b4o2",
            1911300549,
        ),
        Music(
            "メズマライザー",
            "サツキ,初音ミク,重音テト",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iRT6O28m6opi&pwd=7cdy",
            2153390036,
        ),
        Music(
            "妄想感傷代償連盟",
            "DECO*27,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iU4QU28m70mh&pwd=be2w",
            432486474,
        ),
        Music(
            "Ievan Polkka",
            "初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/i2XF328p5jte&pwd=9svf",
            22677558,
        ),
        Music(
            "ツギハギスタッカート",
            "とあ,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iINEU28p5yte&pwd=3r0r",
            30148963,
        ),
        Music(
            "しんかいしょうじょ",
            "初音ミク,ゆうゆ",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iGIEH28p65mj&pwd=dvkh",
            29023577,
        ),
        Music(
            "おこちゃま戦争",
            "Giga,鏡音リン,鏡音レン",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iUMBU2bqe6pa&pwd=c56k",
            29827559,
        ),
        Music(
            "少女A",
            "椎名もた,鏡音リン",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iOWBV2bssjhg&pwd=6dlp",
            33516239,
        ),
        Music(
            "ごめんね ごめんね",
            "きくお,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iTUIU2bst4hc&pwd=g5i4",
            1321385247,
        ),
        Music(
            "みんなみくみくにしてあげる",
            "MOSAIC.WAV,ika,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iBHTY2bsthqj&pwd=bdvh",
            29785926,
        ),
        Music(
            "ぺぽよ-めんへらーめん",
            "ぺぽよ,初音ミク",
            "https://api.jqyy.org/parse_lanzou.php?url=https://wwp.lanzoup.com/iJFKD2div4wb&pwd=6bdd",
            1922379483,
        ),
        Music(
            "死別",
            "シャノン,GUMI",
            "http://music.163.com/song/media/outer/url?id=2134872913.mp3",
            2134872913,
        ),
        Music(
            "ヴァンパイア",
            "DECO*27,初音ミク",
            "http://music.163.com/song/media/outer/url?id=1840474281.mp3",
            1881239894,
        ),
        Music(
            "すとれいきゃっと",
            "タケノコ少年,初音ミク",
            "http://music.163.com/song/media/outer/url?id=2157894445.mp3",
            2157894445,
        ),
        Music(
            "匿名M",
            "ピノキオピー,初音ミク,ARuFa",
            "http://music.163.com/song/media/outer/url?id=2020725645.mp3",
            2020725645,
        ),
        Music(
            "太陽系デスコ",
            "ナユタン星人,初音ミク",
            "http://music.163.com/song/media/outer/url?id=459717345.mp3",
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
    Box {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(musicData) { idx, v ->
                var url by remember {
                    mutableStateOf("")
                }
                LaunchedEffect(Unit) {
                    launch {
                        url = API.fetchCover(v.id)
                        println(url)
                    }
                }
                MusicCard(
                    v.name,
                    v.artist,
                    url,
                    //                cacheKey = v.id.toString(),
                    viewModel = vm,
                    onClick = {
                        onClick(v)
                    },
                )
            }
        }
        var show by remember { mutableStateOf(false) }
        if (show) {
            Dialog({ show = false }) {
                DialogBody()
            }
        }
        FloatingActionButton({
            show=true
        },Modifier.align(Alignment.BottomEnd)) {
            Icon(Icons.Outlined.Add, null)
        }
    }
}

@Composable
fun DialogBody() {
    var text by remember { mutableStateOf("") }
    val regex by remember { mutableStateOf("song\\?id=(\\d+)".toRegex()) }
    Card {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(text, { text = it })
            val matchGroup =
                regex
                    .find(text)
                    ?.groups
                    ?.get(1)
                    ?.value
            if (matchGroup != null) {
                Text("解析到的歌曲id是$matchGroup")
            } else {
                Text("请将网易云分享链接粘贴到输入框")
            }

            Button({}) {
                Text("确认")
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
