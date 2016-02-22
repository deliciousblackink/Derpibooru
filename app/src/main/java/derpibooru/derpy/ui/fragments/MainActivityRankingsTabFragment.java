package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.RankingsProvider;

public class MainActivityRankingsTabFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(new RankingsProvider(getActivity(),
                                                        new ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImageThumbs() {
        ((RankingsProvider) super.getImageListProvider())
                .type(RankingsProvider.RankingsType.fromValue(getArguments().getInt("type")))
                .inDays(3) /* TODO: pass the time limit as an argument */
                .fetch();
    }
}
