package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import derpibooru.derpy.R;

/**
 * An adapter that populates FloatingSearchView's recent search list.
 *
 * @see <a href="http://developer.android.com/training/material/lists-cards.html">
 * implementation details</a>
 */
public class RecentSearchListAdapter extends RecyclerView.Adapter<RecentSearchListAdapter.ViewHolder> {
    private String[] mDataset;

    /* Used by item animation (see 'setAnimation' method) */
    private int mLastPosition = -1;
    private Context mContext;

    public RecentSearchListAdapter(Context context) {
        mContext = context;
        //mDataset = myDataset;
        mDataset = new String[] { "Search history is not implemented yet." };
        /* TODO: initialize the dataset */
    }

    @Override
    public RecentSearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_floating_search_recent_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
        setAnimation(holder.mLayout, position);
    }

    /**
     * Applies slide-in animation for each item.
     *
     * @see <a href="http://stackoverflow.com/a/26748274/1726690">Copied from StackOverflow</a>
     */
    private void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext,
                                                               android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        /* reset item animation */
        holder.mLayout.clearAnimation();
        mLastPosition = -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public LinearLayout mLayout;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textRecentSearch);
            mLayout = (LinearLayout) v.findViewById(R.id.layout);
        }
    }
}
