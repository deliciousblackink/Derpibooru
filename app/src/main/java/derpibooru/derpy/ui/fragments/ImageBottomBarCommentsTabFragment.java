package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageComment;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.server.ImageCommentsProvider;
import derpibooru.derpy.server.ProviderRequestHandler;
import derpibooru.derpy.ui.adapters.ImageCommentsAdapter;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;

public class ImageBottomBarCommentsTabFragment extends ImageBottomBarTabFragment {
    private ImageCommentsProvider mCommentsProvider;
    private ImageCommentsAdapter mCommentsAdapter;
    private SwipeRefreshLayout mCommentsRefreshLayout;
    private RecyclerView mCommentsView;

    private boolean mRefreshingComments = false;

    public ImageBottomBarCommentsTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_bottom_bar_comments_tab, container, false);
        mCommentsView = (RecyclerView) v.findViewById(R.id.viewComments);
        mCommentsProvider = new ImageCommentsProvider(getActivity(), new ImageCommentsRequestHandler());
        if (getArguments().containsKey("info")) {
            displayInfoInView(v, (DerpibooruImageInfo) getArguments().getParcelable("info"));
        }
        mCommentsRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.layoutCommentsRefresh);
        mCommentsRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mCommentsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComments();
            }
        });
        return v;
    }

    @Override
    protected void displayInfoInView(View target, DerpibooruImageInfo info) {
        mCommentsProvider.id(info.getId()).fetch();
    }

    private void refreshComments() {
        mRefreshingComments = true;
        mCommentsProvider.resetPageNumber().fetch();
    }

    private void displayCommentsFromProvider(ArrayList<DerpibooruImageComment> comments) {
        if (mCommentsAdapter == null) {
            mCommentsAdapter = new ImageCommentsAdapter(getActivity(), comments);
            mCommentsView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCommentsView.setAdapter(mCommentsAdapter);
            mCommentsView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                    (LinearLayoutManager) mCommentsView.getLayoutManager()) {
                @Override
                public void onLoadMore(int page) {
                    mCommentsProvider.nextPage().fetch();
                }
            });
        } else if (mRefreshingComments) {
            mRefreshingComments = false;
            mCommentsAdapter.resetImageComments(comments);
            mCommentsRefreshLayout.setRefreshing(false);
        } else {
            mCommentsAdapter.appendImageComments(comments);
        }
        if (getView() != null && getView()
                .findViewById(R.id.progressBottomBarTab).getVisibility() == View.VISIBLE) {
            getView().findViewById(R.id.progressBottomBarTab).setVisibility(View.GONE);
            getView().findViewById(R.id.layoutCommentsRefresh).setVisibility(View.VISIBLE);
        }
    }

    private class ImageCommentsRequestHandler implements ProviderRequestHandler {
        @Override
        public void onRequestCompleted(Object result) {
            displayCommentsFromProvider((ArrayList<DerpibooruImageComment>) result);
        }

        @Override
        public void onRequestFailed() {
            /* TODO: display error message */
        }
    }

    @Override
    protected void onLinkClick(View view) {

    }
}
