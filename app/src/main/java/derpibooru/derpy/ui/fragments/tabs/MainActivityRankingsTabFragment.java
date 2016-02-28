package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.server.providers.RankingImageListProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class MainActivityRankingsTabFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(
                new RankingImageListProvider(getActivity(), new ImageListRequestHandler())
                        .type(RankingImageListProvider.RankingsType.fromValue(getArguments().getInt("type"))));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ImageListProvider getImageListProviderWithParameters(ImageListProvider target) {
        return ((RankingImageListProvider) target).inDays(3);
    }
}
