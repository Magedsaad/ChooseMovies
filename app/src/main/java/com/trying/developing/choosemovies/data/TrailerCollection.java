package com.trying.developing.choosemovies.data;

import java.util.List;

/**
 * Created by developing on 1/15/2018.
 */

public class TrailerCollection {


    private List<Trailer> trailers;

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "TrailerCollection{" +
                "trailers=" + trailers +
                '}';
    }

}
