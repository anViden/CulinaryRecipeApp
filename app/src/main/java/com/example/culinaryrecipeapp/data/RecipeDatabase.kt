package com.example.culinaryrecipeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.culinaryrecipeapp.network.models.Meal

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    //Konwertuje obiekt Meal z API na lokalny obiekt Recipe

    fun mapMealToRecipe(meal: Meal): Recipe {
        val ingredientsList = (1..20).mapNotNull { index ->
            val ingredientField = meal.javaClass.getDeclaredField("strIngredient$index")
            ingredientField.isAccessible = true
            val ingredient = ingredientField.get(meal) as? String
            if (!ingredient.isNullOrBlank()) ingredient.trim() else null
        }

        return Recipe(
            name = meal.strMeal,
            ingredients = ingredientsList.joinToString(separator = "\n"),
            instructions = meal.strInstructions ?: "No instructions available"
        )
    }
}
