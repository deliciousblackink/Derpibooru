package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.ProviderRequestHandler;
import derpibooru.derpy.server.ImageInfoProvider;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    private PhotoViewAttacher mImageViewZoomAttacher;

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

        DerpibooruImageThumb thumb = getIntent().getParcelableExtra("image_thumb");
        setImageScoreFavesCommentCount(thumb, toolbar);
        loadImageWithGlide(thumb.getFullImageUrl());

        /* get image uploader, description, tags */
        ImageInfoProvider i = new ImageInfoProvider(this, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                ((ImageBottomBarView) findViewById(R.id.imageBottomBar))
                        .setTabInfo((DerpibooruImageInfo) result);
            }

            @Override
            public void onRequestFailed() {

            }
        });
        i.id(thumb.getId()).fetch();
    }

    private void setImageScoreFavesCommentCount(DerpibooruImageThumb thumb, Toolbar toolbar) {
        toolbar.setTitle("#" + Integer.toString(thumb.getId()));

        ((ImageTopBarView) findViewById(R.id.imageTopBar))
                .setInfo(thumb.getUpvotes(), thumb.getDownvotes(), thumb.getScore());

        final ImageBottomBarView bottomBar = ((ImageBottomBarView) findViewById(R.id.imageBottomBar));
        bottomBar.setFragmentManager(getSupportFragmentManager())
                .setBasicInfo(thumb.getFaves(), thumb.getCommentCount())
                .post(new Runnable() {
                    @Override
                    public void run() {
                         int bottomBarMaximumHeightWhenExtended = findViewById(R.id.imageView).getMeasuredHeight()
                                 - (findViewById(R.id.toolbarLayout).getMeasuredHeight()
                                 + findViewById(R.id.imageTopBar).getMeasuredHeight());
                         bottomBar.setBarExtensionAttrs(bottomBarMaximumHeightWhenExtended);
                     }
                 });
    }

    private void loadImageWithGlide(String url) {
        ((ProgressBar) findViewById(R.id.progressImage)).getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                                android.graphics.PorterDuff.Mode.SRC_IN);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e("ImageActivity", "Failed to load the image with Glide", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ProgressBar pb = (ProgressBar) findViewById(R.id.progressImage);
                        pb.setVisibility(View.GONE);

                        mImageViewZoomAttacher = new PhotoViewAttacher(imageView);
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
                })
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }
}
