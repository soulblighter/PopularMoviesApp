package br.com.soulblighter.popularmoviesapp.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TmdbTrailerResp implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("results")
    @Expose
    public List<TmdbTrailer> results = new ArrayList<TmdbTrailer>();
    public final static Parcelable.Creator<TmdbTrailerResp> CREATOR = new Creator<TmdbTrailerResp>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TmdbTrailerResp createFromParcel(Parcel in) {
            return new TmdbTrailerResp(in);
        }

        public TmdbTrailerResp[] newArray(int size) {
            return (new TmdbTrailerResp[size]);
        }

    };
    private final static long serialVersionUID = -8395946464078553569L;

    protected TmdbTrailerResp(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.results, (TmdbTrailer.class.getClassLoader()));
    }

    public TmdbTrailerResp() {
    }

    public TmdbTrailerResp withId(int id) {
        this.id = id;
        return this;
    }

    public TmdbTrailerResp withResults(List<TmdbTrailer> results) {
        this.results = results;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}
