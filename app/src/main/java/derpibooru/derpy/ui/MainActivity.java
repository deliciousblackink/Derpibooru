package derpibooru.derpy.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.MainActivityTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

/* TODO: https://github.com/JakeWharton/butterknife */

public class MainActivity extends NavigationDrawerActivity {
    private FragmentTabPagerView mTabViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeNavigationDrawer();
        mTabViewPager = (FragmentTabPagerView) findViewById(R.id.fragmentPagerView);
        mTabViewPager.setFragmentAdapter(
                new MainActivityTabAdapter(this, getSupportFragmentManager(),
                                           new MainActivityTabAdapter.TabSetChangeHandler() {
                                               @Override
                                               public void onTabSetChanged() {
                                                   mTabViewPager.refreshTabTitles();
                                               }
                                           }));
        refreshUserData();
    }

    @Override
    protected void onUserDataRefreshed() {
        if (mTabViewPager != null) {
            ((MainActivityTabAdapter) mTabViewPager.getFragmentAdapter()).toggleWatchedTab();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionRandomImage:
                /* TODO: random image */
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
