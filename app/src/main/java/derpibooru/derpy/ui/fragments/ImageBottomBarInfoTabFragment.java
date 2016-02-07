package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.ui.views.FlowLayout;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageBottomBarInfoTabFragment extends ImageBottomBarTabFragment {
    public ImageBottomBarInfoTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_bottom_bar_info_tab, container, false);
        if (getArguments().containsKey("info")) {
            displayInfoInView(v, (DerpibooruImageInfo) getArguments().getParcelable("info"));
        }
        return v;
    }

    @Override
    protected void displayInfoInView(View target, DerpibooruImageInfo info) {
        setImageUploader(info, target);
        setImageDescription(info, target);
        setImageTags(info, target);
        target.findViewById(R.id.progressBottomBarTab).setVisibility(View.GONE);
        target.findViewById(R.id.textUploaded).setVisibility(View.VISIBLE);
        target.findViewById(R.id.textDescription).setVisibility(View.VISIBLE);
        target.findViewById(R.id.layoutImageTags).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLinkClick(View v) {
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
        super.setTextViewFromHtml(((TextView) layout.findViewById(R.id.textUploaded)),
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
}
