package derpibooru.derpy.ui.fragments;

import derpibooru.derpy.server.SearchResultProvider;

public class SearchResultTabFragment extends ImageListTabFragment {
    public SearchResultTabFragment() {
        super();
    }

    @Override
    protected void loadImages()
    {
        SearchResultProvider provider = new SearchResultProvider(getActivity(), this);
        provider.search(getArguments().getString("query"));
    }
}
