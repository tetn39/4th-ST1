package com.classnumber_00_domaekazuki.api_test_02

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var pokemonImageView: ImageView
    private lateinit var changeButton: Button
    private lateinit var backButton: Button
    private lateinit var listButton: Button
    
    private lateinit var api: PokeApiService
    private var currentPokemonId = 1
    private val pokemonList = mutableListOf<String>()
    private var currentPokemonIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameTextView = findViewById(R.id.nameTextView)
        heightTextView = findViewById(R.id.heightTextView)
        weightTextView = findViewById(R.id.weightTextView)
        pokemonImageView = findViewById(R.id.pokemonImageView)
        changeButton = findViewById(R.id.changeButton)
        backButton = findViewById(R.id.backButton)
        listButton = findViewById(R.id.listButton)

        // ボタンテキストをリソースから設定
        changeButton.text = getString(R.string.next)
        backButton.text = getString(R.string.back)
        listButton.text = getString(R.string.list)

        // RetrofitでAPI通信をする準備
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/") // URLを指定
            .addConverterFactory(GsonConverterFactory.create()) // GsonでJsonオブジェクトに変換する設定
            .build()

        // 作ったRetrofitから、PokeApiServiceインターフェースの実体を作る
        api = retrofit.create(PokeApiService::class.java)
        
        // インテントからポケモン名を取得
        val pokemonName = intent.getStringExtra("POKEMON_NAME")
        
        if (pokemonName != null) {
            // 一覧画面から特定のポケモンが選択された場合
            loadPokemonByName(pokemonName)
        } else {
            // 通常起動の場合は初期ポケモンを表示
            loadPokemon(currentPokemonId)
        }
        
        // ポケモンリストを取得
        loadPokemonList()
        
        // 次のポケモンを表示するボタン
        changeButton.setOnClickListener {
            currentPokemonIndex = (currentPokemonIndex + 1) % pokemonList.size
            loadPokemonByName(pokemonList[currentPokemonIndex])
        }
        
        // 前のポケモンを表示するボタン
        backButton.setOnClickListener {
            currentPokemonIndex = if (currentPokemonIndex > 0) currentPokemonIndex - 1 else pokemonList.size - 1
            loadPokemonByName(pokemonList[currentPokemonIndex])
        }
        
        // 一覧画面に移動するボタン
        listButton.setOnClickListener {
            val intent = Intent(this, PokemonListActivity::class.java)
            startActivity(intent)
        }
    }
    
    // ポケモンリストを取得
    private fun loadPokemonList() {
        api.getPokemonList().enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(call: Call<PokemonListResponse>, response: Response<PokemonListResponse>) {
                val pokemonListResponse = response.body()
                if (pokemonListResponse != null) {
                    pokemonList.clear()
                    pokemonListResponse.results.forEach { pokemon ->
                        pokemonList.add(pokemon.name)
                    }
                    
                    // 現在表示中のポケモンのインデックスを更新
                    val currentName = nameTextView.text.toString().replace("名前: ", "")
                    val index = pokemonList.indexOf(currentName)
                    if (index != -1) {
                        currentPokemonIndex = index
                    }
                }
            }
            
            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                nameTextView.text = getString(R.string.error)
            }
        })
    }
    
    // IDでポケモンを取得
    private fun loadPokemon(id: Int) {
        api.getPokemonById(id).enqueue(object : Callback<PokemonData> {
            override fun onResponse(call: Call<PokemonData>, response: Response<PokemonData>) {
                val pokemon = response.body()
                if (pokemon != null) {
                    displayPokemon(pokemon)
                }
            }
            
            override fun onFailure(call: Call<PokemonData>, t: Throwable) {
                nameTextView.text = getString(R.string.error)
            }
        })
    }
    
    // 名前でポケモンを取得
    private fun loadPokemonByName(name: String) {
        api.getPokemonByName(name).enqueue(object : Callback<PokemonData> {
            override fun onResponse(call: Call<PokemonData>, response: Response<PokemonData>) {
                val pokemon = response.body()
                if (pokemon != null) {
                    displayPokemon(pokemon)
                }
            }
            
            override fun onFailure(call: Call<PokemonData>, t: Throwable) {
                nameTextView.text = getString(R.string.error)
            }
        })
    }
    
    // ポケモン情報を表示
    private fun displayPokemon(pokemon: PokemonData) {
        nameTextView.text = getString(R.string.name, pokemon.name)
        heightTextView.text = getString(R.string.height, (pokemon.height / 10.0).toString())
        weightTextView.text = getString(R.string.weight, (pokemon.weight / 10.0).toString())
        
        thread {
            try {
                val url = URL(pokemon.sprites.front_default)
                val bitmap = BitmapFactory.decodeStream(url.openStream())
                runOnUiThread {
                    pokemonImageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    nameTextView.text = getString(R.string.error)
                }
            }
        }
    }
}