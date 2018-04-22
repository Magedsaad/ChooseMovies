package com.trying.developing.choosemovies.api;

import android.os.AsyncTask;

import com.trying.developing.choosemovies.data.Review;
import com.trying.developing.choosemovies.data.ReviewCollection;


import java.net.URL;
import java.util.List;

/**
 * Created by developing on 1/15/2018.
 */

public abstract class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

    private static final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private String id;

    public FetchReviewsTask(String id) {
        this.id = id;
    }

    @Override
    protected List<Review> doInBackground(String... params) {
        URL reviewsRequestUrl = NetworkUtils.buildTrailersOrReviewsUrl("35ceb1720ce9e586d5f95f059133c080", id, "reviews");
        try {
            String jsonReviewsResponse = NetworkUtils
                    .getResponseFromHttpUrl(reviewsRequestUrl);
            ReviewCollection reviewCollection = ReviewsJsonUtils.parseJson(jsonReviewsResponse);
            return reviewCollection.getReviews();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
