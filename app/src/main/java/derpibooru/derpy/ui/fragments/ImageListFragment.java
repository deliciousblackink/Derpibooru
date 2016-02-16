package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.ui.adapters.ImageListAdapter;
import derpibooru.derpy.ui.views.ImageListRecyclerView;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;

public abstract class ImageListFragment extends Fragment {
    private ImageListAdapter mImageListAdapter;
    private ImageListProvider mImageListProvider;
    private ImageListRecyclerView mImageView;
    private SwipeRefreshLayout mImageRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_list_tab, container, false);
        mImageView = (ImageListRecyclerView) v.findViewById(R.id.viewImages);
        mImageRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.layoutImageRefresh);
        mImageRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mImageRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshImages();
            }
        });
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

    protected void resetImageListAdapter() {
        mImageListAdapter = null;
    }

    protected abstract void fetchImageThumbs();

    protected void refreshImages() {
        mImageRefreshLayout.setRefreshing(true); /* in case the method was called by a subclass */
        mImageListProvider.resetPageNumber().fetch();
    }

    private void displayImagesFromProvider(ArrayList<DerpibooruImageThumb> imageThumbs) {
        if (mImageListAdapter == null) {
            mImageListAdapter = new ImageListAdapter(getActivity(), imageThumbs);
            mImageView.setAdapter(mImageListAdapter);
            mImageView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                    (GridLayoutManager) mImageView.getLayoutManager()) {
                @Override
                public void onLoadMore(int page) {
                    mImageListProvider.nextPage().fetch();
                }
            });
        } else if (mImageRefreshLayout.isRefreshing()) {
            mImageListAdapter.resetImageThumbs(imageThumbs);
            mImageRefreshLayout.setRefreshing(false);
        } else {
            mImageListAdapter.appendImageThumbs(imageThumbs);
        }
    }

    protected class ImageListRequestHandler implements QueryHandler {
        @Override
        public void onQueryExecuted(Object result) {
            displayImagesFromProvider((ArrayList<DerpibooruImageThumb>) result);
        }

        @Override
        public void onQueryFailed() {
            /* TODO: display error message */
        }
    }
}
