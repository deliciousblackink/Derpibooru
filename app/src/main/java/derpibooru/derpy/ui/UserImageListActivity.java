package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import derpibooru.derpy.R;
import derpibooru.derpy.server.providers.UserImageListProvider;
import derpibooru.derpy.ui.fragments.ImageListFragment;
import derpibooru.derpy.ui.fragments.UserImageListsFragment;

public class UserImageListActivity extends NavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image_list);
        super.initializeNavigationDrawer();
        initializeImageListView();
        setActivityTitle();
    }

    @Override
    public void onUserDataRefreshed() { }

    private void initializeImageListView() {
        ImageListFragment imageList = new UserImageListsFragment();
        imageList.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layoutImageList, imageList).commit();
    }

    private void setActivityTitle() {
        switch (UserImageListProvider.UserListType.fromValue(getIntent().getIntExtra("type", 0))) {
            case Faved:
                getSupportActionBar().setTitle(R.string.activity_user_list_faved);
                break;
            case Upvoted:
                getSupportActionBar().setTitle(R.string.activity_user_list_upvoted);
                break;
            case Uploaded:
                getSupportActionBar().setTitle(R.string.activity_user_list_uploaded);
                break;
        }
    }
}
