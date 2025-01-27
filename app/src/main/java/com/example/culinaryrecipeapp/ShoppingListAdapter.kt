package com.example.culinaryrecipeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryrecipeapp.databinding.ItemShoppingListBinding

class ShoppingListAdapter(
    private val items: MutableList<String>
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    private val checkedItems = mutableSetOf<String>() // Przechowuje zaznaczone składniki

    // Getter dla zaznaczonych składników
    fun getCheckedItems(): List<String> {
        return checkedItems.toList() // Zwraca kopię zaznaczonych składników
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, checkedItems.contains(item))

        holder.binding.itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedItems.add(item)
            } else {
                checkedItems.remove(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, isChecked: Boolean) {
            binding.itemText.text = item
            binding.itemCheckbox.isChecked = isChecked
        }
    }
}

