package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
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
    private HtmlTextView mTargetTextView;
    private Context mContext;

    GlideImageGetter(Context context, HtmlTextView target) {
        mContext = context;
        mTargetTextView = target;
    }

    @Override
    public Drawable getDrawable(String source) {
        GlideDrawableWrapper drawable = new GlideDrawableWrapper();
        Glide.with(mContext)
                .load(source)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideViewTarget(drawable));
        return drawable;
    }

    private class GlideViewTarget extends ViewTarget<TextView, GlideDrawable> {
        private final GlideDrawableWrapper mDrawable;

        private GlideViewTarget(GlideDrawableWrapper drawable) {
            super(mTargetTextView);
            mDrawable = drawable;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            Rect rect;

            float width;
            float height;
            if (resource.getIntrinsicWidth() >= getView().getWidth()) {
                float downScale = (float) resource.getIntrinsicWidth() / getView().getWidth();
                width = (float) resource.getIntrinsicWidth() / (float) downScale;
                height = (float) resource.getIntrinsicHeight() / (float) downScale;
            } else {
                    /* float multiplier = (float) getView().getWidth() / resource.getIntrinsicWidth();
                    width = (float) resource.getIntrinsicWidth() * (float) multiplier;
                    height = (float) resource.getIntrinsicHeight() * (float) multiplier;*/
                width = (float) resource.getIntrinsicWidth() ;
                height = (float) resource.getIntrinsicHeight();
            }

            rect = new Rect(0, 0, Math.round(width), Math.round(height));

            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            if (resource.isAnimated()) {
                mDrawable.setCallback(GlideImageGetter.this);
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }
    }

    private class GlideDrawableWrapper extends Drawable implements Drawable.Callback {
        private GlideDrawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) mDrawable.draw(canvas);
        }

        @Override
        public void setAlpha(int alpha) {
            if (mDrawable != null) mDrawable.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            if (mDrawable != null) mDrawable.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return (mDrawable != null) ? mDrawable.getOpacity() : 0;
        }

        public void setDrawable(GlideDrawable drawable) {
            if (mDrawable != null) {
                mDrawable.setCallback(null);
            }
            drawable.setCallback(this);
            mDrawable = drawable;
        }

        @Override
        public void invalidateDrawable(Drawable who) {
            if (getCallback() != null) getCallback().invalidateDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            if (getCallback() != null) getCallback().scheduleDrawable(who, what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            if (getCallback() != null) getCallback().unscheduleDrawable(who, what);
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
