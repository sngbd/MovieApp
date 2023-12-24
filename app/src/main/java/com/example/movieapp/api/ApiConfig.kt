package com.example.movieapp.api
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzNTU2ZTAwY2EwYzQ5YmQ1NzBjYTg1MDU3ODVlYzhhNSIsInN1YiI6IjY1ODdkYmI4YjBjZDIwNTM0YTU3OTk5ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.gh6UKD7PggAugPsosCMuZM8CPPWvaQ4-JZTqyyIdfzI"

        fun getApiService(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AccessTokenInterceptor(ACCESS_TOKEN))
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}

class AccessTokenInterceptor(private val accessToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(newRequest)
    }
}
