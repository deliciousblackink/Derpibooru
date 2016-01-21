package derpibooru.derpy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.SearchResultActivityTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

public class SearchResultActivity extends AppCompatActivity {
    /* TODO: should be a singleTop activity
     * http://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String searchQuery = getIntent().getStringExtra("query");
        setTitle(searchQuery);
        initTabPager(searchQuery);
    }

    private void initTabPager(String searchQuery) {
        FragmentTabPagerView pager = (FragmentTabPagerView) findViewById(R.id.fragmentPagerView);
        pager.setFragmentAdapter(new SearchResultActivityTabAdapter(getSupportFragmentManager(),
                                                                    pager.getViewPager(),
                                                                    searchQuery));
    }

    /* Respond to ActionBar's Up (Back) button */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
