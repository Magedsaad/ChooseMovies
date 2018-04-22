package com.trying.developing.choosemovies.api;

import android.os.AsyncTask;

import com.trying.developing.choosemovies.data.Trailer;
import com.trying.developing.choosemovies.data.TrailerCollection;


import java.net.URL;
import java.util.List;

/**
 * Created by developing on 1/15/2018.
 */

public abstract class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

    private static final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private String id;

    public FetchTrailersTask(String id) {
        this.id = id;
    }

    @Override
    protected List<Trailer> doInBackground(String... params) {
        URL trailersRequestUrl = NetworkUtils.buildTrailersOrReviewsUrl("35ceb1720ce9e586d5f95f059133c080", id, "videos");
        try {
            String jsonTrailersResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailersRequestUrl);
            TrailerCollection trailerCollection = TrailersJsonUtils.parseJson(jsonTrailersResponse);
            return trailerCollection.getTrailers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
