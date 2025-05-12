package com.classnumber_00_domaekazuki.api_test_02

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// API通信のルールを定義するインターフェース
// 「getPokemon() っていう関数を持ってるよ！」
//「これを呼んだら PokemonData がもらえるよ！」 という約束だけ書いてる
//中身の通信の処理は、Retrofitが自動で作ってくる
interface PokeApiService {
    @GET("pokemon/{id}")
    fun getPokemonById(@Path("id") id: Int): Call<PokemonData>
    
    @GET("pokemon/{name}")
    fun getPokemonByName(@Path("name") name: String): Call<PokemonData>
    
    @GET("pokemon")
    fun getPokemonList(): Call<PokemonListResponse>
}

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)