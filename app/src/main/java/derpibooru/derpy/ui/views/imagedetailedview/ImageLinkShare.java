package derpibooru.derpy.ui.views.imagedetailedview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.ShareActionProvider;

import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruTag;

/**
 * A wrapper for {@link ShareActionProvider} that creates a share intent for
 * the particular image link.
 * <p>
 * Unlike {@link ImageShare}, a large binary stream of which may not be applicable for
 * all sharing consumers, this class creates a compact link with as little text as possible.
 */
class ImageLinkShare {
    private final Context mContext;
    private final ShareActionProvider mProvider;

    private Intent mShareIntent;

    ImageLinkShare(Context context, ShareActionProvider provider) {
        mContext = context;
        mProvider = provider;
    }

    /**
     * Enables sharing by setting the intent that contains a link
     * for the specified image for the {@link ShareActionProvider}.
     *
     * @param imageId booru image ID (used to for the accompanying text)
     * @param imageTags image tags (used to for the accompanying text)
     */
    void enableSharing(int imageId, List<DerpibooruTag> imageTags) {
        mShareIntent = getShareIntentWithText(
                getSharingText(imageId, getSignificantTags(imageTags)));
    }

    /**
     * Sets an appropriate intent for the {@link ShareActionProvider}.
     * <p>
     * As far as I can tell, {@link ShareActionProvider#setShareIntent(Intent)} sets an activity-wide
     * intent. Therefore, if there are multiple share actions, the intent has to reset every time the user
     * chooses an action.
     */
    void resetProviderIntent() {
        mProvider.setShareIntent(mShareIntent);
    }

    private String getSharingText(int imageId, String imageTags) {
        return String.format(mContext.getString(R.string.share_link_text), imageTags, imageId);
    }

    private Intent getShareIntentWithText(String sharingText) {
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, sharingText)
                .setType("text/plain");
    }

    /**
     * Returns the significant image tags (those of types
     * {@link derpibooru.derpy.data.server.DerpibooruTag.TagType#Artist} and
     * {@link derpibooru.derpy.data.server.DerpibooruTag.TagType#ContentSafety}),
     * joining them into a string.
     */
    private String getSignificantTags(List<DerpibooruTag> imageTags) {
        StringBuilder tagListBuilder = new StringBuilder();
        for (DerpibooruTag tag : imageTags) {
            if ((tag.getType() == DerpibooruTag.TagType.Artist)
                    || (tag.getType() == DerpibooruTag.TagType.ContentSafety)) {
                tagListBuilder.append(tag.getName());
                tagListBuilder.append(", ");
            }
        }
        if (tagListBuilder.length() > 0) {
            tagListBuilder.delete(tagListBuilder.length() - 2, tagListBuilder.length()); /* remove ', ' */
        }
        return tagListBuilder.toString();
    }
}
