package br.com.soulblighter.popularmoviesapp.retrofit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.popularmoviesapp.model.TmdbReview;

public class TmdbReviewResp implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("page")
    @Expose
    public int page;
    @SerializedName("results")
    @Expose
    public List<TmdbReview> results = new ArrayList<TmdbReview>();
    @SerializedName("total_pages")
    @Expose
    public int totalPages;
    @SerializedName("total_results")
    @Expose
    public int totalResults;
    public final static Parcelable.Creator<TmdbReviewResp> CREATOR = new Creator<TmdbReviewResp>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TmdbReviewResp createFromParcel(Parcel in) {
            return new TmdbReviewResp(in);
        }

        public TmdbReviewResp[] newArray(int size) {
            return (new TmdbReviewResp[size]);
        }

    };
    private final static long serialVersionUID = 6143341560286356150L;

    protected TmdbReviewResp(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.page = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.results, (TmdbReview.class.getClassLoader()));
        this.totalPages = ((int) in.readValue((int.class.getClassLoader())));
        this.totalResults = ((int) in.readValue((int.class.getClassLoader())));
    }

    public TmdbReviewResp() {
    }

    public TmdbReviewResp withId(int id) {
        this.id = id;
        return this;
    }

    public TmdbReviewResp withPage(int page) {
        this.page = page;
        return this;
    }

    public TmdbReviewResp withResults(List<TmdbReview> results) {
        this.results = results;
        return this;
    }

    public TmdbReviewResp withTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public TmdbReviewResp withTotalResults(int totalResults) {
        this.totalResults = totalResults;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(page);
        dest.writeList(results);
        dest.writeValue(totalPages);
        dest.writeValue(totalResults);
    }

    public int describeContents() {
        return 0;
    }

}
