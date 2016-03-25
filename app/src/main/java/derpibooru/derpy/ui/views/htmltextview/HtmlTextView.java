package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

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

    public final void setOnLinkClickListener(OnLinkClickListener listener) {
        mLinkListener = listener;
    }

    public final void setHtml(String html) {
        setText(getSpannableStringBuilderFromHtml(html));
        setHtmlMovementMethod();
    }

    protected void setHtmlMovementMethod() {
        /* http://stackoverflow.com/a/2746708/1726690 */
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected CharSequence getCharSequenceFromHtml(String html) {
        return Html.fromHtml(html);
    }

    private SpannableStringBuilder getSpannableStringBuilderFromHtml(String html) {
        /* http://stackoverflow.com/a/19989677/1726690 */
        CharSequence sequence = getCharSequenceFromHtml(html);
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
}

