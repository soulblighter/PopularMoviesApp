package br.com.soulblighter.popularmoviesapp.json;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TmdbReview implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("content")
    @Expose
    public String content;

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

    }
            ;
    private final static long serialVersionUID = -6456665049320540962L;

    protected TmdbReview(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.author = ((String) in.readValue((String.class.getClassLoader())));
        this.content = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TmdbReview() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(author);
        dest.writeValue(content);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}
