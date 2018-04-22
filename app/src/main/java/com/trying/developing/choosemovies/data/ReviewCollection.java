package com.trying.developing.choosemovies.data;

import java.util.List;

/**
 * Created by developing on 1/15/2018.
 */

public class ReviewCollection {

    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "ReviewCollection{" +
                "reviews=" + reviews +
                '}';
    }

}
