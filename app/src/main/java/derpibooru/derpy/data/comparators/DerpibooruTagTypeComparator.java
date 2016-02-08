package derpibooru.derpy.data.comparators;

import java.util.Comparator;

import derpibooru.derpy.data.server.DerpibooruTag;

/**
 * A comparison function that orders DerpibooruTag objects by their tag type
 * (TagType.ContentSafety -> other types).
 */
public class DerpibooruTagTypeComparator implements Comparator<DerpibooruTag> {
    private final static int LESS_THAN = -1;
    private final static int EQUAL = 0;
    private final static int GREATER_THAN = 1;

    @Override
    public int compare(DerpibooruTag o1, DerpibooruTag o2) {
        return (o1 == null) ? ((o2 == null) ? EQUAL
                                            : LESS_THAN)
                            : ((o2 == null)) ? GREATER_THAN
                                             : compare(o1.getType(), o2.getType());
    }

    private int compare(DerpibooruTag.TagType a, DerpibooruTag.TagType b) {
        return (a == DerpibooruTag.TagType.ContentSafety) ? GREATER_THAN :
               ((b == DerpibooruTag.TagType.ContentSafety) ? LESS_THAN
                                                           : EQUAL);
    }
}
