package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;

public class ImageBottomBarView extends ImageBottomBarViewPagerLayout {
    public ImageBottomBarView(Context context) {
        super(context);
    }

    public ImageBottomBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageBottomBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AccentColorIconButton getFaveButton() {
        return (AccentColorIconButton) findViewById(R.id.buttonFave);
    }

    public void setInfoFromThumb(int commentCount) {
        ((AccentColorIconButton) findViewById(R.id.buttonComments))
                .setText(Integer.toString(commentCount));
    }

    public void setInfoFromDetailed(DerpibooruImageDetailed image) {
        setInfoFromThumb(image.getThumb().getCommentCount());
        initializeTabs(image);
    }
}
