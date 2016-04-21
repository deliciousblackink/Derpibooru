package derpibooru.derpy.ui.views.imagedetailedview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.EnumSet;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.ui.animators.ImageDetailedViewAnimator;
import derpibooru.derpy.ui.presenters.ImageInteractionPresenter;

import static derpibooru.derpy.ui.animators.ImageDetailedViewAnimator.BottomBarExtensionState;

public class ImageDetailedView extends LinearLayout {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.topBar) ImageTopBarView topBar;
    @Bind(R.id.bottomBar) ImageBottomBarView bottomBar;
    @Bind(R.id.transparentOverlay) View transparentOverlay;

    private ImageDetailedViewAnimator mAnimator;
    private ImageDetailedViewHandler mCallbackHandler;

    private ImageDownload mImageDownload;

    private boolean mIsUserLoggedIn;

    public ImageDetailedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(getContext(), R.layout.view_image_detailed, null);
        ButterKnife.bind(this, view);
        addView(view);
    }

    public void displayToolbar(int imageId, OnClickListener toolbarNavigationListener) {
        toolbar.setNavigationOnClickListener(toolbarNavigationListener);
        toolbar.setTitle(String.format(Locale.ENGLISH, "#%d", imageId));
    }

    public void displayDetailedView(FragmentManager childFragmentManager,
                                    boolean isUserLoggedIn,
                                    ImageTagView.OnTagClickListener tagClickListener,
                                    ImageDetailedViewHandler handler,
                                    final @Nullable Bundle savedInstanceState) {
        mIsUserLoggedIn = isUserLoggedIn;
        mCallbackHandler = handler;
        /* bottom bar needs to access Animator's state on initialization, don't change the order of the calls */
        initializeAnimator(savedInstanceState);
        bottomBar.initialize(childFragmentManager, tagClickListener,
                             new BottomBarCallbackHandler(), savedInstanceState);
        /* from here on the order of initialization does not matter */
        initializeInteractionPresenter();
        initializeImageDownload();
        /* show the header extension animation after everything's been initialized */
        if (savedInstanceState == null) {
            mAnimator.animate(mAnimator.new HeadersExtensionAnimation());
        }
    }

    public void toggleView() {
        mAnimator.animate(mAnimator.new DetailedViewToggleAnimation());
    }

    public void saveInstanceState(Bundle outState) {
        mAnimator.saveInstanceState(outState);
        bottomBar.saveInstanceState(outState);
    }

    public void onImageDownloadPermissionsGranted() {
        mImageDownload.start();
    }

    public BottomBarExtensionState getBottomBarExtensionState() {
        return (mAnimator != null) ? mAnimator.getBottomBarExtensionState() : BottomBarExtensionState.None;
    }

    private void initializeAnimator(@Nullable Bundle savedInstanceState) {
        mAnimator = new ImageDetailedViewAnimator(
                transparentOverlay, toolbar, topBar, bottomBar.tabPagerHeader, bottomBar.tabPager);
        if (savedInstanceState != null) {
            mAnimator.restoreInstanceState(savedInstanceState);
        }
    }

    private void initializeImageDownload() {
        mImageDownload = new ImageDownload(getContext(),
                                           mCallbackHandler.getImage().getThumb().getId(), getImageTagNames(),
                                           mCallbackHandler.getImage().getDownloadUrl());
        if (!(hasStoragePermissions() && mImageDownload.isDownloaded())) {
            inflateToolbarMenu();
        } else {
            mImageDownload = null;
        }
    }

    private void inflateToolbarMenu() {
        toolbar.inflateMenu(R.menu.menu_image_activity_main_fragment);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.actionDownloadImage:
                                if (mCallbackHandler != null) {
                                    if (hasStoragePermissions()) {
                                        onImageDownloadPermissionsGranted();
                                    } else {
                                        mCallbackHandler.requestImageDownloadPermissions();
                                    }
                                }
                                break;
                        }
                        return true;
                    }
                });
    private String getImageTagNames() {
        StringBuilder tagListBuilder = new StringBuilder();
        for (DerpibooruTag tag : mCallbackHandler.getImage().getTags()) {
            tagListBuilder.append(tag.getName());
            tagListBuilder.append(", ");
        }
        tagListBuilder.delete(tagListBuilder.length() - 3, tagListBuilder.length() - 1); /* remove ', ' */
        return tagListBuilder.toString();
    }

    private boolean hasStoragePermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void initializeInteractionPresenter() {
        int id = mCallbackHandler.getImage().getThumb().getIdForImageInteractions();
        ImageInteractionPresenter presenter =
                new ImageInteractionPresenter(id, topBar.buttonScore, bottomBar.buttonFave,
                                              topBar.buttonUpvote, topBar.buttonDownvote) {
            @NonNull
            @Override
            protected EnumSet<DerpibooruImageInteraction.InteractionType> getInteractionsSet() {
                return mCallbackHandler.getImage().getThumb().getImageInteractions();
            }

            @Override
            protected void onInteractionFailed() {
                /* TODO: pop up an error screen */
            }

            @Override
            protected void onInteractionCompleted(DerpibooruImageInteraction result) {
                mCallbackHandler.getImage().getThumb().setFaves(result.getFavorites());
                mCallbackHandler.getImage().getThumb().setUpvotes(result.getUpvotes());
                mCallbackHandler.getImage().getThumb().setDownvotes(result.getDownvotes());
                super.onInteractionCompleted(result);
            }

            @Override
            public void refreshInfo(int faves, int upvotes, int downvotes) {
                /* prevent icons from blending into the background by disabling tint toggle on touch
                 * (only in case there was no user interaction) */
                bottomBar.buttonFave.setToggleIconTintOnTouch(
                        getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Fave));
                topBar.buttonUpvote.setToggleIconTintOnTouch(
                        getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Upvote));
                topBar.buttonDownvote.setToggleIconTintOnTouch(
                        getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Downvote));
                super.refreshInfo(faves, upvotes, downvotes);
            }
        };
        if (mIsUserLoggedIn) {
            presenter.enableInteractions(getContext());
        }
        presenter.refreshInfo(mCallbackHandler.getImage().getThumb().getFaves(),
                              mCallbackHandler.getImage().getThumb().getUpvotes(),
                              mCallbackHandler.getImage().getThumb().getDownvotes());
    }

    private class BottomBarCallbackHandler implements ImageBottomBarView.BottomBarHandler {
        @Override
        public DerpibooruFilter getUserFilter() {
            return mCallbackHandler.getUserFilter();
        }

        @Override
        public DerpibooruImageDetailed getImage() {
            return mCallbackHandler.getImage();
        }

        @Override
        public BottomBarExtensionState getExtensionState() {
            return getBottomBarExtensionState();
        }

        @Override
        public void changeExtensionState(BottomBarExtensionState newState) {
            mCallbackHandler.onBottomBarExtensionStateChanged(newState);
            mAnimator.animate(mAnimator.new BottomBarExtensionStateAnimation(newState));
        }
    }

    public interface ImageDetailedViewHandler {
        DerpibooruFilter getUserFilter();
        DerpibooruImageDetailed getImage();
        void requestImageDownloadPermissions();
        void onBottomBarExtensionStateChanged(BottomBarExtensionState newState);
    }
}
