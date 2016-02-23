package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.providers.SearchProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class SearchResultTabFragment extends ImageListFragment {
    private static final String SEARCH_OPTIONS_BUNDLE_KEY = "options";

    private DerpibooruSearchOptions mCurrentOptions;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SEARCH_OPTIONS_BUNDLE_KEY, mCurrentOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentOptions = (DerpibooruSearchOptions)
                ((savedInstanceState == null) ? new DerpibooruSearchOptions()
                                              : savedInstanceState.getParcelable(SEARCH_OPTIONS_BUNDLE_KEY));
        super.setImageListProvider(new SearchProvider(getActivity(), new ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImages() {
        ((SearchProvider) super.getImageListProvider())
                .searching(getArguments().getString("query"))
                .with(mCurrentOptions)
                .fetch();
    }

    public void setSearchOptions(DerpibooruSearchOptions newOptions) {
        if (!mCurrentOptions.equals(newOptions)) {
            /* TODO: look deeper into Parcelable's behavior
             * Apparently, Parcelable does not always create a deep copy of an object,
             * which is required here (in order for comparison to work) */
            mCurrentOptions = DerpibooruSearchOptions.copyFrom(newOptions);

            ((SearchProvider) super.getImageListProvider())
                    .with(mCurrentOptions);
            super.refreshImages();
        }
    }
}
