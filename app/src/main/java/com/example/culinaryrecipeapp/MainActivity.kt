package com.example.culinaryrecipeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culinaryrecipeapp.data.RecipeDatabase
import com.example.culinaryrecipeapp.ui.RecipeAdapter
import com.example.culinaryrecipeapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: RecipeDatabase
    private lateinit var adapter: RecipeAdapter

    companion object {
        const val ADD_RECIPE_REQUEST_CODE = 1
        const val VIEW_RECIPE_REQUEST_CODE = 2
        const val RANDOM_RECIPE_REQUEST_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = RecipeDatabase.getDatabase(this)

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RecipeAdapter(emptyList()) { recipe ->
            val intent = Intent(this, RecipeDetailsActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivityForResult(intent, VIEW_RECIPE_REQUEST_CODE)
        }
        binding.recipeRecyclerView.adapter = adapter

        loadRecipes()

        // Obsługa Floating Action Button do dodawania przepisów
        val addRecipeFab: FloatingActionButton = findViewById(R.id.addRecipeFab)
        addRecipeFab.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivityForResult(intent, ADD_RECIPE_REQUEST_CODE)
        }

        // Obsługa przycisku losowego przepisu
        binding.randomRecipeButton.setOnClickListener {
            val intent = Intent(this, RandomRecipeActivity::class.java)
            startActivityForResult(intent, RANDOM_RECIPE_REQUEST_CODE)
        }
    }

    private fun loadRecipes() {
        lifecycleScope.launch {
            val recipes = withContext(Dispatchers.IO) {
                database.recipeDao().getAllRecipes()
            }
            adapter.updateRecipes(recipes) // Odśwież adapter
            Log.d("MainActivity", "Załadowano ${recipes.size} przepisów.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ADD_RECIPE_REQUEST_CODE, VIEW_RECIPE_REQUEST_CODE, RANDOM_RECIPE_REQUEST_CODE -> {
                    loadRecipes() //Odświeżanie listy
                    Log.d("MainActivity", "Lista przepisów odświeżona.")
                }
            }
        } else {
            Log.d("MainActivity", "Brak wyniku lub wynik nie OK. requestCode: $requestCode, resultCode: $resultCode")
        }
    }
}
