package derpibooru.derpy.ui.views.htmltextview.imageactions;

import com.google.gson.Gson;

public class ExternalGifImageAction implements ImageAction {
    private final String mImageSource;
    private final String mTitle;

    public ExternalGifImageAction(String representation) {
        this(new Gson().fromJson(representation, ExternalGifImageAction.class));
    }

    private ExternalGifImageAction(ExternalGifImageAction action) {
        this(action.getImageSource(), action.getTitle());
    }

    public ExternalGifImageAction(String imageSource, String title) {
        mImageSource = imageSource;
        mTitle = title;
    }

    @Override
    public String toStringRepresentation() {
        return new Gson().toJson(this);
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getImageSource() {
        return mImageSource;
    }
}
