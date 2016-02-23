package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.ImageInfoProvider;

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
        return ((AccentColorIconButton) this.findViewById(R.id.buttonFave));
    }

    public void setBasicInfo(int imageId, int comments) {
        super.inflateLayout();
        ((AccentColorIconButton) this.findViewById(R.id.buttonComments))
                .setText(Integer.toString(comments));
        loadDetailedInfo(imageId);
    }

    private void loadDetailedInfo(int imageId) {
         /* get image uploader, description, tags */
        ImageInfoProvider provider = new ImageInfoProvider(getContext(), new QueryHandler<DerpibooruImageDetailed>() {
            @Override
            public void onQueryExecuted(DerpibooruImageDetailed info) {
                populateViewPagerTabsWithImageInfo(info);
            }

            @Override
            public void onQueryFailed() {
                /* TODO: handle failed request */
            }
        });
        provider.id(imageId).fetch();
    }
}
