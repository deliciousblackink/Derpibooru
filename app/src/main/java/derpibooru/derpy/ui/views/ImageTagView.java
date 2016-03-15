package derpibooru.derpy.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruTag;

public class ImageTagView extends LinearLayout {
    private DerpibooruTag mTag;
    private int mTextSpanStyleId;
    private int mTextSpanActiveStyleId;
    private int mTextSpanColorId;
    private int mTextViewBackgroundColorId;
    private int mTextSpanNumberOfImagesStyleId = R.style.ImageTagNumberOfImages;
    private int mTextSpanNumberOfImagesActiveStyleId = R.style.ImageTagNumberOfImagesActive;

    @Bind(R.id.textTag) TextView textTag;

    public ImageTagView(Context context) {
        super(context);
    }

    public ImageTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnTagClickListener(final OnTagClickListener listener) {
        findViewById(R.id.layoutTag).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTagClicked(mTag.getId());
            }
        });
    }

    @OnTouch(R.id.layoutTag)
    boolean toggleTagColorOnTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            toggleTagColor(true);
        } else if ((event.getActionMasked() == MotionEvent.ACTION_UP)
                || (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
                || (event.getActionMasked() == MotionEvent.ACTION_CANCEL)) {
            toggleTagColor(false);
        }
        return false;
    }

    private void toggleTagColor(boolean active) {
        setSpansWithStylesId(active ? mTextSpanActiveStyleId
                                    : mTextSpanStyleId,
                             active ? mTextSpanNumberOfImagesActiveStyleId
                                    : mTextSpanNumberOfImagesStyleId);
        setBackgroundColorById(active ? mTextSpanColorId
                                      : mTextViewBackgroundColorId);
    }

    public void setTagInfo(DerpibooruTag tag) {
        mTag = tag;
        inflateView();
        setColorsAccordingToTagType();
        setSpansWithStylesId(mTextSpanStyleId, mTextSpanNumberOfImagesStyleId);
        setBackgroundColorById(mTextViewBackgroundColorId);
    }

    private void inflateView() {
        View view = inflate(getContext(), R.layout.view_image_tag, null);
        ButterKnife.bind(this, view);
        addView(view);
    }

    private void setColorsAccordingToTagType() {
        switch (mTag.getType()) {
            case Artist:
                mTextSpanStyleId = R.style.ImageTagArtist;
                mTextSpanActiveStyleId = R.style.ImageTagArtistActive;
                mTextSpanColorId = R.color.colorImageArtistTagText;
                mTextViewBackgroundColorId = R.color.colorImageArtistTag;
                break;
            case ContentSafety:
                mTextSpanStyleId = R.style.ImageTagContentSafety;
                mTextSpanActiveStyleId = R.style.ImageTagContentSafetyActive;
                mTextSpanColorId = R.color.colorImageContentSafetyTagText;
                mTextViewBackgroundColorId = R.color.colorImageContentSafetyTag;
                break;
            case General:
                mTextSpanStyleId = R.style.ImageTagGeneral;
                mTextSpanActiveStyleId = R.style.ImageTagGeneralActive;
                mTextSpanColorId = R.color.colorImageGeneralTagText;
                mTextViewBackgroundColorId = R.color.colorImageGeneralTag;
                break;
        }
    }

    private void setSpansWithStylesId(int tagNameSpanStyle, int numberOfImagesSpanStyle) {
        String numberOfImages = String.format(" (%d)", mTag.getNumberOfImages());

        Spannable text = new SpannableString(mTag.getName() + numberOfImages);
        text.setSpan(new TextAppearanceSpan(getContext(), tagNameSpanStyle), 0,
                     mTag.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(getContext(), numberOfImagesSpanStyle), mTag.getName().length(),
                     (mTag.getName().length() + numberOfImages.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textTag.setText(text, TextView.BufferType.SPANNABLE);
    }

    private void setBackgroundColorById(int colorResourceId) {
        textTag.setBackgroundColor(ContextCompat.getColor(getContext(), colorResourceId));
    }

    public interface OnTagClickListener {
        void onTagClicked(int tagId);
    }
}
