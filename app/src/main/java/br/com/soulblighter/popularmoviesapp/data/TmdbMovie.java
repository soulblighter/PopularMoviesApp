package br.com.soulblighter.popularmoviesapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "movie")
public class TmdbMovie implements Serializable, Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public int id;

    @ColumnInfo(name = "voteCount")
    @SerializedName("vote_count")
    public int voteCount;

    @ColumnInfo(name = "video")
    @SerializedName("video")
    public boolean video;

    @ColumnInfo(name = "voteAverage")
    @SerializedName("vote_average")
    public double voteAverage;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    public String title;

    @ColumnInfo(name = "popularity")
    @SerializedName("popularity")
    public double popularity;

    @ColumnInfo(name = "posterPath")
    @SerializedName("poster_path")
    public String posterPath;

    @ColumnInfo(name = "originalLanguage")
    @SerializedName("original_language")
    public String originalLanguage;

    @ColumnInfo(name = "originalTitle")
    @SerializedName("original_title")
    public String originalTitle;

    @ColumnInfo(name = "genreIds")
    @SerializedName("genre_ids")
    public ArrayList<Integer> genreIds = new ArrayList<Integer>();

    @ColumnInfo(name = "backdropPath")
    @SerializedName("backdrop_path")
    public String backdropPath;

    @ColumnInfo(name = "adult")
    @SerializedName("adult")
    public boolean adult;

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    public String overview;

    @ColumnInfo(name = "releaseDate")
    @SerializedName("release_date")
    public String releaseDate;

    private final static long serialVersionUID = 1417041051496921076L;

    public TmdbMovie() {
    }

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
