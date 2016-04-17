package derpibooru.derpy.ui.views.htmltextview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An action link used in {@link HtmlPostBodyTextView}.
 * <p>
 * {@link android.text.Html.ImageGetter} places images inside {@link android.text.style.ImageSpan},
 * which is not clickable, hence not allowing user to interact with the image.
 * It is possible to such interactions by wrapping an {@code <a>} tag around {@code <img>}
 * and assigning a custom link to the {@code href} attribute.
 * <p>
 * {@link ImageActionSource} is used in conjunction with this class to provide means to determine
 * what image each {@code <a>} link is associated with.
 */
public class ImageActionLink {
    private static final char DELIMITER = '\\';

    private static final String MODIFIER_ACTION_LINK = "action";
    private static final String MODIFIER_GIF_IMAGE = "gif";
    private static final String MODIFIER_EMBEDDED_MAIN_IMAGE = "main";
    private static final String MODIFIER_EMBEDDED_FILTERED_IMAGE = "filtered";

    private static final Pattern PATTERN_IMAGE_ACTION_TYPE = Pattern.compile(
            "^(?:\\\\" + MODIFIER_ACTION_LINK + "\\\\\\d*\\\\)(.*?)(?:\\\\)");
    private static final Pattern PATTERN_GIF_IMAGE_LINK = Pattern.compile(
            "^(?:\\\\" + MODIFIER_ACTION_LINK + "\\\\)(\\d*)(?:\\\\" + MODIFIER_GIF_IMAGE + "\\\\)(.*)");
    private static final Pattern PATTERN_EMBEDDED_IMAGE_ACTION_TYPE = Pattern.compile(
            "^(?:\\\\" + MODIFIER_ACTION_LINK + "\\\\\\d*\\\\)(" + MODIFIER_EMBEDDED_MAIN_IMAGE + "|" + MODIFIER_EMBEDDED_FILTERED_IMAGE + ")");
    private static final Pattern PATTERN_EMBEDDED_MAIN_IMAGE_LINK = Pattern.compile(
            "^(?:\\\\" + MODIFIER_ACTION_LINK + "\\\\)(\\d*)(?:\\\\" + MODIFIER_EMBEDDED_MAIN_IMAGE + "\\\\)(.*)");
    private static final Pattern PATTERN_EMBEDDED_FILTERED_IMAGE_LINK = Pattern.compile(
            "^(?:\\\\" + MODIFIER_ACTION_LINK + "\\\\)(\\d*)(?:\\\\" + MODIFIER_EMBEDDED_FILTERED_IMAGE + "\\\\)(.*)(?:\\\\" + MODIFIER_EMBEDDED_MAIN_IMAGE + "\\\\)(.*)");

    private static final String TEMPLATE_GIF_IMAGE_LINK =
            DELIMITER + MODIFIER_ACTION_LINK + DELIMITER + "%d"+ DELIMITER + MODIFIER_GIF_IMAGE + DELIMITER + "%s";
    private static final String TEMPLATE_EMBEDDED_IMAGE =
            DELIMITER + MODIFIER_ACTION_LINK + DELIMITER + "%d"+ DELIMITER + MODIFIER_EMBEDDED_MAIN_IMAGE + DELIMITER + "%s";
    private static final String TEMPLATE_EMBEDDED_FILTERED_IMAGE =
            DELIMITER + MODIFIER_ACTION_LINK + DELIMITER + "%d"+ DELIMITER + MODIFIER_EMBEDDED_FILTERED_IMAGE + DELIMITER + "%s" + DELIMITER + MODIFIER_EMBEDDED_MAIN_IMAGE + DELIMITER + "%s";

    private static final String TEMPLATE_HTML_DOM_ELEMENT = "<a href=\"%s\"><img src=\"%s\" /></a>";

    private final String mLink;

    public ImageActionLink(String link) {
        mLink = link;
    }

    /**
     * Determines the image action corresponding to the link.
     *
     * @return {@link ImageActionType#None} if the link does not contain an image action,
     * a corresponding image action otherwise.
     */
    public ImageActionType getImageActionType() {
        Matcher actionType = PATTERN_IMAGE_ACTION_TYPE.matcher(mLink);
        if (actionType.find()) {
            switch (actionType.group(1)) {
                case MODIFIER_GIF_IMAGE:
                    return ImageActionType.ExternalGif;
                case MODIFIER_EMBEDDED_MAIN_IMAGE:
                case MODIFIER_EMBEDDED_FILTERED_IMAGE:
                    return ImageActionType.EmbeddedImage;
            }
        }
        return ImageActionType.None;
    }

    public enum ImageActionType {
        /**
         * Use {@link ExternalGifAction}.
         */
        ExternalGif,
        /**
         * Use {@link EmbeddedImageActions#forLink(ImageActionLink)})}
         */
        EmbeddedImage,
        /**
         * The link does not contain an image action.
         */
        None
    }

    public static class ExternalGifAction {
        private final Matcher mMatcher;

