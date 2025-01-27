package com.example.culinaryrecipeapp.utils

import android.app.Application
import com.example.culinaryrecipeapp.data.RecipeDatabase

class AppModule : Application() {

    val database: RecipeDatabase by lazy {
        RecipeDatabase.getDatabase(this)
    }
}
