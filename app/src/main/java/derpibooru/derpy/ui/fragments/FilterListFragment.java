package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.FilterListProvider;
import derpibooru.derpy.server.requesters.FilterChangeRequester;
import derpibooru.derpy.ui.adapters.FilterListAdapter;
import derpibooru.derpy.ui.views.AccentColorProgressBar;

public class FilterListFragment extends UserFragment {
    private static final String BUNDLE_FILTER_LIST = "FilterList";

    private ArrayList<DerpibooruFilter> mAvailableFilterList;
    private FilterListProvider mFilterListProvider;
    private OnFilterChangeListener mFilterChangeListener;

    @Bind(R.id.viewFilterList) RecyclerView mFilterListView;
    @Bind(R.id.progressFilterList) AccentColorProgressBar mProgressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter_list, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelableArrayList(BUNDLE_FILTER_LIST) != null)) {
            mAvailableFilterList = savedInstanceState.getParcelableArrayList(BUNDLE_FILTER_LIST);
            displayFilters();
        } else {
            fetchAvailableFilters();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_FILTER_LIST, mAvailableFilterList);
    }

    @Override
    protected void onUserRefreshed(DerpibooruUser user) {
        fetchAvailableFilters();
    }

    public void setOnFilterChangeListener(OnFilterChangeListener listener) {
        mFilterChangeListener = listener;
    }

    private void fetchAvailableFilters() {
        mFilterListView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
        if (mFilterListProvider == null) {
            initializeFilterListProvider();
        }
        mFilterListProvider.fetch();
    }

    private void displayFilters() {
        if (mFilterListView.getAdapter() != null) {
            ((FilterListAdapter) mFilterListView.getAdapter())
                    .replaceFilters(mAvailableFilterList, getUser().getCurrentFilter());
        } else {
            mFilterListView.setLayoutManager(new LinearLayoutManager(getContext()));
            initializeFilterListAdapter();
        }
        mProgressView.setVisibility(View.GONE);
        mFilterListView.setVisibility(View.VISIBLE);
    }

    private void initializeFilterListAdapter() {
        FilterListAdapter fva =
                new FilterListAdapter(getUser().getCurrentFilter(), mAvailableFilterList,
                                      new FilterListAdapter.FiltersViewHandler() {
                                          @Override
                                          public void changeFilterTo(DerpibooruFilter newFilter) {
                                              setCurrentFilter(newFilter);
                                          }
                                      });
        mFilterListView.setAdapter(fva);
    }

    private void initializeFilterListProvider() {
        mFilterListProvider = new FilterListProvider(getContext(), new QueryHandler<List<DerpibooruFilter>>() {
            @Override
            public void onQueryExecuted(List<DerpibooruFilter> filters) {
                mAvailableFilterList = (ArrayList<DerpibooruFilter>) filters;
                displayFilters();
            }

            @Override
            public void onQueryFailed() {
                Snackbar.make(mFilterListView, R.string.activity_filters_failed_to_fetch_list, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchAvailableFilters();
                            }
                        }).show();
            }
        });
    }

    private void setCurrentFilter(DerpibooruFilter newFilter) {
        new FilterChangeRequester(getContext(), new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
                mFilterChangeListener.onFilterChanged();
            }

            @Override
            public void onQueryFailed() {
                Snackbar.make(mFilterListView, R.string.activity_filters_failed_to_change_filter, Snackbar.LENGTH_SHORT).show();
            }
        }, newFilter).fetch();
    }

    public interface OnFilterChangeListener {
        void onFilterChanged();
    }
}
