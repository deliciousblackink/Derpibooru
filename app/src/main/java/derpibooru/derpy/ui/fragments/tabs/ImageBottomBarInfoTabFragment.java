package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.utils.TextViewHtmlDisplayer;
import derpibooru.derpy.ui.views.FlowLayout;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageBottomBarInfoTabFragment extends Fragment {
    private TextViewHtmlDisplayer mHtmlPresenter;
    private ImageTagView.OnTagClickListener mTagListener;

    @Bind(R.id.textUploaded) TextView textUploaded;
    @Bind(R.id.textDescription) TextView textDescription;
    @Bind(R.id.layoutImageTags) FlowLayout layoutTags;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_image_bottom_bar_info, container, false);
        ButterKnife.bind(this, v);
        mHtmlPresenter = new TextViewHtmlDisplayer() {
            @Override
            protected void onLinkClick(String url) {

            }
        };
        display((DerpibooruImageDetailed) getArguments().getParcelable(ImageActivity.EXTRAS_IMAGE_DETAILED));
        return v;
    }

    public void setOnTagClickListener(ImageTagView.OnTagClickListener listener) {
        mTagListener = listener;
        if ((layoutTags != null) && (layoutTags.getChildCount() > 0)) {
            for (int i = 0; i < layoutTags.getChildCount(); i++) {
                View v = layoutTags.getChildAt(i);
                if (v instanceof ImageTagView) {
                    ((ImageTagView) v).setOnTagClickListener(mTagListener);
                }
            }
        }
    }

    private void display(DerpibooruImageDetailed info) {
        setImageUploader(info);
        setImageDescription(info);
        setImageTags(info);
        textUploaded.setVisibility(View.VISIBLE);
        textDescription.setVisibility(View.VISIBLE);
        layoutTags.setVisibility(View.VISIBLE);
    }

    private void setImageUploader(DerpibooruImageDetailed info) {
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
        mHtmlPresenter.textFromHtml(textUploaded, uploaderHtml);
    }

    private void setImageDescription(DerpibooruImageDetailed info) {
        if (!info.getDescription().isEmpty()) {
            textDescription.setText(Html.fromHtml(info.getDescription()));
        } else {
            textDescription.setVisibility(View.GONE);
        }
    }

    private void setImageTags(DerpibooruImageDetailed info) {
        for (DerpibooruTag tag : info.getTags()) {
            ImageTagView itv = new ImageTagView(getActivity());
            itv.setTagInfo(tag);
            itv.setOnTagClickListener(mTagListener);
            layoutTags.addView(itv);
        }
    }
}
