package com.pulseb.app

/**
 * In-memory list of texts used for search suggestions in the popup.
 * Add or replace with your own data source.
 */
object SearchData {
    val searchableTexts: List<String> = listOf(
        "Apple",
        "Banana",
        "Cherry",
        "Date",
        "Elderberry",
        "Fig",
        "Grape",
        "Honeydew",
        "Kiwi",
        "Lemon",
        "Mango",
        "Nectarine",
        "Orange",
        "Papaya",
        "Quince",
        "Raspberry",
        "Strawberry",
        "Tangerine",
        "Watermelon",
        "Blueberry",
        "Blackberry",
        "Apricot",
        "Avocado",
        "Coconut",
        "Dragon fruit",
        "Guava",
        "Lime",
        "Lychee",
        "Melon",
        "Peach",
        "Pear",
        "Pineapple",
        "Plum",
        "Pomegranate",
    )

    fun search(query: String, maxResults: Int = 4): List<String> {
        val normalized = query.trim().lowercase()
        if (normalized.isEmpty()) return searchableTexts.take(maxResults)
        return searchableTexts
            .filter { it.lowercase().contains(normalized) }
            .take(maxResults)
    }
}
