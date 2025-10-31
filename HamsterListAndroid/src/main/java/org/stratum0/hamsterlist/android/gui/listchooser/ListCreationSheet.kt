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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.models.KnownHamsterList

@Composable
fun ListCreationSheet(
    lastLoadedServer: String?,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    var listId by rememberSaveable { mutableStateOf("") }
    var serverHostName by rememberSaveable {
        mutableStateOf(lastLoadedServer.orEmpty())
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
            label = { Text(stringResource(R.string.listCreation_listName_placeholder)) },
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
            label = { Text(stringResource(R.string.listCreation_server_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Uri
            ),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
        )
        Button(
            onClick = {
                onLoadHamsterList(KnownHamsterList(listId, serverHostName))
                listId = ""
            },
            enabled = isInputValid
        ) {
            Text(text = stringResource(R.string.listCreation_load_button))
        }
    }
}

@PreviewLightDark
@Composable
fun ListCreationSheetPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListCreationSheet(
                lastLoadedServer = "example.com",
                onLoadHamsterList = {}
            )
        }
    }
}
