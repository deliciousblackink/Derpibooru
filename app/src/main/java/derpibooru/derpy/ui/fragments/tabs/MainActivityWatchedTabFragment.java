package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.WatchedProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class MainActivityWatchedTabFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        super.initializeList(
                new WatchedProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler()));
        return v;
    }
}
