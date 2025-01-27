package com.example.culinaryrecipeapp.data

import androidx.room.*

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe): Long // Zwraca ID dodanego przepisu

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): List<Recipe> // Pobiera listę przepisów

    @Query("UPDATE recipes SET imagePath = :imagePath WHERE id = :recipeId")
    fun updateRecipeImage(recipeId: Int, imagePath: String)

    @Update
    fun updateRecipe(recipe: Recipe): Int // Zwraca liczbę zmodyfikowanych rekordów

    @Delete
    fun deleteRecipe(recipe: Recipe): Int // Zwraca liczbę usuniętych rekordów
}
