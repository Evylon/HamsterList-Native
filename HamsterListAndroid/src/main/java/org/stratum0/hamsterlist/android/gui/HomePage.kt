package org.stratum0.hamsterlist.android.gui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun HomePage(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    uiState.dialogState?.let { dialogState ->
        HamsterListDialog(
            dialogState = dialogState,
            onDismiss = { onAction(HomeAction.DismissDialog) }
        )
    }
        if (uiState.sheetState != null && !sheetState.isVisible) {
            LaunchedEffect(uiState.sheetState) {
                sheetState.show()
            }
        } else if (uiState.sheetState == null && sheetState.isVisible) {
            LaunchedEffect(null) {
                sheetState.hide()
            }
        }
    ModalBottomSheetLayout(
        sheetContent = {
            BackHandler { onAction(HomeAction.DismissSheet) }
            HomePageSheetContent(
                sheetState = uiState.sheetState,
                knownHamsterLists = uiState.knownHamsterLists,
                lastLoadedServer = uiState.lastLoadedServer,
                onLoadHamsterList = onLoadHamsterList
            )
        },
        modifier = modifier,
        sheetState = sheetState,
    ) {
        HomePageContent(
            uiState = uiState,
            knownHamsterLists = uiState.knownHamsterLists,
            onAction = onAction,
            onLoadHamsterList = onLoadHamsterList,
        )
    }
}

@Composable
private fun HomePageSheetContent(
    sheetState: HomeSheetState?,
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

        null -> {}
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
