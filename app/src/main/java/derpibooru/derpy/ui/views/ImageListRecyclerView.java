package derpibooru.derpy.ui.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @see <a href="http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html">Based on this AutoFit solution</a>
 */
public class ImageListRecyclerView extends RecyclerView {
    private static final int MIN_IMAGE_THUMB_SIZE = 180;
    private static final int MIN_NUMBER_OF_COLUMNS = 2;
    private static final int MAX_NUMBER_OF_COLUMNS = 4;
    private static final int SPACING_BETWEEN_IMAGE_THUMBS = 10;

    private GridLayoutManager mLayoutManager;

    private int mLastMeasuredWidth;
    private int mLastMeasuredNumberOfColumns;

    public ImageListRecyclerView(Context context) {
        super(context);
        init();
    }

    public ImageListRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageListRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(mLayoutManager);
        addItemDecoration(new ImageListSpacingDecoration());
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (getMeasuredWidth() != mLastMeasuredWidth) {
            mLastMeasuredWidth = getMeasuredWidth();
            mLastMeasuredNumberOfColumns = calculateNumberOfColumns(mLastMeasuredWidth);
        }
        mLayoutManager.setSpanCount(mLastMeasuredNumberOfColumns);
    }

    private int calculateNumberOfColumns(int recyclerViewWidth) {
        /* FIXME: this is by far the dirtiest piece of code I've ever written */
        int currentImageThumbWidth = MIN_IMAGE_THUMB_SIZE;
        if ((MIN_IMAGE_THUMB_SIZE * MIN_NUMBER_OF_COLUMNS) > recyclerViewWidth) {
            return 1;  /* ldpi devices */
        }
        final int sizeIncrease = 100;
        int currentColumnCount = 1;
        while (currentColumnCount < MIN_NUMBER_OF_COLUMNS
                || currentColumnCount > MAX_NUMBER_OF_COLUMNS) {
            currentColumnCount = (recyclerViewWidth / currentImageThumbWidth);
            currentImageThumbWidth += sizeIncrease;
        }
        return currentColumnCount;
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

            outRect.left = SPACING_BETWEEN_IMAGE_THUMBS - column * SPACING_BETWEEN_IMAGE_THUMBS / mLastMeasuredNumberOfColumns;
            outRect.right = (column + 1) * SPACING_BETWEEN_IMAGE_THUMBS / mLastMeasuredNumberOfColumns;

            if (position < mLastMeasuredNumberOfColumns) {
                outRect.top = SPACING_BETWEEN_IMAGE_THUMBS;
            }
            outRect.bottom = SPACING_BETWEEN_IMAGE_THUMBS;
        }
    }
}