package com.example.culinaryrecipeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.culinaryrecipeapp.data.Recipe
import com.example.culinaryrecipeapp.data.RecipeDatabase
import com.example.culinaryrecipeapp.databinding.ActivityRecipeDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var database: RecipeDatabase
    private var recipe: Recipe? = null

    companion object {
        private const val EDIT_RECIPE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = RecipeDatabase.getDatabase(this)

        recipe = intent.getParcelableExtra("recipe")
        recipe?.let {
            binding.recipeNameTextView.text = it.name

            // Wyświetlanie nagłówków i treści
            binding.recipeIngredientsHeader.text = "Składniki:"
            binding.recipeIngredientsTextView.text = formatIngredients(it.ingredients)

            binding.recipeInstructionsHeader.text = "Sposób przygotowania:"
            binding.recipeInstructionsTextView.text = it.instructions

            // Obsługa zdjęcia
            if (!it.imagePath.isNullOrEmpty()) {
                val file = File(it.imagePath)
                if (file.exists()) {
                    binding.recipeImageView.setImageURI(Uri.fromFile(file))
                } else {
                    binding.recipeImageView.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                binding.recipeImageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }

        binding.editRecipeButton.setOnClickListener {
            val intent = Intent(this, EditRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivityForResult(intent, EDIT_RECIPE_REQUEST_CODE)
        }

        binding.deleteRecipeButton.setOnClickListener {
            recipe?.let { recipeToDelete ->
                GlobalScope.launch(Dispatchers.IO) {
                    database.recipeDao().deleteRecipe(recipeToDelete)
                    runOnUiThread {
                        Toast.makeText(this@RecipeDetailsActivity, "Przepis usunięty", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }

        binding.generateShoppingListButton.setOnClickListener {
            val intent = Intent(this, ShoppingListActivity::class.java)
            intent.putExtra("ingredients", recipe?.ingredients)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_RECIPE_REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun formatIngredients(ingredients: String): String {
        return ingredients
            .split(Regex("\\r?\\n|,|;")) // Obsługa separatorów
            .map { it.trim() } // Usuwanie zbędnych spacji
            .filter { it.isNotEmpty() } // Usuwanie pustych składników
            .joinToString("\n") { "• $it" } // Formatowanie jako lista wypunktowana
    }
}
