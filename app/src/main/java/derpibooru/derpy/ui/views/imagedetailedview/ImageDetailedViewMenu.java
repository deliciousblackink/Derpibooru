package derpibooru.derpy.ui.views.imagedetailedview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruTag;

/**
 * A wrapper for {@link ImageDetailedView}'s toolbar menu.
 * Inflates the menu layout from XML, sets menu item listeners and provides behaviors for them
 * ({@link ImageDownload} and {@link ImageShare}.
 */
abstract class ImageDetailedViewMenu implements Toolbar.OnMenuItemClickListener {
    private final Context mContext;
    private final Toolbar mToolbar;
    private final DerpibooruImageDetailed mImageInfo;

    private ImageDownload mImageDownload;
    private ImageShare mImageShare;
    private ImageLinkShare mImageLinkShare;

    ImageDetailedViewMenu(Context context, Toolbar toolbar, DerpibooruImageDetailed imageInfo) {
        mContext = context;
        mToolbar = toolbar;
        mImageInfo = imageInfo;
    }

    abstract void requestImageDownloadPermissions();

    void onImageDownloadPermissionsGranted() {
        mImageDownload.start();
    }

    void onImageLoaded(@Nullable GlideDrawable glideResource) {
        if (!mImageShare.enableSharing(
                glideResource, mImageInfo.getThumb().getId(), getImageTagNames())) {
            mToolbar.getMenu().findItem(R.id.actionShareImage).setVisible(false);
            mImageShare = null;
        }
    }

    void initialize() {
        initializeImageDownload();
        inflateToolbarMenu();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDownloadImage:
                if (hasStoragePermissions()) {
                    onImageDownloadPermissionsGranted();
                } else {
                    requestImageDownloadPermissions();
                }
                break;
            case R.id.actionShareLink:
                mImageLinkShare.resetProviderIntent();
                break;
            case R.id.actionShareImage:
                if ((mImageShare == null) || (!mImageShare.isSharingEnabled())) {
                    Toast.makeText(mContext, R.string.share_image_provider_not_initialized, Toast.LENGTH_SHORT).show();
                } else {
                    mImageShare.resetProviderIntent();
                }
                break;
        }
        return true;
    }

    private void inflateToolbarMenu() {
        mToolbar.inflateMenu(R.menu.menu_image_activity_main_fragment);
        mToolbar.setOnMenuItemClickListener(this);
        if (mImageDownload == null) {
            mToolbar.getMenu().findItem(R.id.actionDownloadImage).setVisible(false);
        }
        initializeShareProvidersInMenu(mToolbar.getMenu());
    }

    private void initializeImageDownload() {
        mImageDownload = new ImageDownload(
                mContext, mImageInfo.getThumb().getId(), getImageTagNames(), mImageInfo.getDownloadUrl());
        if (hasStoragePermissions() && mImageDownload.isDownloaded()) {
            mImageDownload = null;
        }
    }

    private void initializeShareProvidersInMenu(Menu menu) {
        ShareActionProvider shareProvider = getNewInstanceOfShareActionProvider();

        MenuItemCompat.setActionProvider(menu.findItem(R.id.actionShareImage), shareProvider);
        mImageShare = new ImageShare(mContext, shareProvider);

        MenuItemCompat.setActionProvider(menu.findItem(R.id.actionShareLink), shareProvider);
        mImageLinkShare = new ImageLinkShare(mContext, shareProvider);
        mImageLinkShare.enableSharing(mImageInfo.getThumb().getId(), mImageInfo.getTags());
    }

    private ShareActionProvider getNewInstanceOfShareActionProvider() {
        return new ShareActionProvider(mContext) {
            @Override
            public View onCreateActionView() {
                return null; /* hide default share action icon */
            }
        };
    }

    private String getImageTagNames() {
        StringBuilder tagListBuilder = new StringBuilder();
        for (DerpibooruTag tag : mImageInfo.getTags()) {
            tagListBuilder.append(tag.getName());
            tagListBuilder.append(", ");
        }
        tagListBuilder.delete(tagListBuilder.length() - 2, tagListBuilder.length()); /* remove ', ' */
        return tagListBuilder.toString();
    }

    private boolean hasStoragePermissions() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
