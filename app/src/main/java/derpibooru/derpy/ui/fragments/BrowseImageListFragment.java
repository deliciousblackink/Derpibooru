package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.providers.SearchProvider;
import derpibooru.derpy.ui.ImageActivity;

public class BrowseImageListFragment extends ImageListFragment {
    private TagSearchRequestListener mListener;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ImageListFragment.IMAGE_ACTIVITY_REQUEST_CODE)
                && (data != null) && (data.hasExtra(ImageActivity.EXTRAS_TAG_SEARCH_QUERY))) {
            if (mListener != null) {
                mListener.onTagSearchRequested(data.getStringExtra(ImageActivity.EXTRAS_TAG_SEARCH_QUERY));
            }
        }
    }

    public void setTagSearchRequestListener(TagSearchRequestListener listener) {
        mListener = listener;
    }

    public interface TagSearchRequestListener {
        void onTagSearchRequested(String tag);
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
