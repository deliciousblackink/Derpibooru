package derpibooru.derpy.data.types;

import android.support.v4.app.Fragment;

public class FragmentTab {
    private int mPosition;
    private String mTitle;
    private Fragment mContent;

    public FragmentTab(int position, String title, Fragment content) {
        this.mPosition = position;
        this.mTitle = title;
        this.mContent = content;
    }

    public FragmentTab(int position, Fragment content) {
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