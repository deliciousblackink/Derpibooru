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
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.ImageDetailedProvider;
import derpibooru.derpy.ui.utils.ImageInteractionPresenter;
import derpibooru.derpy.ui.views.AccentColorIconButton;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    public static final String EXTRAS_IMAGE_THUMB = "derpibooru.derpy.Image";
    public static final String EXTRAS_IMAGE_DETAILED = "derpibooru.derpy.ImageDetailed";
    public static final String EXTRAS_IMAGE_ID = "derpibooru.derpy.ImageId";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarLayout) View toolbarLayout;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.imageTopBar) ImageTopBarView topBar;
    @Bind(R.id.imageBottomBar) ImageBottomBarView bottomBar;

    private ImageInteractionPresenter mInteractionPresenter;
    private DerpibooruImageDetailed mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws IllegalStateException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.loading);
        if (!isSavedStateAvailable(savedInstanceState)) {
            initializeFromIntent();
        }
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
        if (mImage != null) {
            setResult(Activity.RESULT_OK,
                      new Intent().putExtra(EXTRAS_IMAGE_THUMB, mImage.getThumb()));
        } else {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }
    
    private boolean isSavedStateAvailable(Bundle savedInstanceState) {
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(EXTRAS_IMAGE_DETAILED))) {
            mImage = savedInstanceState.getParcelable(EXTRAS_IMAGE_DETAILED);
            display(mImage);
            return true;
        }
        return false;
    }
    
    private void initializeFromIntent() throws IllegalStateException {
        if (getIntent().getParcelableExtra(EXTRAS_IMAGE_THUMB) != null) {
            DerpibooruImageThumb thumb = getIntent().getParcelableExtra(EXTRAS_IMAGE_THUMB);
            display(thumb);
            fetchDetailedInformation(thumb.getId());
        } else if (getIntent().hasExtra(EXTRAS_IMAGE_ID)) {
            fetchDetailedInformation(getIntent().getIntExtra(EXTRAS_IMAGE_ID, 0));
        } else {
            throw new IllegalStateException("ImageActivity has been provided with neither DerpibooruImage nor image id");
        }
    }

    private void display(DerpibooruImageThumb thumb) {
        if (toolbar.getTitle().equals(getString(R.string.loading))) {
            toolbar.setTitle("#" + Integer.toString(thumb.getId()));
            initializeBottomBarLayout();
            loadImageWithGlide(thumb.getLargeImageUrl());
        }
        topBar.getUpvoteButton().setText(Integer.toString(thumb.getUpvotes()));
        topBar.getUpvoteButton().setEnabled(false);
        topBar.getDownvoteButton().setText(Integer.toString(thumb.getDownvotes()));
        topBar.getDownvoteButton().setEnabled(false);
        topBar.getScoreButton().setText(Integer.toString(thumb.getUpvotes() - thumb.getDownvotes()));
        bottomBar.getFaveButton().setText(Integer.toString(thumb.getFaves()));
        bottomBar.getFaveButton().setEnabled(false);
        bottomBar.setInfoFromThumb(thumb);
    }

    private void display(DerpibooruImageDetailed image) {
        if (toolbar.getTitle().equals(getString(R.string.loading))) {
            toolbar.setTitle("#" + Integer.toString(image.getThumb().getId()));
            initializeBottomBarLayout();
            loadImageWithGlide(image.getThumb().getLargeImageUrl());
        }
        initializeInteractionPresenter(((DerpibooruUser) getIntent().getParcelableExtra(MainActivity.EXTRAS_USER))
                                               .isLoggedIn());
        bottomBar.setInfoFromDetailed(image);
    }

    private void fetchDetailedInformation(int imageId) {
        ImageDetailedProvider provider = new ImageDetailedProvider(
                this, new QueryHandler<DerpibooruImageDetailed>() {
            @Override
            public void onQueryExecuted(DerpibooruImageDetailed info) {
                mImage = info;
                display(mImage);
            }

            @Override
            public void onQueryFailed() {
            /* TODO: handle failed request */
            }
        });
        provider.id(imageId).fetch();
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
    
    private void initializeBottomBarLayout() {
        bottomBar.initializeWithFragmentManager(getSupportFragmentManager());
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
    
    private void initializeInteractionPresenter(boolean isUserLoggedIn) {
        mInteractionPresenter = new ImageInteractionPresenter(this, isUserLoggedIn) {
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
                return mImage.getThumb().getIdForImageInteractions();
            }

            @NonNull
            @Override
            protected Set<DerpibooruImageInteraction.InteractionType> getInteractions() {
                return mImage.getThumb().getImageInteractions();
            }

            @Override
            protected void addInteraction(DerpibooruImageInteraction.InteractionType interaction) {
                mImage.getThumb().getImageInteractions().add(interaction);
            }

            @Override
            protected void removeInteraction(DerpibooruImageInteraction.InteractionType interaction) {
                mImage.getThumb().getImageInteractions().remove(interaction);
            }

            @Override
            protected void onInteractionFailed() {
                /* TODO: pop up an error screen */
            }

            @Override
            protected void onInteractionCompleted(DerpibooruImageInteraction result) {
                mImage.getThumb().setFaves(result.getFavorites());
                mImage.getThumb().setUpvotes(result.getUpvotes());
                mImage.getThumb().setDownvotes(result.getDownvotes());
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
        mInteractionPresenter.refreshInfo(mImage.getThumb().getFaves(), mImage.getThumb().getUpvotes(), mImage.getThumb().getDownvotes());
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
