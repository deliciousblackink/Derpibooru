package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.WatchedProvider;

public class WatchedTabFragment extends ImageListFragment {
    public WatchedTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(new WatchedProvider(getActivity(),
                                                        new ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImageThumbs() {
        super.getImageListProvider().fetch();
    }
}
