package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.server.providers.SearchProvider;
import derpibooru.derpy.ui.SearchResultActivity;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class SearchResultTabFragment extends ImageListFragment {
    private static final String EXTRAS_OPTIONS = "options";

    private DerpibooruSearchOptions mCurrentOptions;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRAS_OPTIONS, mCurrentOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentOptions = (DerpibooruSearchOptions)
                ((savedInstanceState == null) ? new DerpibooruSearchOptions()
                                              : savedInstanceState.getParcelable(EXTRAS_OPTIONS));
        View v = super.onCreateView(inflater, container, savedInstanceState);
        resetList();
        return v;
    }

    private void resetList() {
        super.initializeList(
                new SearchProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler())
                        .searching(getArguments().getString(SearchResultActivity.EXTRAS_SEARCH_QUERY))
                        .with(mCurrentOptions));
    }

    public void setSearchOptions(DerpibooruSearchOptions newOptions) {
        if (!mCurrentOptions.equals(newOptions)) {
            /* TODO: look deeper into Parcelable's behavior
             * Apparently, Parcelable does not always create a deep copy of an object,
             * which is required here (in order for comparison to work) */
            mCurrentOptions = DerpibooruSearchOptions.copyFrom(newOptions);
            resetList();
        }
    }
}
