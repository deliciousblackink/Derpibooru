package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;

public class ImageBottomBarView extends ImageBottomBarViewPagerLayout {
    public ImageBottomBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccentColorIconButton getFaveButton() {
        return (AccentColorIconButton) findViewById(R.id.buttonFave);
    }

    public void setInfoFromDetailed(DerpibooruImageDetailed image, DataRefreshHandler handler) {
        refreshCommentCount(image.getThumb().getCommentCount());
        initializeTabs(image, handler);
    }

    private AccentColorIconButton getCommentButton() {
        return (AccentColorIconButton) findViewById(R.id.buttonComments);
    }

    public void refreshCommentCount(int comments) {
        getCommentButton().setText(Integer.toString(comments));
    }

    public interface DataRefreshHandler {
        void onNewCommentsAdded(int numberOfNewComments);
    }
}
