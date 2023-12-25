package com.example.movieapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE movieId = :movieId")
    suspend fun deleteFavoriteMovie(movieId: Long)

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getFavoriteMovieById(movieId: Long): Movie?

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>
}
