package com.example.culinaryrecipeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culinaryrecipeapp.databinding.ActivityShoppingListBinding


class ShoppingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingListBinding
    private val shoppingList = mutableListOf<String>()
    private lateinit var adapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ingredients = intent.getStringExtra("ingredients") ?: ""
        shoppingList.addAll(ingredients.split("\n").map { it.trim() }.filter { it.isNotEmpty() })

        adapter = ShoppingListAdapter(shoppingList)
        binding.shoppingListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.shoppingListRecyclerView.adapter = adapter
        binding.shoppingListRecyclerView.itemAnimator = DefaultItemAnimator()

        binding.confirmButton.setOnClickListener {
            val selectedItems = adapter.getCheckedItems() // Pobiera zaznaczone składniki
            Toast.makeText(
                this,
                "Zatwierdzono ${selectedItems.size} składników: ${selectedItems.joinToString(", ")}",
                Toast.LENGTH_SHORT
            ).show()
            finish() // Zamknij widok
        }

    }
}