package org.stratum0.hamsterlist.android.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
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
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.viewmodel.HomeUiState

@Composable
fun HomePage(
    uiState: HomeUiState,
    onLoadHamsterList: (username: String, hamsterListName: String, serverHostName: String) -> Unit,
) {
    var listId by rememberSaveable {
        mutableStateOf(uiState.currentListId)
    }
    var serverHostName by rememberSaveable(uiState.serverHostName) {
        mutableStateOf(uiState.serverHostName)
    }
    var username by rememberSaveable(uiState.username) {
        mutableStateOf(uiState.username)
    }
    val isInputValid = !listId.isNullOrBlank() && !serverHostName.isNullOrBlank() && !username.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = username.orEmpty(),
            onValueChange = { username = it },
            singleLine = true,
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
        )
        TextField(
            value = listId.orEmpty(),
            onValueChange = { listId = it },
            singleLine = true,
            label = { Text("HamsterList name") },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
        )
        TextField(
            value = serverHostName.orEmpty(),
            onValueChange = { serverHostName = it },
            singleLine = true,
            label = { Text("Server host name") },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Uri,
                capitalization = KeyboardCapitalization.None
            ),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
        )
        Button(
            onClick = {
                onLoadHamsterList(username.orEmpty(), listId.orEmpty(), serverHostName.orEmpty())
            },
            enabled = isInputValid
        ) {
            Text(text = "Load")
        }
    }
}

@PreviewLightDark
@Composable
fun HomePagePreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomePage(
                uiState = HomeUiState(),
                onLoadHamsterList = { _, _, _ -> }
            )
        }
    }
}
