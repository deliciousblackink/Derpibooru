package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.text.Html;
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
}
