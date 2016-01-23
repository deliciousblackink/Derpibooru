package derpibooru.derpy.server;

import android.content.Context;

import derpibooru.derpy.data.server.DerpibooruImageListType;

public class ImageListProvider extends DataProvider {
    private static final String ALL_TIME = "520w";
    /* 520 weeks (10 years) effectively equals to 'All Time' */

    private DerpibooruImageListType mListType;
    private String mTime = ALL_TIME;

    public ImageListProvider(Context context, DataProviderRequestHandler handler) {
        super(context, handler);
    }

    /**
     * Sets image list type (either Top Scoring or Most commented).
     *
     * @param listType the type of the image list
     */
    public ImageListProvider type(DerpibooruImageListType listType) {
        mListType = listType;
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param hours the time limit in hours
     */
    public ImageListProvider inHours(int hours) {
        mTime = Integer.toString(hours) + "h";
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param days the time limit in days
     */
    public ImageListProvider inDays(int days) {
        mTime = Integer.toString(days) + "d";
        return this;
    }

    /**
     * Sets time limit for an image list in weeks
     * (the default value is All Time).
     *
     * @param weeks the time limit in weeks
     */
    public ImageListProvider inWeeks(int weeks) {
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
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(generateUrl(), new ImageListParser());
    }
}
