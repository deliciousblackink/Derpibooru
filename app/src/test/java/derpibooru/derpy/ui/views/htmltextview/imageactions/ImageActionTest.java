package derpibooru.derpy.ui.views.htmltextview.imageactions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImageActionTest {
    @Test
    public void testExternalGifImageAction() {
        String gifSource = "https://sometotallyunknowndomain.org/hey.gif";
        String title = "";

        ExternalGifImageAction action = new ExternalGifImageAction(gifSource, title);
        String representation = action.toStringRepresentation();
        action = new ExternalGifImageAction(representation);

        assertThat(action.getImageSource(), is(gifSource));
        assertThat(action.getTitle(), is(title));
    }

    @Test
    public void testEmbeddedImageAction() {
        int imageId = 110;
        String imageSource = "https://sometotallyunknowndomain.org/embedded.png";

        EmbeddedImageAction action = new EmbeddedImageAction(imageId, imageSource);
        String representation = action.toStringRepresentation();
        action = new EmbeddedImageAction(representation);

        assertThat(action.getImageId(), is(imageId));
        assertThat(action.getImageSource(), is(imageSource));
    }

    @Test
    public void testEmbeddedFilteredImageAction() {
        int imageId = 110;
        String imageSource = "https://sometotallyunknowndomain.org/embedded.png";
        String filterSource =  "https://sometotallyunknowndomain.org/filter/filter.png";

        EmbeddedFilteredImageAction action = new EmbeddedFilteredImageAction(imageId, imageSource, filterSource);
        String representation = action.toStringRepresentation();
        action = new EmbeddedFilteredImageAction(representation);

        assertThat(action.getImageId(), is(imageId));
        assertThat(action.getImageSource(), is(filterSource));
        action.unspoiler();
        assertThat(action.getImageSource(), is(imageSource));
    }
}
