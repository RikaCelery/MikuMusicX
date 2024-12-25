package github.rikacelery.mikumusicx.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun AppTopBar(onclick: () -> Unit = {}) {
    Row(Modifier.statusBarsPadding().padding(6.dp).height(46.dp), verticalAlignment = Alignment.CenterVertically) {
//        IconButton(onClick = onclick) {
//            Icon(Icons.Filled.MoreVert, "settings")
//        }
        Spacer(Modifier.width(10.dp))
        Text("MikuMusicX", fontSize = 20.sp)
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview
@Composable
fun AppTopBarPreview() {
    MikuMusicXTheme {
        AppTopBar()
    }
}
