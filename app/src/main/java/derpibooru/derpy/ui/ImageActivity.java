package derpibooru.derpy.ui;

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

import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.ui.utils.ImageInteractionPresenter;
import derpibooru.derpy.ui.views.AccentColorIconButton;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    private ImageInteractionPresenter mInteractionPresenter;
    private List<DerpibooruImageInteraction.InteractionType> mInteractions;

    private ImageTopBarView mTopBar;
    private ImageBottomBarView mBottomBar;

    /* TODO: should be a singleTop activity
     * http://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopBar = ((ImageTopBarView) findViewById(R.id.imageTopBar));
        mBottomBar = ((ImageBottomBarView) findViewById(R.id.imageBottomBar));
        DerpibooruImageThumb thumb = getIntent().getParcelableExtra("derpibooru.derpy.ImageThumb");
        setImageInfoFromThumb(thumb, toolbar);
        loadImageWithGlide(thumb.getLargeImageUrl());
        initializeImageInteractions(thumb);

        /* TODO: handle configuration changes */
    }

    private void setImageInfoFromThumb(DerpibooruImageThumb thumb, Toolbar toolbar) {
        toolbar.setTitle("#" + Integer.toString(thumb.getId()));

        mBottomBar.setFragmentManager(getSupportFragmentManager());
        mBottomBar.setBasicInfo(thumb.getId(), thumb.getCommentCount());
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

    private void initializeImageInteractions(final DerpibooruImageThumb image) {
        mInteractions = image.getImageInteractions();
        mInteractionPresenter = new ImageInteractionPresenter(this) {
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
                return image.getInternalId();
            }

            @NonNull
            @Override
            protected List<DerpibooruImageInteraction.InteractionType> getInteractions() {
                return mInteractions;
            }

            @Override
            protected void addInteraction(DerpibooruImageInteraction.InteractionType interaction) {
                mInteractions.add(interaction);
            }

            @Override
            protected void removeInteraction(DerpibooruImageInteraction.InteractionType interaction) {
                mInteractions.remove(interaction);
            }

            @Override
            protected void onInteractionFailed() {

            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void refreshInfo(int faves, int upvotes, int downvotes) {
                /* prevent icons from blending into the background by disabling tint toggle on touch
                 * (only in case there was no user interaction) */
                getFaveButton().setToggleIconTintOnTouch(
                        mInteractions.contains(DerpibooruImageInteraction.InteractionType.Fave));
                getUpvoteButton().setToggleIconTintOnTouch(
                        mInteractions.contains(DerpibooruImageInteraction.InteractionType.Upvote));
                getDownvoteButton().setToggleIconTintOnTouch(
                        mInteractions.contains(DerpibooruImageInteraction.InteractionType.Downvote));
                super.refreshInfo(faves, upvotes, downvotes);
            }
        };
        mInteractionPresenter.refreshInfo(image.getFaves(), image.getUpvotes(), image.getDownvotes());
    }

    private class GlideRequestListener implements RequestListener<String, GlideDrawable> {
        private ImageView mImageView;

        public GlideRequestListener(ImageView glideTarget) {
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
