package derpibooru.derpy.ui.views.htmltextview.imageactions;

public class EmbeddedFilteredImageAction extends EmbeddedImageAction {
    private final String mFilterSource;
    private final String mFilteredTagName;

    private boolean mSpoilered = true;

    public EmbeddedFilteredImageAction(int imageId, String imageSource, String filterSource, String filteredTagName) {
        super(imageId, imageSource);
        mFilterSource = filterSource;
        mFilteredTagName = filteredTagName;
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

    public String getFilteredTagName() {
        return mFilteredTagName;
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
                    && (a.getFilteredTagName().equals(this.getFilteredTagName()))
                    && (a.getImageSourceReal().equals(this.getImageSourceReal())
                    && (a.getFilterSourceReal().equals(this.getFilterSourceReal())));
        }
        return super.equals(o);
    }
}
