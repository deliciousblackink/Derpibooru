package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.adapters.SearchResultActivityTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

public class SearchResultActivity extends AppCompatActivity {
    public static final String EXTRAS_SEARCH_QUERY = "derpibooru.derpy.SearchQuery";

    /* TODO: should be a singleTop activity
     * http://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String searchQuery = getIntent().getStringExtra(EXTRAS_SEARCH_QUERY);
        setTitle(searchQuery);
        initTabPager(searchQuery);
    }

    private void initTabPager(String searchQuery) {
        final FragmentTabPagerView tabView = (FragmentTabPagerView) findViewById(R.id.fragmentPagerView);
        tabView.setFragmentAdapter(new SearchResultActivityTabAdapter(
                getSupportFragmentManager(), searchQuery, (DerpibooruUser) getIntent().getParcelableExtra(MainActivity.EXTRAS_USER)));
        tabView.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ((SearchResultActivityTabAdapter) tabView.getFragmentAdapter())
                            .transferSearchOptionsToSearchResultsTab(tabView.getViewPager().getId());
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageScrollStateChanged(int state) {
                /* hide the keyboard in case it's left after editing text fields in the Options tab
                 * see http://stackoverflow.com/a/12422905/1726690 */
                if (state == ViewPager.SCROLL_STATE_IDLE
                        && tabView.getViewPager().getCurrentItem() == 0) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(tabView.getViewPager().getWindowToken(), 0);
                }
            }
        });
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
