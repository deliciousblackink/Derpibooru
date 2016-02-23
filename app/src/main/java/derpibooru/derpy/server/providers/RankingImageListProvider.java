package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImage;
import derpibooru.derpy.server.QueryHandler;

public class RankingImageListProvider extends ImageListProvider {
    private static final String ALL_TIME = "520w";
    /* 520 weeks (10 years) effectively equals to 'All Time' */

    private RankingsType mListType;
    private String mTime = ALL_TIME;

    public RankingImageListProvider(Context context, QueryHandler<List<DerpibooruImage>> handler) {
        super(context, handler);
    }

    /**
     * Sets image list type (either Top Scoring or Most commented).
     *
     * @param listType the type of the image list
     */
    public RankingImageListProvider type(RankingsType listType) {
        mListType = listType;
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param hours the time limit in hours
     */
    public RankingImageListProvider inHours(int hours) {
        mTime = Integer.toString(hours) + "h";
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param days the time limit in days
     */
    public RankingImageListProvider inDays(int days) {
        mTime = Integer.toString(days) + "d";
        return this;
    }

    /**
     * Sets time limit for an image list in weeks
     * (the default value is All Time).
     *
     * @param weeks the time limit in weeks
     */
    public RankingImageListProvider inWeeks(int weeks) {
        mTime = Integer.toString(weeks) + "w";
        return this;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        switch (mListType) {
            case TopScoring:
                sb.append("lists/top_scoring.json");
                break;
            case MostCommented:
                sb.append("lists/top_commented.json");
                break;
        }
        sb.append("?last=");
        sb.append(mTime);
        sb.append("&perpage=");
        sb.append(IMAGES_PER_PAGE);
        sb.append("&page=");
        sb.append(super.getCurrentPage());
        return sb.toString();
    }

    public enum RankingsType {
        TopScoring(1),
        MostCommented(2);

        private int mValue;

        RankingsType(int value) {
            mValue = value;
        }

        public static RankingsType fromValue(int value) {
            for (RankingsType type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return TopScoring;
        }

        public int toValue() {
            return mValue;
        }
    }
}
