package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import derpibooru.derpy.data.server.DerpibooruImageDetailed;

public abstract class ImageBottomBarTabFragment extends Fragment {
    protected ImageBottomBarTabFragment() {
        setArguments(new Bundle());
    }

    protected void setTextViewFromHtml(TextView view, String html) {
        /* http://stackoverflow.com/a/19989677/1726690 */
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        view.setText(strBuilder);
        /* http://stackoverflow.com/a/2746708/1726690 */
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, URLSpan span) {
        /* http://stackoverflow.com/a/19989677/1726690 */
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                onLinkClick(view);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public void onTabInfoFetched(DerpibooruImageDetailed info) {
        if (getView() != null) {
            displayInfoInView(getView(), info);
        }
    }

    protected abstract void displayInfoInView(View target, DerpibooruImageDetailed info);

    protected abstract void onLinkClick(View view);
}
