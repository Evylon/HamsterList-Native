package org.stratum0.hamsterlist.network

import kotlinx.serialization.Serializable
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.randomUUID

@Serializable
data class LocalHamsterList(
    val hamsterList: HamsterList,
    val syncResponse: SyncResponse
) {
    companion object {
        val defaultCategories: List<CategoryDefinition> = listOf(
            CategoryDefinition(
                id = randomUUID(),
                name = "Obst und Gemüse",
                shortName = "OG",
                color = "#8BC34A",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Eier",
                shortName = "E",
                color = "#8D6E63",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Gewürze",
                shortName = "GW",
                color = "#D7CCC8",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Müsli und Brotaufstrich",
                shortName = "MB",
                color = "#DCEDC8",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Kaffee und Tee",
                shortName = "KT",
                color = "#4E342E",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Reis und Hülsenfrüchte",
                shortName = "R",
                color = "#FFFFFF",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Backzutaten",
                shortName = "BZ",
                color = "#C6FF00",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Nudeln",
                shortName = "N",
                color = "#FFECB3",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Öl, Essig, Soßen",
                shortName = "Ö",
                color = "#FBC02D",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Milchprodukte und Kühlung",
                shortName = "M",
                color = "#FFEB3B",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Tiefkühlprodukte",
                shortName = "TK",
                color = "#81D4FA",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Fleisch",
                shortName = "F",
                color = "#D32F2F",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Brot und Backwaren",
                shortName = "B",
                color = "#795548",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Konserven",
                shortName = "K",
                color = "#757575",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Feinkost",
                shortName = "FK",
                color = "#673AB7",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Süßes und Snacks",
                shortName = "S",
                color = "#FF6F00",
                lightText = false
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Haushaltswaren und Kosmetik",
                shortName = "H",
                color = "#C2185B",
                lightText = true
            ),
            CategoryDefinition(
                id = randomUUID(),
                name = "Getränke",
                shortName = "G",
                color = "#1976D2",
                lightText = true
            )
        )
    }
}
