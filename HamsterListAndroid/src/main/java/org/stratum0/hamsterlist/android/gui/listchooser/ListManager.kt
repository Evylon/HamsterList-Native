package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.models.KnownHamsterList

@Composable
fun ListManager(
    uiState: ListChooserState,
    onLoadList: (KnownHamsterList) -> Unit,
    onDeleteList: (KnownHamsterList) -> Unit,
    openListCreationSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxHeight()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.listManager_headline),
                style = MaterialTheme.typography.headlineSmall
            )
            AnimatedVisibility(uiState.hamsterLists.isNotEmpty()) {
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.listManager_edit_description),
                        modifier = Modifier.size(30.dp),
                        tint = if (isEditing) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
        Crossfade(
            uiState.hamsterLists.isEmpty(),
            modifier = Modifier
        ) { isEmpty ->
            if (isEmpty) {
                EmptyListsState(modifier = Modifier)
            } else {
                ListChooser(
                    hamsterLists = uiState.hamsterLists,
                    isEditing = isEditing,
                    onClick = if (isEditing) {
                        onDeleteList
                    } else {
                        onLoadList
                    },
                )
            }
        }
        Button(
            onClick = {
                isEditing = false
                openListCreationSheet()
            },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.listManager_addNew_button))
        }
    }
}

@Composable
fun ListChooser(
    hamsterLists: List<KnownHamsterList>,
    isEditing: Boolean,
    onClick: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier
            .background(
                color = HamsterListTheme.colors.shapeBackgroundColor,
                shape = MaterialTheme.shapes.medium
            )
            .animateContentSize()
    ) {
        items(hamsterLists) { hamsterList ->
            HamsterListItem(
                hamsterList = hamsterList,
                isEditing = isEditing,
                onClick = onClick
            )
            if (hamsterLists.last() != hamsterList) {
                Divider()
            }
        }
    }
}

@Composable
private fun EmptyListsState(modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.listManager_empty_label),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HamsterListItem(
    hamsterList: KnownHamsterList,
    isEditing: Boolean,
    onClick: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(hamsterList) }
            .padding(4.dp)
    ) {
        Text(
            text = hamsterList.listId,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        AnimatedVisibility(isEditing) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.listManager_delete_icon),
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ListChooserPreview() {
    HamsterListTheme {
        Surface {
            ListManager(
                uiState = ListChooserState(
                    listOf(
                        KnownHamsterList(
                            "LocalList",
                            ""
                        ), KnownHamsterList(
                            "RemoteList",
                            ""
                        )
                    )
                ),
                onLoadList = {},
                onDeleteList = {},
                openListCreationSheet = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun EmptyListPreview() {
    HamsterListTheme {
        Surface {
            ListManager(
                uiState = ListChooserState(listOf()),
                onLoadList = {},
                onDeleteList = {},
                openListCreationSheet = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
