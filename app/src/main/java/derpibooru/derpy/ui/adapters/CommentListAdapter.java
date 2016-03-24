package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.ui.representations.ServerDate;
import derpibooru.derpy.ui.views.HtmlTextView;

public abstract class CommentListAdapter extends RecyclerViewPaginationAdapter<DerpibooruComment, CommentListAdapter.ViewHolder> {
    public CommentListAdapter(Context context, List<DerpibooruComment> items) {
        super(context, items);
    }

    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_bottom_bar_comments_item, parent, false);
        return new ViewHolder(v);
    }

    protected abstract void fetchCommentReply(CommentReplyItem replyItem);

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
        holder.textComment.setOnLinkClickListener(new HtmlTextView.OnLinkClickListener() {
            @Override
            public void onLinkClick(String linkUrl) {
                Matcher commentMatcher = Pattern.compile("(?!#comment_)([\\d*\\.]+)$").matcher(linkUrl);
                if (commentMatcher.find()) {
                    int replyId = Integer.parseInt(commentMatcher.group(1));
                    fetchCommentReply(item);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageAvatar) CircleImageView imageAvatar;
        @Bind(R.id.textAuthor) TextView textAuthor;
        @Bind(R.id.textPostedAt) TextView textPostedAt;
        @Bind(R.id.textComment) HtmlTextView textComment;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
