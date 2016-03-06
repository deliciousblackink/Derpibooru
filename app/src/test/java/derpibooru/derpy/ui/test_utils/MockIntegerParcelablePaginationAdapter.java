package derpibooru.derpy.ui.test_utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import derpibooru.derpy.ui.adapters.RecyclerViewPaginationAdapter;

public class MockIntegerParcelablePaginationAdapter extends RecyclerViewPaginationAdapter<MockIntegerParcelable, MockIntegerParcelablePaginationAdapter.ViewHolder> {
    public MockIntegerParcelablePaginationAdapter(Context context, List<MockIntegerParcelable> initialItems) {
        super(context, initialItems);
    }

    @Override
    public MockIntegerParcelablePaginationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new View(getContext());
        v.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        ViewHolder(View v) {
            super(v);
            view = v;
        }
    }
}
