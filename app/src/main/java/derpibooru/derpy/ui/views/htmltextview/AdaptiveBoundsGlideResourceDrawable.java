package derpibooru.derpy.ui.views.htmltextview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

class AdaptiveBoundsGlideResourceDrawable extends Drawable implements Drawable.Callback {
    private GlideDrawable mGlideResource;

    public boolean isAnimated() {
        return mGlideResource.isAnimated();
    }

    public void playAnimated(boolean loop) {
        mGlideResource.setLoopCount(loop ? GlideDrawable.LOOP_FOREVER : 1);
        mGlideResource.start();
    }

    /**
     * Sets the drawable resource ({@link GlideDrawable} and its bounds
     * according to both the size of the resource and the size of the holder view,
     * refreshes the source.
     *
     * @param holder the view the drawable is placed inside
     * @param holderMaxWidth max width of the drawable holder view
     * @param holderMaxHeight max height of the drawable holder view
     */
    public void setResource(GlideDrawable resource, View holder, int holderMaxWidth, int holderMaxHeight) {
        if (mGlideResource != null) {
            mGlideResource.setCallback(null);
        }
        mGlideResource = resource;
        mGlideResource.setCallback(this);
        int[] widthHeight = determineWidthHeightBounds(holderMaxWidth, holderMaxHeight);
        setDrawableBounds(widthHeight);
        setDrawableInHolder(holder, widthHeight);
    }

    protected int[] determineWidthHeightBounds(int holderMaxWidth, int holderMaxHeight) {
        /* testing on JB, I've noticed that if resource's width matches that of the view, the right side gets chopped off.
         * I think it may be related to padding or something; until that's fixed, a 90% limit will do. */
        int maxResourceWidth = (int) (holderMaxWidth * 0.9);
        int maxResourceHeight = (int) (holderMaxHeight * 0.9);
        /* on high-density screens, embedded images end up tiny since they are all ~300px in width, hence upscaling */
        int minResourceWidth = (int) (holderMaxWidth * 0.6);
        float width = mGlideResource.getIntrinsicWidth();
        float height = mGlideResource.getIntrinsicHeight();
        if ((width > maxResourceWidth) || (height > maxResourceHeight)) {
            float downScale = Math.max((width / maxResourceWidth), (height / maxResourceHeight));
            width /= downScale;
            height /= downScale;
        } else if (width < minResourceWidth) {
            float upScale = minResourceWidth / width;
            width *= upScale;
            height *= upScale;
        }
        return new int[] { Math.round(width), Math.round(height) }; /* android.util.Size is much more preferable, but it's only available in API 21+ */
    }

    protected void setDrawableBounds(int[] widthHeight) {
        mGlideResource.setBounds(0, 0, widthHeight[0], widthHeight[1]);
        setBounds(0, 0, widthHeight[0], widthHeight[1]);
    }

    protected void setDrawableInHolder(View drawableHolder, int[] widthHeight) {
        drawableHolder.getLayoutParams().width = widthHeight[0];
        drawableHolder.getLayoutParams().height = widthHeight[1];
        drawableHolder.requestLayout();
        if (drawableHolder instanceof ImageView) {
            ((ImageView) drawableHolder).setImageDrawable(mGlideResource);
        }
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
