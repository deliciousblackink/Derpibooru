package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedFilteredImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ExternalGifImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

abstract class ImageActionDrawableWrapper extends AdaptiveBoundsGlideResourceDrawable {
    private ImageAction mImageAction;
    private Paint mTextPaint;

    protected abstract Context getContext();

    protected abstract TextView getDrawableHolder();

    @Nullable
    public ImageAction getImageAction() {
        return mImageAction;
    }

    /**
     * Sets the drawable resource ({@link GlideDrawable} and its bounds
     * according to both the size of the resource and the size of the holder view,
     * refreshes the source.
     *
     * @param imageAction an {@link ImageAction} associated with the resource, or {@code null} if there isn't one.
     */
    public void setResource(GlideDrawable resource, @Nullable ImageAction imageAction) {
        mImageAction = imageAction;
        /* there's no height limit (it's adaptive), hence the width is passed as the limit for both dimensions */
        setResource(resource, getDrawableHolder(),
                    getDrawableHolder().getMeasuredWidth(), getDrawableHolder().getMeasuredWidth());
        initializeTextPaint(imageAction);
    }

    private void initializeTextPaint(@Nullable ImageAction imageAction) {
        if (imageAction instanceof ExternalGifImageAction ||
                (imageAction instanceof EmbeddedFilteredImageAction && ((EmbeddedFilteredImageAction) imageAction).isSpoilered())) {
            mTextPaint = new Paint();
            mTextPaint.setARGB(255, 255, 255, 255);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            getResource().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.colorDarkSemiTransparentOverlay), PorterDuff.Mode.SRC_ATOP);
        } else {
            mTextPaint = null;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mTextPaint != null) {
            if (mImageAction instanceof ExternalGifImageAction) {
                mTextPaint.setTextSize((float) (getResource().getBounds().width() * 0.2));
                drawTextCentered("▶", canvas);
            }
            if (mImageAction instanceof EmbeddedFilteredImageAction) {
                mTextPaint.setTextSize((float) (getResource().getBounds().width() * 0.1));
                drawTextCentered(((EmbeddedFilteredImageAction) mImageAction).getFilteredTagName(), canvas);
            }
        }
    }

    private void drawTextCentered(String text, Canvas target) {
        /* http://stackoverflow.com/a/11121873/1726690 */
        int xPos = (getResource().getBounds().width() / 2);
        int yPos = (int) ((getResource().getBounds().height() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        target.drawText(text, xPos, yPos, mTextPaint);
    }

    @Override
    protected void setDrawableInHolder(View drawableHolder, int[] widthHeight) {
        getDrawableHolder().setText(getDrawableHolder().getText());
        getDrawableHolder().requestLayout();
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(who);
        } else {
            getDrawableHolder().invalidate();
        }
    }
}
