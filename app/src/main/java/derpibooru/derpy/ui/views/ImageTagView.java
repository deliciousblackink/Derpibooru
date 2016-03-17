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
    private int mTextViewBackgroundColorId;
    private int mTextViewBackgroundActiveColorId;

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
        setTextSpanStyle(active ? mTextSpanActiveStyleId
                                : mTextSpanStyleId);
        setTextBackgroundColor(active ? mTextViewBackgroundActiveColorId
                                      : mTextViewBackgroundColorId);
    }

    public void setTagInfo(DerpibooruTag tag) {
        mTag = tag;
        inflateView();
        setColorsAccordingToTagType();
        setTextSpanStyle(mTextSpanStyleId);
        setTextBackgroundColor(mTextViewBackgroundColorId);
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
                mTextViewBackgroundColorId = R.color.colorImageArtistTag;
                mTextViewBackgroundActiveColorId = R.color.colorImageArtistTagActive;
                break;
            case ContentSafety:
                mTextSpanStyleId = R.style.ImageTagContentSafety;
                mTextSpanActiveStyleId = R.style.ImageTagContentSafetyActive;
                mTextViewBackgroundColorId = R.color.colorImageContentSafetyTag;
                mTextViewBackgroundActiveColorId = R.color.colorImageContentSafetyTagActive;
                break;
            case General:
                mTextSpanStyleId = R.style.ImageTagGeneral;
                mTextSpanActiveStyleId = R.style.ImageTagGeneralActive;
                mTextViewBackgroundColorId = R.color.colorImageGeneralTag;
                mTextViewBackgroundActiveColorId = R.color.colorImageGeneralTagActive;
                break;
        }
    }

    private void setTextSpanStyle(int tagNameSpanStyle) {
        String numberOfImages = String.format(" (%d)", mTag.getNumberOfImages());

        Spannable text = new SpannableString(mTag.getName() + numberOfImages);
        text.setSpan(new TextAppearanceSpan(getContext(), tagNameSpanStyle), 0,
                     mTag.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(getContext(), R.style.ImageTagNumberOfImages), mTag.getName().length(),
                     (mTag.getName().length() + numberOfImages.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textTag.setText(text, TextView.BufferType.SPANNABLE);
    }

    private void setTextBackgroundColor(int colorResourceId) {
        textTag.setBackgroundColor(ContextCompat.getColor(getContext(), colorResourceId));
    }

    public interface OnTagClickListener {
        void onTagClicked(int tagId);
    }
}
