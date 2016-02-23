package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.RankingImageListProvider;

public class MainActivityRankingsTabFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(new RankingImageListProvider(getActivity(),
                                                        new ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImages() {
        ((RankingImageListProvider) super.getImageListProvider())
                .type(RankingImageListProvider.RankingsType.fromValue(getArguments().getInt("type")))
                .inDays(3) /* TODO: pass the time limit as an argument */
                .fetch();
    }
}
