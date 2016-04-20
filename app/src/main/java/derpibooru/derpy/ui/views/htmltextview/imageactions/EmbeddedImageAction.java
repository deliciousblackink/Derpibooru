package derpibooru.derpy.ui.views.htmltextview.imageactions;

public class EmbeddedImageAction extends ImageAction {
    private final int mImageId;
    protected final String mImageSource;

    public EmbeddedImageAction(int imageId, String imageSource) {
        mImageId = imageId;
        mImageSource = imageSource;
    }

    public int getImageId() {
        return mImageId;
    }

    @Override
    public String getImageSource() {
        return mImageSource;
    }


    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            EmbeddedImageAction a = (EmbeddedImageAction) o;
            return (a.getImageId() == this.getImageId())
                    && (a.getImageSource().equals(this.getImageSource()));
        }
        return super.equals(o);
    }
}
