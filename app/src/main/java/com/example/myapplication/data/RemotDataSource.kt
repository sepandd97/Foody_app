package com.example.myapplication.data

import android.content.ContentValues
import android.util.Log
import com.example.myapplication.models.FoodRecipe
import com.example.myapplication.data.network.FoodRecipesApi
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {
    suspend fun getRecipes(queries: Map<String,String> ):Response<FoodRecipe>{

    return  foodRecipesApi.getRecipes(queries)
    }
}