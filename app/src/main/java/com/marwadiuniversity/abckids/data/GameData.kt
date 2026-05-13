package com.marwadiuniversity.abckids.data

import com.marwadiuniversity.abckids.R

data class QuizQuestion(
    val imageResId: Int,
    val correctAnswer: String,
    val options: List<String>
)

object GameData {
    val itemsByLetter = mapOf(
        'A' to listOf("Apple", "Ant", "Airplane"),
        'B' to listOf("Ball", "Bear", "Butterfly"),
        'C' to listOf("Cat", "Car", "Cake"),
        'D' to listOf("Dog", "Duck", "Doll"),
        'E' to listOf("Eagle", "Egg", "Ear"),
        'F' to listOf("Fish", "Frog", "Flower"),
        'G' to listOf("Giraffe", "Grapes", "Guitar"),
        'H' to listOf("Horse", "Hat", "House"),
        'I' to listOf("Ice cream", "Iguana", "Insect"),
        'J' to listOf("Jet", "Juice", "Jelly"),
        'K' to listOf("Kite", "Kangaroo", "Key"),
        'L' to listOf("Lion", "Lemon", "Leaf"),
        'M' to listOf("Monkey", "Moon", "Mouse"),
        'N' to listOf("Nest", "Nightingale", "Nose"),
        'O' to listOf("Orange", "Octopus", "Owl"),
        'P' to listOf("Parrot", "Pizza", "Penguin"),
        'Q' to listOf("Queen", "Quail", "Quilt"),
        'R' to listOf("Rabbit", "Rose", "Rocket"),
        'S' to listOf("Sunflower", "Sheep", "Star"),
        'T' to listOf("Tiger", "Tree", "Truck"),
        'U' to listOf("Umbrella", "Unicorn", "Up"),
        'V' to listOf("Vegetable", "Vulture", "Van"),
        'W' to listOf("Wolf", "Whale", "Window"),
        'X' to listOf("Xylophone", "X-ray"),
        'Y' to listOf("Yacht", "Yo-yo", "Yellow"),
        'Z' to listOf("Zebra", "Zoo", "Zero")
    )

    private val allItems = itemsByLetter.values.flatten()

    private val letterImages = mapOf(
        'A' to R.drawable.a,
        'B' to R.drawable.b,
        'C' to R.drawable.c,
        'D' to R.drawable.letter_d,
        'E' to R.drawable.letter_e,
        'F' to R.drawable.letter_f,
        'G' to R.drawable.letter_g,
        'H' to R.drawable.letter_h,
        'I' to R.drawable.letter_i,
        'J' to R.drawable.letter_j,
        'K' to R.drawable.letter_k,
        'L' to R.drawable.letter_l,
        'M' to R.drawable.letter_m,
        'N' to R.drawable.letter_n,
        'O' to R.drawable.letter_o,
        'P' to R.drawable.letter_p,
        'Q' to R.drawable.letter_q,
        'R' to R.drawable.letter_r,
        'S' to R.drawable.letter_s,
        'T' to R.drawable.letter_t,
        'U' to R.drawable.letter_u,
        'V' to R.drawable.letter_v,
        'W' to R.drawable.letter_w,
        'X' to R.drawable.letter_x,
        'Y' to R.drawable.letter_y,
        'Z' to R.drawable.letter_z
    )

    val alphabetQuestions = ('A'..'Z').mapNotNull { letter ->
        val items = itemsByLetter[letter]
        if (items.isNullOrEmpty()) null
        else {
            // The first item in each list is treated as the correct answer.
            val correctAnswer = items.first()
            val wrongAnswers = (allItems - items).shuffled().take(3)
            val options = (listOf(correctAnswer) + wrongAnswers).shuffled()
            val imageResId = letterImages[letter] ?: R.drawable.letter_d
            QuizQuestion(imageResId, correctAnswer, options)
        }
    }
}


