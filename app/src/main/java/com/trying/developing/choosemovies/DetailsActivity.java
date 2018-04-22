package com.trying.developing.choosemovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trying.developing.choosemovies.api.FetchReviewsTask;
import com.trying.developing.choosemovies.api.FetchTrailersTask;
import com.trying.developing.choosemovies.api.NetworkUtils;
import com.trying.developing.choosemovies.data.Movies;
import com.trying.developing.choosemovies.data.MoviesContract;
import com.trying.developing.choosemovies.data.MoviesDbHelper;
import com.trying.developing.choosemovies.data.Review;
import com.trying.developing.choosemovies.data.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.image_details_poster)
    ImageView imagePoster;

    @BindView(R.id.text_details_title)
    TextView textDetailsTitle;

    @BindView(R.id.text_details_release_date)
    TextView textDetailsReleaseDate;

    @BindView(R.id.text_details_synopsis)
    TextView textDetailsSynopsis;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.text_reviews_title)
    TextView textReviewsTitle;

    @BindView(R.id.layout_reviews_list)
    LinearLayout linearLayoutReviews;

    @BindView(R.id.text_trailer_title)
    TextView textTrailerTitle;

    @BindView(R.id.layout_trailers_list)
    LinearLayout linearLayoutTrailers;

    @BindView(R.id.favorite_image)
    ImageView imageFavorite;

    private MoviesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Bundle data = getIntent().getExtras();
        final Movies movie = data.getParcelable("movieDetails");
        setMovieDetails(movie);
        dbHelper = new MoviesDbHelper(this);
        if (checkIfMovieIsInDb(movie)) {
            changeToFilledFavIcon();
        }
        imageFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfMovieIsInDb(movie)) {
                    changeToFilledFavIcon();
                    saveMovieInDb(movie);
                } else {
                    changeToEmptyFavIcon();
                    deleteMovieFromDb(movie);
                }
            }
        });
    }

    private void deleteMovieFromDb(Movies movie) {
        String selection = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(movie.getId())};
        getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI, selection, selectionArgs);
    }

    private void saveMovieInDb(Movies movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_DESCRIPTION, movie.getOverview());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH, movie.getImage());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getDate());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getRating());
        getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
    }

    private void changeToEmptyFavIcon() {
        imageFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
    }

    private void changeToFilledFavIcon() {
        imageFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
    }

    private boolean checkIfMovieIsInDb(Movies movie) {
        Cursor cursor = getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int movieId = cursor.getInt(
                        cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
                if (movieId == movie.getId()) {
                    return true;
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    private void setMovieDetails(Movies movie) {
        Picasso.with(imagePoster.getContext())
                .load(NetworkUtils.buildPosterUrl(movie.getImage()))
                .placeholder(R.drawable.shape_movie_poster)
                .into(imagePoster);
        textDetailsTitle.setText(movie.getTitle());
        textDetailsReleaseDate.setText(
                String.format(
                        getResources().getString(R.string.release_date), movie.getDate()));
        textDetailsSynopsis.setText(movie.getOverview());
        ratingBar.setRating(movie.getRating());
        new FetchTrailersTask(String.valueOf(movie.getId())) {
            @Override
            protected void onPostExecute(List<Trailer> trailers) {
                addTrailersToLayout(trailers);
            }
        }.execute();
        new FetchReviewsTask(String.valueOf(movie.getId())) {
            @Override
            protected void onPostExecute(List<Review> reviews) {
                addReviewsToLayout(reviews);
            }
        }.execute();

    }



    private void addTrailersToLayout(List<Trailer> trailers) {
        if (trailers != null && !trailers.isEmpty()) {
            for (Trailer trailer : trailers) {
                if (trailer.getType().equals(getString(R.string.trailer_type)) &&
                        trailer.getSite().equals(getString(R.string.trailer_site_youtube))) {
                    View view = getTrailerView(trailer);
                    linearLayoutTrailers.addView(view);
                }
            }
        } else {
            hideTrailersSection();
        }
    }

    private void addReviewsToLayout(List<Review> reviews) {
        if (reviews != null && !reviews.isEmpty()) {
            for (Review review : reviews) {
                View view = getReviewView(review);
                linearLayoutReviews.addView(view);
            }
        } else {
            hideReviewsSection();
        }
    }

    private void hideTrailersSection() {
        textTrailerTitle.setVisibility(View.GONE);
        linearLayoutTrailers.setVisibility(View.GONE);
    }

    private void hideReviewsSection() {
        textReviewsTitle.setVisibility(View.GONE);
        linearLayoutReviews.setVisibility(View.GONE);
    }

    private View getTrailerView(final Trailer trailer) {
        LayoutInflater inflater = LayoutInflater.from(DetailsActivity.this);
        View view = inflater.inflate(R.layout.trailer_list_item, linearLayoutTrailers, false);
        TextView trailerNameTextView = ButterKnife.findById(view, R.id.text_trailer_item_name);
        trailerNameTextView.setText(trailer.getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(NetworkUtils.buildYouTubeUrl(trailer.getKey())));
                startActivity(intent);
            }
        });
        return view;
    }

    private View getReviewView(final Review review) {
        LayoutInflater inflater = LayoutInflater.from(DetailsActivity.this);
        View view = inflater.inflate(R.layout.review_list_item, linearLayoutReviews, false);
        TextView contentTextView = ButterKnife.findById(view, R.id.text_content_review);
        TextView authorTextView = ButterKnife.findById(view, R.id.text_author_review);
        authorTextView.setText(getString(R.string.by_author_review, review.getAuthor()));
        contentTextView.setText(review.getContent());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = review.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        return view;
    }
}
