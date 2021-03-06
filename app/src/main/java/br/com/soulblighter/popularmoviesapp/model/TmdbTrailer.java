package br.com.soulblighter.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TmdbTrailer implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("iso_639_1")
    @Expose
    public String iso6391;

    @SerializedName("iso_3166_1")
    @Expose
    public String iso31661;

    @SerializedName("key")
    @Expose
    public String key;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("site")
    @Expose
    public String site;

    @SerializedName("size")
    @Expose
    public int size;

    @SerializedName("type")
    @Expose
    public String type;

    public final static Parcelable.Creator<TmdbTrailer> CREATOR = new Creator<TmdbTrailer>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TmdbTrailer createFromParcel(Parcel in) {
            return new TmdbTrailer(in);
        }

        public TmdbTrailer[] newArray(int size) {
            return (new TmdbTrailer[size]);
        }

    };
    private final static long serialVersionUID = 8476480491718329175L;

    protected TmdbTrailer(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.iso6391 = ((String) in.readValue((String.class.getClassLoader())));
        this.iso31661 = ((String) in.readValue((String.class.getClassLoader())));
        this.key = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.site = ((String) in.readValue((String.class.getClassLoader())));
        this.size = ((int) in.readValue((int.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TmdbTrailer() {
    }

    public TmdbTrailer withId(String id) {
        this.id = id;
        return this;
    }

    public TmdbTrailer withIso6391(String iso6391) {
        this.iso6391 = iso6391;
        return this;
    }

    public TmdbTrailer withIso31661(String iso31661) {
        this.iso31661 = iso31661;
        return this;
    }

    public TmdbTrailer withKey(String key) {
        this.key = key;
        return this;
    }

    public TmdbTrailer withName(String name) {
        this.name = name;
        return this;
    }

    public TmdbTrailer withSite(String site) {
        this.site = site;
        return this;
    }

    public TmdbTrailer withSize(int size) {
        this.size = size;
        return this;
    }

    public TmdbTrailer withType(String type) {
        this.type = type;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(iso6391);
        dest.writeValue(iso31661);
        dest.writeValue(key);
        dest.writeValue(name);
        dest.writeValue(site);
        dest.writeValue(size);
        dest.writeValue(type);
    }

    public int describeContents() {
        return 0;
    }

}
