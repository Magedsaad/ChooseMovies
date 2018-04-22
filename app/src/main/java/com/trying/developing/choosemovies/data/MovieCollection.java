package com.trying.developing.choosemovies.data;

import java.util.List;

/**
 * Created by developing on 1/20/2018.
 */

public class MovieCollection {

    List<Movies> movies;

    public List<Movies> getMovies() {
        return movies;
    }

    public void setMovies(List<Movies> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "MovieCollection{" +
                "movies=" + movies +
                '}';
    }

}
