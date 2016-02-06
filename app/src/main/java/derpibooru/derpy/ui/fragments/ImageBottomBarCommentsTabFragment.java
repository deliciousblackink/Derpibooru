package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;

public class ImageBottomBarCommentsTabFragment extends ImageBottomBarTabFragment {
    public ImageBottomBarCommentsTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_comments_tab, container, false);
        if (getArguments().containsKey("info")) {
            displayInfoInView(v, (DerpibooruImageInfo) getArguments().getParcelable("info"));
        }
        return v;
    }

    @Override
    protected void displayInfoInView(View target, DerpibooruImageInfo info) {

    }

    @Override
    protected void onLinkClick(View view) {

    }
}
