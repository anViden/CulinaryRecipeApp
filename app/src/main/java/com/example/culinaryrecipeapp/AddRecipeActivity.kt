package com.example.culinaryrecipeapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.culinaryrecipeapp.data.Recipe
import com.example.culinaryrecipeapp.data.RecipeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var database: RecipeDatabase
    private lateinit var recipeImageView: ImageView
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_SELECT_PHOTO = 2
        const val REQUEST_CAMERA_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        database = RecipeDatabase.getDatabase(this)

        val nameEditText: EditText = findViewById(R.id.recipeNameEditText)
        val ingredientsEditText: EditText = findViewById(R.id.recipeIngredientsEditText)
        val instructionsEditText: EditText = findViewById(R.id.recipeInstructionsEditText)
        val saveButton: Button = findViewById(R.id.saveRecipeButton)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        recipeImageView = findViewById(R.id.recipeImageView)

        takePhotoButton.setOnClickListener { takePhoto() }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val ingredients = ingredientsEditText.text.toString().trim()
            val instructions = instructionsEditText.text.toString().trim()

            if (name.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(this, "Wszystkie pola są wymagane!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipe = Recipe(
                name = name,
                ingredients = ingredients,
                instructions = instructions,
                imagePath = currentPhotoPath
            )

            lifecycleScope.launch(Dispatchers.IO) {
                database.recipeDao().insertRecipe(recipe)
                runOnUiThread {
                    setResult(RESULT_OK)
                    finish()
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
                    recipeImageView.setImageURI(photoUri)
                }
                REQUEST_SELECT_PHOTO -> {
                    data?.data?.let { uri ->
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        recipeImageView.setImageURI(uri)
                        currentPhotoPath = uri.toString()
                    }
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
