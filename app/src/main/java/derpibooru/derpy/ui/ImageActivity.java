package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImage;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.utils.ImageInteractionPresenter;
import derpibooru.derpy.ui.views.AccentColorIconButton;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    public static final String EXTRAS_IMAGE = "derpibooru.derpy.Image";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarLayout) View toolbarLayout;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.imageTopBar) ImageTopBarView topBar;
    @Bind(R.id.imageBottomBar) ImageBottomBarView bottomBar;

    private DerpibooruImage mImageData;

    /* TODO: should be a singleTop activity
     * http://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mImageData = (DerpibooruImage)
                ((savedInstanceState == null) ? getIntent().getParcelableExtra(EXTRAS_IMAGE)
                                              : savedInstanceState.getParcelable(EXTRAS_IMAGE));
        setImageInfo();
        loadImageWithGlide(mImageData.getLargeImageUrl());
        initializeImageInteractions((DerpibooruUser) getIntent().getParcelableExtra(MainActivity.EXTRAS_USER));

        /* TODO: handle configuration changes */
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(EXTRAS_IMAGE, mImageData);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK,
                  new Intent().putExtra(EXTRAS_IMAGE, mImageData));
        super.onBackPressed();
    }

    private void setImageInfo() {
        toolbar.setTitle("#" + Integer.toString(mImageData.getId()));

        bottomBar.setFragmentManager(getSupportFragmentManager());
        bottomBar.setBasicInfo(mImageData.getId(), mImageData.getCommentCount());
        bottomBar.post(new Runnable() {
            @Override
            public void run() {
                int bottomBarMaximumHeightWhenExtended =
                        imageView.getMeasuredHeight() - toolbarLayout.getMeasuredHeight();
                bottomBar.setBarExtensionAttrs(bottomBarMaximumHeightWhenExtended);
                bottomBar.getLayoutParams().height = bottomBarMaximumHeightWhenExtended;
                bottomBar.requestLayout();
            }
        });
    }

    private void loadImageWithGlide(String url) {
        if (url.endsWith(".gif")) {
            Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new GlideRequestListener(imageView))
                    .into(imageView);
        } else {
            Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new GlideRequestListener(imageView))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(imageView);
        }
    }

    private void initializeImageInteractions(DerpibooruUser user) {
        ImageInteractionPresenter interactionPresenter = new ImageInteractionPresenter(this, user.isLoggedIn()) {
            @Nullable
            @Override
            protected AccentColorIconButton getScoreButton() {
                return topBar.getScoreButton();
            }

            @Nullable
            @Override
            protected AccentColorIconButton getFaveButton() {
                return bottomBar.getFaveButton();
            }

            @Nullable
            @Override
            protected AccentColorIconButton getUpvoteButton() {
                return topBar.getUpvoteButton();
            }

            @Nullable
            @Override
            protected AccentColorIconButton getDownvoteButton() {
                return topBar.getDownvoteButton();
            }

            @Override
            protected int getIdForImageInteractions() {
                return mImageData.getIdForImageInteractions();
            }

            @NonNull
            @Override
            protected Set<DerpibooruImageInteraction.InteractionType> getInteractions() {
                return mImageData.getImageInteractions();
            }

            @Override
            protected void addInteraction(DerpibooruImageInteraction.InteractionType interaction) {
                mImageData.getImageInteractions().add(interaction);
            }

            @Override
            protected void removeInteraction(DerpibooruImageInteraction.InteractionType interaction) {
                mImageData.getImageInteractions().remove(interaction);
            }

            @Override
            protected void onInteractionFailed() {

            }

            @Override
            protected void onInteractionCompleted(DerpibooruImageInteraction result) {
                mImageData.setFaves(result.getFavorites());
                mImageData.setUpvotes(result.getUpvotes());
                mImageData.setDownvotes(result.getDownvotes());
                super.onInteractionCompleted(result);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void refreshInfo(int faves, int upvotes, int downvotes) {
                /* prevent icons from blending into the background by disabling tint toggle on touch
                 * (only in case there was no user interaction) */
                getFaveButton().setToggleIconTintOnTouch(
                        getInteractions().contains(DerpibooruImageInteraction.InteractionType.Fave));
                getUpvoteButton().setToggleIconTintOnTouch(
                        getInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote));
                getDownvoteButton().setToggleIconTintOnTouch(
                        getInteractions().contains(DerpibooruImageInteraction.InteractionType.Downvote));
                super.refreshInfo(faves, upvotes, downvotes);
            }
        };
        interactionPresenter.refreshInfo(mImageData.getFaves(), mImageData.getUpvotes(), mImageData.getDownvotes());
    }

    private class GlideRequestListener implements RequestListener<String, GlideDrawable> {
        private ImageView mImageView;

        GlideRequestListener(ImageView glideTarget) {
            mImageView = glideTarget;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.e("ImageActivity", "Failed to load the image with Glide", e);
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            findViewById(R.id.progressImage).setVisibility(View.GONE);
            attachPhotoView(mImageView);
            return false;
        }

        private void attachPhotoView(ImageView target) {
            new PhotoViewAttacher(target).setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (toolbarLayout.getVisibility() == View.VISIBLE) {
                        toolbarLayout.setVisibility(View.INVISIBLE);
                        topBar.setVisibility(View.INVISIBLE);
                        bottomBar.setVisibility(View.INVISIBLE);
                    } else {
                        toolbarLayout.setVisibility(View.VISIBLE);
                        topBar.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
