package derpibooru.derpy.ui.adapters;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;

public class FiltersViewAdapter extends RecyclerView.Adapter<FiltersViewAdapter.ViewHolder> {
    private ArrayList<DerpibooruFilter> mFilters;
    public FiltersViewAdapter() {
    }

    public FiltersViewAdapter(ArrayList<DerpibooruFilter> filters) {
        mFilters = filters;
    }

    @Override
    public FiltersViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_filters_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextName.setText(mFilters.get(position).getName());
        holder.mTextUsedBy.setText(String.format("Used by %d people",
                                                 mFilters.get(position).getUserCount()));
        holder.mTextStatistics.setText(String.format("Spoilers %d tags and hides %d tags",
                                                     mFilters.get(position).getSpoileredTagNames().size(),
                                                     mFilters.get(position).getHiddenTagNames().size()));
        holder.mTextDescription.setText(mFilters.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return (mFilters != null) ? mFilters.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextName;
        public TextView mTextUsedBy;
        public TextView mTextStatistics;
        public TextView mTextDescription;

        public AppCompatButton mButtonDetails;
        public AppCompatButton mButtonUse;

        public ViewHolder(View v) {
            super(v);
            mTextName = (TextView) v.findViewById(R.id.textName);
            mTextUsedBy = (TextView) v.findViewById(R.id.textUsedBy);
            mTextStatistics = (TextView) v.findViewById(R.id.textStatistics);
            mTextDescription = (TextView) v.findViewById(R.id.textDescription);

            mButtonDetails = (AppCompatButton) v.findViewById(R.id.buttonDetails);
            mButtonUse = (AppCompatButton) v.findViewById(R.id.buttonUse);
        }
    }
}