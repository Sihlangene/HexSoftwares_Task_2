package com.example.cryptotracker

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CryptoAdapter(
    private val context: Context,
    private var cryptoList: List<CryptoCurrency>,
    private var favorites: Set<String>,
    private val onFavoriteClick: (String) -> Unit
) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    // Method to update the data and notify the adapter
    fun updateData(newCryptoList: List<CryptoCurrency>, newFavorites: Set<String>) {
        this.cryptoList = newCryptoList
        this.favorites = newFavorites
        notifyDataSetChanged()
    }

    inner class CryptoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.crypto_name)
        val priceText: TextView = view.findViewById(R.id.crypto_price)
        val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val crypto = cryptoList[position]

        // Set cryptocurrency name and price
        holder.nameText.text = crypto.name
        holder.priceText.text = "$${crypto.current_price}"

        // Highlight price changes
        val previousPrice = holder.priceText.tag as? Double ?: crypto.current_price
        when {
            crypto.current_price > previousPrice -> {
                holder.priceText.setTextColor(Color.GREEN) // Price increased
            }
            crypto.current_price < previousPrice -> {
                holder.priceText.setTextColor(Color.RED) // Price decreased
            }
            else -> {
                holder.priceText.setTextColor(Color.BLACK) // No change
            }
        }
        // Save the current price as tag for comparison
        holder.priceText.tag = crypto.current_price

        // Update favorite button state (filled or border)
        holder.favoriteButton.setImageResource(
            if (favorites.contains(crypto.id)) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        // Log current state for debugging
        Log.d("CryptoAdapter", "Binding item: ${crypto.name}, Favorite: ${favorites.contains(crypto.id)}")

        // Set click listener for the favorite button
        holder.favoriteButton.setOnClickListener {
            Log.d("CryptoAdapter", "Favorite button clicked for: ${crypto.id}")
            onFavoriteClick(crypto.id) // Notify the parent (e.g., MainActivity or ViewModel) of the click
        }
    }

    override fun getItemCount() = cryptoList.size
}



