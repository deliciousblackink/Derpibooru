package derpibooru.derpy.ui.adapters;

import android.support.v4.app.Fragment;

public class Tab {
    private int mPosition;
    private String mTitle;
    private Fragment mContent;

    public Tab(int position, String title, Fragment content) {
        this.mPosition = position;
        this.mTitle = title;
        this.mContent = content;
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