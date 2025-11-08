package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.stratum0.hamsterlist.models.HamsterList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCreationSheet(
    lastLoadedServer: String?,
    onLoadHamsterList: (HamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(
        modifier = modifier.padding(20.dp),
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = modifier.padding(bottom = 20.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                text = {
                    Text(
                        "Server",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = { selectedTab = 0 }
            )
            Tab(
                selected = selectedTab == 1,
                text = {
                    Text(
                        "Local",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = { selectedTab = 1 }
            )
        }
        AnimatedContent(
            targetState = selectedTab,

            ) { selectedTab ->
            when (selectedTab) {
                0 -> RemoteHamsterListCreation(
                    lastLoadedServer = lastLoadedServer,
                    onLoadHamsterList = onLoadHamsterList,
                )

                1 -> LocalHamsterListCreation(onLoadHamsterList = onLoadHamsterList)
                else -> {}
            }
        }
    }
}

@Composable
private fun RemoteHamsterListCreation(
    lastLoadedServer: String?,
    onLoadHamsterList: (HamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    var listId by rememberSaveable { mutableStateOf("") }
    var serverHostName by rememberSaveable {
        mutableStateOf(lastLoadedServer.orEmpty())
    }
    val isInputValid = listId.isNotBlank() && serverHostName.isNotBlank()

    Column(
        modifier = modifier.fillMaxWidth(),
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
        )
        TextField(
            value = serverHostName,
            onValueChange = { serverHostName = it },
            singleLine = true,
            label = { Text(stringResource(R.string.listCreation_server_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isInputValid) {
                        onLoadHamsterList(HamsterList(listId, serverHostName))
                        listId = ""
                    }
                }
            )
        )
        Button(
            onClick = {
                onLoadHamsterList(HamsterList(listId, serverHostName))
                listId = ""
            },
            enabled = isInputValid
        ) {
            Text(text = stringResource(R.string.listCreation_load_button))
        }
    }
}

@Composable
private fun LocalHamsterListCreation(
    onLoadHamsterList: (HamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    var listId by rememberSaveable { mutableStateOf("") }
    val isInputValid = listId.isNotBlank()

    Column(
        modifier = modifier.fillMaxWidth(),
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
        )
        Button(
            onClick = {
                onLoadHamsterList(
                    HamsterList(listId, serverHostName = "", isLocal = true)
                )
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
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            ListCreationSheet(
                lastLoadedServer = "example.com",
                onLoadHamsterList = {}
            )
        }
    }
}
