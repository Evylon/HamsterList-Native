package org.stratum0.hamsterlist.android.gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.stratum0.hamsterlist.android.BuildConfig
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.android.gui.components.CheckboxWithLabel
import org.stratum0.hamsterlist.android.gui.components.DialogState
import org.stratum0.hamsterlist.android.gui.components.HamsterListDialog
import org.stratum0.hamsterlist.android.gui.listchooser.ListChooserState
import org.stratum0.hamsterlist.android.gui.listchooser.ListCreationSheet
import org.stratum0.hamsterlist.android.gui.listchooser.ListManager
import org.stratum0.hamsterlist.android.gui.listchooser.ListSharingSheet
import org.stratum0.hamsterlist.models.KnownHamsterList
import org.stratum0.hamsterlist.viewmodel.home.HomeUiState

@Composable
fun HomePage(
    uiState: HomeUiState,
    hasSharedContent: Boolean,
    onLoadHamsterList: (
        username: String,
        loadedList: KnownHamsterList,
        autoLoadLast: Boolean
    ) -> Unit,
    onDeleteHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    var autoLoadLast by rememberSaveable(uiState.autoLoadLast) {
        mutableStateOf(uiState.autoLoadLast)
    }
    var username by rememberSaveable(uiState.username) {
        mutableStateOf(uiState.username.orEmpty())
    }
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        HamsterListDialog(
            dialogState = DialogState.UsernameMissing,
            onDismiss = { showDialog = false }
        )
    }
    // TODO switch to event based callback pattern
    val loadHamsterList: (KnownHamsterList) -> Unit = { loadedList ->
        if (username.isNotBlank()) {
            coroutineScope.launch { sheetState.hide() }
            onLoadHamsterList(username, loadedList, autoLoadLast)
        } else {
            showDialog = true
        }
    }
    LaunchedEffect(hasSharedContent) {
        if (hasSharedContent) {
            coroutineScope.launch { sheetState.show() }
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            HomePageSheetContent(
                hasSharedContent = hasSharedContent,
                knownHamsterLists = uiState.knownHamsterLists,
                lastLoadedServer = uiState.lastLoadedServer,
                onLoadHamsterList = loadHamsterList
            )
        },
        modifier = modifier,
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.background
    ) {
        HomePageContent(
            username = username,
            autoLoadLast = autoLoadLast,
            knownHamsterLists = uiState.knownHamsterLists,
            sheetState = sheetState,
            onUsernameChange = { username = it },
            onAutoLoadLastChange = { autoLoadLast = it },
            onLoadHamsterList = loadHamsterList,
            onDeleteHamsterList = onDeleteHamsterList
        )
    }
}

@Composable
private fun HomePageSheetContent(
    hasSharedContent: Boolean,
    knownHamsterLists: List<KnownHamsterList>,
    lastLoadedServer: String?,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    if (hasSharedContent) {
        ListSharingSheet(
            knownHamsterLists = knownHamsterLists,
            onLoadHamsterList = onLoadHamsterList,
            modifier = modifier
        )
    } else {
        ListCreationSheet(
            lastLoadedServer = lastLoadedServer,
            onLoadHamsterList = onLoadHamsterList,
            modifier = modifier
        )
    }
}

@Composable
private fun HomePageHeader(
    username: String,
    autoLoadLast: Boolean,
    onUsernameChange: (String) -> Unit,
    onAutoLoadLastChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.hamster),
            contentDescription = stringResource(R.string.hamsterList_logo_description),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(140.dp)
        )
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            singleLine = true,
            label = { Text(stringResource(R.string.homepage_username_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
        )
        CheckboxWithLabel(
            label = stringResource(R.string.homepage_openLast_checkbox),
            checked = autoLoadLast,
            onCheckedChange = onAutoLoadLastChange
        )
    }
}

@Composable
@Suppress("LongParameterList") // TODO improve UIStates, switch to events
private fun HomePageContent(
    username: String,
    autoLoadLast: Boolean,
    knownHamsterLists: List<KnownHamsterList>,
    sheetState: ModalBottomSheetState,
    onUsernameChange: (String) -> Unit,
    onAutoLoadLastChange: (Boolean) -> Unit,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    onDeleteHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomePageHeader(
            username = username,
            autoLoadLast = autoLoadLast,
            onUsernameChange = onUsernameChange,
            onAutoLoadLastChange = onAutoLoadLastChange
        )
        ListManager(
            uiState = ListChooserState(knownHamsterLists),
            onLoadList = onLoadHamsterList,
            onDeleteList = onDeleteHamsterList,
            openListCreationSheet = {
                coroutineScope.launch { sheetState.show() }
            },
            modifier = Modifier.padding(vertical = 24.dp)
        )
        Spacer(Modifier.weight(1f))
        VersionNote(Modifier.padding(bottom = 8.dp))
    }
}

@Composable
private fun VersionNote(modifier: Modifier = Modifier) {
    Text(
        text = BuildConfig.VERSION_NAME,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
fun NewHomePagePreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomePage(
                uiState = HomeUiState(),
                hasSharedContent = false,
                onLoadHamsterList = { _, _, _ -> },
                onDeleteHamsterList = {}
            )
        }
    }
}
