package com.classnumber_00_domaekazuki.api_test_02

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.URL
import kotlin.concurrent.thread

class PokemonListAdapter(
    private val pokemonList: List<PokemonListItem>,
    private val onItemClick: (PokemonListItem) -> Unit
) : RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemPokemonImage)
        val nameTextView: TextView = itemView.findViewById(R.id.itemPokemonName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun getItemCount(): Int = pokemonList.size

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.nameTextView.text = pokemon.name
        
        // URLからポケモンIDを抽出
        val pokemonId = extractPokemonId(pokemon.url)
        
        // ポケモンの画像URLを構築
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"
        
        // 画像を非同期で読み込む
        thread {
            try {
                val url = URL(imageUrl)
                val bitmap = BitmapFactory.decodeStream(url.openStream())
                holder.imageView.post {
                    holder.imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // アイテムクリック時の処理
        holder.itemView.setOnClickListener {
            onItemClick(pokemon)
        }
    }
    
    // ポケモンのURLからIDを抽出するヘルパーメソッド
    private fun extractPokemonId(url: String): String {
        // URLの形式は "https://pokeapi.co/api/v2/pokemon/{id}/" のような形
        val regex = ".*/pokemon/(\\d+)/.*".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1) ?: "1"
    }
} 