        /**
         * @throws IllegalArgumentException the link does not match external GIF image action
         */
        public ExternalGifAction(ImageActionLink actionLink) throws IllegalArgumentException {
            mMatcher = PATTERN_GIF_IMAGE_LINK.matcher(actionLink.mLink);
            if (!mMatcher.find()) throw new IllegalArgumentException();
        }

        public int getImageActionId() {
            return Integer.parseInt(mMatcher.group(1));
        }

        public String getGifImageSource() {
            return mMatcher.group(2);
        }
    }

    public enum EmbeddedImageActions {
        FilteredImage(PATTERN_EMBEDDED_FILTERED_IMAGE_LINK, MODIFIER_EMBEDDED_FILTERED_IMAGE),
        Image(PATTERN_EMBEDDED_MAIN_IMAGE_LINK, MODIFIER_EMBEDDED_MAIN_IMAGE) {
            @Override
            String getFilterImage() {
                return "";
            }

            @Override
            String getSourceImage() {
                return mMatcher.group(2);
            }
        };

        private final Pattern mPattern;

        protected Matcher mMatcher;
        protected final String mActionLinkModifier;

        EmbeddedImageActions(Pattern pattern, String actionLinkModifier) {
            mPattern = pattern;
            mActionLinkModifier = actionLinkModifier;
        }

        /**
         * Returns the embedded image action for the specified link (the link that is received on
         * an {@code <a>} tag click, i.e. the {@code href} attribute of the said tag).
         *
         * @throws IllegalArgumentException the link does not match any embedded image action
         */
        public static EmbeddedImageActions forLink(ImageActionLink actionLink) throws IllegalArgumentException {
            Matcher m = PATTERN_EMBEDDED_IMAGE_ACTION_TYPE.matcher(actionLink.mLink);
            if (m.find()) {
                for (EmbeddedImageActions action : values()) {
                    if (m.group(1).equals(action.mActionLinkModifier)) {
                        action.mMatcher = action.mPattern.matcher(actionLink.mLink);
                        if (action.mMatcher.find()) {
                            return action;
                        }
                    }
                }
            }
            throw new IllegalArgumentException();
        }

        int getImageActionId() {
            return Integer.parseInt(mMatcher.group(1));
        }

        String getFilterImage() {
            return mMatcher.group(2);
        }

        String getSourceImage() {
            return mMatcher.group(3);
        }
    }

    /**
     * Action link inserter that operates on Jsoup HTML DOM {@link Element}s.
     */
    public static class LinkInserter {
        /**
         * Wraps the {@code <img>} {@link Element} in an action link ({@code <a>}) for GIF images.
         *
         * @param imageActionSourceId {@link ImageActionSource} ID to be assigned to the action link
         * @param gifSource GIF image action source (obtainable via {@link derpibooru.derpy.ui.views.htmltextview.ImageActionSource.SourceBuilder})
         */
        public static Element getWrappedExternalGifImage(int imageActionSourceId, String gifSource) {
            String link = String.format(TEMPLATE_GIF_IMAGE_LINK, imageActionSourceId, gifSource);
            String element = String.format(TEMPLATE_HTML_DOM_ELEMENT, link, gifSource);
            return Jsoup.parse(element).select("a").first();
        }

        /**
         * Returns an {@link Element} consisting of a <strong>filtered</strong> embedded image action
         * link ({@code <a>}) wrapped around {@code <img>}.
         *
         * @param imageActionSourceId {@link ImageActionSource} ID to be assigned to the action link
         * @param filterImage filter image action source (obtainable via {@link derpibooru.derpy.ui.views.htmltextview.ImageActionSource.SourceBuilder})
         * @param sourceImage main image action source (obtainable via {@link derpibooru.derpy.ui.views.htmltextview.ImageActionSource.SourceBuilder})
         */
        public static Element getWrappedEmbeddedImage(int imageActionSourceId, String filterImage, String sourceImage) {
            String link = String.format(TEMPLATE_EMBEDDED_FILTERED_IMAGE, imageActionSourceId, filterImage, sourceImage);
            String element = String.format(TEMPLATE_HTML_DOM_ELEMENT, link, filterImage);
            return Jsoup.parse(element).select("a").first();
        }

        /**
         * Returns an {@link Element} consisting of an embedded image action link ({@code <a>})
         * wrapped around {@code <img>}.
         *
         * @param imageActionSourceId {@link ImageActionSource} ID to be assigned to the action link
         * @param sourceImage main image action source (obtainable via {@link derpibooru.derpy.ui.views.htmltextview.ImageActionSource.SourceBuilder})
         */
        public static Element getWrappedEmbeddedImage(int imageActionSourceId, String sourceImage) {
            String link = String.format(TEMPLATE_EMBEDDED_IMAGE, imageActionSourceId, sourceImage);
            String element = String.format(TEMPLATE_HTML_DOM_ELEMENT, link, sourceImage);
            return Jsoup.parse(element).select("a").first();
        }
    }
}
