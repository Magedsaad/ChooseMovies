package com.trying.developing.choosemovies;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.trying.developing.choosemovies.api.NetworkUtils;
import com.trying.developing.choosemovies.data.Movies;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by developing on 1/20/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movies> movies=new ArrayList<>();

    public void setMoviesData(List<Movies> list) {
        movies = list;
        notifyDataSetChanged();
    }



    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.movie_list_item,parent,false);
        MovieAdapterViewHolder viewHolder=new MovieAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        Picasso.with(holder.imagePoster.getContext())
                .load(NetworkUtils.buildPosterUrl(movies.get(position).getImage()))
                .placeholder(R.drawable.shape_movie_poster)
                .into(holder.imagePoster);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.Image_poster)
        ImageView imagePoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(v.getContext(), DetailsActivity.class);
            Movies currentMovie = movies.get(getAdapterPosition());
            intent.putExtra("movieDetails", currentMovie);
            v.getContext().startActivity(intent);

        }


    }
}
