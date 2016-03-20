package derpibooru.derpy;

import android.os.Environment;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTag;

public class ImageDownload {
    private static final String FILE_PATH_FORMAT = "Derpibooru/%s";

    private Context mContext;
    private String mDownloadTitle;
    private String mDownloadDescription;
    private String mDownloadFileParth;
    private Uri mUri;

    public ImageDownload(Context context, int imageId, List<DerpibooruTag> imageTags, String imageUrl) {
        mContext = context;
        mDownloadTitle = getDownloadTitle(imageId, imageTags);
        mDownloadDescription = getDownloadDescription();
        mDownloadFileParth = getFilePath(imageId);
        mUri = Uri.parse(imageUrl);
    }

    public void start() {
        Thread thread = new Thread(new DownloaderRunnable());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private String getDownloadTitle(int imageId, List<DerpibooruTag> imageTags) {
        StringBuilder tagListBuilder = new StringBuilder();
        for (DerpibooruTag tag : imageTags) {
            tagListBuilder.append(' ');
            tagListBuilder.append(tag.getName());
            tagListBuilder.append(',');
        }
        tagListBuilder.deleteCharAt(tagListBuilder.length() - 1); /* remove the ',' */
        return String.format(mContext.getString(R.string.download_image_notification_title), imageId, tagListBuilder.toString());
    }

    private String getDownloadDescription() {
        return mContext.getString(R.string.download_image_notification_description);
    }

    private String getFilePath(int imageId) {
        return String.format(FILE_PATH_FORMAT, Integer.toString(imageId));
    }

    private class DownloaderRunnable implements Runnable {
        @Override
        public void run() {
            DownloadManager manager = (DownloadManager)
                    mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(mUri);
            request.setTitle(mDownloadTitle)
                    .setDescription(mDownloadDescription)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mDownloadFileParth)
                    .allowScanningByMediaScanner();
            manager.enqueue(request);
        }
    }
}
