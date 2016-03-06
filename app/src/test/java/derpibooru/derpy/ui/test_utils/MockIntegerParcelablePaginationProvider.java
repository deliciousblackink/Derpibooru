package derpibooru.derpy.ui.test_utils;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.PaginatedListProvider;

public class MockIntegerParcelablePaginationProvider extends PaginatedListProvider<MockIntegerParcelable> {
    private List<MockIntegerParcelable> mReturn;
    private Runnable mDoAfter;
    private Runnable mDoBefore;

    public MockIntegerParcelablePaginationProvider(Context context, QueryHandler<List<MockIntegerParcelable>> handler) {
        super(context, handler);
    }

    @Override
    protected String generateUrl() {
        return null;
    }

    public MockIntegerParcelablePaginationProvider returnNext(List<MockIntegerParcelable> list) {
        mReturn = list;
        return this;
    }

    public MockIntegerParcelablePaginationProvider beforeReturning(Runnable action) {
        mDoBefore = action;
        return this;
    }

    public MockIntegerParcelablePaginationProvider afterReturning(Runnable action) {
        mDoAfter = action;
        return this;
    }

    @Override
    public void fetch() {
        if (mDoBefore != null) {
            mDoBefore.run();
        }
        mHandler.onQueryExecuted(mReturn);
        if (mDoAfter != null) {
            mDoAfter.run();
        }
    }
}
