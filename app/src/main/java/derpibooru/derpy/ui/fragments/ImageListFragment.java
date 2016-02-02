package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.ImageListProvider;
import derpibooru.derpy.server.ProviderRequestHandler;
import derpibooru.derpy.ui.adapters.ImageListAdapter;
import derpibooru.derpy.ui.views.ImageListRecyclerView;

public abstract class ImageListFragment extends Fragment {
    private ImageListAdapter mImageListAdapter;
    private ImageListRecyclerView mImageView;
    private ImageListProvider mImageListProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_list_tab, container, false);
        mImageView = (ImageListRecyclerView) v.findViewById(R.id.viewImages);
        if (mImageListProvider != null) {
            fetchImageThumbs();
        } else {
            Log.e("ImageListFragment", "call setImageListProvider before super.onCreateView");
        }
        return v;
    }

    protected void setImageListProvider(ImageListProvider provider) {
        mImageListProvider = provider;
    }

    protected ImageListProvider getImageListProvider() {
        return mImageListProvider;
    }

    protected class ImageListRequestHandler implements ProviderRequestHandler {
        @Override
        public void onRequestCompleted(Object result) {
            displayImagesFromProvider((ArrayList<DerpibooruImageThumb>) result);
        }

        @Override
        public void onRequestFailed() {
            /* TODO: display error message */
        }
    }

    protected abstract void fetchImageThumbs();

    protected void resetImageListAdapter() {
        mImageListAdapter = null;
    }

    private void displayImagesFromProvider(ArrayList<DerpibooruImageThumb> imageThumbs) {
        if (mImageListAdapter == null) {
            mImageListAdapter = new ImageListAdapter(getActivity(), imageThumbs);
            mImageView.setAdapter(mImageListAdapter);
            mImageView.addOnScrollListener(new EndlessScrollListener(
                    (GridLayoutManager) mImageView.getLayoutManager()) {
                @Override
                public void onLoadMore(int page) {
                    mImageListProvider.nextPage().fetch();
                }
            });
        } else {
            mImageListAdapter.appendImageThumbs(imageThumbs);
        }
    }

    /**
     * @author https://gist.github.com/rogerhu/17aca6ad4dbdb3fa5892
     */
    private abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
        private final int ITEMS_LEFT_TO_START_LOADING_MORE = 4;
        private final int START_PAGE_INDEX = 0;

        private int mCurrentPage = 0;
        private int mPreviousTotalItemCount = 0;
        private boolean mIsLoading = true;

        private GridLayoutManager mLayoutManager;

        public EndlessScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            int totalItemCount = mLayoutManager.getItemCount();

            /* if the total item count is zero and the previous isn't, assume the
             * list is invalidated and should be reset back to initial state */
            if (totalItemCount < mPreviousTotalItemCount) {
                mCurrentPage = START_PAGE_INDEX;
                mPreviousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    mIsLoading = true;
                }
            }

            /* if it’s still loading, we check to see if the dataset count has
             * changed, if so we conclude it has finished loading and update the current page
             * number and total item count. */
            if (mIsLoading && (totalItemCount > mPreviousTotalItemCount)) {
                mIsLoading = false;
                mPreviousTotalItemCount = totalItemCount;
            }

            /* if it isn’t currently loading, we check to see if we need to reload more data.
             * If we do need to reload some more data, we execute onLoadMore to fetch the data. */
            if (!mIsLoading && totalItemCount <= (lastVisibleItem + ITEMS_LEFT_TO_START_LOADING_MORE)) {
                mCurrentPage++;
                onLoadMore(mCurrentPage);
                mIsLoading = true;
            }
        }

        public abstract void onLoadMore(int page);
    }
}
