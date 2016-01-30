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
    private ArrayList<DerpibooruFilter> mFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setTitle(R.string.activity_filters);
        initializeNavigationDrawer();

        if (savedInstanceState != null) {
            mFilters = savedInstanceState.getParcelableArrayList("filters");
            if (mFilters != null) {
                displayFilters();
            } else {
                fetchAvailableFilters();
            }
        } else {
            fetchAvailableFilters();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("filters", mFilters);
    }

    private void fetchAvailableFilters() {
        Filters f = new Filters(this, new Filters.FiltersHandler() {
            @Override
            public void onAvailableFiltersFetched(ArrayList<DerpibooruFilter> filters) {
                mFilters = filters;
                displayFilters();
            }
        });
        f.fetchAvailableFilters();
    }

    private void displayFilters() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        FiltersViewAdapter fva = new FiltersViewAdapter(mFilters);

        RecyclerView view = ((RecyclerView) findViewById(R.id.viewFilters));
        view.setLayoutManager(llm);
        view.setAdapter(fva);
    }
}
