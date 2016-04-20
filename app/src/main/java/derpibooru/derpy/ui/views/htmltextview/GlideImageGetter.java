package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

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
    public Drawable getDrawable(String rawSource) {
        ImageActionDrawableWrapper imageWrapper = new ImageActionDrawableWrapper() {
            @Override
            protected TextView getDrawableHolder() {
                return mTargetTextView;
            }
        };
        loadIntoWrapper(rawSource, imageWrapper);
        return imageWrapper;
    }

    public void loadImageActionIntoWrapper(ImageAction imageAction, ImageActionDrawableWrapper targetWrapper) {
        GlideViewTarget target = new GlideViewTarget(targetWrapper, imageAction);
        loadWithGlide(imageAction.getImageSource(), target);
    }

    private void loadIntoWrapper(String rawSource, ImageActionDrawableWrapper targetWrapper) {
        ImageAction imageAction = null;
        if (ImageAction.doesStringRepresentImageAction(rawSource)) {
            imageAction = ImageAction.fromStringRepresentation(rawSource);
        }
        GlideViewTarget target = new GlideViewTarget(targetWrapper, imageAction);
        loadWithGlide(((imageAction != null) ? imageAction.getImageSource() : rawSource), target);
    }

    private void loadWithGlide(String imageUrl, GlideViewTarget target) {
        Glide.with(mContext)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(target);
    }

    private class GlideViewTarget extends ViewTarget<TextView, GlideDrawable> {
        private final ImageActionDrawableWrapper mWrapper;
        private final ImageAction mImageAction;

        private GlideViewTarget(ImageActionDrawableWrapper wrapper, @Nullable ImageAction imageAction) {
            super(mTargetTextView);
            mWrapper = wrapper;
            mImageAction = imageAction;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            mWrapper.setResource(resource, mImageAction);
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
