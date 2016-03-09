package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.server.QueryHandler;

public abstract class PaginatedListProvider<TItem> extends Provider<List<TItem>> {
    private int mCurrentPage = 1;

    public PaginatedListProvider(Context context, QueryHandler<List<TItem>> handler) {
        super(context, handler);
    }

    public PaginatedListProvider nextPage() {
        mCurrentPage++;
        return this;
    }

    public PaginatedListProvider previousPage() {
        mCurrentPage--;
        return this;
    }

    public PaginatedListProvider resetPageNumber() {
        mCurrentPage = 1;
        return this;
    }

    public PaginatedListProvider fromPage(int page) {
        mCurrentPage = page;
        return this;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
