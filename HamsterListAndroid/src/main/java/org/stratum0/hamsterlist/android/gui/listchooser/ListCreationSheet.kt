package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import org.stratum0.hamsterlist.viewmodel.home.HomeUiState

@Composable
fun ListCreationSheet(
    uiState: HomeUiState,
    onLoadHamsterList: (
        hamsterListName: String,
        serverHostName: String,
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var listId by rememberSaveable(uiState.currentListId) {
        mutableStateOf(uiState.currentListId.orEmpty())
    }
    var serverHostName by rememberSaveable(uiState.serverHostName) {
        mutableStateOf(uiState.serverHostName.orEmpty())
    }
    val isInputValid = listId.isNotBlank() && serverHostName.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {

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
        Button(
            onClick = {
                onLoadHamsterList(listId, serverHostName)
            },
            enabled = isInputValid
        ) {
            Text(text = "Load")
        }
    }
}

@PreviewLightDark
@Composable
fun ListCreationSheetPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListCreationSheet(
                uiState = HomeUiState(),
                onLoadHamsterList = { _, _ -> }
            )
        }
    }
}
