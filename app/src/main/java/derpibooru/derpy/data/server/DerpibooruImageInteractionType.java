package derpibooru.derpy.data.server;

public enum DerpibooruImageInteractionType {
    None(0),
    Fave(1),
    Upvote(2),
    Downvote(3);

    private int mValue;

    DerpibooruImageInteractionType(int value) {
        mValue = value;
    }

    public static DerpibooruImageInteractionType fromValue(int value) {
        for (DerpibooruImageInteractionType type : values()) {
            if (type.mValue == value) {
                return type;
            }
        }
        return None;
    }

    public int toValue() {
        return mValue;
    }
}
