package derpibooru.derpy.ui.views.htmltextview;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

abstract class ImageActionDrawableWrapper extends AdaptiveBoundsGlideResourceDrawable {
    private ImageAction mImageAction;

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
