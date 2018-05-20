package br.com.soulblighter.popularmoviesapp.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TmdbReview implements Serializable, Parcelable {

    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("url")
    @Expose
    public String url;
    public final static Parcelable.Creator<TmdbReview> CREATOR = new Creator<TmdbReview>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TmdbReview createFromParcel(Parcel in) {
            return new TmdbReview(in);
        }

        public TmdbReview[] newArray(int size) {
            return (new TmdbReview[size]);
        }

    };
    private final static long serialVersionUID = 7726291736286045489L;

    protected TmdbReview(Parcel in) {
        this.author = ((String) in.readValue((String.class.getClassLoader())));
        this.content = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TmdbReview() {
    }

    public TmdbReview withAuthor(String author) {
        this.author = author;
        return this;
    }

    public TmdbReview withContent(String content) {
        this.content = content;
        return this;
    }

    public TmdbReview withId(String id) {
        this.id = id;
        return this;
    }

    public TmdbReview withUrl(String url) {
        this.url = url;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(author);
        dest.writeValue(content);
        dest.writeValue(id);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}
