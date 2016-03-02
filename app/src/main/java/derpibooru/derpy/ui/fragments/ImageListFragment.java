package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.MainActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;
import derpibooru.derpy.ui.views.ImageListRecyclerView;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;

public abstract class ImageListFragment extends NavigationDrawerUserFragment {
    private static final String EXTRAS_RECYCLER_VIEW_POSITION = "derpibooru.derpy.ImageListRecyclerViewPosition";
    private static final String EXTRAS_PROVIDER_PAGE = "derpibooru.derpy.ImageListProviderPage";
    private static final String EXTRAS_IMAGE_LIST = "derpibooru.derpy.ImageListItems";

    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;

    private ImageListAdapter mImageListAdapter;
    private ImageListProvider mImageListProvider;

    @Bind(R.id.layoutImageRefresh) SwipeRefreshLayout mImageRefreshLayout;
    @Bind(R.id.viewImages) ImageListRecyclerView mImageView;

    /**
     * Returns an inflated image list view.
     * <br>
     * <strong>Warning:</strong> make sure you call {@link #setImageListProvider(ImageListProvider)} before this.
     *
     * @throws IllegalStateException the method was called before an ImageListProvider has been set
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) throws IllegalStateException {
        View v = inflater.inflate(R.layout.fragment_image_list, container, false);
        ButterKnife.bind(this, v);
        initializeImageRefreshLayout();
        /* disable item change animations for image interactions */
        ((SimpleItemAnimator) mImageView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (mImageListProvider == null) {
            throw new IllegalStateException("ImageListFragment: call 'setImageListProvider(ImageListProvider) before 'onCreateView'");
        }
        /* reset the adapter in case the fragment was restored from a FragmentManager backstack, otherwise it doesn't show anything */
        mImageListAdapter = null;
        if (savedInstanceState != null) {
            restoreImages(savedInstanceState);
        } else {
            fetchFirstPage();
        }
        return v;
    }

    /**
     * Sets a subclass of ImageListProvider for the image list. Call this method <strong>before</strong>
     * getting the inflated view from {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}. Do not
     * configure the provider with any paramters that may change during the runtime (see {@link #getImageListProviderWithParameters(ImageListProvider)})
     * <br>
     * <strong>Note:</strong> pass an instance of {@link ImageListRequestHandler} to the provider.
     */
    protected void setImageListProvider(ImageListProvider provider) {
        mImageListProvider = provider;
    }

    /**
     * Returns an ImageListProvider configured with additional parameters that are set <strong>depending on the user input.</strong>
     * Do not set parameters from Fragment's {@link #getArguments()} as the method may be called after a configuration change, after which
     * the argument Bundle may be null. See {@link #setImageListProvider(ImageListProvider)}.
     * <br>
     * Consider an example:
     * <pre>{@code      @Override
     * protected void getImageListProviderWithParameters(ImageListProvider target) {
     *     return ((MyImageListProvider) super.getImageListProvider()).withArguments(args);
     * }}</pre>
     *
     * @param target ImageListProvider to configure
     * @return a configured ImageListProvider
     */
    protected abstract ImageListProvider getImageListProviderWithParameters(ImageListProvider target);

    /**
     * Refreshes the image list if:
     * <ul>
     * <li>the user has logged in/out</li>
     * <li>the user has changed the filter</li>
     * </ul>
     * Does nothing otherwise.
     *
     * @param user updated user data
     */
    @Override
    protected void onUserRefreshed(DerpibooruUser user) {
        if (mImageListAdapter == null) {
            return;
        }
        if (user.isLoggedIn() != getUser().isLoggedIn()) {
            /* reset the adapter to ensure image interactions are set according to the authentication status */
            mImageListAdapter = null;
            refreshImages();
        } else if (!user.getCurrentFilter().equals(getUser().getCurrentFilter())) {
            refreshImages();
        }
    }

    /**
     * Requests the provider to fetch the first page of an image list. Here you can pass additional parameters to the
     * ImageListProvider depending on the user input:
     */
    private void fetchFirstPage() {
        getImageListProviderWithParameters(mImageListProvider).resetPageNumber().fetch();
    }

    /**
     * Requests the provider to fetch the next page of an image list.
     */
    private void fetchNextPage() {
        getImageListProviderWithParameters(mImageListProvider).nextPage().fetch();
    }

    protected void refreshImages() {
        mImageRefreshLayout.setRefreshing(true); /* in case the method was called by a subclass */
        fetchFirstPage();
    }

    private void displayImagesFromProvider(List<DerpibooruImageThumb> images) {
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

    /**
     * Creates an instance of {@link ImageListAdapter} and adds an EndlessScrollListener to the RecyclerView
     * that handles pagination.
     *
     * @param images initial adapter items
     */
    private void initializeImageListAdapter(List<DerpibooruImageThumb> images) {
        mImageListAdapter = new ImageListAdapter(getActivity(), images, getUser().isLoggedIn()) {
            @Override
            public void startImageActivity(DerpibooruImageThumb image) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra(ImageActivity.EXTRAS_IMAGE_THUMB, image);
                intent.putExtra(MainActivity.EXTRAS_USER, getUser());
                startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
            }
        };
        mImageView.setAdapter(mImageListAdapter);
        mImageView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                (GridLayoutManager) mImageView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page) {
                fetchNextPage();
            }
        });
    }

    /**
     * Retrieves updated image information (image interactions) from the ImageActivity and passes it to the adapter.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (IMAGE_ACTIVITY_REQUEST_CODE):
                if (mImageListAdapter != null) {
                    mImageListAdapter.replaceItem(
                            (DerpibooruImageThumb) data.getParcelableExtra(ImageActivity.EXTRAS_IMAGE_THUMB));
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ((mImageView != null) && (mImageListAdapter != null)) {
            int itemPosition = ((LinearLayoutManager) mImageView.getLayoutManager()).findFirstVisibleItemPosition();
            int page = mImageListProvider.getCurrentPage();
            List<DerpibooruImageThumb> items = mImageListAdapter.getItems();
            outState.putInt(EXTRAS_RECYCLER_VIEW_POSITION, itemPosition);
            outState.putInt(EXTRAS_PROVIDER_PAGE, page);
            outState.putParcelableArrayList(EXTRAS_IMAGE_LIST, (ArrayList) items);
        }
    }

    private void restoreImages(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(EXTRAS_IMAGE_LIST)) {
            List<DerpibooruImageThumb> images = savedInstanceState.getParcelableArrayList(EXTRAS_IMAGE_LIST);
            mImageListProvider.fromPage(
                    savedInstanceState.getInt(EXTRAS_PROVIDER_PAGE));
            initializeImageListAdapter(images);
            mImageView.getLayoutManager().scrollToPosition(
                    savedInstanceState.getInt(EXTRAS_RECYCLER_VIEW_POSITION));
            mImageRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mImageRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            fetchFirstPage();
        }
    }

    public class ImageListRequestHandler implements QueryHandler<List<DerpibooruImageThumb>> {
        @Override
        public void onQueryExecuted(List<DerpibooruImageThumb> result) {
            displayImagesFromProvider(result);
        }

        @Override
        public void onQueryFailed() {
            if (mImageView != null) {
                Snackbar.make(mImageView, R.string.fragment_image_list_failed_to_fetch_list, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /* request the same page as before */
                                getImageListProviderWithParameters(mImageListProvider).fetch();
                            }
                        }).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
