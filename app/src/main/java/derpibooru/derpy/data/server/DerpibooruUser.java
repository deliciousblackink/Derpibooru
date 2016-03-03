package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.primitives.Ints;

public class DerpibooruUser implements Parcelable {
    private boolean mIsLoggedIn;
    private String mUsername;
    private String mAvatarUrl;
    private DerpibooruFilter mCurrentFilter;

    public DerpibooruUser(String username, String avatarUrl) {
        mUsername = username;
        mAvatarUrl = avatarUrl;
        mIsLoggedIn = !username.isEmpty();
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public DerpibooruFilter getCurrentFilter() {
        return mCurrentFilter;
    }

    public DerpibooruUser setCurrentFilter(DerpibooruFilter newFilter) {
        mCurrentFilter = newFilter;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected DerpibooruUser(Parcel in) {
        mIsLoggedIn = (in.readInt() == 1);
        mUsername = in.readString();
        mAvatarUrl = in.readString();
        mCurrentFilter = in.readParcelable(DerpibooruFilter.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIsLoggedIn ? 1 : 0);
        dest.writeString(mUsername);
        dest.writeString(mAvatarUrl);
        dest.writeParcelable(mCurrentFilter, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruUser> CREATOR = new Parcelable.Creator<DerpibooruUser>() {
        @Override
        public DerpibooruUser createFromParcel(Parcel in) {
            return new DerpibooruUser(in);
        }

        @Override
        public DerpibooruUser[] newArray(int size) {
            return new DerpibooruUser[size];
        }
    };
}