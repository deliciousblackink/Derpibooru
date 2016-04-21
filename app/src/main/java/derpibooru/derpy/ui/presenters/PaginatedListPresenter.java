package derpibooru.derpy.ui.presenters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.PaginatedListProvider;
import derpibooru.derpy.ui.adapters.RecyclerViewPaginationAdapter;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;

/**
 * Presents paginated lists with endless scroll and swipe-to-refresh functionality.
 * Couples {@link PaginatedListProvider} with a {@link RecyclerViewPaginationAdapter} operating on a RecyclerView
 * and a SwipeRefreshLayout set via constructor arguments.
 *
 * @param <TItem> type of an individual item of a list; must implement Parcelable
 */
public abstract class PaginatedListPresenter<TItem extends Parcelable> {
    private static final String EXTRAS_RECYCLER_VIEW_POSITION = "derpibooru.derpy.PaginatedRecyclerViewPosition";
    private static final String EXTRAS_PROVIDER_PAGE = "derpibooru.derpy.PaginatedListProviderPage";
    private static final String EXTRAS_PAGINATED_LIST = "derpibooru.derpy.PaginatedListItems";

    private PaginatedListProvider<TItem> mProvider;
    private RecyclerViewPaginationAdapter<TItem, ?> mAdapter;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mListView;

    /**
     * Sets UI views used to present a list.
     * <br>
     * Call {@link #initializeWithProvider(PaginatedListProvider)} to initialize the presenter and display the first page of a list.
     * <br>
     * Call {@link #initializeWithProvider(PaginatedListProvider, Bundle)} to recover the presenter from a saved instance state
     * ({@link #onSaveInstanceState(Bundle)} should be called beforehand to save the state).
     */
    public PaginatedListPresenter(@NonNull SwipeRefreshLayout refreshLayout,
                                  @NonNull RecyclerView listView) {
        mRefreshLayout = refreshLayout;
        mListView = listView;
        initializeRefreshLayout();
    }

    /**
     * Initializes the presenter with the provider specified and displays the first page of a list.
     * <br>
     * Specify {@link PaginatedListProviderHandler} as the provider's {@link QueryHandler} to enable the
     * presenter to handle pagination.
     */
    public void initializeWithProvider(PaginatedListProvider<TItem> provider) {
        mProvider = provider;
        fetchFirstPage();
    }

    /**
     * Initializes the presenter with the provider specified and restores the items displayed on a saved state.
     * <br>
     * Specify {@link PaginatedListProviderHandler} as the provider's {@link QueryHandler} to enable the
     * presenter to handle pagination.
     */
    public void initializeWithProvider(PaginatedListProvider<TItem> provider, @NonNull Bundle savedInstanceState) {
        mProvider = provider;
        restoreInstanceState(savedInstanceState);
    }

    /**
     * Resets the adapter by calling {@link #getNewInstanceOfListAdapter(List)} with the items from the first
     * page of a list.
     */
    public void resetAdapterAndRefreshList() {
        mAdapter = null;
        refreshList();
    }

    /**
     * Returns a new instance of list adapter, initialized with the items provided.
     */
    public abstract RecyclerViewPaginationAdapter<TItem, ?> getNewInstanceOfListAdapter(List<TItem> initialItems);

    /**
     * Returns the adapter currently in use.
     *
     * @return current adapter or {@code null} if the adapter is not instantiated yet.
     */
    @Nullable
    public RecyclerViewPaginationAdapter<TItem, ?> getAdapter() {
        return mAdapter;
    }

    /**
     * Refreshes the list. Call this method in conjunction with {@link #getListProvider()} to reset the list
     * with new query parameters.
     */
    public void refreshList() {
        mRefreshLayout.setRefreshing(true);
        fetchFirstPage();
    }

    /**
     * Returns the currently used instance of the list provider. Use this method to change query
     * parameters on user input.
     * <br>
     * Consider an example:
     * <pre>{@code
     * ((MyImageListProvider) super.getImageListProvider()).setUserInput(args);
     * super.refreshList();
     * }</pre>
     */
    public PaginatedListProvider<TItem> getListProvider() {
        return mProvider;
    }

    /**
     * Saves navigation information (current page & items displayed). Call this method on configuration change,
     * and later initialize the presenter providing the 'savedInstanceState' bundle.
     */
    public void onSaveInstanceState(Bundle outState) {
        if ((mListView != null) && (mAdapter != null)) {
            int itemPosition = ((LinearLayoutManager) mListView.getLayoutManager()).findFirstVisibleItemPosition();
            outState.putInt(EXTRAS_RECYCLER_VIEW_POSITION, itemPosition);
            int page = mProvider.getCurrentPage();
            outState.putInt(EXTRAS_PROVIDER_PAGE, page);
            List<TItem> items = mAdapter.getItems();
            outState.putParcelableArrayList(EXTRAS_PAGINATED_LIST, (ArrayList) items);
        }
    }

    private void fetchFirstPage() {
        mProvider.resetPageNumber().fetch();
    }

    private void fetchNextPage() {
        mProvider.nextPage().fetch();
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(EXTRAS_PAGINATED_LIST)) {
            ArrayList<TItem> items = savedInstanceState.getParcelableArrayList(EXTRAS_PAGINATED_LIST);
            initializeListAdapter(items);
            mProvider.fromPage(savedInstanceState.getInt(EXTRAS_PROVIDER_PAGE));
            mListView.getLayoutManager().scrollToPosition(
                    savedInstanceState.getInt(EXTRAS_RECYCLER_VIEW_POSITION));
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            fetchFirstPage();
        }
    }

    /**
     * Creates an instance of {@link PaginatedListProvider} and adds an EndlessScrollListener
     * (that handles pagination) to the RecyclerView.
     *
     * @param initialItems initial adapter items
     */
    private void initializeListAdapter(List<TItem> initialItems) {
        mAdapter = getNewInstanceOfListAdapter(initialItems);
        mListView.setAdapter(mAdapter);
        mListView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                (LinearLayoutManager) mListView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page) {
                onEndlessScrollBottomReached();
            }
        });
    }

    protected void onEndlessScrollBottomReached() {
        fetchNextPage();
    }

    private void initializeRefreshLayout() {
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        /* show progress animation for intial image loading
         * ("why post a Runnable?" -> http://stackoverflow.com/a/26910973/1726690) */
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void displayItemsFromProvider(List<TItem> images) {
        if (mListView == null) {
            return; /* the view has been destroyed prior to async callback from provider */
        }
        if (mAdapter == null) {
            initializeListAdapter(images);
            mRefreshLayout.setRefreshing(false);
        } else if (mRefreshLayout.isRefreshing()) {
            mAdapter.resetItems(images);
            mRefreshLayout.setRefreshing(false);
        } else {
            mAdapter.appendItems(images);
        }
    }

    public class PaginatedListProviderHandler implements QueryHandler<List<TItem>> {
        @Override
        public void onQueryExecuted(List<TItem> result) {
            displayItemsFromProvider(result);
        }

        @Override
        public void onQueryFailed() {
            if (mListView != null) {
                Snackbar.make(mListView, R.string.paginated_list_presenter_failed_to_fetch_list, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /* request the same page as before */
                                mProvider.fetch();
                            }
                        }).show();
            }
        }
    }
}
