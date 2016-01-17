package derpibooru.derpy.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruTag;

public class ImageTagView extends LinearLayout {
    public ImageTagView(Context context) {
        super(context);
    }

    public ImageTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTagInfo(DerpibooruTag tag) {
        View view = inflate(getContext(), R.layout.view_image_tag, null);
        addView(view);

        String numberOfImages = " (" + Integer.toString(tag.getNumberOfImages()) + ")";
        Spannable text = new SpannableString(tag.getName() + numberOfImages);

        int textSpanStyleId = 0;
        int textViewBackgroundColorId = 0;
        switch (tag.getType()) {
            case Artist:
                textSpanStyleId = R.style.ImageTagArtist;
                textViewBackgroundColorId = R.color.colorImageArtistTag;
                break;
            case ContentSafety:
                textSpanStyleId = R.style.ImageTagContentSafety;
                textViewBackgroundColorId = R.color.colorImageContentSafetyTag;
                break;
            case General:
                textSpanStyleId = R.style.ImageTagGeneral;
                textViewBackgroundColorId = R.color.colorImageGeneralTag;
                break;
        }
        int textSpanNumberOfImagesStyleId = R.style.ImageTagNumberOfImages;

        text.setSpan(new TextAppearanceSpan(getContext(), textSpanStyleId),
                     0, tag.getName().length(),
                     Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(getContext(), textSpanNumberOfImagesStyleId),
                     tag.getName().length(), (tag.getName().length() + numberOfImages.length()),
                     Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) view.findViewById(R.id.textTag);
        textView.setText(text, TextView.BufferType.SPANNABLE);

        textView.setBackgroundColor(ContextCompat.getColor(getContext(),
                                                           textViewBackgroundColorId));
    }
}
