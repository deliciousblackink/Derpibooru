package derpibooru.derpy.ui.views.htmltextview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@code <img> src} attribute used in conjunction with {@link ImageActionLink}.
 * <p>
 * A unique ID (see {@link SourceBuilder} for the definition of 'unique') is assigned to each {@code <img> src}
 * and its complementary {@code <a>} action link, making it possible to determine if the former belongs to the latter,
 * assuming there may be more than one image with the same {@code src} in the HTML document.
 */
public class ImageActionSource {
    private static final char DELIMITER = '|';

    private static final Pattern PATTERN_ACTION_SOURCE = Pattern.compile("^(\\d*)(?:\\|)(.*)");
    private static final String TEMPLATE_ACTION_SOURCE = "%d" + DELIMITER + "%s";

    private final Matcher mSourceMatcher;

    /**
     * @param source {@code <img> src} attribute of an element created using {@link SourceBuilder}.
     * @throws IllegalArgumentException the source does not match the {@link SourceBuilder} format (use {@link #isImageActionSource(String)} to determine if it does)
     */
    public ImageActionSource(String source) throws IllegalArgumentException {
        mSourceMatcher = PATTERN_ACTION_SOURCE.matcher(source);
        if (!mSourceMatcher.find()) {
            throw new IllegalArgumentException();
        }
    }

    public static boolean isImageActionSource(String source) {
        Matcher m = PATTERN_ACTION_SOURCE.matcher(source);
        return m.find();
    }

    public int getActionLinkId() {
        return Integer.parseInt(mSourceMatcher.group(1));
    }

    public String getImageSource() {
        return mSourceMatcher.group(2);
    }

    /**
     * Provides image source IDs that are unique for a single instance of the class. If multiple instances are used in
     * the same HTML document, IDs are <strong>repeated</strong>.
     */
    public static class SourceBuilder {
        private int mCurrentImage = 0;

        /**
         * Returns an image source ID to be used in {@link #getImageActionSource(int, String)}
         * and to be later passed to {@link derpibooru.derpy.ui.views.htmltextview.ImageActionLink.LinkInserter}.
         *
         * @return an ID value unique to this particular instance of the class.
         */
        public int getSourceId() {
            return mCurrentImage++;
        }

        /**
         * Returns an action source that needs to be set as a {@link src} attribute of an {@code <img>} element
         * wrapped in a {@link ImageActionLink}.
         * @param sourceId an ID obtainable via {@link #getSourceId()} method
         * @param imageSource an URL pointing to the image file
         * @return
         */
        public String getImageActionSource(int sourceId, String imageSource) {
            return String.format(TEMPLATE_ACTION_SOURCE, sourceId, imageSource);
        }
    }
}
