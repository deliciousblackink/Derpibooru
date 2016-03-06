package derpibooru.derpy.ui.test_utils;

import android.os.Parcel;
import android.os.Parcelable;

public class MockIntegerParcelable implements Parcelable {
    private int mId;

    public MockIntegerParcelable(int id) {
        mId = id;
    }

    public int getValue() {
        return mId;
    }

    protected MockIntegerParcelable(Parcel in) {
        mId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
    }

    @SuppressWarnings("unused")
    final Parcelable.Creator<MockIntegerParcelable> CREATOR = new Parcelable.Creator<MockIntegerParcelable>() {
        @Override
        public MockIntegerParcelable createFromParcel(Parcel in) {
            return new MockIntegerParcelable(in);
        }

        @Override
        public MockIntegerParcelable[] newArray(int size) {
            return new MockIntegerParcelable[size];
        }
    };
}
