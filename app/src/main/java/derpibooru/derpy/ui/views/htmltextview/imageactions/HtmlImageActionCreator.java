package derpibooru.derpy.ui.views.htmltextview.imageactions;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * Jsoup HTML DOM {@link org.jsoup.nodes.Element} creator for {@link ImageAction} string representations.
 */
public class HtmlImageActionCreator {
    /**
     * Returns an {@link org.jsoup.nodes.Element} consisting of an {@code <a>} tag wrapped around
     * the {@code <img>} tag, both of which share the {@link ImageAction} link representation provided.
     */
    public static Element getImageActionElement(String imageActionStringRepresentation) {
        return new Element(Tag.valueOf("a"), "")
                .attr("href", imageActionStringRepresentation)
                .appendChild(
                        new Element(Tag.valueOf("img"), "")
                                .attr("src", imageActionStringRepresentation));
    }
}
