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

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.ui.utils.ImageInteractionPresenter;
import derpibooru.derpy.ui.views.AccentColorIconButton;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_IMAGE_THUMB = "derpibooru.derpy.ImageThumb";

    private ImageTopBarView mTopBar;
    private ImageBottomBarView mBottomBar;

    private DerpibooruImageThumb mImageData;

    /* TODO: should be a singleTop activity
     * http://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTopBar = ((ImageTopBarView) findViewById(R.id.imageTopBar));
        mBottomBar = ((ImageBottomBarView) findViewById(R.id.imageBottomBar));
        mImageData = (DerpibooruImageThumb)
                ((savedInstanceState == null) ? getIntent().getParcelableExtra(INTENT_EXTRA_IMAGE_THUMB)
                                              : savedInstanceState.getParcelable(INTENT_EXTRA_IMAGE_THUMB));
        setImageInfoFromThumb(toolbar);
        loadImageWithGlide(mImageData.getLargeImageUrl());
        initializeImageInteractions();

        /* TODO: handle configuration changes */
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(INTENT_EXTRA_IMAGE_THUMB, mImageData);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK,
                  new Intent().putExtra(INTENT_EXTRA_IMAGE_THUMB, mImageData));
        super.onBackPressed();
    }

    private void setImageInfoFromThumb(Toolbar toolbar) {
        toolbar.setTitle("#" + Integer.toString(mImageData.getId()));

        mBottomBar.setFragmentManager(getSupportFragmentManager());
        mBottomBar.setBasicInfo(mImageData.getId(), mImageData.getCommentCount());
        mBottomBar.post(new Runnable() {
            @Override
            public void run() {
                int bottomBarMaximumHeightWhenExtended = findViewById(R.id.imageView).getMeasuredHeight()
                        - (findViewById(R.id.toolbarLayout).getMeasuredHeight());
                mBottomBar.setBarExtensionAttrs(bottomBarMaximumHeightWhenExtended);
                mBottomBar.getLayoutParams().height = bottomBarMaximumHeightWhenExtended;
                mBottomBar.requestLayout();
            }
        });
    }

    private void loadImageWithGlide(String url) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
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

    private void initializeImageInteractions() {
        ImageInteractionPresenter interactionPresenter = new ImageInteractionPresenter(this) {
            @Nullable
            @Override
            protected AccentColorIconButton getScoreButton() {
                return mTopBar.getScoreButton();
            }

            @Nullable
            @Override
            protected AccentColorIconButton getFaveButton() {
                return mBottomBar.getFaveButton();
            }

            @Nullable
            @Override
            protected AccentColorIconButton getUpvoteButton() {
                return mTopBar.getUpvoteButton();
            }

            @Nullable
            @Override
            protected AccentColorIconButton getDownvoteButton() {
                return mTopBar.getDownvoteButton();
            }

            @Override
            protected int getInternalImageId() {
                return mImageData.getInternalId();
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
    }

    private void attachPhotoView(ImageView target) {
        new PhotoViewAttacher(target).setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (findViewById(R.id.toolbarLayout).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.toolbarLayout).setVisibility(View.INVISIBLE);
                    findViewById(R.id.imageTopBar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.imageBottomBar).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.toolbarLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageTopBar).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageBottomBar).setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
