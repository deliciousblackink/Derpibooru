package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.FilterListProvider;
import derpibooru.derpy.server.requesters.FilterChangeRequester;
import derpibooru.derpy.ui.adapters.FiltersViewAdapter;

public class FiltersActivity extends NavigationDrawerActivity {
    private ArrayList<DerpibooruFilter> mAvailableFilterList;
    private FilterListProvider mFilterListProvider;

    private RecyclerView mViewFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setTitle(R.string.activity_filters);
        super.initializeNavigationDrawer();

        mViewFilters = (RecyclerView) findViewById(R.id.viewFilters);
        mViewFilters.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null
                && savedInstanceState.getParcelableArrayList("filters") != null) {
            mAvailableFilterList = savedInstanceState.getParcelableArrayList("filters");
            displayFilters();
        } else {
            fetchAvailableFilters();
        }
    }

    @Override
    public void onUserDataRefreshed() {
        fetchAvailableFilters();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("filters", mAvailableFilterList);
    }

    private void fetchAvailableFilters() {
        if (mFilterListProvider == null) {
            initFilterListProvider();
        }
        mFilterListProvider.fetch();
    }

    private void setCurrentFilter(DerpibooruFilter newFilter) {
        new FilterChangeRequester(this, new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
                FiltersActivity.super.refreshUserData();
            }

            @Override
            public void onQueryFailed() {
                Snackbar.make(mViewFilters, R.string.activity_filters_failed_to_change_filter, Snackbar.LENGTH_SHORT).show();
            }
        }, newFilter).fetch();
    }

    private void initFilterListProvider() {
        mFilterListProvider = new FilterListProvider(this, new QueryHandler<List<DerpibooruFilter>>() {
            @Override
            public void onQueryExecuted(List<DerpibooruFilter> filters) {
                mAvailableFilterList = (ArrayList<DerpibooruFilter>) filters;
                displayFilters();
            }

            @Override
            public void onQueryFailed() {
                Snackbar.make(mViewFilters, R.string.activity_filters_failed_to_fetch_list, Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchAvailableFilters();
                            }
                        }).show();
            }
        });
    }

    private void displayFilters() {
        if (mViewFilters.getAdapter() == null) {
            FiltersViewAdapter fva =
                    new FiltersViewAdapter(mFilterListProvider.getCurrentFilter(), mAvailableFilterList,
                                           new FiltersViewAdapter.FiltersViewHandler() {
                                               @Override
                                               public void changeFilterTo(DerpibooruFilter newFilter) {
                                                   setCurrentFilter(newFilter);
                                               }
                                           });
            mViewFilters.setAdapter(fva);
        } else {
            ((FiltersViewAdapter) mViewFilters.getAdapter())
                    .replaceFilters(mAvailableFilterList, mFilterListProvider.getCurrentFilter());
        }
    }
}
