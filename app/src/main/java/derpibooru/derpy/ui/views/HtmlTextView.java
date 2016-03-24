package derpibooru.derpy.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

public class HtmlTextView extends TextView {
    private OnLinkClickListener mLinkListener;

    public HtmlTextView(Context context) {
        super(context);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnLinkClickListener(OnLinkClickListener listener) {
        mLinkListener = listener;
    }

    public void setHtml(String html) {
        setText(getSpannableStringBuilderFromHtml(html));
        /* http://stackoverflow.com/a/2746708/1726690 */
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    private SpannableStringBuilder getSpannableStringBuilderFromHtml(String html) {
        /* http://stackoverflow.com/a/19989677/1726690 */
        CharSequence sequence = Html.fromHtml(html, new GlideImageGetter(), null);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        return strBuilder;
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, URLSpan span) {
        final String url = span.getURL();
        /* http://stackoverflow.com/a/19989677/1726690 */
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                onLinkClick(url);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void onLinkClick(String url) {
        if (mLinkListener != null) {
            mLinkListener.onLinkClick(url);
        }
    }

    public interface OnLinkClickListener {
        void onLinkClick(String linkUrl);
    }

    /**
     * @author https://gist.github.com/AndroidT/97bf92f94cadf8f7af72
     */
    private class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
        @Override
        public Drawable getDrawable(String source) {
            GlideDrawableWrapper drawable = new GlideDrawableWrapper();
            Glide.with(getContext())
                    .load(source)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new GlideViewTarget(drawable));
            return drawable;
        }

        private class GlideViewTarget extends ViewTarget<TextView, GlideDrawable> {
            private final GlideDrawableWrapper mDrawable;

            private GlideViewTarget(GlideDrawableWrapper drawable) {
                super(HtmlTextView.this);
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
            HtmlTextView.this.invalidate();
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) { }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) { }
    }
}
