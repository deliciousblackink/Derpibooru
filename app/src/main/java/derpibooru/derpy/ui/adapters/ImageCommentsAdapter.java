package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageComment;

public class ImageCommentsAdapter extends RecyclerViewEndlessScrollAdapter<DerpibooruImageComment, ImageCommentsAdapter.ViewHolder> {
    public ImageCommentsAdapter(Context context, ArrayList<DerpibooruImageComment> items) {
        super(context, items);
    }

    @Override
    public ImageCommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_bottom_bar_comments_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.data = getItems().get(position);

        /* FIXME: Support library has some SVG compatibility solutions, dig that up */
        if (!holder.data.getAuthorAvatarUrl().endsWith(".svg")) {
            Glide.with(getContext())
                    .load(holder.data.getAuthorAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate().into(holder.imageAvatar);
        } else {
            Glide.with(getContext()).load(R.drawable.no_avatar).dontAnimate().into(holder.imageAvatar);
        }

        holder.textAuthor.setText(holder.data.getAuthor());
        holder.textPostedAt.setText(getRelativeDate(holder.data.getPostedAt()));
        holder.textComment.setText(holder.data.getText());
    }

    private String getRelativeDate(String date) {
        /* TODO: decide whether this belongs here or should be moved to the parser class */
        SimpleDateFormat f = new SimpleDateFormat("HH:mm, MMMM dd, yyyy", Locale.ENGLISH);
        f.setTimeZone(TimeZone.getTimeZone("UTC")); /* the server returns date in UTC zone */
        try {
            long nowInMilliseconds = new Date().getTime();
            long commentInMilliseconds = f.parse(date).getTime();
            /* SECOND_IN_MILLIS to display "x seconds ago" */
            return DateUtils.getRelativeTimeSpanString(commentInMilliseconds,
                                                       nowInMilliseconds, DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            Log.e("ImageCommentsAdapter", "error parsing date string", e);
        }
        return "";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageAvatar;
        public TextView textAuthor;
        public TextView textPostedAt;
        public TextView textComment;
        public DerpibooruImageComment data;

        public ViewHolder(View v) {
            super(v);
            imageAvatar = (CircleImageView) v.findViewById(R.id.imageAvatar);
            textAuthor = (TextView) v.findViewById(R.id.textAuthor);
            textPostedAt = (TextView) v.findViewById(R.id.textPostedAt);
            textComment = (TextView) v.findViewById(R.id.textComment);
        }
    }
}
