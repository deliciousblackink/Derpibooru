package derpibooru.derpy.data.server;

public enum DerpibooruRankingsListType {
    TopScoring(1),
    MostCommented(2);

    private int mValue;

    DerpibooruRankingsListType(int value) {
        mValue = value;
    }

    public static DerpibooruRankingsListType fromValue(int value) {
        for (DerpibooruRankingsListType type : values()) {
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
