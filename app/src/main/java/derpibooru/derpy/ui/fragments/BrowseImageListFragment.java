package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.providers.SearchProvider;

public class BrowseImageListFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        super.initializeList(
                new SearchProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler())
                        .searching((DerpibooruSearchOptions)
                                           getArguments().getParcelable(BrowseFragment.EXTRAS_SEARCH_OPTIONS)));
        return v;
    }

    public enum Type {
        NewImages(0),
        UserWatched(1),
        UserFaved(2),
        UserUpvoted(3),
        UserUploaded(4);

        private int mValue;

        Type(int value) {
            mValue = value;
        }

        public static Type fromValue(int value) {
            for (Type type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return NewImages;
        }

        public int toValue() {
            return mValue;
        }
    }
}
