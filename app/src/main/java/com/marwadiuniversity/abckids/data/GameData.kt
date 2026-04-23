package com.marwadiuniversity.abckids.data

data class QuizQuestion(
    val emoji: String,
    val correctAnswer: String,
    val options: List<String>
)

object GameData {
    val animalQuestions = listOf(
        QuizQuestion("\uD83D\uDC36", "Dog", listOf("Cat", "Dog", "Cow", "Lion")),
        QuizQuestion("\uD83D\uDC31", "Cat", listOf("Dog", "Cat", "Bear", "Frog")),
        QuizQuestion("\uD83D\uDC2E", "Cow", listOf("Cow", "Horse", "Sheep", "Pig")),
        QuizQuestion("\uD83D\uDC37", "Pig", listOf("Pig", "Dog", "Cat", "Cow")),
        QuizQuestion("\uD83D\uDC34", "Horse", listOf("Horse", "Sheep", "Cow", "Lion")),
        QuizQuestion("\uD83D\uDC11", "Sheep", listOf("Sheep", "Goat", "Cow", "Pig")),
        QuizQuestion("\uD83D\uDC38", "Frog", listOf("Frog", "Duck", "Penguin", "Rabbit")),
        QuizQuestion("\uD83D\uDC18", "Elephant", listOf("Elephant", "Lion", "Bear", "Tiger")),
        QuizQuestion("\uD83E\uDD81", "Lion", listOf("Lion", "Tiger", "Dog", "Wolf")),
        QuizQuestion("\uD83D\uDC2F", "Tiger", listOf("Tiger", "Lion", "Bear", "Cat")),
        QuizQuestion("\uD83D\uDC3B", "Bear", listOf("Bear", "Dog", "Cat", "Monkey")),
        QuizQuestion("\uD83D\uDC35", "Monkey", listOf("Monkey", "Cat", "Dog", "Lion")),
        QuizQuestion("\uD83D\uDC14", "Chicken", listOf("Chicken", "Duck", "Bird", "Penguin")),
        QuizQuestion("\uD83D\uDC26", "Bird", listOf("Bird", "Eagle", "Owl", "Duck")),
        QuizQuestion("\uD83E\uDD86", "Duck", listOf("Duck", "Penguin", "Chicken", "Goose")),
        QuizQuestion("\uD83D\uDC3A", "Wolf", listOf("Wolf", "Dog", "Tiger", "Lion")),
        QuizQuestion("\uD83E\uDD85", "Eagle", listOf("Eagle", "Owl", "Bird", "Crow")),
        QuizQuestion("\uD83D\uDC19", "Octopus", listOf("Octopus", "Fish", "Crab", "Whale")),
        QuizQuestion("\uD83D\uDC27", "Penguin", listOf("Penguin", "Duck", "Swan", "Goose")),
        QuizQuestion("\uD83E\uDD89", "Owl", listOf("Owl", "Eagle", "Crow", "Parrot"))
    )
}

