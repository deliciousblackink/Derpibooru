package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.google.common.base.Objects;

import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedFilteredImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ExternalGifImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

public class HtmlPostBodyTextView extends HtmlTextView {
    private FragmentManager mDialogFragmentManager;

    public HtmlPostBodyTextView(Context context) {
        super(context);
    }

    public HtmlPostBodyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlPostBodyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDialogFragmentManager(FragmentManager fragmentManager) {
        mDialogFragmentManager = fragmentManager;
    }

    @Override
    protected void setHtmlMovementMethod() {
        setMovementMethod(new CustomFormattingMovementMethod());
    }

    @Override
    protected CharSequence getCharSequenceFromHtml(String html) {
        return Html.fromHtml(
                html, new GlideImageGetter(getContext(), this), new CustomFormattingTagHandler(getContext()));
    }

    @Override
    protected boolean onLinkClicked(String url) {
        if (ImageAction.doesStringRepresentImageAction(url)) {
            ImageActionDrawableWrapper drawableWrapper =
                    getWrapperByImageAction(ImageAction.fromStringRepresentation(url));
            ImageAction wrapperAction = drawableWrapper.getImageAction(); /* the link contains ImageAction in its initial state; the wrapper updates the object as it changes */
            if (wrapperAction instanceof EmbeddedFilteredImageAction) {
                EmbeddedFilteredImageAction filteredImageAction = (EmbeddedFilteredImageAction) wrapperAction;
                if (filteredImageAction.isSpoilered()) {
                    filteredImageAction.unspoiler();
                    new GlideImageGetter(getContext(), this).loadImageActionIntoWrapper(wrapperAction, drawableWrapper);
                    return true;
                } else {
                    openImageDialog(wrapperAction.toStringRepresentation());
                    return true;
                }
            }
            if ((wrapperAction instanceof EmbeddedImageAction)
                    || (wrapperAction instanceof ExternalGifImageAction)) {
                openImageDialog(url);
                return true;
            }
        }
        return super.onLinkClicked(url);
    }

    private void openImageDialog(String imageActionRepresentation) {
        Bundle args = new Bundle();
        args.putString(ImageActionDialogFragment.EXTRAS_IMAGE_ACTION_REPRESENTATION, imageActionRepresentation);
        ImageActionDialogFragment dialog = new ImageActionDialogFragment();
        dialog.setArguments(args);
        dialog.show(mDialogFragmentManager, "imagedialog");
    }

    @Nullable
    private ImageActionDrawableWrapper getWrapperByImageAction(ImageAction action) {
        ImageSpan[] imageSpans = ((SpannableString) getText()).getSpans(0, getText().length(), ImageSpan.class);
        for (ImageSpan span : imageSpans) {
            ImageActionDrawableWrapper wrapper = (ImageActionDrawableWrapper) span.getDrawable();
            if (Objects.equal(wrapper.getImageAction(), action)) {
                return wrapper;
            }
        }
        return null;
    }
}