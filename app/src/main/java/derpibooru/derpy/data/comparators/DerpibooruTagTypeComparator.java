package derpibooru.derpy.data.comparators;

import com.google.common.primitives.Ints;

import java.util.Comparator;

import derpibooru.derpy.data.server.DerpibooruTag;

/**
 * Orders DerpibooruTag objects by their tag type
 * (Artist -> SpoilerWarning -> ContentSafety -> OC -> General), or
 * (ContentSafety -> other types) if requested.
 * <br>
 * <strong>Note that the order is defined by
 * {@link derpibooru.derpy.data.server.DerpibooruTag.TagType} enum values for the tags.</strong>
 */
public class DerpibooruTagTypeComparator implements Comparator<DerpibooruTag> {
    private static final int LESS_THAN = -1;
    private static final int EQUAL = 0;
    private static final int GREATER_THAN = 1;

    private boolean mValueSpoilerWarning;

    /**
     * See the description for the class.
     *
     * @param filterSort if {@code true}, uses the order of
     *                   (ContentSafety -> other types);
     *                   if {@code false}, uses the order of
     *                   (Artist -> SpoilerWarning -> ContentSafety -> OC -> General)
     */
    public DerpibooruTagTypeComparator(boolean filterSort) {
        mValueSpoilerWarning = filterSort;
    }

    @Override
    public int compare(DerpibooruTag o1, DerpibooruTag o2) {
        return (o1 == null) ? ((o2 == null) ? EQUAL
                                            : LESS_THAN)
                            : ((o2 == null)) ? GREATER_THAN
                                             : compare(o1.getType(), o2.getType());
    }

    private int compare(DerpibooruTag.TagType a, DerpibooruTag.TagType b) {
        if (mValueSpoilerWarning) {
               return (a == DerpibooruTag.TagType.ContentSafety) ? GREATER_THAN :
               ((b == DerpibooruTag.TagType.ContentSafety) ? LESS_THAN
                                                           : EQUAL);
        } else {
            return Ints.compare(a.toValue(), b.toValue());
        }
    }
}