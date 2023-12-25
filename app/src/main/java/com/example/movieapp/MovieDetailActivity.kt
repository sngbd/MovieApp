package com.example.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movieapp.adapter.GenreFinder
import com.example.movieapp.api.ApiConfig
import com.example.movieapp.api.MovieDetailItem
import com.example.movieapp.database.Movie
import com.example.movieapp.database.MovieDao
import com.example.movieapp.database.MovieRoomDatabase
import com.example.movieapp.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var movieDao: MovieDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieDao = MovieRoomDatabase.getDatabase(application).movieDao()

        val intent = intent
        val movieId = intent.getLongExtra("movie_id", 0)

        val checkBox = findViewById<CheckBox>(R.id.activity_detail_add_to_favourite)

        CoroutineScope(Dispatchers.IO).launch {
            val favoriteMovie = movieDao.getFavoriteMovieById(movieId)
            withContext(Dispatchers.Main) {
                checkBox.isChecked = (favoriteMovie != null)
            }
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveFavoriteMovieToDatabase(movieId)
            } else {
                deleteFavoriteMovieFromDatabase(movieId)
            }
        }

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

        getMovieDetail(movieId)
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun saveFavoriteMovieToDatabase(movieId: Long) {
        val favoriteMovie = Movie(movieId)
        CoroutineScope(Dispatchers.IO).launch {
            movieDao.insertFavoriteMovie(favoriteMovie)
        }
    }

    private fun deleteFavoriteMovieFromDatabase(movieId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            movieDao.deleteFavoriteMovie(movieId)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MovieDetailActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun getMovieDetail(movieId: Long) {
        val client = ApiConfig.getApiService().getMovieDetail(movieId)
        client.enqueue(object : Callback<MovieDetailItem> {
            override fun onResponse(call: Call<MovieDetailItem>, response: Response<MovieDetailItem>) {
                if (response.isSuccessful) {
                    val movieResponse: MovieDetailItem? = response.body()

                    val backdropImageUrl = "https://image.tmdb.org/t/p/w200" + movieResponse?.backdropPath ?: ""
                    val posterImageUrl = "https://image.tmdb.org/t/p/w500" + movieResponse?.posterPath ?: ""

                    Glide.with(this@MovieDetailActivity)
                        .load(backdropImageUrl)
                        .into(binding.activityDetailBackdropImage)

                    Glide.with(this@MovieDetailActivity)
                        .load(posterImageUrl)
                        .into(binding.activityDetailPosterImage)

                    binding.activityDetailMovieTitle.text = movieResponse?.title ?: ""
                    binding.activityDetailMovieDate.text = movieResponse?.releaseDate ?: ""
                    var adult = "adult: false"
                    if (movieResponse!!.adult) {
                        adult = "adult: true"
                    }
                    binding.activityDetailAdult.text = adult
                    binding.activityDetailVoteAverage.text = "rating: " + String.format("%.1f", movieResponse.voteAverage).toDouble().toString() + "/10"
                    binding.activityDetailVoteCount.text = "votes: " + movieResponse.voteCount.toString()
                    binding.activityDetailRuntime.text = "runtime: " + movieResponse.runtime.toString() + " minutes"
                    binding.activityDetailBudget.text = "budget: $" + movieResponse.budget.toString()
                    binding.activityDetailOverview.text = movieResponse.overview ?: ""

                    var genreName = ""
                    for (genre in movieResponse.genres) {
                        genreName += genre.name
                        if (genre != movieResponse.genres.last()) {
                            genreName += ", "
                        }
                    }

                    binding.activityDetailGenre.text = genreName
                } else {
                    Log.e("MovieDetailActivity", "Network call failure on response: ${response.message()}")
                    showToast("Network call failure on response: ${response.message()}")
                }
            }

            private fun showToast(message: String) {
                Toast.makeText(this@MovieDetailActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<MovieDetailItem>, t: Throwable) {
                Log.e("MovieDetailActivity", "Network call failure: ${t.message.toString()}")
                showToast("Network call failure: ${t.message.toString()}")
            }
        })
    }
}
