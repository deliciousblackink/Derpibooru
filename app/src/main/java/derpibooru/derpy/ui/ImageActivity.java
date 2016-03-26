package derpibooru.derpy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.ImageDetailedProvider;
import derpibooru.derpy.ui.adapters.ImageActivityFragmentAdapter;
import derpibooru.derpy.ui.fragments.ImageActivityMainFragment;
import derpibooru.derpy.ui.fragments.ImageActivityTagFragment;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public class ImageActivity extends AppCompatActivity {
    public static final String EXTRAS_TAG_SEARCH_QUERY = "derpibooru.derpy.SearchTagName";
    public static final String EXTRAS_IMAGE_DETAILED = "derpibooru.derpy.ImageDetailed";
    public static final String EXTRAS_IMAGE_ID = "derpibooru.derpy.ImageId";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarLayout) View toolbarLayout;
    @Bind(R.id.fragmentLayout) FrameLayout contentLayout;

    private DerpibooruImageDetailed mImage;

    private ImageActivityFragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DerpibooruImageThumb placeholderThumb = null;

        if ((savedInstanceState != null) && (savedInstanceState.containsKey(EXTRAS_IMAGE_DETAILED))) {
            mImage = savedInstanceState.getParcelable(EXTRAS_IMAGE_DETAILED);
        } else if (getIntent().hasExtra(EXTRAS_IMAGE_ID)) {
            fetchDetailedInformation(getIntent().getIntExtra(EXTRAS_IMAGE_ID, 0));
        } else if (getIntent().hasExtra(ImageListFragment.EXTRAS_IMAGE_THUMB)) {
            placeholderThumb = getIntent().getParcelableExtra(ImageListFragment.EXTRAS_IMAGE_THUMB);
            fetchDetailedInformation(placeholderThumb.getId());
        }

        initializeAdapter(savedInstanceState, placeholderThumb);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mImage != null) {
            savedInstanceState.putParcelable(EXTRAS_IMAGE_DETAILED, mImage);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentAdapter.restoreFragmentFromBackStack()) {
            if ((mImage != null)
                    && (mFragmentAdapter.getCurrentFragment() instanceof ImageActivityMainFragment)) {
                ((ImageActivityMainFragment) mFragmentAdapter.getCurrentFragment()).resetView();
                ((ImageActivityMainFragment) mFragmentAdapter.getCurrentFragment()).onDetailedImageFetched();
            }
        } else {
            setActivityResult();
            super.onBackPressed();
        }
    }

    private void setActivityResult() {
        setResult(RESULT_OK, getActivityResultIntent());
    }

    private void setActivityResult(String tagSearch) {
        Intent intent = getActivityResultIntent();
        intent.putExtra(EXTRAS_TAG_SEARCH_QUERY, tagSearch);
        setResult(RESULT_OK, intent);
    }

    private Intent getActivityResultIntent() {
        Intent intent = new Intent();
        if (mImage != null) {
            intent.putExtra(ImageListFragment.EXTRAS_IMAGE_THUMB, mImage.getThumb());
        }
        return intent;
    }

    private void initializeAdapter(@Nullable Bundle savedInstanceState,
                                   @Nullable DerpibooruImageThumb placeholderThumb) {
        mFragmentAdapter = new ImageActivityFragmentAdapter(
                getSupportFragmentManager(), contentLayout.getId(), savedInstanceState, placeholderThumb) {
            @Override
            protected boolean isUserLoggedIn() {
                return ((DerpibooruUser) getIntent().getParcelableExtra(MainActivity.EXTRAS_USER)).isLoggedIn();
            }

            @Override
            protected void setFragmentCallbackHandlers(@Nullable Fragment target) {
                if (target instanceof ImageActivityMainFragment) {
                    ((ImageActivityMainFragment) target)
                            .setActivityCallbacks(new MainFragmentCallbackHandler());
                } else if (target instanceof ImageActivityTagFragment) {
                    ((ImageActivityTagFragment) target)
                            .setActivityCallbacks(new TagFragmentCallbackHandler());
                }
            }
        };
    }

    private void fetchDetailedInformation(int imageId) {
        ImageDetailedProvider provider = new ImageDetailedProvider(
                this, new QueryHandler<DerpibooruImageDetailed>() {
            @Override
            public void onQueryExecuted(DerpibooruImageDetailed info) {
                mImage = info;
                if (mFragmentAdapter != null) {
                    mFragmentAdapter.onDetailedImageFetched();
                }
            }

            @Override
            public void onQueryFailed() {
            /* TODO: handle failed request */
            }
        });
        provider.id(imageId).fetch();
    }

    private class MainFragmentCallbackHandler implements ImageActivityMainFragment.ImageActivityMainFragmentHandler {
        @Override
        public boolean isToolbarVisible() {
            return toolbarLayout.getVisibility() == View.VISIBLE;
        }

        @Override
        public void setToolbarVisible(boolean visible) {
            toolbarLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        @Override
        public DerpibooruImageDetailed getImage() {
            return mImage;
        }

        @Override
        public void setToolbarTitle(final String title) {
            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    setTitle(title);
                }
            });
        }

        @Override
        public void openTagInformation(int tagId) {
            mFragmentAdapter.displayTagFragment(tagId);
        }
    }

    private class TagFragmentCallbackHandler implements ImageActivityTagFragment.ImageActivityTagFragmentHandler {
        @Override
        public void onTagSearchRequested(String tagName) {
            setActivityResult(tagName);
            ImageActivity.super.finish();
        }

        @Override
        public void setToolbarTitle(String title) {
            toolbar.setTitle(title);
        }
    }
}
