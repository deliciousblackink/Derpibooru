package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.providers.RankingImageListProvider;
import derpibooru.derpy.server.providers.SearchProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class RankingImageListFragment extends ImageListFragment {
    public static final String EXTRAS_RANKING_LIST_TYPE = "derpibooru.derpy.RankingListType";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        RankingImageListProvider.RankingsType listType =
                RankingImageListProvider.RankingsType.fromValue(getArguments().getInt(EXTRAS_RANKING_LIST_TYPE));
        super.initializeList(
                new RankingImageListProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler())
                        .type(listType).inDays(3));
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) bar.setTitle(
                listType == RankingImageListProvider.RankingsType.TopScoring ? R.string.image_list_top_scoring
                                                                             : R.string.image_list_most_commented);
        return v;
    }

}
