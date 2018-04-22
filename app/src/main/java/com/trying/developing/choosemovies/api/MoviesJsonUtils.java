package com.trying.developing.choosemovies.api;


import android.support.annotation.NonNull;
import android.util.Log;

import com.trying.developing.choosemovies.data.MovieCollection;
import com.trying.developing.choosemovies.data.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developing on 1/20/2018.
 */

public class MoviesJsonUtils {
    private static final String LOG_TAG = MoviesJsonUtils.class.getCanonicalName();
    private static final String statusError = "status_code";
    private static final String movies = "results";
    private static final String posterKey = "poster_path";
    private static final String overviewKey = "overview";
    private static final String releaseDateKey = "release_date";
    private static final String titleKey = "title";
    private static final String idKey = "id";
    private static final String voteAverageKey = "vote_average";

    public static MovieCollection parseJson(String json)
            throws JSONException {
        JSONObject responseJson = new JSONObject(json);
        if (responseJson.has(statusError)) {
            int errorCode = responseJson.getInt(statusError);
            Log.e(LOG_TAG, "parse json movies error code: " + String.valueOf(errorCode));
        }
        JSONArray moviesArray = responseJson.getJSONArray(movies);
        List<Movies> movieList = parseMovieList(moviesArray);
        MovieCollection movieCollection = new MovieCollection();
        movieCollection.setMovies(movieList);
        return movieCollection;
    }

    @NonNull
    private static List<Movies> parseMovieList(JSONArray moviesArray) throws JSONException {
        List<Movies> movieList = new ArrayList<>();
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            Movies currentMovie = parseMovie(movie);
            movieList.add(currentMovie);
        }
        return movieList;
    }

    @NonNull
    private static Movies parseMovie(JSONObject movie) throws JSONException {
        Movies currentMovie = new Movies();
        currentMovie.setImage(movie.getString(posterKey));
        currentMovie.setOverview(movie.getString(overviewKey));
        currentMovie.setDate(movie.getString(releaseDateKey));
        currentMovie.setTitle(movie.getString(titleKey));
        currentMovie.setId(movie.getInt(idKey));
        currentMovie.setRating(movie.getLong(voteAverageKey));
        return currentMovie;
    }
}
