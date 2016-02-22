package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;
import derpibooru.derpy.ui.views.ImageListRecyclerView;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;

public abstract class ImageListFragment extends Fragment {
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private ImageListAdapter mImageListAdapter;
    private ImageListProvider mImageListProvider;
    private ImageListRecyclerView mImageView;
    private SwipeRefreshLayout mImageRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_list_tab, container, false);
        mImageView = (ImageListRecyclerView) v.findViewById(R.id.viewImages);
        /* disable item change animations for image interactions */
        ((SimpleItemAnimator) mImageView.getItemAnimator()).setSupportsChangeAnimations(false);
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

    protected ImageListProvider getImageListProvider() {
        return mImageListProvider;
    }

    protected void setImageListProvider(ImageListProvider provider) {
        mImageListProvider = provider;
    }

    protected abstract void fetchImageThumbs();

    protected void refreshImages() {
        mImageRefreshLayout.setRefreshing(true); /* in case the method was called by a subclass */
        mImageListProvider.resetPageNumber().fetch();
    }

    private void displayImagesFromProvider(ArrayList<DerpibooruImageThumb> imageThumbs) {
        if (mImageListAdapter == null) {
            initializeImageListAdapter(imageThumbs);
        } else if (mImageRefreshLayout.isRefreshing()) {
            mImageListAdapter.resetItems(imageThumbs);
            mImageRefreshLayout.setRefreshing(false);
        } else {
            mImageListAdapter.appendItems(imageThumbs);
        }
    }

    private void initializeImageListAdapter(ArrayList<DerpibooruImageThumb> imageThumbs) {
        mImageListAdapter = new ImageListAdapter(getActivity(), imageThumbs) {
            @Override
            public void startImageActivityWithThumb(DerpibooruImageThumb thumb) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra(ImageActivity.INTENT_EXTRA_IMAGE_THUMB, thumb);
                startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
            }
        };
        mImageView.setAdapter(mImageListAdapter);
        mImageView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                (GridLayoutManager) mImageView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page) {
                mImageListProvider.nextPage().fetch();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (IMAGE_ACTIVITY_REQUEST_CODE):
                if (mImageListAdapter != null) {
                    mImageListAdapter.replaceImageThumb(
                            (DerpibooruImageThumb) data.getParcelableExtra(ImageActivity.INTENT_EXTRA_IMAGE_THUMB));
                }
                break;
        }
    }

    protected class ImageListRequestHandler implements QueryHandler<List<DerpibooruImageThumb>> {
        @Override
        public void onQueryExecuted(List<DerpibooruImageThumb> result) {
            displayImagesFromProvider((ArrayList<DerpibooruImageThumb>) result);
        }

        @Override
        public void onQueryFailed() {
            Snackbar.make(mImageView, R.string.fragment_image_list_failed_to_fetch_list, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_action_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fetchImageThumbs();
                        }
                    }).show();
        }
    }
}
