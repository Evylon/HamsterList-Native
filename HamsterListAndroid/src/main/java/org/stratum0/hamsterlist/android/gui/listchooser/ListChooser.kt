package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.models.KnownHamsterList

@Composable
fun ListChooser(
    uiState: ListChooserState,
    onLoadList: (KnownHamsterList) -> Unit,
    onDeleteList: (KnownHamsterList) -> Unit,
    openListCreationSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Your lists",
                style = MaterialTheme.typography.h5
            )
            AnimatedVisibility(uiState.hamsterLists.isNotEmpty()) {
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Hamsterlists",
                        modifier = Modifier.size(30.dp),
                        tint = if (isEditing) {
                            MaterialTheme.colors.error
                        } else {
                            MaterialTheme.colors.primary
                        }
                    )
                }
            }
        }
        Crossfade(uiState.hamsterLists.isEmpty()) { isEmpty ->
            if (isEmpty) {
                EmptyListsState(modifier = modifier)
            } else {
                LazyColumn(
                    modifier
                        .background(
                            color = MaterialTheme.colors.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .animateContentSize()
                ) {
                    items(uiState.hamsterLists) { hamsterList ->
                        HamsterListItem(
                            hamsterList = hamsterList,
                            isEditing = isEditing,
                            onLoadList = onLoadList,
                            onDeleteList = onDeleteList
                        )
                        if (uiState.hamsterLists.last() != hamsterList) {
                            Divider()
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                isEditing = false
                openListCreationSheet()
            },
            modifier = modifier
                .padding(vertical = 4.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Add new list")
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
            "No Lists added yet.",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HamsterListItem(
    hamsterList: KnownHamsterList,
    isEditing: Boolean,
    onLoadList: (KnownHamsterList) -> Unit,
    onDeleteList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isEditing) {
                    onDeleteList(hamsterList)
                } else {
                    onLoadList(hamsterList)
                }
            }
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
                contentDescription = "Delete",
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colors.error
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ListChooserPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListChooser(
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
        Surface(color = MaterialTheme.colors.background) {
            ListChooser(
                uiState = ListChooserState(listOf()),
                onLoadList = {},
                onDeleteList = {},
                openListCreationSheet = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
