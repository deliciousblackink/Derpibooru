package derpibooru.derpy.ui.fragments;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.SearchProvider;

public class SearchResultTabFragment extends ImageListTabFragment {
    public SearchResultTabFragment() {
        super();
    }

    @Override
    protected void fetchDerpibooruImageThumbs() {
        new SearchProvider(getActivity(), this)
                .searching(getArguments().getString("query"))
                .with(new DerpibooruSearchOptions())
                .fetch();
    }

    public void setSearchOptions(DerpibooruSearchOptions searchOptions) {
        stopFetchingImages(); /* there may still be downloads in progress for the previous search request */
        new SearchProvider(getActivity(), this)
                .searching(getArguments().getString("query"))
                .with(searchOptions)
                .fetch();
    }
}
