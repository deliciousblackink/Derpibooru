package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.providers.SearchProvider;

public class SearchResultTabFragment extends ImageListFragment {
    private DerpibooruSearchOptions mCurrentOptions;

    public SearchResultTabFragment() {
        super();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("options", mCurrentOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentOptions = savedInstanceState.getParcelable("options");
        } else {
            mCurrentOptions = new DerpibooruSearchOptions();
        }
        super.setImageListProvider(new SearchProvider(getActivity(), new ImageListRequestHandler()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void fetchImageThumbs() {
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
