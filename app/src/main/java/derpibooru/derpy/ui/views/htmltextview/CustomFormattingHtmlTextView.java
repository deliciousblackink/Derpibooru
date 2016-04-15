package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import java.util.regex.Matcher;

import derpibooru.derpy.server.parsers.CommentParser;

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
        Matcher embedded = CommentParser.PATTERN_EMBEDDED_IMAGE.matcher(url);
        if (!embedded.find()) {
            return super.onLinkClicked(url);
        }
        Matcher spoileredImageMatcher = CommentParser.PATTERN_SPOILERED_IMAGE_LINK.matcher(url);
        if (spoileredImageMatcher.find()) {
            unfilterImage(spoileredImageMatcher);
            return true;
        }
        Matcher hiddenImageMatcher = CommentParser.PATTERN_HIDDEN_IMAGE_LINK.matcher(url);
        if (hiddenImageMatcher.find()) {
            unfilterImage(hiddenImageMatcher);
            return true;
        }
        return super.onLinkClicked(url);
    }

    private void unfilterImage(Matcher filterMatcher) {
        String filterImageSource = filterMatcher.group(1);
        String mainImageSource = filterMatcher.group(2);
        ImageSpan oldSpan = getImageSpanWithSource(filterImageSource);
        EmbeddedImageDrawableWrapper wrapper = ((EmbeddedImageDrawableWrapper) oldSpan.getDrawable());
        new GlideImageGetter(getContext(), this).loadDrawableIntoWrapper(mainImageSource, wrapper);
    }

    @Nullable
    private ImageSpan getImageSpanWithSource(String imageSource) {
        ImageSpan[] imageSpans = ((SpannableString) getText()).getSpans(0, getText().length(), ImageSpan.class);
        for (ImageSpan span : imageSpans) {
            if (span.getSource().equals(imageSource)) {
                return span;
            }
        }
        return null;
    }
}