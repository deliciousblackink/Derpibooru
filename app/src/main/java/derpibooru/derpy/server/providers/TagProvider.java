package derpibooru.derpy.server.providers;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.TagListParser;
import derpibooru.derpy.storage.TagStorage;

public class TagProvider extends Provider<List<DerpibooruTagDetailed>> {
    private List<DerpibooruTagDetailed> mCachedTags = new ArrayList<>();
    private TagStorage mTagStorage;

    private List<Integer> mRequestedTagIds;

    private boolean mOverrideCache = false;

    public TagProvider(Context context, QueryHandler<List<DerpibooruTagDetailed>> handler) {
        super(context, handler);
        mTagStorage = new TagStorage(context);
    }

    public TagProvider tags(List<Integer> tagIds) {
        mRequestedTagIds = tagIds;
        return this;
    }

    public TagProvider overrideCache() {
        mOverrideCache = true;
        return this;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append(DERPIBOORU_API_ENDPOINT);
        sb.append("tags/fetch_many.json?");
        for (int tagId : mRequestedTagIds) {
            try {
                sb.append(URLEncoder.encode("ids[]", "UTF-8"))
                        .append("=").append(tagId).append("&");
            } catch (UnsupportedEncodingException e) {
                break;
            }
        }
        /* remove '&' */
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public void fetch() {
        if (mOverrideCache || !areTagsCached()) {
            super.executeQuery(new TagListParser(mCachedTags));
        }
    }

    @Override
    protected void cacheResponse(List<DerpibooruTagDetailed> tagsFromServer) {
        for (DerpibooruTagDetailed tag : tagsFromServer) {
            mTagStorage.setTag(tag);
        }
    }

    private boolean areTagsCached() {
        List<Integer> tagsToBeFetchedFromServer = new ArrayList<>();
        for (Integer tagId : mRequestedTagIds) {
            DerpibooruTagDetailed tag = mTagStorage.getTag(tagId);
            if (tag != null) {
                mCachedTags.add(tag);
            } else {
                tagsToBeFetchedFromServer.add(tagId);
            }
        }
        if (tagsToBeFetchedFromServer.isEmpty()) {
            mHandler.onQueryExecuted(mCachedTags);
            return true;
        }
        mRequestedTagIds = tagsToBeFetchedFromServer;
        return false;
    }
}