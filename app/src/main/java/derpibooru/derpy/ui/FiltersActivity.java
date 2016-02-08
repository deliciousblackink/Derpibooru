package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.Filters;
import derpibooru.derpy.ui.adapters.FiltersViewAdapter;

public class FiltersActivity extends NavigationDrawerActivity {
    private ArrayList<DerpibooruFilter> mAvailableFilterList;
    private RecyclerView mViewFilters;
    private Filters mFilterActions;

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
    protected void onUserDataRefreshed() {
        fetchAvailableFilters();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("filters", mAvailableFilterList);
    }

    private void fetchAvailableFilters() {
        if (mFilterActions == null) {
            initFilterActions();
        }
        mFilterActions.fetchAvailableFilters();
    }

    private void setCurrentFilter(DerpibooruFilter newFilter) {
        if (mFilterActions == null) {
            initFilterActions();
        }
        mFilterActions.changeCurrentFilterTo(newFilter);
    }

    private void initFilterActions() {
        mFilterActions = new Filters(this, new Filters.FiltersRequestHandler() {
            @Override
            public void onAvailableFiltersFetched(ArrayList<DerpibooruFilter> filters) {
                mAvailableFilterList = filters;
                displayFilters();
            }

            @Override
            public void onFilterChangedSuccessfully() {
                FiltersActivity.super.refreshUserData();
            }

            @Override
            public void onNetworkError() {
                /* TODO: handle network errors */
            }
        });
    }

    private void displayFilters() {
        if (mViewFilters.getAdapter() == null) {
            FiltersViewAdapter fva =
                    new FiltersViewAdapter(mFilterActions.getCurrentFilter(), mAvailableFilterList,
                                           new FiltersViewAdapter.FiltersViewHandler() {
                                               @Override
                                               public void changeFilterTo(DerpibooruFilter newFilter) {
                                                   setCurrentFilter(newFilter);
                                               }
                                           });
            mViewFilters.setAdapter(fva);
        } else {
            ((FiltersViewAdapter) mViewFilters.getAdapter())
                    .replaceFilters(mAvailableFilterList, mFilterActions.getCurrentFilter());
        }
    }
}
