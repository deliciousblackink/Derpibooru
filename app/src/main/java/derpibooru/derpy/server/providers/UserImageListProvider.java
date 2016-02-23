package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImage;
import derpibooru.derpy.server.QueryHandler;

public class UserImageListProvider extends ImageListProvider {
    private UserListType mListType;

    public UserImageListProvider(Context context, QueryHandler<List<DerpibooruImage>> handler) {
        super(context, handler);
    }

    /**
     * Sets image list type.
     *
     * @param listType the type of the image list
     */
    public UserImageListProvider type(UserListType listType) {
        mListType = listType;
        return this;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        switch (mListType) {
            case Faved:
                sb.append("images/favourites.json");
                break;
            case Upvoted:
                sb.append("images/upvoted.json");
                break;
            case Uploaded:
                sb.append("images/uploaded.json");
                break;
        }
        sb.append("&perpage=");
        sb.append(IMAGES_PER_PAGE);
        sb.append("&page=");
        sb.append(super.getCurrentPage());
        return sb.toString();
    }

    public enum UserListType {
        Faved(0),
        Upvoted(1),
        Uploaded(2);

        private int mValue;

        UserListType(int value) {
            mValue = value;
        }

        public static UserListType fromValue(int value) {
            for (UserListType type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return Faved;
        }

        public int toValue() {
            return mValue;
        }
    }
}
