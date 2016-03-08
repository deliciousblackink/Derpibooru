package derpibooru.derpy.ui.fragments.tabs;

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
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.ui.views.FlowLayout;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageBottomBarInfoTabFragment extends ImageBottomBarTabFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_image_bottom_bar_info, container, false);
        if (getArguments().containsKey("info")) {
            displayInfoInView(v, (DerpibooruImageDetailed) getArguments().getParcelable("info"));
        }
        return v;
    }

    @Override
    protected void displayInfoInView(View target, DerpibooruImageDetailed info) {
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

    private void setImageUploader(DerpibooruImageDetailed info, View layout) {
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

    private void setImageDescription(DerpibooruImageDetailed info, View layout) {
        if (!info.getDescription().equals("")) {
            ((TextView) layout.findViewById(R.id.textDescription))
                    .setText(Html.fromHtml(info.getDescription()));
        } else {
            layout.findViewById(R.id.textDescription)
                    .setVisibility(View.GONE);
        }
    }

    private void setImageTags(DerpibooruImageDetailed info, View layout) {
        FlowLayout l = (FlowLayout) layout.findViewById(R.id.layoutImageTags);
        for (DerpibooruTag tag : info.getTags()) {
            ImageTagView itv = new ImageTagView(getActivity());
            itv.setTagInfo(tag);
            l.addView(itv);
        }
    }
}
