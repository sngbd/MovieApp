package com.example.movieapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.adapter.MovieItemAdapter
import com.example.movieapp.api.ApiConfig
import com.example.movieapp.api.MovieResponseItem
import com.example.movieapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerContainer.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.recyclerContainer.addItemDecoration(itemDecoration)

        getMovieItem()
    }

    private fun getMovieItem() {
        val client = ApiConfig.getApiService().getNowShowing()
        client.enqueue(object : Callback<MovieResponseItem> {
            override fun onResponse(call: Call<MovieResponseItem>, response: Response<MovieResponseItem>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    val movies = movieResponse?.results ?: emptyList()
                    val adapter = MovieItemAdapter(movies)
                    binding.recyclerContainer.adapter = adapter
                } else {
                    Log.e("MainActivity", "Network call failure on response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieResponseItem>, t: Throwable) {
                Log.e("MainActivity", "network call failure: ${t.message.toString()}")
            }
        })
    }
}
