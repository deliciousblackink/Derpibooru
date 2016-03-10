package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import derpibooru.derpy.ui.adapters.RecyclerViewPaginationAdapter;
import derpibooru.derpy.ui.presenters.PaginatedListPresenter;
import derpibooru.derpy.ui.views.ImageListRecyclerView;

public abstract class ImageListFragment extends NavigationDrawerUserFragment {
    public static final String EXTRAS_IMAGE_THUMB = "derpibooru.derpy.Image";
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;

    private PaginatedListPresenter<DerpibooruImageThumb> mImageListPresenter;
    private Bundle mSavedInstanceState;

    @Bind(R.id.layoutImageRefresh) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.viewImages) ImageListRecyclerView recyclerView;

    /**
     * Returns an inflated image list view.
     * <br>
     * Call {@link #initializeList(ImageListProvider)} to initialize the list view and display the first page of a list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) throws IllegalStateException {
        View v = inflater.inflate(R.layout.fragment_image_list, container, false);
        ButterKnife.bind(this, v);
        /* disable item change animations for image interactions */
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mSavedInstanceState = savedInstanceState;
        mImageListPresenter = new PaginatedListPresenter<DerpibooruImageThumb>(refreshLayout, recyclerView) {
            @Override
            public RecyclerViewPaginationAdapter<DerpibooruImageThumb, ?> getNewInstanceOfListAdapter(List<DerpibooruImageThumb> initialItems) {
                return getNewInstanceOfImageListAdapter(initialItems);
            }
        };
        return v;
    }

    /**
     * Initializes the list view and displays the first page of a list. Call {@link #getNewInstanceOfProviderQueryHandler()}
     * to obtain an instance of {@link QueryHandler} that needs to be passed to the provider.
     */
    protected void initializeList(ImageListProvider provider) {
        if (mSavedInstanceState == null) {
            mImageListPresenter.initializeWithProvider(provider);
        } else {
            mImageListPresenter.initializeWithProvider(provider, mSavedInstanceState);
        }
    }

    /**
     * Returns a new instance of a {@link QueryHandler} to be passed to the list provider.
     */
    protected QueryHandler<List<DerpibooruImageThumb>> getNewInstanceOfProviderQueryHandler() {
        return mImageListPresenter.new PaginatedListProviderHandler();
    }

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
        if ((recyclerView == null) || (recyclerView.getAdapter() == null)) {
            return;
        }
        if (user.isLoggedIn() != getUser().isLoggedIn()) {
            /* reset the adapter to set image interactions according to the user auth status */
            mImageListPresenter.resetAdapterAndRefreshList();
        } else if (!user.getCurrentFilter().equals(getUser().getCurrentFilter())) {
            mImageListPresenter.refreshList();
        }
    }

    private ImageListAdapter getNewInstanceOfImageListAdapter(List<DerpibooruImageThumb> initialItems) {
        return new ImageListAdapter(getActivity(), initialItems, getUser().isLoggedIn()) {
            @Override
            public void startImageActivity(DerpibooruImageThumb image) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra(EXTRAS_IMAGE_THUMB, image);
                intent.putExtra(MainActivity.EXTRAS_USER, getUser());
                startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
            }
        };
    }

    /**
     * Retrieves updated image information (image interactions) from the ImageActivity and passes it to the adapter.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (IMAGE_ACTIVITY_REQUEST_CODE):
                if ((recyclerView != null) && (recyclerView.getAdapter() != null) && (data != null)) {
                    ((ImageListAdapter) recyclerView.getAdapter()).replaceItem(
                            (DerpibooruImageThumb) data.getParcelableExtra(EXTRAS_IMAGE_THUMB));
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageListPresenter != null) {
            mImageListPresenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
