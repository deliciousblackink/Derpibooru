package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class RecyclerViewEndlessScrollAdapter<TItem, TViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<TViewHolder> {
    private Context mContext;
    private List<TItem> mItems;

    protected RecyclerViewEndlessScrollAdapter(Context context, List<TItem> items) {
        mContext = context;
        mItems = items;
    }

    public void resetItems(List<TItem> newComments) {
        super.notifyItemRangeRemoved(0, mItems.size());
        mItems = newComments;
        super.notifyItemRangeInserted(0, mItems.size() - 1);
    }

    public void appendItems(List<TItem> newComments) {
        int oldItemCount = mItems.size();
        mItems.addAll(newComments);
        int newItemCount = mItems.size() - 1;
        super.notifyItemRangeInserted(oldItemCount, newItemCount);
    }

    protected Context getContext() {
        return mContext;
    }

    protected List<TItem> getItems() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
