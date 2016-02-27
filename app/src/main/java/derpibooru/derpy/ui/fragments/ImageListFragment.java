package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImage;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.MainActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;
import derpibooru.derpy.ui.views.ImageListRecyclerView;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;

public abstract class ImageListFragment extends UserFragment {
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;

    private ImageListAdapter mImageListAdapter;
    private ImageListProvider mImageListProvider;

    @Bind(R.id.layoutImageRefresh) SwipeRefreshLayout mImageRefreshLayout;
    @Bind(R.id.viewImages) ImageListRecyclerView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_list, container, false);
        ButterKnife.bind(this, v);
        ((SimpleItemAnimator) mImageView.getItemAnimator()).setSupportsChangeAnimations(false); /* disable item change animations for image interactions */
        initializeImageRefreshLayout();
        if (mImageListProvider != null) {
            /* reset the adapter in case the fragment was restored from a FragmentManager backstack, otherwise it doesn't show anything */
            /* TODO: instead of resetting the adapter, find a way to reuse it */
            mImageListAdapter = null;
            fetchImages();
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

    protected abstract void fetchImages();

    @Override
    protected void onUserRefreshed(DerpibooruUser user) {
        if (mImageListAdapter == null) {
            return;
        }
        if ((user.isLoggedIn() != getUser().isLoggedIn())
                || (!user.getCurrentFilter().equals(getUser().getCurrentFilter()))) {
            refreshImages();
        }
    }

    protected void refreshImages() {
        mImageRefreshLayout.setRefreshing(true); /* in case the method was called by a subclass */
        mImageListProvider.resetPageNumber().fetch();
    }

    private void displayImagesFromProvider(List<DerpibooruImage> images) {
        if (mImageListAdapter == null) {
            initializeImageListAdapter(images);
            mImageRefreshLayout.setRefreshing(false);
        } else if (mImageRefreshLayout.isRefreshing()) {
            mImageListAdapter.resetItems(images, getUser().isLoggedIn());
            mImageRefreshLayout.setRefreshing(false);
        } else {
            mImageListAdapter.appendItems(images);
        }
    }

    private void initializeImageRefreshLayout() {
        mImageRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mImageRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshImages();
            }
        });
        /* show progress animation for intial image loading
         * ("why post a Runnable?" -> http://stackoverflow.com/a/26910973/1726690) */
        mImageRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mImageRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void initializeImageListAdapter(List<DerpibooruImage> images) {
        mImageListAdapter = new ImageListAdapter(getActivity(), images, getUser().isLoggedIn()) {
            @Override
            public void startImageActivity(DerpibooruImage image) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra(ImageActivity.EXTRAS_IMAGE, image);
                intent.putExtra(MainActivity.EXTRAS_USER, getUser());
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (IMAGE_ACTIVITY_REQUEST_CODE):
                if (mImageListAdapter != null) {
                    mImageListAdapter.replaceItem(
                            (DerpibooruImage) data.getParcelableExtra(ImageActivity.EXTRAS_IMAGE));
                }
                break;
        }
    }

    public class ImageListRequestHandler implements QueryHandler<List<DerpibooruImage>> {
        @Override
        public void onQueryExecuted(List<DerpibooruImage> result) {
            displayImagesFromProvider(result);
        }

        @Override
        public void onQueryFailed() {
            if (mImageView != null) {
                Snackbar.make(mImageView, R.string.fragment_image_list_failed_to_fetch_list, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchImages();
                            }
                        }).show();
            }
        }
    }
}
