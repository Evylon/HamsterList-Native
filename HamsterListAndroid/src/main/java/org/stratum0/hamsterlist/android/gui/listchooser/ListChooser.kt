package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme

data class ListChooserState(
    val hamsterLists: List<String>
)

@Composable
fun ListChooser(
    uiState: ListChooserState,
    onLoadList: (String) -> Unit,
    openListCreationSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Your lists",
                style = MaterialTheme.typography.h5
            )
            IconButton(onClick = openListCreationSheet) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Load or create new list",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
        Crossfade(uiState.hamsterLists.isEmpty()) { isEmpty ->
            if (isEmpty) {
                EmptyListsState(modifier = modifier)
            } else {
                Card {
                    LazyColumn {
                        items(uiState.hamsterLists) { hamsterList ->
                            HamsterListItem(
                                hamsterList = hamsterList,
                                onLoadList = onLoadList
                            )
                            if (uiState.hamsterLists.last() != hamsterList) {
                                Divider()
                            }
                        }
                    }
                }
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
            "No Lists added yet.",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HamsterListItem(
    hamsterList: String,
    onLoadList: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLoadList(hamsterList) }
            .padding(4.dp)
    ) {
        Text(
            hamsterList,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@PreviewLightDark
@Composable
fun ListChooserPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListChooser(
                uiState = ListChooserState(
                    listOf("LocalList", "RemoteList")
                ),
                onLoadList = {},
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
                openListCreationSheet = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
