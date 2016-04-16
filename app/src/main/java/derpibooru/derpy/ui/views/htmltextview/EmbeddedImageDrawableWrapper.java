package derpibooru.derpy.ui.views.htmltextview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import derpibooru.derpy.ui.animators.DrawableBoundAnimator;

abstract class EmbeddedImageDrawableWrapper extends Drawable implements Drawable.Callback {
    private GlideDrawable mGlideResource;
    private String mSource;

    protected abstract TextView getDrawableHolder();

    public String getSource() {
        return mSource;
    }

    public boolean isAnimated() {
        return mGlideResource.isAnimated();
    }

    public void playAnimatedDrawable() {
        mGlideResource.setLoopCount(1);
        mGlideResource.start();
    }

    /**
     * Sets the drawable resource ({@link GlideDrawable} and its bounds
     * according to both the size of the resource and the size of the holder view,
     * refreshes the source.
     */
    public void setResource(GlideDrawable resource, String resourceSource) {
        mSource = resourceSource;
        if (mGlideResource != null) {
            mGlideResource.setCallback(null);
        }
        mGlideResource = resource;
        mGlideResource.setCallback(this);
        determineResourceBounds();
    }

    private void determineResourceBounds() {
        /* testing on JB, I've noticed that if resource's width matches that of the view, the right side gets chopped off.
         * I think it may be related to padding or something; until that's fixed, a 90% limit will do. */
        int maxResourceWidth = (int) (getDrawableHolder().getWidth() * 0.9);
        float width = mGlideResource.getIntrinsicWidth();
        float height = mGlideResource.getIntrinsicHeight();
        if (width >= maxResourceWidth) {
            float downScale = width / maxResourceWidth;
            width /= downScale;
            height /= downScale;
        }
        int roundedWidth = Math.round(width);
        int roundedHeight = Math.round(height);
        mGlideResource.setBounds(0, 0, roundedWidth, roundedHeight);
        setBounds(0, 0, roundedWidth, roundedHeight);
        getDrawableHolder().setText(getDrawableHolder().getText());
        getDrawableHolder().requestLayout();
    }

    public void animateBoundsChange(int right, int bottom) {
        new DrawableBoundAnimator(this, getDrawableHolder())
                .animateRightBottom(right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mGlideResource != null) mGlideResource.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mGlideResource != null) mGlideResource.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mGlideResource != null) mGlideResource.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return (mGlideResource != null) ? mGlideResource.getOpacity() : 0;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(who);
        } else {
            getDrawableHolder().invalidate();
        }
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
