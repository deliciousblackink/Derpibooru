package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    private PhotoViewAttacher mImageViewZoomAttacher;
    private ImageBottomBarView mBottomBarView;

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

        mBottomBarView = ((ImageBottomBarView) findViewById(R.id.imageBottomBar));
        DerpibooruImageThumb thumb = getIntent().getParcelableExtra("derpibooru.derpy.ImageThumb");
        setImageInfoFromThumb(thumb, toolbar);
        loadImageWithGlide(thumb.getLargeImageUrl());

        /* FIXME: handle configuration changes */
    }

    private void setImageInfoFromThumb(DerpibooruImageThumb thumb, Toolbar toolbar) {
        toolbar.setTitle("#" + Integer.toString(thumb.getId()));

        ((ImageTopBarView) findViewById(R.id.imageTopBar))
                .setInfo(thumb.getUpvotes(), thumb.getDownvotes(), thumb.getScore());

        mBottomBarView.setFragmentManager(getSupportFragmentManager());
        mBottomBarView.setBasicInfo(thumb.getId(), thumb.getFaves(), thumb.getCommentCount());
        mBottomBarView.post(new Runnable() {
            @Override
            public void run() {
                int bottomBarMaximumHeightWhenExtended = findViewById(R.id.imageView).getMeasuredHeight()
                        - (findViewById(R.id.toolbarLayout).getMeasuredHeight());
                mBottomBarView.setBarExtensionAttrs(bottomBarMaximumHeightWhenExtended);
                mBottomBarView.getLayoutParams().height = bottomBarMaximumHeightWhenExtended;
                mBottomBarView.requestLayout();
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
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressImage);
            pb.setVisibility(View.GONE);

            mImageViewZoomAttacher = new PhotoViewAttacher(mImageView);
            mImageViewZoomAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
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
            return false;
        }
    }
}
