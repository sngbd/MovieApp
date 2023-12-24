package com.example.movieapp.api
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("3/movie/now_playing?language=en-US&page=1")
    fun getNowShowing(): Call<MovieResponseItem>
}
