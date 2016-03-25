package derpibooru.derpy.data.internal;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentReplyItem implements Parcelable {
    private int mId;
    private int mAdapterPosition;

    public CommentReplyItem(int replyId, int adapterPosition) {
        mId = replyId;
        mAdapterPosition = adapterPosition;
    }

    public int getReplyId() {
        return mId;
    }

    public int getAdapterPosition() {
        return mAdapterPosition;
    }

    public void shiftAdapterPositionForward() {
        mAdapterPosition++;
    }

    protected CommentReplyItem(Parcel in) {
        mId = in.readInt();
        mAdapterPosition = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mAdapterPosition);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentReplyItem> CREATOR = new Parcelable.Creator<CommentReplyItem>() {
        @Override
        public CommentReplyItem createFromParcel(Parcel in) {
            return new CommentReplyItem(in);
        }

        @Override
        public CommentReplyItem[] newArray(int size) {
            return new CommentReplyItem[size];
        }
    };
}
