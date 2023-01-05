package com.example.myapplication.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Repository
import com.example.myapplication.models.FoodRecipe
import com.example.myapplication.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application):
    AndroidViewModel(application)  {

        var recipesResponse : MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

        fun getRecipes(queries:Map<String,String>) {
            viewModelScope.launch {
                getRecipesSafeCall(queries)


            }
        }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()

            if (hasInternetConnection()){
           try {
               val response = repository.remote.getRecipes(queries)

               recipesResponse.value = handelFoodRecipesResponse(response)

           }catch (e:Exception){Log.d(ContentValues.TAG,e.toString() )
               recipesResponse.value =   NetworkResult.Error("Recipe not found")
           }
        } else{
       recipesResponse.value = NetworkResult.Error("No Internet Connection")


            }
    }

    private fun handelFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when{
            response.message().toString().contains("timeout") ->{
                return NetworkResult.Error("internet Timeout")
            }
            response.code() == 402 -> return NetworkResult.Error("Api key limited.")
            response.body()?.results.isNullOrEmpty() -> return NetworkResult.Error("Recipe Not found")
            response.isSuccessful ->  {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)

            }
            else -> return NetworkResult.Error(response.message())
        }

    }

    private fun hasInternetConnection():Boolean{
            val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
             val activeNetwork = connectivityManager.activeNetwork ?:return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        }


}