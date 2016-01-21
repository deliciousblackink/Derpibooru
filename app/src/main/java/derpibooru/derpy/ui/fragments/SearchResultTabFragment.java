package derpibooru.derpy.ui.fragments;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.SearchResultProvider;

public class SearchResultTabFragment extends ImageListTabFragment {
    public SearchResultTabFragment() {
        super();
    }

    @Override
    protected void fetchDerpibooruImageThumbs() {
        new SearchResultProvider(getActivity(), this)
                .search(getArguments().getString("query"));
    }

    public void setSearchOptions(DerpibooruSearchOptions searchOptions) {
        new SearchResultProvider(getActivity(), this)
                .search(getArguments().getString("query"), searchOptions);
    }
}
