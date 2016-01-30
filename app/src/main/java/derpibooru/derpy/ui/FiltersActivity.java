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
    private Filters mFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setTitle(R.string.activity_filters);
        initializeNavigationDrawer();

        fetchAvailableFilters();
    }

    private void fetchAvailableFilters() {
        mFilters = new Filters(this, new Filters.FiltersHandler() {
            @Override
            public void onAvailableFiltersFetched(ArrayList<DerpibooruFilter> filters) {
                displayFilters(filters);
            }
        });
        mFilters.fetchAvailableFilters();
    }

    private void displayFilters(ArrayList<DerpibooruFilter> filters) {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        FiltersViewAdapter fva = new FiltersViewAdapter(filters);

        RecyclerView view = ((RecyclerView) findViewById(R.id.viewFilters));
        view.setLayoutManager(llm);
        view.setAdapter(fva);
    }
}
