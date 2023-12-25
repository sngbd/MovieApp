package com.example.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.adapter.MovieItemAdapter
import com.example.movieapp.adapter.StarItemAdapter
import com.example.movieapp.api.ApiConfig
import com.example.movieapp.api.MovieDetailItem
import com.example.movieapp.api.MovieResponseItem
import com.example.movieapp.database.Movie
import com.example.movieapp.database.MovieDao
import com.example.movieapp.database.MovieRoomDatabase
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.databinding.ActivityStarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// ... (imports remain unchanged)

class StarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStarBinding
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.starRecyclerContainer.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.starRecyclerContainer.addItemDecoration(itemDecoration)

        movieDao = MovieRoomDatabase.getDatabase(application).movieDao()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    navigateToActivity(MainActivity::class.java)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.stars -> {
                    navigateToActivity(StarActivity::class.java)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val allMovies = getAllMoviesFromDao()

            val movieItemsDeferred = allMovies.map { movie ->
                CoroutineScope(Dispatchers.IO).async {
                    getMovieDetail(movie.movieId)
                }
            }

            val movieItems = movieItemsDeferred.awaitAll()

            withContext(Dispatchers.Main) {
                getStarItem(movieItems)
            }
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private suspend fun getAllMoviesFromDao(): List<Movie> {
        return movieDao.getAllMovies()
    }

    private suspend fun getMovieDetail(movieId: Long): MovieDetailItem {
        return ApiConfig.getApiService().getMovieDetail(movieId).execute().body()
            ?: throw Exception("Movie detail not found")
    }

    private fun getStarItem(movieItems: List<MovieDetailItem>) {
        val adapter = StarItemAdapter(movieItems)
        binding.starRecyclerContainer.adapter = adapter
    }
}
