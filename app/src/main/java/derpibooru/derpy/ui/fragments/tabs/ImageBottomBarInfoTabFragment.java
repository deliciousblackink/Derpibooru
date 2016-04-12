package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.representations.ServerDate;
import derpibooru.derpy.ui.views.FlowLayout;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageBottomBarInfoTabFragment extends Fragment {
    private ImageTagView.OnTagClickListener mTagListener;

    @Bind(R.id.textUploaded) TextView textUploaded;
    @Bind(R.id.textDescription) TextView textDescription;
    @Bind(R.id.layoutImageTags) FlowLayout layoutTags;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_image_bottom_bar_info, container, false);
        ButterKnife.bind(this, v);
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
        String uploader = String.format(getString(R.string.image_info_uploaded),
                                        info.getUploader(),
                                        new ServerDate(info.getCreatedAt())
                                                .getFormattedTimeString("E, MMM dd yyyy HH:mm", Locale.ENGLISH));
        textUploaded.setText(uploader);
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
