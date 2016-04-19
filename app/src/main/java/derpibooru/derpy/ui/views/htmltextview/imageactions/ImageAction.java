package derpibooru.derpy.ui.views.htmltextview.imageactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import derpibooru.derpy.data.GsonAbstractClassAdapter;
import derpibooru.derpy.ui.views.htmltextview.HtmlPostBodyTextView;

/**
 * An abstract class defining a user interaction with an image in {@link HtmlPostBodyTextView}.
 * <p>
 * {@link android.text.Html.ImageGetter} places images inside {@link android.text.style.ImageSpan},
 * which is not clickable, hence not allowing user to interact with the image.
 * The obvious solution is to wrap an {@code <a>} tag around the {@code <img>} element and assign
 * a custom link to the former's {@code href} attribute.
 * <p>
 * This class and its subclasses provide a functionality to store additional data in such links and
 * are meant to be used in the following way:
 * <ol>
 *     <li>
 *         An HTML parser obtains a string representation of a concrete {@link ImageAction} using its {@link #toStringRepresentation()} method
 *         and uses {@link HtmlImageActionCreator#getImageActionElement(String)} to obtain a Jsoup HTML DOM {@link org.jsoup.nodes.Element} that
 *         includes an {@code <a>} tag wrapped around the {@code <img>} tag;
 *     </li>
 *     <li>
 *         An {@link android.text.Html.ImageGetter} recreates the {@link ImageAction} object from the string it receives
 *         as the {@code src} attribute of an {@code <img>} element using the {@link #fromStringRepresentation(String)} method.
 *     </li>
 * </ol>
 * <p>
 */
public abstract class ImageAction {
    /**
     * Returns the actual image source.
     */
    public abstract String getImageSource();

    /**
     * Determines if the string provided contains an image action representation.
     * <p>
     * <strong>Warning: </strong> the method returns {@code true} if the string contains any JSON data.
     */
    public static boolean doesStringRepresentImageAction(String assumedRepresentation) {
        return assumedRepresentation.startsWith("{");
    }

    /**
     * Returns a string representation of the object.
     */
    public String toStringRepresentation() {
        return getGsonInstance().toJson(this);
    }

    /**
     * Returns an {@link ImageAction} object represented by the string.
     *
     * @param representation a string representation of an object
     * @throws IllegalArgumentException the string is not a valid representation of an object
     */
    protected static ImageAction fromStringRepresentation(String representation) throws IllegalArgumentException {
        try {
            return getGsonInstance().fromJson(representation, ImageAction.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Gson getGsonInstance() {
        return new GsonBuilder().registerTypeHierarchyAdapter(
                ImageAction.class, new GsonAbstractClassAdapter<ImageAction>()).create();
    }
}
