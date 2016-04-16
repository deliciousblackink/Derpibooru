package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

public class CustomFormattingHtmlTextView extends HtmlTextView {
    public CustomFormattingHtmlTextView(Context context) {
        super(context);
    }

    public CustomFormattingHtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFormattingHtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setHtmlMovementMethod() {
        setMovementMethod(new CustomFormattingMovementMethod());
    }

    @Override
    protected CharSequence getCharSequenceFromHtml(String html) {
        return Html.fromHtml(
                html, new GlideImageGetter(getContext(), this), new CustomFormattingTagHandler(getContext()));
    }

    @Override
    protected boolean onLinkClicked(String url) {
        ImageActionLink actionLink = new ImageActionLink(url);
        if (!actionLink.containsAction()) {
            return super.onLinkClicked(url);
        }
        String gifImage = actionLink.getGifImageSource();
        if (gifImage != null) {
            return true;
        }
        ImageActionLink.EmbeddedImageActions embeddedImage =
                ImageActionLink.EmbeddedImageActions.forLink(url);
        if (!embeddedImage.getFilterImage().isEmpty()) {
            unfilterImage(embeddedImage.getFilterImage(), embeddedImage.getSourceImage());
            return true;
        }
        return super.onLinkClicked(url);
    }

    private void unfilterImage(String filterImage, String mainImage) {
        ImageSpan span = getImageSpanWithSource(filterImage);
        EmbeddedImageDrawableWrapper imageWrapper =
                ((EmbeddedImageDrawableWrapper) span.getDrawable());
        new GlideImageGetter(getContext(), this)
                .loadIntoWrapper(mainImage, imageWrapper);
    }

    @Nullable
    private ImageSpan getImageSpanWithSource(String imageSource) {
        ImageSpan[] imageSpans = ((SpannableString) getText()).getSpans(0, getText().length(), ImageSpan.class);
        for (ImageSpan span : imageSpans) {
            if (((EmbeddedImageDrawableWrapper) span.getDrawable()).getSource().equals(imageSource)) {
                return span;
            }
        }
        return null;
    }
}