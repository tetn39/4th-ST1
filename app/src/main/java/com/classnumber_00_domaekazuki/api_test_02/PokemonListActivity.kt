package com.classnumber_00_domaekazuki.api_test_02

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokemonListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var api: PokeApiService
    private val pokemonList = mutableListOf<PokemonListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pokemon_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.pokemonRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3列のグリッド

        // RetrofitでAPI通信を準備
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // APIサービスの作成
        api = retrofit.create(PokeApiService::class.java)

        // ポケモンリストを取得
        loadPokemonList()
    }

    private fun loadPokemonList() {
        api.getPokemonList().enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(call: Call<PokemonListResponse>, response: Response<PokemonListResponse>) {
                val pokemonListResponse = response.body()
                if (pokemonListResponse != null) {
                    pokemonList.clear()
                    pokemonList.addAll(pokemonListResponse.results)
                    
                    // アダプターをセット
                    val adapter = PokemonListAdapter(pokemonList) { pokemon ->
                        // ポケモンがクリックされたときの処理
                        val intent = Intent(this@PokemonListActivity, MainActivity::class.java)
                        intent.putExtra("POKEMON_NAME", pokemon.name)
                        startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
} 