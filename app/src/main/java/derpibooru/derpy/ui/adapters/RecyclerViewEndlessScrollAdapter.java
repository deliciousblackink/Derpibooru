package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public abstract class RecyclerViewEndlessScrollAdapter<TItem, TViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<TViewHolder> {
    private Context mContext;
    private ArrayList<TItem> mItems;

    public RecyclerViewEndlessScrollAdapter(Context context, ArrayList<TItem> items) {
        mContext = context;
        mItems = items;
    }

    public void resetItems(ArrayList<TItem> newComments) {
        super.notifyItemRangeRemoved(0, mItems.size());
        mItems = newComments;
        super.notifyItemRangeInserted(0, mItems.size() - 1);
    }

    public void appendItems(ArrayList<TItem> newComments) {
        int oldItemCount = mItems.size();
        mItems.addAll(newComments);
        int newItemCount = mItems.size() - 1;
        super.notifyItemRangeInserted(oldItemCount, newItemCount);
    }

    protected Context getContext() {
        return mContext;
    }

    protected ArrayList<TItem> getItems() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
