package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * @author https://gist.github.com/AndroidT/97bf92f94cadf8f7af72
 */
class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
    private final HtmlTextView mTargetTextView;
    private final Context mContext;

    GlideImageGetter(Context context, HtmlTextView target) {
        mContext = context;
        mTargetTextView = target;
    }

    @Override
    public Drawable getDrawable(String imageSource) {
        EmbeddedImageDrawableWrapper imageWrapper = new EmbeddedImageDrawableWrapper() {
            @Override
            protected TextView getDrawableHolder() {
                return mTargetTextView;
            }
        };
        loadIntoWrapper(imageSource, imageWrapper);
        return imageWrapper;
    }

    public void loadIntoWrapper(String imageSource, EmbeddedImageDrawableWrapper existingWrapper) {
        GlideViewTarget target;
        if (ImageActionSource.isImageActionSource(imageSource)) {
            ImageActionSource imageActionSource = new ImageActionSource(imageSource);
            imageSource = imageActionSource.getImageSource();
            target = new GlideViewTarget(existingWrapper, imageActionSource);
        } else {
            target = new GlideViewTarget(existingWrapper, imageSource);
        }
        Glide.with(mContext)
                .load(imageSource)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(target);
    }

    private class GlideViewTarget extends ViewTarget<TextView, GlideDrawable> {
        private final EmbeddedImageDrawableWrapper mWrapper;
        private final String mSource;
        private final int mImageSourceId;

        private GlideViewTarget(EmbeddedImageDrawableWrapper wrapper, String source) {
            super(mTargetTextView);
            mWrapper = wrapper;
            mSource = source;
            mImageSourceId = EmbeddedImageDrawableWrapper.NOT_LINKED_TO_IMAGE_ACTION;
        }

        private GlideViewTarget(EmbeddedImageDrawableWrapper wrapper, ImageActionSource actionSource) {
            super(mTargetTextView);
            mWrapper = wrapper;
            mSource = actionSource.getImageSource();
            mImageSourceId = actionSource.getActionLinkId();
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            mWrapper.setResource(resource, mSource, mImageSourceId);
        }
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        mTargetTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) { }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) { }
}
