package github.rikacelery.mikumusicx.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import github.rikacelery.mikumusicx.VM
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingsScreen(viewModel: VM = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    rememberColorPickerController()
    val scope = rememberCoroutineScope()
    val channel = Channel<Color>(1)
    LaunchedEffect(Unit) {
        channel.consumeAsFlow().debounce(30).collect { value ->
            viewModel.setSeedColor(value)
        }
    }
    Column(
        modifier =
            Modifier
                .padding(20.dp)
                .fillMaxHeight(),
//        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            "设置",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
        )
        SettingGroup("General") {
            HorizontalSettingItem("主色调", leadingIcon = {
                Icon(Icons.Rounded.ColorLens, null)
            }) {
                HueBar(state.seedColor) {
                    scope.launch {
                        channel.send(Color.hsv(it, 1f, .5f))
                    }
                }
            }
            HorizontalSettingItem("自动取色", leadingIcon = {
                Icon(Icons.Rounded.Colorize, null)
            }) {
                Switch(state.dynamicColor, onCheckedChange = {
                    viewModel.setDynamicColor(it)
                })
            }
            HorizontalSettingItem("夜间模式", leadingIcon = {
                Icon(Icons.Rounded.DarkMode, null)
            }) {
                Selects(listOf("跟随系统", "日间模式", "夜间模式"), state.darkMode) {
                    viewModel.setDarkMode(it)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.Selects(
    options: List<String>,
    select: Int,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Surface(
            Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(5.dp),
        ) {
            Row(Modifier.padding(15.dp, 10.dp)) {
                Text(
                    options[select],
                )
                Spacer(Modifier.width(5.dp))
                Icon(Icons.Filled.ExpandMore, null)
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { idx, option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSelect(idx)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.padding(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        Column(Modifier.padding(vertical = 10.dp), content = content)
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HorizontalSettingItem(
    title: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        leadingIcon?.invoke()
        if (leadingIcon != null) {
            Spacer(Modifier.width(5.dp))
        }
        if (title != null) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.width(10.dp))
        Spacer(Modifier.weight(1f))
        content()
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(backgroundColor = 0xFFF0F0F0)
@Composable
fun PreviewSettingItem() {
    val seedColor = Color.Cyan
    MikuMusicXTheme {
        Column {
            SettingGroup("主色调") {
                HueBar(seedColor) {
                }
                Switch(true, {})
            }
            HorizontalSettingItem("主色调") {
                HueBar(seedColor) {
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
fun PreviewSettings() {
    MikuMusicXTheme {
        SettingsScreen()
    }
}
