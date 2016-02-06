package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;

public class ImageBottomBarCommentsTabFragment extends ImageBottomBarTabFragment {
    private static final ImageBottomBarTabAdapter.ImageBottomBarTab TAB_ID =
            ImageBottomBarTabAdapter.ImageBottomBarTab.Comments;

    public ImageBottomBarCommentsTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_comments_tab, container, false);
        return v;
    }

    @Override
    protected void onLinkClick(View view) {

    }
}
