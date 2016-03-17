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

    @Override
    protected void onCreate(Bundle savedInstanceState) throws IllegalStateException {
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
        setFragmentCallbackHandlers(getCurrentFragment());
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(EXTRAS_IMAGE_DETAILED))) {
            mImage = savedInstanceState.getParcelable(EXTRAS_IMAGE_DETAILED);
            if (getCurrentFragment() instanceof ImageActivityMainFragment) {
                forceMainFragmentToDisplayDetailedImage(getCurrentFragment());
            }
        } else if (getIntent().hasExtra(EXTRAS_IMAGE_ID)) {
            fetchDetailedInformation(getIntent().getIntExtra(EXTRAS_IMAGE_ID, 0));
        } else if (getIntent().hasExtra(ImageListFragment.EXTRAS_IMAGE_THUMB)) {
            DerpibooruImageThumb thumb = getIntent().getParcelableExtra(ImageListFragment.EXTRAS_IMAGE_THUMB);
            displayMainFragment(thumb);
            fetchDetailedInformation(thumb.getId());
        }
    }

    private void setFragmentCallbackHandlers(Fragment target) {
        if (target instanceof ImageActivityMainFragment) {
            ((ImageActivityMainFragment) target)
                    .setActivityCallbacks(new MainFragmentCallbackHandler());
        } else if (target instanceof ImageActivityTagFragment) {
            ((ImageActivityTagFragment) target)
                    .setActivityCallbacks(new TagFragmentCallbackHandler());
        }
    }

    /**
     * If {@link ImageActivityMainFragment} has not created its view yet, it is forced to
     * skip the placeholder thumb and call {@link ImageActivityMainFragment.ImageActivityMainFragmentHandler#getImage()}.
     */
    private void forceMainFragmentToDisplayDetailedImage(Fragment mainFragment) {
        mainFragment.getArguments().remove(ImageListFragment.EXTRAS_IMAGE_THUMB);
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                setFragmentCallbackHandlers(fragment);
            }
        }
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            setActivityResult();
            super.onBackPressed();
        } else if ((getCurrentFragment() instanceof ImageActivityMainFragment) && (mImage != null)) {
            ((ImageActivityMainFragment) getCurrentFragment()).resetView();
            ((ImageActivityMainFragment) getCurrentFragment()).onDetailedImageFetched();
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

    private void fetchDetailedInformation(int imageId) {
        ImageDetailedProvider provider = new ImageDetailedProvider(
                this, new QueryHandler<DerpibooruImageDetailed>() {
            @Override
            public void onQueryExecuted(DerpibooruImageDetailed info) {
                mImage = info;
                displayMainFragment(null);
            }

            @Override
            public void onQueryFailed() {
            /* TODO: handle failed request */
            }
        });
        provider.id(imageId).fetch();
    }

    private void displayMainFragment(@Nullable DerpibooruImageThumb placeholderThumb) {
        if (placeholderThumb != null) {
            initializeMainFragmentWithPlaceholderThumb(placeholderThumb);
        } else if (getCurrentFragment() instanceof ImageActivityMainFragment) {
            /* the main fragment has already been instantiated with a placeholder thumb */
            ((ImageActivityMainFragment) getCurrentFragment())
                    .onDetailedImageFetched();
        } else {
            initializeMainFragmentWithDetailed();
        }
    }

    private void initializeMainFragmentWithDetailed() {
        if (getCurrentFragment() instanceof ImageActivityMainFragment) {
            setFragmentCallbackHandlers(getCurrentFragment());
            forceMainFragmentToDisplayDetailedImage(getCurrentFragment());
        } else {
            instantiateMainFragment(null);
        }
    }

    private void initializeMainFragmentWithPlaceholderThumb(DerpibooruImageThumb thumb) {
        if (!(getCurrentFragment() instanceof ImageActivityMainFragment)) {
            instantiateMainFragment(thumb);
        }
    }

    private void instantiateMainFragment(@Nullable DerpibooruImageThumb placeholderThumb) {
        ImageActivityMainFragment mainFragment = new ImageActivityMainFragment();
        mainFragment.setActivityCallbacks(new MainFragmentCallbackHandler());
        mainFragment.setArguments(getMainFragmentArguments(placeholderThumb));

        safelyCommitFragmentTransaction(
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(contentLayout.getId(), mainFragment));
    }

    private Bundle getMainFragmentArguments(@Nullable DerpibooruImageThumb thumb) {
        boolean isUserLoggedIn = ((DerpibooruUser)
                getIntent().getParcelableExtra(MainActivity.EXTRAS_USER)).isLoggedIn();
        Bundle args = new Bundle();
        args.putBoolean(ImageActivityMainFragment.EXTRAS_IS_USER_LOGGED_IN, isUserLoggedIn);
        if (thumb != null) {
            args.putParcelable(ImageListFragment.EXTRAS_IMAGE_THUMB, thumb);
        }
        return args;
    }

    @Nullable
    protected Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(contentLayout.getId());
    }

    private void displayTagFragment(int tagId) {
        Bundle args = new Bundle();
        args.putInt(ImageActivityTagFragment.EXTRAS_TAG_ID, tagId);

        ImageActivityTagFragment fragment = new ImageActivityTagFragment();
        fragment.setActivityCallbacks(new TagFragmentCallbackHandler());
        fragment.setArguments(args);

        safelyCommitFragmentTransaction(
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.image_activity_tag_enter,
                                             R.anim.image_activity_tag_exit,
                                             R.anim.image_activity_tag_back_stack_pop_enter,
                                             R.anim.image_activity_tag_back_stack_pop_exit)
                        .addToBackStack(null)
                        .replace(contentLayout.getId(), fragment));
    }

    /**
     * Somewhat dirty workaround for the FragmentTransaction
     * <a href="http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html">state loss</a>,
     * which can occur due to ImageDetailedProvider callback (see {@link #fetchDetailedInformation(int)}) being called when
     * the activity's view is destroyed.
     * <br>
     * Unlike {@link FragmentTransaction#commitAllowingStateLoss()}, the method does <strong>not</strong> commit the
     * transaction after onSaveInstanceState. Moreover, it logs the exception should it be required for debugging.
     */
    private int safelyCommitFragmentTransaction(FragmentTransaction transaction) {
        try {
            return transaction.commit();
        } catch (IllegalStateException e) {
            Log.e("ImageActivity", "safelyCommitFragmentTransaction: failed to commit transaction", e);
            return -1;
        }
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
        public void setToolbarTitle(String title) {
            toolbar.setTitle(title);
        }

        @Override
        public void openTagInformation(int tagId) {
            displayTagFragment(tagId);
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
