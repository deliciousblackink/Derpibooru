package derpibooru.derpy.ui.views.htmltextview.imageactions;

import com.google.gson.Gson;

public class EmbeddedImageAction implements ImageAction {
    private final int mImageId;
    protected final String mImageSource;

    public EmbeddedImageAction(String representation) {
        this(new Gson().fromJson(representation, EmbeddedImageAction.class));
    }

    private EmbeddedImageAction(EmbeddedImageAction action) {
        this(action.getImageId(), action.getImageSource());
    }

    public EmbeddedImageAction(int imageId, String imageSource) {
        mImageId = imageId;
        mImageSource = imageSource;
    }

    @Override
    public String toStringRepresentation() {
        return new Gson().toJson(this);
    }

    public int getImageId() {
        return mImageId;
    }

    @Override
    public String getImageSource() {
        return mImageSource;
    }
}
