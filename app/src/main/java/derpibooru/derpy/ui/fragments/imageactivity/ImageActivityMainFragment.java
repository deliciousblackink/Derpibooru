package derpibooru.derpy.ui.fragments.imageactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.ui.views.imagedetailedview.ImageTagView;
import derpibooru.derpy.ui.views.imagedetailedview.ImageDetailedView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivityMainFragment extends Fragment {
    public static final String EXTRAS_IS_USER_LOGGED_IN = "derpibooru.derpy.IsLoggedIn";
    public static final String EXTRAS_USER_FILTER = "derpibooru.derpy.UserFilter";

    private static final int REQUEST_WRITE_STORAGE = 142;

    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.imageDetailedView) ImageDetailedView imageDetailedView;

    private ImageActivityMainFragmentHandler mActivityCallbacks;

    private int mImageId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_image_fragment_main, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        setImageId(mImageId);
        imageDetailedView.displayToolbar(mImageId, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        if (mActivityCallbacks.getImage() != null) {
            v.post(new Runnable() {
                @Override
                public void run() {
                    displayImageInRootView(savedInstanceState);
                }
            });
        }
        return v;
    }

    public void setActivityCallbacks(ImageActivityMainFragmentHandler handler) {
        mActivityCallbacks = handler;
    }

    public void setImageId(int imageId) {
        mImageId = imageId;
    }

    public void onDetailedImageFetched() {
        if (getView() != null) {
            displayImageInRootView(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageDetailedView != null) {
            imageDetailedView.saveInstanceState(outState);
        }
    }

    private void displayImageInRootView(@Nullable Bundle savedInstanceState) {
        showDetailedView(savedInstanceState);
        loadImage();
    }

    private void showDetailedView(@Nullable Bundle savedInstanceState) {
        imageDetailedView.displayDetailedView(
                getChildFragmentManager(),
                getArguments().getBoolean(EXTRAS_IS_USER_LOGGED_IN),
                new ImageTagView.OnTagClickListener() {
                    @Override
                    public void onTagClicked(int tagId) {
                        mActivityCallbacks.openTagInformation(tagId);
                    }
                }, new ImageDetailedView.ImageDetailedViewHandler() {
                    @Override
                    public DerpibooruFilter getUserFilter() {
                        return getArguments().getParcelable(EXTRAS_USER_FILTER);
                    }

                    @Override
                    public DerpibooruImageDetailed getImage() {
                        return mActivityCallbacks.getImage();
                    }

                    @Override
                    public void requestImageDownloadPermissions() {
                        requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_STORAGE);
                    }
                },
                savedInstanceState);
    }

    private void loadImage() {
        String url = mActivityCallbacks.getImage().getThumb().getLargeImageUrl();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_WRITE_STORAGE)
                && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if (imageDetailedView != null) {
                imageDetailedView.onImageDownloadPermissionsGranted();
            }
        }
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
            if (getView() != null) getView().findViewById(R.id.progressImage).setVisibility(View.GONE);
            attachPhotoView(mImageView);
            return false;
        }

        private void attachPhotoView(ImageView target) {
            new PhotoViewAttacher(target).setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    imageDetailedView.toggleView();
                }
            });
        }
    }

    public interface ImageActivityMainFragmentHandler {
        DerpibooruImageDetailed getImage();
        void openTagInformation(int tagId);
    }
}
