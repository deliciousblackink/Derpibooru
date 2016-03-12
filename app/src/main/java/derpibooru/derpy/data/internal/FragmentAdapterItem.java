package derpibooru.derpy.data.internal;

import android.support.v4.app.Fragment;

public class FragmentAdapterItem {
    private final int mPosition;
    private final Fragment mContent;

    public FragmentAdapterItem(int position, Fragment content) {
        mPosition = position;
        mContent = content;
    }

    public int getPosition() {
        return mPosition;
    }

    public Fragment getContent() {
        return mContent;
    }
}