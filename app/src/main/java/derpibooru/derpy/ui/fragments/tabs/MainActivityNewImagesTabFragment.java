package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;
import derpibooru.derpy.ui.presenters.PaginatedListPresenter;

public class MainActivityNewImagesTabFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        super.initializeList(
                new ImageListProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler()));
        return v;
    }
}
