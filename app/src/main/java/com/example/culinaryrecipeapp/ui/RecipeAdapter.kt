package com.example.culinaryrecipeapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryrecipeapp.data.Recipe
import com.example.culinaryrecipeapp.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        val diffCallback = RecipeDiffCallback(recipes, newRecipes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        recipes = newRecipes
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipeNameTextView.text = recipe.name
            binding.recipeNameTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            binding.recipeNameTextView.gravity = android.view.Gravity.CENTER
            binding.root.setOnClickListener { onRecipeClick(recipe) }
        }
    }

    class RecipeDiffCallback(
        private val oldList: List<Recipe>,
        private val newList: List<Recipe>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
