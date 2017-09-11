package br.com.soulblighter.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";

    ImageView iv_poster;
    TextView tv_date;
    TextView tv_rating;
    TextView tv_summary;
    TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        TmdbMovie movie = i.getParcelableExtra(EXTRA_MOVIE);

        iv_poster = (ImageView)findViewById(R.id.iv_poster);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_rating = (TextView) findViewById(R.id.tv_rating);
        tv_summary = (TextView) findViewById(R.id.tv_summary);
        tv_name = (TextView) findViewById(R.id.tv_name);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date =  df.parse(movie.releaseDate);
            tv_date.setText(this.getString(R.string.release_date)+ " " + String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(date)));
        } catch (ParseException pe) {
            pe.printStackTrace();
            tv_date.setText(this.getString(R.string.release_date)+ " " + String.valueOf(movie.releaseDate));
        }

        tv_rating.setText(this.getString(R.string.rating)+ " " + String.valueOf(movie.voteAverage) + " / 10.0");
        tv_summary.setText(String.valueOf(movie.overview));
        tv_name.setText(String.valueOf(movie.title));

        String url = NetworkUtils.IMAGE_TMDB_URL + movie.posterPath;

        if(url != null) {
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.color.colorPrimary)
                    .into(iv_poster);
        }
    }
}
