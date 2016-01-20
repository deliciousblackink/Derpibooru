package derpibooru.derpy.data.server;

public enum DerpibooruImageListType {
    TopScoring(1),
    MostCommented(2);

    private int mValue;

    DerpibooruImageListType(int value) {
        mValue = value;
    }

    public static DerpibooruImageListType getFromValue(int value) {
        for (DerpibooruImageListType type : values()) {
            if (type.mValue == value) {
                return type;
            }
        }
        return TopScoring;
    }

    public int convertToValue() {
        return mValue;
    }
}
