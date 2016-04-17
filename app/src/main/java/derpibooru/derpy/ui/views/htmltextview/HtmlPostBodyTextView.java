package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import derpibooru.derpy.ui.views.htmltextview.fragments.EmbeddedImageDialogFragment;

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
        ImageActionLink actionLink = new ImageActionLink(url);
        ImageActionLink.ImageActionType linkType = actionLink.getImageActionType();
        switch (linkType) {
            case None:
                return super.onLinkClicked(url);
            case ExternalGif:
                return true;
            case EmbeddedImage:
                ImageActionLink.EmbeddedImageActions embeddedImage =
                        ImageActionLink.EmbeddedImageActions.forLink(actionLink);
                EmbeddedImageDrawableWrapper wrapper =
                        getWrapperByImageActionId(embeddedImage.getImageActionId());
                if (embeddedImage.getFilterImage().equals(wrapper.getSource())) {
                    new GlideImageGetter(getContext(), this)
                            .loadIntoWrapper(embeddedImage.getSourceImage(), wrapper);
                } else {
                    openImageDialog();
                }
                return true;
        }
        return super.onLinkClicked(url);
    }

    private void openImageDialog() {
        EmbeddedImageDialogFragment dialogFragment = new EmbeddedImageDialogFragment();
        dialogFragment.show(mDialogFragmentManager, "Sample Fragment");
    }

    @Nullable
    private EmbeddedImageDrawableWrapper getWrapperByImageActionId(int imageActionId) {
        ImageSpan[] imageSpans = ((SpannableString) getText()).getSpans(0, getText().length(), ImageSpan.class);
        for (ImageSpan span : imageSpans) {
            EmbeddedImageDrawableWrapper wrapper = (EmbeddedImageDrawableWrapper) span.getDrawable();
            if (wrapper.getImageActionId() == imageActionId) {
                return wrapper;
            }
        }
        return null;
    }
}