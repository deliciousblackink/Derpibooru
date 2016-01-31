package derpibooru.derpy.ui.adapters;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;

public class FiltersViewAdapter extends RecyclerView.Adapter<FiltersViewAdapter.ViewHolder> {
    private ArrayList<DerpibooruFilter> mFilters;
    private DerpibooruFilter mCurrentFilter;

    private OnFilterSelectedHandler mHandler;

    public FiltersViewAdapter(DerpibooruFilter currentFilter,
                              ArrayList<DerpibooruFilter> filters,
                              OnFilterSelectedHandler handler) {
        mFilters = filters;
        mHandler = handler;

        /* the server, when requested for a list of available filters,
         * does not specify which one of them is currently used.
         *
         * the lines below are a hacky workaround for that. if you can fix it, please do so. */
        int indexOfCurrentFilterInFilterList = mFilters.indexOf(currentFilter);
        mCurrentFilter = mFilters.get(indexOfCurrentFilterInFilterList);
        mFilters.remove(indexOfCurrentFilterInFilterList);
    }

    @Override
    public FiltersViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_filters_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == 0) {
            /* show current filter */
            displayFilterInViewHolder(holder, mCurrentFilter);
            holder.buttonUse.setVisibility(View.GONE);
        } else {
            displayFilterInViewHolder(holder, mFilters.get(position - 1));
            holder.buttonUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.changeFilterTo(mFilters.get(position - 1));
                }
            });
        }
    }

    private void displayFilterInViewHolder(ViewHolder holder, DerpibooruFilter filter) {
        holder.textName.setText(filter.getName());
        holder.textUsedBy.setText(String.format("Used by %d people",
                                                 filter.getUserCount()));
        holder.textStatistics.setText(String.format("Spoilers %d tags and hides %d tags",
                                                     filter.getSpoileredTagNames().size(),
                                                     filter.getHiddenTagNames().size()));
        holder.textDescription.setText(filter.getDescription());
    }

    @Override
    public int getItemCount() {
        return (mFilters != null) ? (mFilters.size() + 1) : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public TextView textUsedBy;
        public TextView textStatistics;
        public TextView textDescription;

        public AppCompatButton buttonDetails;
        public AppCompatButton buttonUse;

        public ViewHolder(View v) {
            super(v);
            textName = (TextView) v.findViewById(R.id.textName);
            textUsedBy = (TextView) v.findViewById(R.id.textUsedBy);
            textStatistics = (TextView) v.findViewById(R.id.textStatistics);
            textDescription = (TextView) v.findViewById(R.id.textDescription);

            buttonDetails = (AppCompatButton) v.findViewById(R.id.buttonDetails);
            buttonUse = (AppCompatButton) v.findViewById(R.id.buttonUse);
        }
    }

    public interface OnFilterSelectedHandler {
        void changeFilterTo(DerpibooruFilter newFilter);
        /* TODO: a separate activity for filter details/editing */
    }
}