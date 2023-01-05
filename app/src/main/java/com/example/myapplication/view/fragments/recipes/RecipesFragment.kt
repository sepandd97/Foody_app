package com.example.myapplication.view.fragments.recipes

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.adapters.RecipesAdapter
import com.example.myapplication.databinding.FragmentRecipesBinding
import com.example.myapplication.utils.Constants.Companion.API_KEY
import com.example.myapplication.utils.NetworkResult
import com.example.myapplication.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private var binding: FragmentRecipesBinding? = null
    private val mAdapter by  lazy { RecipesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)


        setupRecyclerView()
        requestApiData()


        val view = binding!!.root

        return view
    }
    private  fun  requestApiData(){
            mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                    Log.d(TAG, response.data.toString())  }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(requireContext(),response.messages.toString(),Toast.LENGTH_LONG).show()

                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()

                }
                else -> {showShimmerEffect()}
            }

        }
    }

private fun setupRecyclerView(){
    binding!!.apply {
        recyclerview.adapter = mAdapter
        recyclerview.layoutManager = LinearLayoutManager(context)
        showShimmerEffect()
    }


}
private fun showShimmerEffect(){
    binding!!.recyclerview.showShimmer()
}
    private fun hideShimmerEffect(){
        binding!!.recyclerview.hideShimmer()
    }
}