package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.api.Result

class MovieItemAdapter(private val movieResponseItem: List<Result>) :
    RecyclerView.Adapter<MovieItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = movieResponseItem[position]
        holder.title.text = data.title
        holder.rating.rating = (data.voteAverage.toFloat() / 10) * 5

        val genreFinder = GenreFinder()
        var genreName = ""
        for (genre in data.genreIds) {
            genreName += genreFinder.getGenreNameById(genre)
            if (genre != data.genreIds.last()) {
                genreName += ", "
            }
        }

        holder.type.text = genreName
        holder.release_date.text = data.releaseDate
        holder.overview.text = data.overview

        val imageUrl = "https://image.tmdb.org/t/p/w200" + data.posterPath
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.image)
    }

    override fun getItemCount(): Int = movieResponseItem.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.movie_item_title)
        val rating: RatingBar = itemView.findViewById(R.id.movie_item_rating)
        val type: TextView = itemView.findViewById(R.id.movie_item_type)
        val release_date: TextView = itemView.findViewById(R.id.movie_item_release_date)
        val overview: TextView = itemView.findViewById(R.id.movie_item_overview)
        val image: ImageView = itemView.findViewById(R.id.movie_item_image)
    }
}
