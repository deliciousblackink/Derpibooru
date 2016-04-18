package derpibooru.derpy.ui.views.htmltextview.imageactions;

import com.google.gson.Gson;

public class EmbeddedFilteredImageAction extends EmbeddedImageAction {
    private final String mFilterSource;

    private boolean mSpoilered = true;

    public EmbeddedFilteredImageAction(String representation) {
        this(new Gson().fromJson(representation, EmbeddedFilteredImageAction.class));
    }

    private EmbeddedFilteredImageAction(EmbeddedFilteredImageAction action) {
        this(action.getImageId(), action.getImageSourceReal(), action.getFilterSource());
    }

    public EmbeddedFilteredImageAction(int imageId, String imageSource, String filterSource) {
        super(imageId, imageSource);
        mFilterSource = filterSource;
    }

    public void unspoiler() {
        mSpoilered = false;
    }

    @Override
    public String toStringRepresentation() {
        return new Gson().toJson(this);
    }

    @Override
    public String getImageSource() {
        return mSpoilered ? mFilterSource : getImageSourceReal();
    }

    private String getImageSourceReal() {
        return super.mImageSource;
    }

    private String getFilterSource() {
        return mFilterSource;
    }
}
