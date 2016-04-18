package derpibooru.derpy.ui.views.htmltextview.imageactions;

import derpibooru.derpy.ui.views.htmltextview.HtmlPostBodyTextView;

/**
 * A contract for a user interaction with an image in {@link HtmlPostBodyTextView}.
 * <p>
 * {@link android.text.Html.ImageGetter} places images inside {@link android.text.style.ImageSpan},
 * which is not clickable, hence not allowing user to interact with the image.
 * The obvious solution is to wrap an {@code <a>} tag around the {@code <img>} element and assign
 * a custom link to the former's {@code href} attribute.
 * <p>
 * The objects implementing this interface provide a functionality to store additional data in such links and
 * are meant to be used in the following way:
 * <ol>
 *     <li>
 *         An HTML parser obtains a string representation of a specific {@link ImageAction} using the {@link #toStringRepresentation()} method
 *         and uses {@link HtmlImageActionCreator#getImageActionElement(String)} to obtain a Jsoup HTML DOM {@link org.jsoup.nodes.Element} that
 *         includes an {@code <a>} tag wrapped around the {@code <img>} tag;
 *     </li>
 *     <li>
 *         An {@link android.text.Html.ImageGetter} recreates the subclass of an {@link ImageAction} object from the
 *         string it receives as the {@code src} attribute of an {@code <img>} element using the appropriate constructor.
 *     </li>
 * </ol>
 * <p>
 * <strong>Note: all objects implementing the interface are expected to have a constructor that accepts their
 * string representation</strong> (obtainable via the {@link #toStringRepresentation()} method). The restriction, unfortunately, cannot be
 * imposed, as Java does not allow static interface/abstract methods.
 */
public interface ImageAction {
    /**
     * Returns the actual image source.
     */
    String getImageSource();

    /**
     * Returns a string representation of the object.
     */
    String toStringRepresentation();
}
