package info.mik.mru.github;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by mik on 2018-05-16.
 */

public class ModelRepo implements Parcelable {

   public ModelRepo(Parcel in) {
        mName=in.readString();
        mDescription=in.readString();
        mSize =in.readInt();
    }

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("description")
    @Expose
    private String mDescription;

    @SerializedName("size")
    @Expose
    private int mSize;

    @SerializedName("has_wiki")
    @Expose
    private Boolean mHasWiki;


    public String getmName() {
        return mName;
    }

    public String getmDescription() {
        return mDescription;
    }

    public int getmSize() {
        return mSize;
    }

    public void setmSize(int mSize) {
        this.mSize = mSize;
    }

    public Boolean getmHasWiki() {
        return mHasWiki;
    }

    public void setmHasWiki(Boolean mHasWiki) {
        this.mHasWiki = mHasWiki;
    }

    @SerializedName("owner")
    @Expose
    private Owner owner;
    public Owner getOwner() {
        return owner;
    }

    public class Owner {
        @SerializedName("login")
        private String login;
        @SerializedName("avatar_url")
        private String avatar_url;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getAvatar_url() {
            return avatar_url;
        }
    }

    public static final Creator<ModelRepo> CREATOR = new Creator<ModelRepo>() {
        @Override
        public ModelRepo createFromParcel(Parcel in) {
            return new ModelRepo(in);
        }

        @Override
        public ModelRepo[] newArray(int size) {
            return new ModelRepo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mDescription);
        parcel.writeInt(mSize);
    }

}
