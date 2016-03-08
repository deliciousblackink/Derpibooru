package derpibooru.derpy.ui.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import derpibooru.derpy.R;

/**
 * GridLayoutManager requires a definite number of columns: it doesn't resize itself based on the screen dimensions.
 * This view takes care of that.
 * <br>The minimum size of an item is specified by the "image_list_item_min_size" dimen attribute.
 * Note that the item may be enlarged since the number of columns is rounded down.
 * The view also adds padding between items (by applying a custom RecyclerView.ItemDecoration).
 *
 * @see <a href="http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html">Based on this AutoFit solution</a>
 */
public class ImageListRecyclerView extends RecyclerView {
    @BindDimen(R.dimen.image_list_item_min_size) int imageSize;
    @BindDimen(R.dimen.image_list_spacing_between_items) int imageSpacing;

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
        ButterKnife.bind(this);
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(mLayoutManager);
        addItemDecoration(new ImageListSpacingDecoration());
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
        return Math.max(1, (width / imageSize));
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

            outRect.left = imageSpacing - column * imageSpacing / mLastMeasuredNumberOfColumns;
            outRect.right = (column + 1) * imageSpacing / mLastMeasuredNumberOfColumns;

            if (position < mLastMeasuredNumberOfColumns) {
                outRect.top = imageSpacing;
            }
            outRect.bottom = imageSpacing;
        }
    }
}