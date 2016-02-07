package derpibooru.derpy.ui.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @author https://gist.github.com/rogerhu/17aca6ad4dbdb3fa5892
 */
public abstract class RecyclerViewEndlessScrollListener extends RecyclerView.OnScrollListener {
    private final int ITEMS_LEFT_TO_START_LOADING_MORE = 4;
    private final int START_PAGE_INDEX = 0;

    private int mCurrentPage = 0;
    private int mPreviousTotalItemCount = 0;
    private boolean mIsLoading = true;

    private LinearLayoutManager mLayoutManager;

    public RecyclerViewEndlessScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        int totalItemCount = mLayoutManager.getItemCount();

        /* if the total item count is zero and the previous isn't, assume the
         * list is invalidated and should be reset back to initial state */
        if (totalItemCount < mPreviousTotalItemCount) {
            mCurrentPage = START_PAGE_INDEX;
            mPreviousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                mIsLoading = true;
            }
        }

        /* if it’s still loading, we check to see if the dataset count has
         * changed, if so we conclude it has finished loading and update the current page
         * number and total item count. */
        if (mIsLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mIsLoading = false;
            mPreviousTotalItemCount = totalItemCount;
        }

        /* if it isn’t currently loading, we check to see if we need to reload more data.
         * If we do need to reload some more data, we execute onLoadMore to fetch the data. */
        if (!mIsLoading && totalItemCount <= (lastVisibleItem + ITEMS_LEFT_TO_START_LOADING_MORE)) {
            mCurrentPage++;
            onLoadMore(mCurrentPage);
            mIsLoading = true;
        }
    }

    public abstract void onLoadMore(int page);
}