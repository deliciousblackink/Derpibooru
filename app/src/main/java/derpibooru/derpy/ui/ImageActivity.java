package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruImageInfo;
import derpibooru.derpy.data.types.DerpibooruImageThumb;
import derpibooru.derpy.server.ImageFetcher;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;

public class ImageActivity extends AppCompatActivity
                           implements QueryHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DerpibooruImageThumb thumb = getIntent().getParcelableExtra("image_thumb");
        setBasicImageInfo(thumb);
        loadImageWithGlide(thumb.getFullImageUrl());

        ImageFetcher i = new ImageFetcher(this, this);
        i.id(thumb.getId()).fetch(); /* async load; see 'queryFailed' and 'queryPerformed' */
    }

    private void setBasicImageInfo(DerpibooruImageThumb thumb) {
        setTitle("#" + Integer.toString(thumb.getId()));

        ((ImageTopBarView) findViewById(R.id.imageInfo))
                .setInfo(thumb.getUpvotes(), thumb.getDownvotes(), thumb.getScore());

        ((ImageBottomBarView) findViewById(R.id.imageBottomBar))
                .setFragmentManager(getSupportFragmentManager())
                .setViewAbove(findViewById(R.id.imageViewLayout))
                .setBasicInfo(thumb.getFaves(), thumb.getCommentCount());
    }

    private void loadImageWithGlide(String url) {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
                .load(url)
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
        ((ImageBottomBarView) findViewById(R.id.imageBottomBar))
                .setTabInfo((DerpibooruImageInfo) image);
    }

    @Override
    public void queryFailed() {

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
