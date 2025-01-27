package com.example.culinaryrecipeapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.culinaryrecipeapp.data.Recipe
import com.example.culinaryrecipeapp.data.RecipeDatabase
import com.example.culinaryrecipeapp.databinding.ActivityEditRecipeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditRecipeBinding
    private lateinit var database: RecipeDatabase
    private var recipe: Recipe? = null
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null


    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = RecipeDatabase.getDatabase(this)

        recipe = intent.getParcelableExtra("recipe")
        recipe?.let {
            binding.recipeNameEditText.setText(it.name)
            binding.recipeIngredientsEditText.setText(it.ingredients)
            binding.recipeInstructionsEditText.setText(it.instructions)

            if (!it.imagePath.isNullOrEmpty()) {
                val file = File(it.imagePath)
                if (file.exists()) {
                    binding.editRecipeImageView.setImageURI(Uri.fromFile(file))
                }
            }
        }
        binding.editTakePhotoButton.setOnClickListener { takePhoto() }

        binding.saveRecipeButton.setOnClickListener {
            saveEditedRecipe()
            val updatedRecipe = recipe?.copy(
                name = binding.recipeNameEditText.text.toString(),
                ingredients = binding.recipeIngredientsEditText.text.toString(),
                instructions = binding.recipeInstructionsEditText.text.toString()
            )

            updatedRecipe?.let { recipe ->
                lifecycleScope.launch(Dispatchers.IO) {
                    database.recipeDao().updateRecipe(recipe)
                    runOnUiThread {
                        Toast.makeText(this@EditRecipeActivity, "Przepis zaktualizowany!", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK) // Informujemy MainActivity o konieczności odświeżenia
                        finish()
                    }
                }
            }
        }
    }

    private fun takePhoto() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return
        }

        val photoFile = File(externalCacheDir, "${System.currentTimeMillis()}.jpg")
        currentPhotoPath = photoFile.absolutePath
        photoUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    binding.editRecipeImageView.setImageURI(photoUri)
                    recipe?.imagePath = currentPhotoPath // Zapisujemy ścieżkę w obiekcie przepisu
                }
            }
        }
    }

    private fun saveEditedRecipe() {
        val updatedRecipe = recipe?.copy(
            name = binding.recipeNameEditText.text.toString(),
            ingredients = binding.recipeIngredientsEditText.text.toString(),
            instructions = binding.recipeInstructionsEditText.text.toString(),
            imagePath = recipe?.imagePath // Zamiast `currentPhotoPath` używamy `recipe?.imagePath`, które zostało zaktualizowane
        )

        updatedRecipe?.let { recipe ->
            lifecycleScope.launch(Dispatchers.IO) {
                database.recipeDao().updateRecipe(recipe)
                runOnUiThread {
                    Toast.makeText(this@EditRecipeActivity, "Przepis zaktualizowany!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            Toast.makeText(this, "Aby zrobić zdjęcie, musisz udzielić dostępu do aparatu", Toast.LENGTH_LONG).show()
        }
    }

}
