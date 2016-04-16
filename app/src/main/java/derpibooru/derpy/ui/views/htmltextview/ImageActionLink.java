package derpibooru.derpy.ui.views.htmltextview;

import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An action link used in {@link CustomFormattingHtmlTextView}.
 * <p>
 * {@link android.text.Html.ImageGetter} places images inside {@link android.text.style.ImageSpan},
 * which is not clickable, hence not allowing user to interact with the image.
 * It is possible to such interactions by wrapping an {@code <a>} tag around {@code <img>}
 * and assigning a custom link to the {@code href} attribute.
 * <p>
 * This class provides a number of basic links for embedded images in comments.
 */
public class ImageActionLink {
    private static final char ACTION_LINK_DELIMITER = '\\';

    private static final String ACTION_LINK_START_MODIFIER = "action";
    private static final String ACTION_LINK_MODIFIER_GIF_IMAGE = "gif";
    private static final String ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE = "main";
    private static final String ACTION_LINK_MODIFIER_EMBEDDED_FILTERED_IMAGE = "filtered";

    private static final Pattern PATTERN_IMAGE_ACTION_LINK = Pattern.compile(
            "^(?:\\\\" + ACTION_LINK_START_MODIFIER + "\\\\)(.*)");
    private static final Pattern PATTERN_GIF_IMAGE_LINK = Pattern.compile(
            "^(?:\\\\" + ACTION_LINK_START_MODIFIER + "\\\\" + ACTION_LINK_MODIFIER_GIF_IMAGE + "\\\\)(.*)");
    private static final Pattern PATTERN_EMBEDDED_IMAGE_ACTION_TYPE = Pattern.compile(
            "^(?:\\\\" + ACTION_LINK_START_MODIFIER + "\\\\)(" + ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE + "|" + ACTION_LINK_MODIFIER_EMBEDDED_FILTERED_IMAGE + ")");
    private static final Pattern PATTERN_EMBEDDED_MAIN_IMAGE_LINK = Pattern.compile(
            "^(?:\\\\" + ACTION_LINK_START_MODIFIER + "\\\\" + ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE + "\\\\)(.*)");
    private static final Pattern PATTERN_EMBEDDED_FILTERED_IMAGE_LINK = Pattern.compile(
            "^(?:\\\\" + ACTION_LINK_START_MODIFIER + "\\\\" + ACTION_LINK_MODIFIER_EMBEDDED_FILTERED_IMAGE + "\\\\)(.*)(?:\\\\" + ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE + "\\\\)(.*)");

    private static final String ACTION_LINK_TEMPLATE_GIF = ACTION_LINK_DELIMITER + ACTION_LINK_START_MODIFIER + ACTION_LINK_DELIMITER
            + ACTION_LINK_MODIFIER_GIF_IMAGE + ACTION_LINK_DELIMITER + "%s";
    private static final String ACTION_LINK_TEMPLATE_EMBEDDED_IMAGE = ACTION_LINK_DELIMITER + ACTION_LINK_START_MODIFIER + ACTION_LINK_DELIMITER
            + ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE + ACTION_LINK_DELIMITER + "%s";
    private static final String ACTION_LINK_TEMPLATE_EMBEDDED_FILTERED_IMAGE = ACTION_LINK_DELIMITER + ACTION_LINK_START_MODIFIER
            + ACTION_LINK_DELIMITER + ACTION_LINK_MODIFIER_EMBEDDED_FILTERED_IMAGE + ACTION_LINK_DELIMITER
            + "%s" + ACTION_LINK_DELIMITER + ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE + ACTION_LINK_DELIMITER + "%s";

    private static final String DOM_ELEMENT_TEMPLATE = "<a href=\"%s\"><img src=\"%s\" /></a>";

    private final String mLink;

    public ImageActionLink(String link) {
        mLink = link;
    }

    /**
     * Determines if the link contains an image action.
     */
    public boolean containsAction() {
        Matcher action = PATTERN_IMAGE_ACTION_LINK.matcher(mLink);
        return action.find();
    }

    /**
     * Returns a source link for a GIF image.
     * @return an URL <strong>if the action link is associated with a GIF image</strong>; {@code null} otherwise.
     */
    @Nullable
    public String getGifImageSource() {
        Matcher gif = PATTERN_GIF_IMAGE_LINK.matcher(mLink);
        return gif.find() ? gif.group(1) : null;
    }

    public enum EmbeddedImageActions {
        FilteredImage(PATTERN_EMBEDDED_FILTERED_IMAGE_LINK, ACTION_LINK_MODIFIER_EMBEDDED_FILTERED_IMAGE),
        Image(PATTERN_EMBEDDED_MAIN_IMAGE_LINK, ACTION_LINK_MODIFIER_EMBEDDED_MAIN_IMAGE) {
            @Override
            String getFilterImage() {
                return "";
            }

            @Override
            String getSourceImage() {
                return mMatcher.group(1);
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
        public static EmbeddedImageActions forLink(String link) throws IllegalArgumentException {
            Matcher m = PATTERN_EMBEDDED_IMAGE_ACTION_TYPE.matcher(link);
            if (m.find()) {
                for (EmbeddedImageActions action : values()) {
                    if (m.group(1).equals(action.mActionLinkModifier)) {
                        action.mMatcher = action.mPattern.matcher(link);
                        if (action.mMatcher.find()) {
                            return action;
                        }
                    }
                }
            }
            throw new IllegalArgumentException();
        }

        String getFilterImage() {
            return mMatcher.group(1);
        }

        String getSourceImage() {
            return mMatcher.group(2);
        }
    }

    /**
     * Action link inserter that operates on Jsoup HTML DOM {@link Element}s.
     */
    public static class LinkInserter {
        /**
         * Wraps the {@code <img>} {@link Element} in an action link ({@code <a>}) for GIF images.
         */
        public static void wrapGifImage(Element img) {
            img.wrap(String.format("<a href=\"%s\"></a>",
                                   String.format(ACTION_LINK_TEMPLATE_GIF, img.attr("src"))));
        }

        /**
         * Returns an {@link Element} consisting of a <strong>filtered</strong> embedded image action
         * link ({@code <a>}) wrapped around {@code <img>}.
         *
         * @param filterImage filter image source
         * @param sourceImage main image source
         */
        public static Element getWrappedEmbeddedImage(String filterImage, String sourceImage) {
            String link = String.format(ACTION_LINK_TEMPLATE_EMBEDDED_FILTERED_IMAGE, filterImage, sourceImage);
            String element = String.format(DOM_ELEMENT_TEMPLATE, link, filterImage);
            return Jsoup.parse(element).select("a").first();
        }

        /**
         * Returns an {@link Element} consisting of an embedded image action link ({@code <a>})
         * wrapped around {@code <img>}.
         *
         * @param sourceImage main image source
         */
        public static Element getWrappedEmbeddedImage(String sourceImage) {
            String link = String.format(ACTION_LINK_TEMPLATE_EMBEDDED_IMAGE, sourceImage);
            String element = String.format(DOM_ELEMENT_TEMPLATE, link, sourceImage);
            return Jsoup.parse(element).select("a").first();
        }
    }
}
