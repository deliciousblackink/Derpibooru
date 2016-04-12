package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.internal.CommentReplyItem;
import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.CommentListProvider;
import derpibooru.derpy.server.providers.CommentProvider;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.CommentListAdapter;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;
import derpibooru.derpy.ui.adapters.RecyclerViewPaginationAdapter;
import derpibooru.derpy.ui.presenters.PaginatedListPresenter;
import derpibooru.derpy.ui.views.imagedetailedview.ImageBottomBarView;

public class ImageBottomBarCommentListTabFragment extends Fragment {
    private PaginatedListPresenter<DerpibooruComment> mCommentListPresenter;

    /* TODO: notify bottom bar when the number of comments changes */
    @Bind(R.id.layoutCommentsRefresh) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.viewComments) RecyclerView recyclerView;

    private CommentListAdapter.OnCommentCountChangeListener mCommentCountChangeListener;
    private Bundle mSavedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_image_bottom_bar_comments, container, false);
        ButterKnife.bind(this, v);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentListPresenter = new PaginatedListPresenter<DerpibooruComment>(refreshLayout, recyclerView) {
            @Override
            public RecyclerViewPaginationAdapter<DerpibooruComment, ?> getNewInstanceOfListAdapter(List<DerpibooruComment> initialItems) {
                return getNewInstanceOfCommentListAdapter(initialItems);
            }
        };
        mSavedInstanceState = savedInstanceState;
        initializeCommentListPresenter();
        return v;
    }

    public void setCommentCountChangeListener(CommentListAdapter.OnCommentCountChangeListener commentCountChangeListener) {
        mCommentCountChangeListener = commentCountChangeListener;
    }

    private void initializeCommentListPresenter() {
        if (mSavedInstanceState == null) {
            mCommentListPresenter.initializeWithProvider(
                    new CommentListProvider(getActivity(), getNewInstanceOfProviderQueryHandler())
                            .id(getArguments().getInt(ImageActivity.EXTRAS_IMAGE_ID)));
        } else {
            mCommentListPresenter.initializeWithProvider(
                    new CommentListProvider(getActivity(), getNewInstanceOfProviderQueryHandler())
                            .id(getArguments().getInt(ImageActivity.EXTRAS_IMAGE_ID)), mSavedInstanceState);
        }
    }

    /**
     * Returns a new instance of a {@link QueryHandler} to be passed to the list provider.
     */
    protected QueryHandler<List<DerpibooruComment>> getNewInstanceOfProviderQueryHandler() {
        return mCommentListPresenter.new PaginatedListProviderHandler();
    }

    private CommentListAdapter getNewInstanceOfCommentListAdapter(List<DerpibooruComment> initialItems) {
        return new CommentListAdapter(getActivity(), mCommentCountChangeListener,
                                      initialItems, mSavedInstanceState) {
            @Override
            protected void fetchCommentReply(final CommentReplyItem replyItem) {
                new CommentProvider(getContext(), new QueryHandler<DerpibooruComment>() {
                    @Override
                    public void onQueryExecuted(DerpibooruComment result) {
                        if (mCommentListPresenter != null) {
                            if (mCommentListPresenter.getAdapter() != null) {
                                mCommentListPresenter.getAdapter()
                                        .appendItemAtPosition(replyItem.getAdapterPosition(), result);
                                recyclerView.smoothScrollToPosition(replyItem.getAdapterPosition());
                            }
                        }
                    }

                    @Override
                    public void onQueryFailed() { }
                }).id(replyItem.getReplyId()).fetch();
            }

            @Override
            protected void scrollToPosition(int adapterPosition) {
                recyclerView.smoothScrollToPosition(adapterPosition);
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCommentListPresenter != null) {
            mCommentListPresenter.onSaveInstanceState(outState);
            if (mCommentListPresenter.getAdapter() instanceof CommentListAdapter) {
                ((CommentListAdapter) mCommentListPresenter.getAdapter()).onSaveInstanceState(outState);
            }
        }
    }
}
