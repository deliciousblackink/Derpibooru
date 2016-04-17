package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * An integration test for {@link ImageActionLink} and {@link ImageActionSource}.
 */
@Config(sdk = 19, manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ImageActionTest {
    Context context;

    /* shared between tests */
    ImageActionSource.SourceBuilder sourceBuilder;

    public ImageActionTest() {
        sourceBuilder = new ImageActionSource.SourceBuilder();
    }

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testGifActionLink() {
        int sourceId = sourceBuilder.getSourceId();
        String gifSource = sourceBuilder.getImageActionSource(sourceId, "image.gif");

        Element wrappedGif = ImageActionLink.LinkInserter.getWrappedExternalGifImage(sourceId, gifSource);
        String linkWrap = wrappedGif.parent().select("a").attr("href");

        ImageActionLink actionLink = new ImageActionLink(linkWrap);
        ImageActionLink.ImageActionType linkType = actionLink.getImageActionType();
        assertThat(linkType, is(ImageActionLink.ImageActionType.ExternalGif));

        ImageActionLink.ExternalGifAction gifAction = new ImageActionLink.ExternalGifAction(actionLink);
        assertThat(gifAction.getImageActionId(), is(sourceId));
        assertThat(gifAction.getGifImageSource(), is(gifSource));
    }

    @Test
    public void testEmbeddedMainImageActionLink() {
        int sourceId = sourceBuilder.getSourceId();
        String mainSource = sourceBuilder.getImageActionSource(sourceId, "main.gif");

        Element wrapped = ImageActionLink.LinkInserter.getWrappedEmbeddedImage(sourceId, mainSource);
        testEmbeddedImageActionLink(new ImageActionLink(wrapped.parent().select("a").attr("href")), sourceId, "", mainSource);
    }

    @Test
    public void testEmbeddedFilteredImageActionLink() {
        int sourceId = sourceBuilder.getSourceId();
        String filterSource = sourceBuilder.getImageActionSource(sourceId, "filter.png");
        String mainSource = sourceBuilder.getImageActionSource(sourceId, "main.gif");

        Element wrappedFiltered = ImageActionLink.LinkInserter.getWrappedEmbeddedImage(sourceId, filterSource, mainSource);
        testEmbeddedImageActionLink(new ImageActionLink(wrappedFiltered.parent().select("a").attr("href")), sourceId, filterSource, mainSource);
    }

    private void testEmbeddedImageActionLink(ImageActionLink actionLink, int actionSourceId, String filterSource, String mainSource) {
        ImageActionLink.ImageActionType linkType = actionLink.getImageActionType();
        assertThat(linkType, is(ImageActionLink.ImageActionType.EmbeddedImage));

        ImageActionLink.EmbeddedImageActions emb = ImageActionLink.EmbeddedImageActions.forLink(actionLink);
        assertThat(emb.getImageActionId(), is(actionSourceId));
        assertThat(emb.getFilterImage(), is(filterSource));
        assertThat(emb.getSourceImage(), is(mainSource));
    }
}
