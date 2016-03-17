package derpibooru.derpy.server;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruTag;

public class ImageDownloader {
    private Context mContext;
    private String mDownloadTitle;
    private String mDownloadDescription;
    private Uri mUri;

    public ImageDownloader(Context context, int imageId, List<DerpibooruTag> imageTags, String imageUrl) {
        mContext = context;
        mDownloadTitle = getDownloadTitle(imageId);
        mDownloadDescription = getDownloadDescription(imageId, imageTags);
        mUri = Uri.parse(imageUrl);
    }

    public void download() {
        Thread thread = new Thread(new DownloaderRunnable());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private String getDownloadTitle(int imageId) {
        return String.format(mContext.getString(R.string.download_image_notification_title), imageId);
    }

    private String getDownloadDescription(int imageId, List<DerpibooruTag> imageTags) {
        StringBuilder tagListBuilder = new StringBuilder();
        for (DerpibooruTag tag : imageTags) {
            tagListBuilder.append(' ');
            tagListBuilder.append(tag.getName());
            tagListBuilder.append(',');
        }
        tagListBuilder.deleteCharAt(tagListBuilder.length() - 1); /* remove the ',' */
        return String.format(mContext.getString(R.string.download_image_notification_description), imageId, tagListBuilder.toString());
    }

    private class DownloaderRunnable implements Runnable {
        @Override
        public void run() {
            DownloadManager manager = (DownloadManager)
                    mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(mUri);
            request.setTitle(mDownloadTitle);
            request.setDescription(mDownloadDescription);
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            manager.enqueue(request);
        }
    }
}
