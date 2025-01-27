package com.example.culinaryrecipeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.culinaryrecipeapp.data.Recipe
import com.example.culinaryrecipeapp.data.RecipeDatabase
import com.example.culinaryrecipeapp.databinding.ActivityRandomRecipeBinding
import com.example.culinaryrecipeapp.network.ApiClient
import com.example.culinaryrecipeapp.network.FreeMealApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RandomRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomRecipeBinding
    private lateinit var database: RecipeDatabase
    private val api = ApiClient.retrofit.create(FreeMealApi::class.java)
    private var currentRecipe: Recipe? = null // Przechowanie obecnego przepisu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = RecipeDatabase.getDatabase(this)

        // Inicjalnie pobierz losowy przepis
        fetchAndDisplayRandomRecipe()

        // Obsługa przycisku „Losuj ponownie”
        binding.fetchRandomRecipeButton.setOnClickListener {
            fetchAndDisplayRandomRecipe()
        }

        // Obsługa przycisku „Zapisz przepis”
        binding.saveRecipeButton.setOnClickListener {
            saveCurrentRecipe()
        }
    }

    private fun fetchAndDisplayRandomRecipe() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = api.getRandomMeal()
                val meal = response.meals.first()
                val recipe = database.mapMealToRecipe(meal)
                currentRecipe = recipe

                withContext(Dispatchers.Main) {
                    binding.randomRecipeNameTextView.text = recipe.name
                    binding.randomRecipeIngredientsTextView.text = formatIngredients(recipe.ingredients)
                    binding.randomRecipeInstructionsTextView.text = recipe.instructions
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RandomRecipeActivity, "Błąd pobierania przepisu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun formatIngredients(ingredients: String): String {
        return ingredients
            .split(Regex("\\r?\\n|,|;")) // Obsługa separatorów
            .map { it.trim() } // Usuwanie zbędnych spacji
            .filter { it.isNotEmpty() } // Usuwanie pustych składników
            .joinToString("\n") { "• $it" } // Formatowanie jako kulki
    }

    private fun saveCurrentRecipe() {
        currentRecipe?.let { recipe ->
            lifecycleScope.launch(Dispatchers.IO) {
                database.recipeDao().insertRecipe(recipe)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RandomRecipeActivity, "Przepis zapisany!", Toast.LENGTH_SHORT).show()
                    Log.d("RandomRecipeActivity", "Przepis zapisany: ${recipe.name}")
                    setResult(RESULT_OK) // Ustaw wynik jako OK
                    finish() // Zamknij aktywność
                }
            }
        }
    }
}
