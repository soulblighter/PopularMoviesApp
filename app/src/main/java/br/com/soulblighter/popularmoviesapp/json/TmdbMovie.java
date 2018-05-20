package br.com.soulblighter.popularmoviesapp.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TmdbMovie implements Serializable, Parcelable {

    @SerializedName("vote_count")
    @Expose
    public int voteCount;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("video")
    @Expose
    public boolean video;
    @SerializedName("vote_average")
    @Expose
    public double voteAverage;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("popularity")
    @Expose
    public double popularity;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("genre_ids")
    @Expose
    public List<Integer> genreIds = new ArrayList<Integer>();
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("adult")
    @Expose
    public boolean adult;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    public final static Parcelable.Creator<TmdbMovie> CREATOR = new Creator<TmdbMovie>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TmdbMovie createFromParcel(Parcel in) {
            return new TmdbMovie(in);
        }

        public TmdbMovie[] newArray(int size) {
            return (new TmdbMovie[size]);
        }

    };
    private final static long serialVersionUID = 1417041051496921076L;

    protected TmdbMovie(Parcel in) {
        this.voteCount = ((int) in.readValue((int.class.getClassLoader())));
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.video = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.voteAverage = ((double) in.readValue((double.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.popularity = ((double) in.readValue((double.class.getClassLoader())));
        this.posterPath = ((String) in.readValue((String.class.getClassLoader())));
        this.originalLanguage = ((String) in.readValue((String.class.getClassLoader())));
        this.originalTitle = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.genreIds, (java.lang.Integer.class.getClassLoader()));
        this.backdropPath = ((String) in.readValue((String.class.getClassLoader())));
        this.adult = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.overview = ((String) in.readValue((String.class.getClassLoader())));
        this.releaseDate = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TmdbMovie() {
    }

    public TmdbMovie withVoteCount(int voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public TmdbMovie withId(int id) {
        this.id = id;
        return this;
    }

    public TmdbMovie withVideo(boolean video) {
        this.video = video;
        return this;
    }

    public TmdbMovie withVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    public TmdbMovie withTitle(String title) {
        this.title = title;
        return this;
    }

    public TmdbMovie withPopularity(double popularity) {
        this.popularity = popularity;
        return this;
    }

    public TmdbMovie withPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public TmdbMovie withOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
        return this;
    }

    public TmdbMovie withOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }

    public TmdbMovie withGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
        return this;
    }

    public TmdbMovie withBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public TmdbMovie withAdult(boolean adult) {
        this.adult = adult;
        return this;
    }

    public TmdbMovie withOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public TmdbMovie withReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(voteCount);
        dest.writeValue(id);
        dest.writeValue(video);
        dest.writeValue(voteAverage);
        dest.writeValue(title);
        dest.writeValue(popularity);
        dest.writeValue(posterPath);
        dest.writeValue(originalLanguage);
        dest.writeValue(originalTitle);
        dest.writeList(genreIds);
        dest.writeValue(backdropPath);
        dest.writeValue(adult);
        dest.writeValue(overview);
        dest.writeValue(releaseDate);
    }

    public int describeContents() {
        return 0;
    }

}
