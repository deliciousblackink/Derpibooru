package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import derpibooru.derpy.R;

import derpibooru.derpy.data.server.DerpibooruImageInfo;

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

    public ImageBottomBarView setBasicInfo(int faves, int comments) {
        super.inflateLayout();
        TextView d = (TextView) this.findViewById(R.id.textFaves);
        d.setText(Integer.toString(faves));
        TextView u = (TextView) this.findViewById(R.id.textComments);
        u.setText(Integer.toString(comments));
        return this;
    }

    public ImageBottomBarView setTabInfo(DerpibooruImageInfo info) {
        setUpViewPager(info);
        return this;
    }
}
