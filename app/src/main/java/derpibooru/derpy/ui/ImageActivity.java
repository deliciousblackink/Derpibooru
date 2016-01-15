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
import derpibooru.derpy.data.types.Image;
import derpibooru.derpy.data.types.ImageThumb;
import derpibooru.derpy.server.ImageFetcher;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.ui.animations.ExpandViewAnimation;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageBottomBarViewHandler;
import derpibooru.derpy.ui.views.ImageTopBarView;

public class ImageActivity extends AppCompatActivity
        implements QueryHandler, ImageBottomBarViewHandler {
    private static final int TOOLBAR_ANIMATION_DURATION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        loadImage((ImageThumb) intent.getParcelableExtra("image_data"));
    }

    private void loadImage(ImageThumb it) {
        setTitle("#" + Integer.toString(it.getId()));

        ImageTopBarView iiv = (ImageTopBarView) findViewById(R.id.imageInfo);
        iiv.setInfo(it.getUpvotes(), it.getDownvotes(), it.getScore());

        ImageBottomBarView ibbv = (ImageBottomBarView) findViewById(R.id.imageBottomBar);
        ibbv.setFragmentManager(getSupportFragmentManager())
                .setBottomToolbarViewHandler(this);
        ibbv.setInfo(it.getFaves(), it.getCommentCount());

        ImageFetcher i = new ImageFetcher(this, this);
        i.id(it.getId()).fetch();
    }

    @Override
    public void queryFailed() {

    }

    @Override
    public void queryPerformed(Object image) {
        Image i = (Image) image;

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
                .load(i.getImgUrl())
                .fitCenter()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ProgressBar pb = (ProgressBar) findViewById(R.id.imageProgress);
                        pb.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .crossFade()
                .into(iv);
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
