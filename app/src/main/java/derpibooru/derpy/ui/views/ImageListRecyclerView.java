package derpibooru.derpy.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import derpibooru.derpy.R;

/**
 * @see <a href="http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html">Based on this AutoFit solution</a>
 */
public class ImageListRecyclerView extends RecyclerView {
    private static final int SPACING_BETWEEN_IMAGES = 10;

    private GridLayoutManager mLayoutManager;

    private int mImageSize;
    private int mLastMeasuredWidth;
    private int mLastMeasuredNumberOfColumns;

    public ImageListRecyclerView(Context context) {
        super(context);
        init();
    }

    public ImageListRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setAttrs(context, attrs);
    }

    public ImageListRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setAttrs(context, attrs);
    }

    private void init() {
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(mLayoutManager);
        addItemDecoration(new ImageListSpacingDecoration());
    }

    private void setAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageListRecyclerView);
        try {
            mImageSize =
                    a.getDimensionPixelSize(R.styleable.ImageListRecyclerView_imageSize, 1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (getMeasuredWidth() != mLastMeasuredWidth) {
            mLastMeasuredWidth = getMeasuredWidth();
            mLastMeasuredNumberOfColumns =
                    calculateNumberOfColumns(getMeasuredWidth());
        }
        mLayoutManager.setSpanCount(mLastMeasuredNumberOfColumns);
    }

    private int calculateNumberOfColumns(int width) {
        return Math.max(1, (width / mImageSize));
    }

    /**
     * @author http://stackoverflow.com/a/30701422/1726690
     */
    private class ImageListSpacingDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % mLastMeasuredNumberOfColumns;

            outRect.left = SPACING_BETWEEN_IMAGES - column * SPACING_BETWEEN_IMAGES / mLastMeasuredNumberOfColumns;
            outRect.right = (column + 1) * SPACING_BETWEEN_IMAGES / mLastMeasuredNumberOfColumns;

            if (position < mLastMeasuredNumberOfColumns) {
                outRect.top = SPACING_BETWEEN_IMAGES;
            }
            outRect.bottom = SPACING_BETWEEN_IMAGES;
        }
    }
}