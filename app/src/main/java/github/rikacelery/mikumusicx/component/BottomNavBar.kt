package github.rikacelery.mikumusicx.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun AppBottomNavBar(
    selectedItem: Int = 0,
    onclick: (Int) -> Unit = {},
) {
    val items = listOf("Home", "Musics", "Settings")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.MusicNote, Icons.Filled.Settings)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.MusicNote, Icons.Outlined.Settings)
    NavigationBarDefaults
    NavigationBar(
        tonalElevation = 8.dp,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    onclick(index)
                },
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview
@Composable
fun AppBottomNavBarPreview() {
    MikuMusicXTheme {
        AppBottomNavBar()
    }
}
