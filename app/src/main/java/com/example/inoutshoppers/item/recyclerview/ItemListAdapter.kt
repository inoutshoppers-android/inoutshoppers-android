package com.example.inoutshoppers.item.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inoutshoppers.databinding.ItemListLayoutBinding

class ItemListAdapter(private val items: List<String>) : RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListLayoutBinding.inflate(layoutInflater, parent, false)
        return ItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemListViewHolder(private val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemName: String) {
            binding.itemName.text = itemName
        }
    }
}