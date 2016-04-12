package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import derpibooru.derpy.R;
import derpibooru.derpy.data.internal.CommentReplyItem;
import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.ui.representations.ServerDate;
import derpibooru.derpy.ui.views.htmltextview.CustomFormattingHtmlTextView;

public abstract class CommentListAdapter extends RecyclerViewPaginationAdapter<DerpibooruComment, CommentListAdapter.ViewHolder> {
    private static final String EXTRAS_COMMENT_REPLIES = "derpibooru.derpy.CommentReplies";

    private ArrayList<CommentReplyItem> mCommentReplies = new ArrayList<>(0);

    private OnCommentCountChangeListener mCommentCountChangeListener;

    public CommentListAdapter(Context context, OnCommentCountChangeListener commentCountChangeListener,
                              List<DerpibooruComment> items, @Nullable Bundle savedInstanceState) {
        super(context, items);
        mCommentCountChangeListener = commentCountChangeListener;
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(EXTRAS_COMMENT_REPLIES))) {
            mCommentReplies = savedInstanceState.getParcelableArrayList(EXTRAS_COMMENT_REPLIES);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRAS_COMMENT_REPLIES, mCommentReplies);
    }

    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_detailed_bottom_bar_comments_item, parent, false);
        return new ViewHolder(v);
    }

    protected abstract void fetchCommentReply(CommentReplyItem replyItem);

    protected abstract void scrollToPosition(int adapterPosition);

    @Override
    public void resetItems(List<DerpibooruComment> newItems) {
        mCommentReplies = new ArrayList<>(0);
        checkForNewComments(newItems);
        super.resetItems(newItems);
    }

    private void checkForNewComments(List<DerpibooruComment> newComments) {
        int commentsAdded = 0;
        for (int i = 0; i < newComments.size(); i++) {
            if ((getItems().isEmpty()) || (newComments.get(i).getId() != getItems().get(0).getId())) {
                commentsAdded++;
            } else {
                break;
            }
        }
        if (commentsAdded > 0) {
            mCommentCountChangeListener.onNewCommentsAdded(commentsAdded);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        /* FIXME: Support library has some SVG compatibility solutions, dig that up */
        if (!getItems().get(position).getAuthorAvatarUrl().endsWith(".svg")) {
            Glide.with(getContext())
                    .load(getItems().get(position).getAuthorAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate().into(holder.imageAvatar);
        } else {
            Glide.with(getContext()).load(R.drawable.no_avatar).dontAnimate().into(holder.imageAvatar);
        }
        holder.textAuthor.setText(getItems().get(position).getAuthor());
        holder.textPostedAt.setText(
                new ServerDate(getItems().get(position).getPostedAt())
                        .getRelativeTimeSpanString());
        holder.textComment.setHtml(getItems().get(position).getText());
        holder.textComment.setOnLinkClickListener(new CustomFormattingHtmlTextView.OnLinkClickListener() {
            @Override
            public void onLinkClick(String linkUrl) {
                Matcher commentMatcher = Pattern.compile("(?!#comment_)([\\d*\\.]+)$").matcher(linkUrl);
                if (commentMatcher.find()) {
                    int replyId = Integer.parseInt(commentMatcher.group(1));
                    if (!isCommentReplyDisplayed(replyId)) {
                        CommentReplyItem item = new CommentReplyItem(replyId, position);
                        addCommentReply(item);
                        fetchCommentReply(item);
                    } else {
                        scrollToPosition(getPositionByReplyId(replyId));
                    }
                }
            }
        });
        ((View) holder.textComment.getParent()).setBackgroundColor(
                ContextCompat.getColor(getContext(), doesPositionHoldReply(position) ? R.color.colorPrimaryLight : android.R.color.white));
    }

    private void addCommentReply(CommentReplyItem item) {
        for (int i = 0; i < mCommentReplies.size(); i++) {
            if (mCommentReplies.get(i).getAdapterPosition() >= item.getAdapterPosition()) {
                mCommentReplies.get(i).shiftAdapterPositionForward();
            }
        }
        mCommentReplies.add(item);
    }

    private boolean doesPositionHoldReply(final int position) {
        return Iterables.any(mCommentReplies, new Predicate<CommentReplyItem>() {
            @Override
            public boolean apply(CommentReplyItem input) {
                return input.getAdapterPosition() == position;
            }
        });
    }

    private boolean isCommentReplyDisplayed(final int replyId) {
        return Iterables.any(mCommentReplies, new Predicate<CommentReplyItem>() {
            @Override
            public boolean apply(CommentReplyItem input) {
                return input.getReplyId() == replyId;
            }
        });
    }

    private int getPositionByReplyId(final int replyId) throws NoSuchElementException {
        return Iterables.find(mCommentReplies, new Predicate<CommentReplyItem>() {
            @Override
            public boolean apply(CommentReplyItem input) {
                return input.getReplyId() == replyId;
            }
        }).getAdapterPosition();
    }

    public interface OnCommentCountChangeListener {
        void onNewCommentsAdded(int commentsAdded);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageAvatar) CircleImageView imageAvatar;
        @Bind(R.id.textAuthor) TextView textAuthor;
        @Bind(R.id.textPostedAt) TextView textPostedAt;
        @Bind(R.id.textComment) CustomFormattingHtmlTextView textComment;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
