package org.stratum0.hamsterlist.android.gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.BuildConfig
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.viewmodel.home.HomeUiState

@Composable
fun HomePage(
    uiState: HomeUiState,
    onLoadHamsterList: (
        username: String,
        hamsterListName: String,
        serverHostName: String,
        autoLoadLast: Boolean
    ) -> Unit,
) {
    var listId by rememberSaveable(uiState.currentListId) {
        mutableStateOf(uiState.currentListId.orEmpty())
    }
    var serverHostName by rememberSaveable(uiState.serverHostName) {
        mutableStateOf(uiState.serverHostName.orEmpty())
    }
    var username by rememberSaveable(uiState.username) {
        mutableStateOf(uiState.username.orEmpty())
    }
    var autoLoadLast by rememberSaveable(uiState.autoLoadLast) {
        mutableStateOf(uiState.autoLoadLast ?: false)
    }
    val isInputValid = listId.isNotBlank() && serverHostName.isNotBlank() && username.isNotBlank()

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                singleLine = true,
                label = { Text("Username") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
            )
            TextField(
                value = listId,
                onValueChange = { listId = it },
                singleLine = true,
                label = { Text("HamsterList name") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
            )
            TextField(
                value = serverHostName,
                onValueChange = { serverHostName = it },
                singleLine = true,
                label = { Text("Server host name") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Uri
                ),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
            )
            CheckboxWithLabel(
                label = "Automatically open last list",
                checked = autoLoadLast,
                onCheckedChange = { autoLoadLast = it }
            )
            Button(
                onClick = {
                    onLoadHamsterList(username, listId, serverHostName, autoLoadLast)
                },
                enabled = isInputValid
            ) {
                Text(text = "Load")
            }
        }
        VersionNote()
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = colors
        )
        Text(
            text = label,
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable { onCheckedChange(!checked) },
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun BoxScope.VersionNote(modifier: Modifier = Modifier) {
    Text(
        text = BuildConfig.VERSION_NAME,
        style = MaterialTheme.typography.caption,
        modifier = modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 8.dp)
    )
}

@PreviewLightDark
@Composable
fun HomePagePreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomePage(
                uiState = HomeUiState(),
                onLoadHamsterList = { _, _, _, _ -> }
            )
        }
    }
}
