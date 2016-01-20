package derpibooru.derpy.data.internal;

import android.support.v4.app.Fragment;

public class FragmentAdapterItem {
    private int mPosition;
    private String mTitle;
    private Fragment mContent;

    public FragmentAdapterItem(int position, String title, Fragment content) {
        this.mPosition = position;
        this.mTitle = title;
        this.mContent = content;
    }

    public FragmentAdapterItem(int position, Fragment content) {
        this.mPosition = position;
        this.mContent = content;

        this.mTitle = "";
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