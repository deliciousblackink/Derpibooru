package derpibooru.derpy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruImageInfo;
import derpibooru.derpy.data.types.DerpibooruImageThumb;
import derpibooru.derpy.server.ImageFetcher;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.ui.animations.ExpandViewAnimation;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageBottomBarViewHandler;
import derpibooru.derpy.ui.views.ImageTopBarView;

public class ImageActivity extends AppCompatActivity
        implements QueryHandler, ImageBottomBarViewHandler {
    private static final int TOOLBAR_ANIMATION_DURATION = 200;

    private DerpibooruImageThumb mImageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mImageInfo = intent.getParcelableExtra("image_data");

        setImageInfo();
        loadImageWithGlide();

        ImageFetcher i = new ImageFetcher(this, this);
        i.id(mImageInfo.getId()).fetch();
    }

    private void setImageInfo() {
        setTitle("#" + Integer.toString(mImageInfo.getId()));

        ImageTopBarView iiv = (ImageTopBarView) findViewById(R.id.imageInfo);
        iiv.setInfo(mImageInfo.getUpvotes(), mImageInfo.getDownvotes(), mImageInfo.getScore());

        ImageBottomBarView ibbv = (ImageBottomBarView) findViewById(R.id.imageBottomBar);
        ibbv.setFragmentManager(getSupportFragmentManager())
                .setBottomToolbarViewHandler(this);
        ibbv.setInfo(mImageInfo.getFaves(), mImageInfo.getCommentCount());
    }

    private void loadImageWithGlide() {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
                .load(mImageInfo.getImageUrl())
                .fitCenter()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ProgressBar pb = (ProgressBar) findViewById(R.id.imageProgress);
                        pb.setVisibility(View.GONE);
                        return false;
                    }
                })
                .crossFade()
                .into(iv);
    }

    @Override
    public void queryPerformed(Object image) {
        DerpibooruImageInfo i = (DerpibooruImageInfo) image;
    }

    @Override
    public void queryFailed() {

    }

    @Override
    public void showBottomToolbarOnly() {
        RelativeLayout imageView = (RelativeLayout) findViewById(R.id.imageViewLayout);
        /* TODO: move the magic numbers away
         * ! don't forget to change the layout file;
         * ImageBottomBarView's weight is also hardcoded
         */
        toggleToolbarAnimation(imageView, 2.0f, 4.0f);
    }

    @Override
    public void showBottomToolbarWithTabs() {
        RelativeLayout imageView = (RelativeLayout) findViewById(R.id.imageViewLayout);
         /* TODO: move the magic numbers away
         * ! don't forget to change the layout file;
         * ImageBottomBarView's weight is also hardcoded
         */
        toggleToolbarAnimation(imageView, 4.0f, 2.0f);
    }

    private void toggleToolbarAnimation(View content, float currentWeight,
                                        float targetWeight) {
        Animation a;
        a = new ExpandViewAnimation(content, currentWeight, targetWeight);
        a.setDuration(TOOLBAR_ANIMATION_DURATION);
        content.startAnimation(a);
    }

    /* Respond to ActionBar's Up (Back) button */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
