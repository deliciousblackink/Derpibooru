package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class MainActivityNewImagesTabFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(new ImageListProvider(getActivity(),
                                                         new ImageListFragment.ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ImageListProvider getImageListProviderWithParameters(ImageListProvider target) {
        return target;
    }
}
