package derpibooru.derpy.ui.views.htmltextview.imageactions;

public class ExternalGifImageAction extends ImageAction {
    private final String mImageSource;

    public ExternalGifImageAction(String imageSource) {
        mImageSource = imageSource;
    }

    @Override
    public String getImageSource() {
        return mImageSource;
    }
}
