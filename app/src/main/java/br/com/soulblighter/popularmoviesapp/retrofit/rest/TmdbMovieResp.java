package br.com.soulblighter.popularmoviesapp.retrofit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;

public class TmdbMovieResp implements Serializable, Parcelable {

    @SerializedName("page")
    @Expose
    public int page;

    @SerializedName("total_results")
    @Expose
    public int totalResults;

    @SerializedName("total_pages")
    @Expose
    public int totalPages;

    @SerializedName("results")
    @Expose
    public List<TmdbMovie> results = new ArrayList<TmdbMovie>();

    public final static Parcelable.Creator<TmdbMovieResp> CREATOR = new Creator<TmdbMovieResp>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TmdbMovieResp createFromParcel(Parcel in) {
            return new TmdbMovieResp(in);
        }

        public TmdbMovieResp[] newArray(int size) {
            return (new TmdbMovieResp[size]);
        }

    };
    private final static long serialVersionUID = -5677095513871265881L;

    protected TmdbMovieResp(Parcel in) {
        this.page = ((int) in.readValue((int.class.getClassLoader())));
        this.totalResults = ((int) in.readValue((int.class.getClassLoader())));
        this.totalPages = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.results, (TmdbMovie.class.getClassLoader()));
    }

    public TmdbMovieResp() {
    }

    public TmdbMovieResp withPage(int page) {
        this.page = page;
        return this;
    }

    public TmdbMovieResp withTotalResults(int totalResults) {
        this.totalResults = totalResults;
        return this;
    }

    public TmdbMovieResp withTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public TmdbMovieResp withResults(List<TmdbMovie> results) {
        this.results = results;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeValue(totalResults);
        dest.writeValue(totalPages);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}
