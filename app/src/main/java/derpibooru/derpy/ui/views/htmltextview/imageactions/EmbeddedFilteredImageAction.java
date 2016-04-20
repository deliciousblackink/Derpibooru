package derpibooru.derpy.ui.views.htmltextview.imageactions;

public class EmbeddedFilteredImageAction extends EmbeddedImageAction {
    private final String mFilterSource;

    private boolean mSpoilered = true;

    public EmbeddedFilteredImageAction(int imageId, String imageSource, String filterSource) {
        super(imageId, imageSource);
        mFilterSource = filterSource;
    }

    public boolean isSpoilered() {
        return mSpoilered;
    }

    public void unspoiler() {
        mSpoilered = false;
    }

    @Override
    public String getImageSource() {
        return mSpoilered ? mFilterSource : getImageSourceReal();
    }

    private String getFilterSourceReal() {
        return mFilterSource;
    }

    private String getImageSourceReal() {
        return super.mImageSource;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            EmbeddedFilteredImageAction a = (EmbeddedFilteredImageAction) o;
            return (a.getImageId() == this.getImageId())
                    && (a.getImageSourceReal().equals(this.getImageSourceReal())
                    && (a.getFilterSourceReal().equals(this.getFilterSourceReal())));
        }
        return super.equals(o);
    }
}
