package org.stratum0.hamsterlist.android.gui.listchooser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.models.HamsterList

@Composable
fun ListSharingSheet(
    knownHamsterLists: List<HamsterList>,
    onLoadHamsterList: (HamsterList) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Share items to list",
            style = MaterialTheme.typography.headlineSmall,
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
        Surface {
            ListSharingSheet(
                knownHamsterLists = List(size = 4) {
                    HamsterList("List$it", "")
                },
                onLoadHamsterList = {}
            )
        }
    }
}
