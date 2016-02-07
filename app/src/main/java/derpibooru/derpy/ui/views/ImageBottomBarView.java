package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.server.ImageInfoProvider;
import derpibooru.derpy.server.ProviderRequestHandler;

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

    public void setBasicInfo(int imageId, int faves, int comments) {
        super.inflateLayout();
        TextView d = (TextView) this.findViewById(R.id.textFaves);
        d.setText(Integer.toString(faves));
        TextView u = (TextView) this.findViewById(R.id.textComments);
        u.setText(Integer.toString(comments));
        loadDetailedInfo(imageId);
    }

    private void loadDetailedInfo(int imageId) {
         /* get image uploader, description, tags */
        ImageInfoProvider provider = new ImageInfoProvider(getContext(), new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                populateViewPagerTabsWithImageInfo((DerpibooruImageInfo) result);
            }

            @Override
            public void onRequestFailed() {
                /* TODO: handle failed request */
            }
        });
        provider.id(imageId).fetch();
    }
}
