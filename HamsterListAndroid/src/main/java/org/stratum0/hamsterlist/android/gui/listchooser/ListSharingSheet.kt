package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.models.KnownHamsterList

@Composable
fun ListSharingSheet(
    knownHamsterLists: List<KnownHamsterList>,
    onLoadHamsterList: (KnownHamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Share items to list",
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        ListChooser(
            hamsterLists = knownHamsterLists,
            isEditing = false,
            onClick = onLoadHamsterList
        )
    }
}

@PreviewLightDark
@Composable
private fun ListSharingSheetPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ListSharingSheet(
                knownHamsterLists = List(size = 4) {
                    KnownHamsterList("List$it", "")
                },
                onLoadHamsterList = {}
            )
        }
    }
}
