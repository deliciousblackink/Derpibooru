package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.SearchProvider;

public class SearchResultTabFragment extends ImageListFragment {
    public SearchResultTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(new SearchProvider(getActivity(),
                                                      new ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImageThumbs() {
        ((SearchProvider) getImageListProvider())
                .searching(getArguments().getString("query"))
                .with(new DerpibooruSearchOptions())
                .fetch();
    }

    public void setSearchOptions(DerpibooruSearchOptions searchOptions) {
        super.resetImageListAdapter();
        /* TODO: there may still be downloads in progress for the previous search request, stop them */
        ((SearchProvider) getImageListProvider())
                .searching(getArguments().getString("query"))
                .with(searchOptions)
                .startingFromFirstPage()
                .fetch();
    }
}
