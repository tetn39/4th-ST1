package com.classnumber_00_domaekazuki.api_test_02

data class PokemonData(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>
)

data class Sprites(
    val front_default: String
)

data class TypeSlot(
    val slot: Int,
    val type: Type
)

data class Type(
    val name: String,
    val url: String
)