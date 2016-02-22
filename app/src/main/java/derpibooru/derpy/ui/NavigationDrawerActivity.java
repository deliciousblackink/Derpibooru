package derpibooru.derpy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import derpibooru.derpy.R;

abstract class NavigationDrawerActivity extends AppCompatActivity {
    private NavigationDrawer mNavigationDrawer;

    protected void initializeNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawer = new NavigationDrawer(this, ((DrawerLayout) findViewById(R.id.drawerLayout)),
                                                 toolbar, ((NavigationView) findViewById(R.id.navigationView)));
    }

    protected void refreshUserData() {
        mNavigationDrawer.refreshUserData();
    }

    public abstract void onUserDataRefreshed();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mNavigationDrawer.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen()) {
            mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
