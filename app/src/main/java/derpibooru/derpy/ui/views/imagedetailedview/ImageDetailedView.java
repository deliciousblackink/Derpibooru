package derpibooru.derpy.ui.views.imagedetailedview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.EnumSet;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.ui.animators.ImageDetailedViewAnimator;
import derpibooru.derpy.ui.presenters.ImageInteractionPresenter;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageDetailedView extends LinearLayout {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.topBar) ImageTopBarView topBar;
    @Bind(R.id.bottomBar) ImageBottomBarView bottomBar;
    @Bind(R.id.transparentOverlay) View transparentOverlay;

    private ImageDetailedViewAnimator mAnimator;
    private ImageDetailedViewHandler mCallbackHandler;

    private boolean mIsUserLoggedIn;

    public ImageDetailedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout();
    }

    public void displayToolbar(int imageId, OnClickListener toolbarNavigationListener) {
        toolbar.setNavigationOnClickListener(toolbarNavigationListener);
        toolbar.setTitle(String.format(Locale.ENGLISH, "#%d", imageId));
    }

    public void displayDetailedView(FragmentManager childFragmentManager,
                                    boolean isUserLoggedIn,
                                    ImageTagView.OnTagClickListener tagClickListener,
                                    ImageDetailedViewHandler handler,
                                    @Nullable Bundle savedInstanceState) {
        mIsUserLoggedIn = isUserLoggedIn;
        mCallbackHandler = handler;

        topBar.inflateLayout();
        bottomBar.inflateLayout();
        mAnimator = new ImageDetailedViewAnimator(transparentOverlay, toolbar, topBar, bottomBar.tabPagerHeader, bottomBar.tabPager);
        if (savedInstanceState != null) {
            mAnimator.restoreInstanceState(savedInstanceState);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    mAnimator.animate(mAnimator.new HeadersExtensionAnimation());
                }
            });
        }
        bottomBar.initialize(childFragmentManager, tagClickListener, new BottomBarCallbackHandler(), savedInstanceState);
        initializeInteractionPresenter();
    }

    public void toggleView() {
        mAnimator.animate(mAnimator.new DetailedViewToggleAnimation());
    }

    public void saveInstanceState(Bundle outState) {
        mAnimator.saveInstanceState(outState);
        bottomBar.saveInstanceState(outState);
    }

    private void inflateLayout() {
        View view = inflate(getContext(), R.layout.view_image_detailed, null);
        ButterKnife.bind(this, view);
        addView(view);
    }

    private void initializeInteractionPresenter() {
        int id = mCallbackHandler.getImage().getThumb().getId();
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

            @SuppressWarnings("ConstantConditions")
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
        public DerpibooruImageDetailed getImage() {
            return mCallbackHandler.getImage();
        }

        @Override
        public ImageDetailedViewAnimator.BottomBarExtensionState getExtensionState() {
            return mAnimator.getBottomBarExtensionState();
        }

        @Override
        public void changeExtensionState(ImageDetailedViewAnimator.BottomBarExtensionState newState) {
            mAnimator.animate(mAnimator.new BottomBarExtensionStateAnimation(newState));
        }
    }

    public interface ImageDetailedViewHandler {
        DerpibooruImageDetailed getImage();
    }
}
