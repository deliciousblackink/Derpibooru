package derpibooru.derpy.ui;

import android.os.Bundle;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.fragments.ImageListFragment;
import derpibooru.derpy.ui.fragments.UserImageListsFragment;

public class UserImageListActivity extends NavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image_list);
        super.initializeNavigationDrawer();
        initializeImageListView();
    }

    @Override
    public void onUserDataRefreshed() { }

    private void initializeImageListView() {
        ImageListFragment imageList = new UserImageListsFragment();
        imageList.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layoutImageList, imageList).commit();
    }
}
