package derpibooru.derpy.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.HomeTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

public class MainActivity extends NavigationDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initializeNavigationDrawer();
    }

    @Override
    public void onUserDataRefreshed() {
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
