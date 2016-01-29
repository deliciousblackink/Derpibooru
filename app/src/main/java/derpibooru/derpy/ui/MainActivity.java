package derpibooru.derpy.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.MainActivityTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

/* TODO: https://github.com/JakeWharton/butterknife */

public class MainActivity extends NavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeNavigationDrawer();

        ((FragmentTabPagerView) findViewById(R.id.fragmentPagerView))
                .setFragmentAdapter(new MainActivityTabAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionRandomImage) {
            /* TODO: random image */
        }

        return super.onOptionsItemSelected(item);
    }
}
