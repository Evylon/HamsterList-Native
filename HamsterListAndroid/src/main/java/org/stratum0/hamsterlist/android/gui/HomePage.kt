package org.stratum0.hamsterlist.android.gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.BuildConfig
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.android.gui.components.CheckboxWithLabel
import org.stratum0.hamsterlist.android.gui.components.HamsterListDialog
import org.stratum0.hamsterlist.android.gui.listchooser.ListChooserState
import org.stratum0.hamsterlist.android.gui.listchooser.ListCreationSheet
import org.stratum0.hamsterlist.android.gui.listchooser.ListManager
import org.stratum0.hamsterlist.android.gui.listchooser.ListSharingSheet
import org.stratum0.hamsterlist.models.KnownHamsterList
import org.stratum0.hamsterlist.viewmodel.home.HomeAction
import org.stratum0.hamsterlist.viewmodel.home.HomeSheetState
import org.stratum0.hamsterlist.viewmodel.home.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    uiState.dialogState?.let { dialogState ->
        HamsterListDialog(
            dialogState = dialogState,
            onDismiss = { onAction(HomeAction.DismissDialog) }
        )
    }
    uiState.sheetState?.let { sheetState ->
        ModalBottomSheet(
            onDismissRequest = { onAction(HomeAction.DismissSheet) }
        ) {
            HomePageSheetContent(
                sheetState = sheetState,
                knownHamsterLists = uiState.knownHamsterLists,
                lastLoadedServer = uiState.lastLoadedServer,
                onLoadHamsterList = onLoadHamsterList
            )
        }
    }
    HomePageContent(
        uiState = uiState,
        knownHamsterLists = uiState.knownHamsterLists,
        onAction = onAction,
        onLoadHamsterList = onLoadHamsterList,
        modifier = modifier
    )
}

@Composable
private fun HomePageSheetContent(
    sheetState: HomeSheetState,
    knownHamsterLists: List<KnownHamsterList>,
    lastLoadedServer: String?,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    when (sheetState) {
        is HomeSheetState.ContentSharing -> ListSharingSheet(
            knownHamsterLists = knownHamsterLists,
            onLoadHamsterList = onLoadHamsterList,
            modifier = modifier
        )

        is HomeSheetState.ListCreation -> ListCreationSheet(
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
    onAction: (HomeAction) -> Unit,
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
            onValueChange = { onAction(HomeAction.UpdateUsername(it)) },
            singleLine = true,
            label = { Text(stringResource(R.string.homepage_username_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            ),
        )
        CheckboxWithLabel(
            label = stringResource(R.string.homepage_openLast_checkbox),
            checked = autoLoadLast,
            onCheckedChange = { onAction(HomeAction.UpdateAutoLoadLast(it)) }
        )
    }
}

@Composable
@Suppress("LongParameterList") // TODO improve UIStates, switch to events
private fun HomePageContent(
    uiState: HomeUiState,
    knownHamsterLists: List<KnownHamsterList>,
    onAction: (HomeAction) -> Unit,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomePageHeader(
            username = uiState.username.orEmpty(),
            autoLoadLast = uiState.autoLoadLast,
            onAction = onAction
        )
        ListManager(
            uiState = ListChooserState(knownHamsterLists),
            onLoadList = onLoadHamsterList,
            onDeleteList = { list -> onAction(HomeAction.DeleteHamsterList(list)) },
            openListCreationSheet = { onAction(HomeAction.OpenListCreationSheet) },
            modifier = Modifier.padding(vertical = 24.dp)
                .weight(1f)
        )
        VersionNote(Modifier.padding(bottom = 8.dp))
    }
}

@Composable
private fun VersionNote(modifier: Modifier = Modifier) {
    Text(
        text = BuildConfig.VERSION_NAME,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Suppress("MagicNumber")
@PreviewLightDark
@Composable
fun NewHomePagePreview() {
    HamsterListTheme {
        Surface {
            HomePage(
                uiState = HomeUiState(
                    knownHamsterLists = List(3) {
                        KnownHamsterList("List $it", "")
                    }
                ),
                onAction = {},
                onLoadHamsterList = {},
            )
        }
    }
}
