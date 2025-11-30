package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import org.stratum0.hamsterlist.android.gui.components.CheckboxWithLabel
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.utils.parseHamsterListFromUrl
import org.stratum0.hamsterlist.viewmodel.home.ListCreationTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCreationSheet(
    lastLoadedServer: String?,
    onLoadHamsterList: (HamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { ListCreationTab.entries }
    var selectedTab by rememberSaveable { mutableStateOf(tabs.first()) }
    Column(
        modifier = modifier.padding(20.dp),
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = modifier.padding(bottom = 20.dp)
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    text = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    onClick = { selectedTab = tab }
                )
            }
        }
        AnimatedContent(targetState = selectedTab) { selectedTab ->
            when (selectedTab) {
                ListCreationTab.LINK -> LinkHamsterListCreation(onLoadHamsterList = onLoadHamsterList)
                ListCreationTab.SERVER -> RemoteHamsterListCreation(
                    lastLoadedServer = lastLoadedServer,
                    onLoadHamsterList = onLoadHamsterList,
                )

                ListCreationTab.LOCAL -> LocalHamsterListCreation(onLoadHamsterList = onLoadHamsterList)
            }
        }
    }
}

@Composable
fun LinkHamsterListCreation(
    modifier: Modifier = Modifier,
    onLoadHamsterList: (HamsterList) -> Unit,
) {
    var url by rememberSaveable { mutableStateOf("") }
    val parsedHamsterList = remember(url) {
        parseHamsterListFromUrl(url)
    }
    val onLoad = {
        if (parsedHamsterList != null) {
            onLoadHamsterList(parsedHamsterList)
            url = ""
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = url,
            onValueChange = { url = it },
            singleLine = true,
            label = { Text(stringResource(R.string.listCreation_link_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            )
        )
        Button(
            onClick = onLoad,
            enabled = parsedHamsterList != null
        ) {
            Text(text = stringResource(R.string.listCreation_load_button))
        }
    }
}

@Composable
private fun RemoteHamsterListCreation(
    lastLoadedServer: String?,
    onLoadHamsterList: (HamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf("") }
    var serverHostName by rememberSaveable {
        mutableStateOf(lastLoadedServer.orEmpty())
    }
    val isInputValid = title.isNotBlank() && serverHostName.isNotBlank()
    var useTitleAsId by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
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
        )
        CheckboxWithLabel(
            label = stringResource(R.string.use_title_as_identifier),
            checked = useTitleAsId,
            onCheckedChange = { useTitleAsId = !useTitleAsId }
        )
        Button(
            onClick = {
                val hamsterList = if (useTitleAsId) {
                    HamsterList(listId = title, serverHostName = serverHostName)
                } else {
                    HamsterList(serverHostName = serverHostName, title = title)
                }
                onLoadHamsterList(hamsterList)
                title = ""
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
    var title by rememberSaveable { mutableStateOf("") }
    val isInputValid = title.isNotBlank()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            singleLine = true,
            label = { Text(stringResource(R.string.listCreation_listName_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            ),
        )
        Button(
            onClick = {
                onLoadHamsterList(
                    HamsterList(listId = title, serverHostName = "", title = title, isLocal = true)
                )
                title = ""
            },
            enabled = isInputValid
        ) {
            Text(text = stringResource(R.string.listCreation_load_button))
        }
    }
}

private val ListCreationTab.title: String
    @Composable
    get() = when (this) {
        ListCreationTab.LINK -> stringResource(R.string.listCreation_link_tabTitle)
        ListCreationTab.SERVER -> stringResource(R.string.listCreation_server_tabTitle)
        ListCreationTab.LOCAL -> stringResource(R.string.listCreation_local_tabTitle)
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
