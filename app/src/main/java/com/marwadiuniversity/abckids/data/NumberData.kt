package com.marwadiuniversity.abckids.data

import com.marwadiuniversity.abckids.R

data class NumberItem(
    val number: String,
    val word: String,
    val fingerImageRes: Int, // Image resource for finger counting
    val value: Int,
    val colorStart: String,
    val colorEnd: String
)

object NumberData {
    val numberList = listOf(
        // 1 - Pink to Bright Pink
        NumberItem("1", "One", R.drawable.number_1, 1, "#FFB6C1", "#FF69B4"),

        // 2 - Sky Blue to Indigo
        NumberItem("2", "Two", R.drawable.number_2, 2, "#87CEFA", "#4169E1"),

        // 3 - Mint Green to Emerald
        NumberItem("3", "Three", R.drawable.number_3, 3, "#98FB98", "#228B22"),

        // 4 - Yellow to Orange
        NumberItem("4", "Four", R.drawable.number_4, 4, "#FFF176", "#FFA726"),

        // 5 - Lavender to Purple
        NumberItem("5", "Five", R.drawable.number_5, 5, "#E6E6FA", "#9370DB"),

        // 6 - Peach to Coral
        NumberItem("6", "Six", R.drawable.number_6, 6, "#FFDAB9", "#FF7F50"),

        // 7 - Aqua Blue to Teal
        NumberItem("7", "Seven", R.drawable.number_7, 7, "#40E0D0", "#008080"),

        // 8 - Light Green to Lime Green
        NumberItem("8", "Eight", R.drawable.number_8, 8, "#98FB98", "#32CD32"),

        // 9 - Gold to Deep Amber
        NumberItem("9", "Nine", R.drawable.number_9, 9, "#FFD700", "#FF8C00"),

        // 10 - Rose Pink to Magenta
        NumberItem("10", "Ten", R.drawable.number_10, 10, "#FF69B4", "#C71585")
    )
}
