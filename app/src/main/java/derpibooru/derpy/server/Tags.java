package derpibooru.derpy.server;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagFull;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.parsers.TagListParser;
import derpibooru.derpy.storage.TagStorage;

public class Tags {
    private TagRequestHandler mHandler;
    private Context mContext;

    private TagListProvider mTagListProvider;
    private TagStorage mTagStorage;

    public Tags(Context context, TagRequestHandler handler) {
        mHandler = handler;
        mContext = context;

        mTagListProvider = new TagListProvider(context, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                List<DerpibooruTagFull> tags = (List<DerpibooruTagFull>) result;
                mHandler.onTagsFetched(tags);
                for (DerpibooruTagFull tag : tags) {
                    mTagStorage.setTag(tag);
                }
            }

            @Override
            public void onRequestFailed() { }
        });
        mTagStorage = new TagStorage(context);
    }

    public void fetchTags(List<Integer> tagIds) {
        List<DerpibooruTagFull> tagsAvailableInLocalStorage = new ArrayList<>();
        List<Integer> tagsToBeFetchedFromServer = new ArrayList<>();
        for (Integer tagId : tagIds) {
            DerpibooruTagFull tag = mTagStorage.getTag(tagId);
            if (tag != null) {
                tagsAvailableInLocalStorage.add(tag);
            } else {
                tagsToBeFetchedFromServer.add(tagId);
            }
        }
        if (tagsToBeFetchedFromServer.size() == 0) {
            mHandler.onTagsFetched(tagsAvailableInLocalStorage);
        } else if (tagsAvailableInLocalStorage.size() != 0) {
            mTagListProvider.tags(tagsToBeFetchedFromServer)
                    .withExistingTags(tagsAvailableInLocalStorage)
                    .fetch();
        } else {
            mTagListProvider.tags(tagsToBeFetchedFromServer)
                    .fetch();
        }
    }

    public void fetchSpoileredTagsForCurrentFilter() {
        User user = new User(mContext, new User.UserRequestHandler() {
            @Override
            public void onUserDataObtained(DerpibooruUser userData) {
                fetchTags(userData.getCurrentFilter().getSpoileredTags());
            }

            @Override
            public void onNetworkError() { }
        });
        user.fetchUserData();
    }

    public interface TagRequestHandler {
        void onTagsFetched(List<DerpibooruTagFull> tagList);
    }

    private static class TagListProvider extends Provider {
        private List<Integer> mTagIds;
        private List<DerpibooruTagFull> mExistingTags;

        public TagListProvider(Context context, ProviderRequestHandler handler) {
            super(context, handler);
        }

        public TagListProvider tags(List<Integer> tagIds) {
            mTagIds = tagIds;
            return this;
        }

        public TagListProvider withExistingTags(List<DerpibooruTagFull> existingTags) {
            mExistingTags = existingTags;
            return this;
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN);
            sb.append(DERPIBOORU_API_ENDPOINT);
            sb.append("tags/fetch_many.json?");
            for (int tagId : mTagIds) {
                try {
                    sb.append(URLEncoder.encode("ids[]", "UTF-8"))
                            .append("=").append(tagId).append("&");
                } catch (UnsupportedEncodingException e) {
                    break;
                }
            }
            /* remove & */
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

        @Override
        public void fetch() {
            if (mExistingTags == null) {
                super.executeQuery(new TagListParser());
            } else {
                super.executeQuery(new TagListParser(mExistingTags));
            }
        }
    }
}
