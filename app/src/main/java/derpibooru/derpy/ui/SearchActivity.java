package derpibooru.derpy.ui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.FloatingSearchView;

public class SearchActivity extends NavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.activity_search);
        initializeNavigationDrawer();

        FloatingSearchView search = (FloatingSearchView) findViewById(R.id.floatingSearch);
        search.setSearchResultActivity(SearchResultActivity.class);
        search.setOnLayoutSizeChangedListener(new FloatingSearchView.OnLayoutSizeChangedListener() {
            @Override
            public void onSizeChanged() {
                findViewById(R.id.topLayout).requestLayout();
            }
        });

        ((TextView) findViewById(R.id.textSearchHelp))
                .setText(Html.fromHtml(getString(R.string.search_help)));
    }

    @Override
    public void onUserDataRefreshed() { }
}
