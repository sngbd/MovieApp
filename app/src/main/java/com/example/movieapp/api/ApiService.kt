package com.example.movieapp.api
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("3/movie/now_playing?language=en-US&page=1")
    fun getNowShowing(): Call<MovieResponseItem>

    @GET("3/movie/{movie_id}")
    fun getMovieDetail(@Path("movie_id") movieId: Long): Call<MovieDetailItem>
}
