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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.ui.utils.RelativeDateConverter;
import derpibooru.derpy.ui.utils.TextViewHtmlDisplayer;

public class CommentListAdapter extends RecyclerViewPaginationAdapter<DerpibooruComment, CommentListAdapter.ViewHolder> {
    private RelativeDateConverter mDate;
    private TextViewHtmlDisplayer mHtmlDisplayer;

    public CommentListAdapter(Context context, List<DerpibooruComment> items) {
        super(context, items);
        mDate = new RelativeDateConverter(RelativeDateConverter.DATE_FORMAT_RETURNED_BY_DERPIBOORU,
                                          RelativeDateConverter.TIMEZONE_RETURNED_BY_DERPIBOORU);
        mHtmlDisplayer = new TextViewHtmlDisplayer() {
            @Override
            protected void onLinkClick(String url) {

            }
        };
    }

    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_bottom_bar_comments_item, parent, false);
        return new ViewHolder(v);
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
        holder.textPostedAt.setText(mDate.getRelativeDate(getItems().get(position).getPostedAt()));
        mHtmlDisplayer.textFromHtml(
                holder.textComment, getItems().get(position).getText());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageAvatar) CircleImageView imageAvatar;
        @Bind(R.id.textAuthor) TextView textAuthor;
        @Bind(R.id.textPostedAt) TextView textPostedAt;
        @Bind(R.id.textComment) TextView textComment;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
