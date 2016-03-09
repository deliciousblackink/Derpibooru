package derpibooru.derpy.data.internal;

import android.support.v4.app.Fragment;

public class FragmentAdapterItem {
    private final int mPosition;
    private final String mTitle;
    private final Fragment mContent;

    public FragmentAdapterItem(int position, String title, Fragment content) {
        mPosition = position;
        mTitle = title;
        mContent = content;
    }

    public FragmentAdapterItem(int position, Fragment content) {
        mPosition = position;
        mContent = content;
        mTitle = "";
    }

    public int getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public Fragment getContent() {
        return mContent;
    }
}