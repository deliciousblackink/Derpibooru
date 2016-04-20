package derpibooru.derpy.ui.views.htmltextview.imageactions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImageActionTest {
    @Test
    public void testExternalGifImageAction() {
        String gifSource = "https://sometotallyunknowndomain.org/hey.gif";

        ExternalGifImageAction action = (ExternalGifImageAction)
                getSerializedDeserialized(new ExternalGifImageAction(gifSource));

        assertThat(action.getImageSource(), is(gifSource));
    }

    private ImageAction getSerializedDeserialized(ImageAction sourceObject) {
        String serializedRepresentation = "random_string";
        assertThat(ImageAction.doesStringRepresentImageAction(serializedRepresentation), is(false));
        serializedRepresentation = sourceObject.toStringRepresentation();
        assertThat(ImageAction.doesStringRepresentImageAction(serializedRepresentation), is(true));

        ImageAction deserializedObject = ImageAction.fromStringRepresentation(serializedRepresentation);
        assertThat(deserializedObject.getClass().getName(), is(sourceObject.getClass().getName()));
        return deserializedObject;
    }

    @Test
    public void testEmbeddedImageAction() {
        int imageId = 110;
        String imageSource = "https://sometotallyunknowndomain.org/embedded.png";

        EmbeddedImageAction action = (EmbeddedImageAction)
                getSerializedDeserialized(new EmbeddedImageAction(imageId, imageSource));

        assertThat(action.getImageId(), is(imageId));
        assertThat(action.getImageSource(), is(imageSource));
    }

    @Test
    public void testEmbeddedFilteredImageAction() {
        int imageId = 110;
        String imageSource = "https://sometotallyunknowndomain.org/embedded.png";
        String filterSource =  "https://sometotallyunknowndomain.org/filter/filter.png";

        EmbeddedFilteredImageAction action = (EmbeddedFilteredImageAction)
                getSerializedDeserialized(new EmbeddedFilteredImageAction(imageId, imageSource, filterSource));

        assertThat(action.getImageId(), is(imageId));
        assertThat(action.getImageSource(), is(filterSource));
        action.unspoiler();
        assertThat(action.getImageSource(), is(imageSource));
    }
}
