package com.trying.developing.choosemovies;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.trying.developing.choosemovies.api.MoviesJsonUtils;
import com.trying.developing.choosemovies.api.NetworkUtils;
import com.trying.developing.choosemovies.data.FavoritesCursorLoader;
import com.trying.developing.choosemovies.data.MovieCollection;
import com.trying.developing.choosemovies.data.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

     MovieAdapter mMovieGridAdapter;
    private ArrayList<Movies> mMovies = null;
    private int selectedOption = R.id.action_popular;
    private static final String FILTER_TYPE_1 = "popular";
    private static final String FILTER_TYPE_2 = "top_rated";
    public static final int ID_FAVORITES_LOADER = 11;
    private static final String MOVIES_KEY = "movies";

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, getSpan());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mMovieGridAdapter=new MovieAdapter();
        recyclerView.setAdapter(mMovieGridAdapter);
        if (savedInstanceState == null) {
            setMovieAdapterPopular();
        }else if(savedInstanceState.containsKey(MOVIES_KEY)){
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            mMovieGridAdapter.setMoviesData(mMovies);
        }
    }





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("optionSelected", selectedOption);
        if(mMovies !=null){
        outState.putParcelableArrayList(MOVIES_KEY,mMovies);
        }
    }

    private int getSpan() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 4;
        }
        return 2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        loadAdapterPerOptionSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void loadAdapterPerOptionSelected(int selectedOption) {
        this.selectedOption = selectedOption;
        if (selectedOption == R.id.action_popular) {
            setMovieAdapterPopular();
        }
        if (selectedOption == R.id.action_top_rated) {
            setMovieAdapterTopRated();
        }
        if (selectedOption == R.id.action_favorites) {
            setMovieAdapterFavorites();
        }
    }

    private void setMovieAdapterFavorites() {
        FavoritesAdapter favoritesAdapter = new FavoritesAdapter();
        recyclerView.setAdapter(favoritesAdapter);
        getSupportLoaderManager().initLoader(
                ID_FAVORITES_LOADER, null, new FavoritesCursorLoader(this, favoritesAdapter));
    }

    private void setMovieAdapterTopRated() {
        mMovieGridAdapter = new MovieAdapter();
        FetchMoviesTask moviesTask = new FetchMoviesTask(mMovieGridAdapter);
        recyclerView.setAdapter(mMovieGridAdapter);
        moviesTask.execute(FILTER_TYPE_2);
    }

    private void setMovieAdapterPopular() {
        mMovieGridAdapter = new MovieAdapter();
        FetchMoviesTask moviesTask = new FetchMoviesTask(mMovieGridAdapter);
        recyclerView.setAdapter(mMovieGridAdapter);
        moviesTask.execute(FILTER_TYPE_1);
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {

        MovieAdapter mMovieGridAdapter;

        public FetchMoviesTask(MovieAdapter adapter) {
            this.mMovieGridAdapter = adapter;
        }

        @Override
        protected List<Movies> doInBackground(String... params) {
            URL moviesRequestUrl = NetworkUtils.buildUrl("35ceb1720ce9e586d5f95f059133c080", params[0]);
            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);
                MovieCollection movieCollection = MoviesJsonUtils.parseJson(jsonMoviesResponse);
                return movieCollection.getMovies();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            if (movies != null) {
                if (mMovieGridAdapter != null) {
                    mMovieGridAdapter.setMoviesData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }


}
