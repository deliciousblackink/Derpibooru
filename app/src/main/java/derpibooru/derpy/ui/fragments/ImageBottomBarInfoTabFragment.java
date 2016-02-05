package derpibooru.derpy.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;
import derpibooru.derpy.ui.views.FlowLayout;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageBottomBarInfoTabFragment extends Fragment {
    private ImageBottomBarTabAdapter.ViewPagerContentHeightChangeHandler mContentHeightHandler;

    public ImageBottomBarInfoTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_image_info_tab, container, false);

        DerpibooruImageInfo info = this.getArguments().getParcelable("image_info");

        setImageUploader(info, v);
        setImageDescription(info, v);
        setImageTags(info, v);

        if (mContentHeightHandler != null) {
            v.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new LayoutHeightHandler((LinearLayout) v));
        }

        return v;
    }

    public void setContentHeightHandler(ImageBottomBarTabAdapter
                                                .ViewPagerContentHeightChangeHandler handler) {
        mContentHeightHandler = handler;
    }

    private void onLinkClick(View v) {
        /* TODO: handle profile view/image link/external link */
    }

    private void setImageUploader(DerpibooruImageInfo info, View layout) {
        String uploaderHtml;
        try {
            Date imageCreatedAt;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            imageCreatedAt = format.parse(info.getCreatedAt());
            format = new SimpleDateFormat("E, MMM dd yyyy HH:mm", Locale.ENGLISH);

            uploaderHtml = String.format("Uploaded by <a href=\"%s\">%s</a><br>on %s",
                                         info.getUploader(), info.getUploader(),
                                         format.format(imageCreatedAt));
        } catch (ParseException e) {
            Log.e("ImageInfoTabFragment", "setImageUploader: " + e.getMessage());
            uploaderHtml = String.format("Uploaded by <a href=\"%s\">%s</a>",
                                         info.getUploader(), info.getUploader());
        }

        setTextViewFromHtml(((TextView) layout.findViewById(R.id.textUploaded)),
                            uploaderHtml);
    }

    private void setImageDescription(DerpibooruImageInfo info, View layout) {
        if (!info.getDescription().equals("")) {
            ((TextView) layout.findViewById(R.id.textDescription))
                    .setText(Html.fromHtml(info.getDescription()));
        } else {
            layout.findViewById(R.id.textDescription)
                    .setVisibility(View.GONE);
        }
    }

    private void setImageTags(DerpibooruImageInfo info, View layout) {
        FlowLayout l = (FlowLayout) layout.findViewById(R.id.layoutImageTags);
        for (DerpibooruTag tag : info.getTags()) {
            ImageTagView itv = new ImageTagView(getActivity());
            itv.setTagInfo(tag);
            l.addView(itv);
        }
    }

    private void setTextViewFromHtml(TextView view, String html) {
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

    private void makeLinkClickable(SpannableStringBuilder strBuilder, URLSpan span) {
        /* http://stackoverflow.com/a/19989677/1726690 */
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                onLinkClick(view);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private class LayoutHeightHandler implements ViewTreeObserver.OnGlobalLayoutListener {
        private LinearLayout mLinearLayout;

        public LayoutHeightHandler(LinearLayout root) {
            mLinearLayout = root;
        }

        @Override
        public void onGlobalLayout() {
            int height = 0;
            for (int x = 0; x < mLinearLayout.getChildCount(); x++){
                height += mLinearLayout.getChildAt(x).getMeasuredHeight();
            }
            mContentHeightHandler.childHeightUpdated(height);

            ViewTreeObserver obs = mLinearLayout.getViewTreeObserver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                obs.removeOnGlobalLayoutListener(this);
            } else {
                obs.removeGlobalOnLayoutListener(this);
            }
        }
    }
}
