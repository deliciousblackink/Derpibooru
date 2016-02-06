package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruRankingsListType;
import derpibooru.derpy.server.RankingsProvider;

public class RankingsTabFragment extends ImageListFragment {
    public RankingsTabFragment() {
        super();
    }

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
                .type(DerpibooruRankingsListType.getFromValue(getArguments().getInt("type")))
                .inDays(3) /* TODO: pass the time limit as an argument */
                .fetch();
    }
}
