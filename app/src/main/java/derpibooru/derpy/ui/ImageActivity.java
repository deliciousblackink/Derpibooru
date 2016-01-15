package derpibooru.derpy.ui;

import android.content.Intent;
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
import derpibooru.derpy.data.types.Image;
import derpibooru.derpy.server.ImageFetcher;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.ui.views.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTopBarView;

public class ImageActivity extends AppCompatActivity
        implements QueryHandler {

    private ImageBottomBarView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBottomBar = (ImageBottomBarView) findViewById(R.id.imageBottomBar);
        mBottomBar.setFragmentManager(getSupportFragmentManager());

        /* TODO: Speed up the loading by passing ImageThumb object & fetching data from it */
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        setTitle("#" + Integer.toString(id));
        ImageFetcher i = new ImageFetcher(this, this);
        i.id(id).fetch();
    }

    @Override
    public void queryFailed() {

    }

    @Override
    public void queryPerformed(Object image) {
        Image i = (Image) image;
        ImageTopBarView iiv = (ImageTopBarView) findViewById(R.id.imageInfo);
        iiv.setInfo(i.getUpvotes(), i.getDownvotes(), i.getScore());

        mBottomBar.setInfo(i.getFaves(), i.getCommentCount());

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
