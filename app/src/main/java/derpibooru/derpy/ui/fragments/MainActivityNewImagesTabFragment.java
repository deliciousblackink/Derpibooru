package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.ImageListProvider;

public class MainActivityNewImagesTabFragment extends ImageListFragment {
    public MainActivityNewImagesTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(new ImageListProvider(getActivity(),
                                                         new ImageListFragment.ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImageThumbs() {
        super.getImageListProvider().fetch();
    }
}