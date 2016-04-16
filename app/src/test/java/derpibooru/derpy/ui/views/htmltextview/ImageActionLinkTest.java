package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@Config(sdk = 19, manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ImageActionLinkTest {
    Context context;

    ImageActionLink.LinkInserter linkInserter;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        linkInserter = new ImageActionLink.LinkInserter();
    }

    @Test
    public void testGifActionLink() {
        String gifSource = "image.gif";
        Element image = Jsoup.parse("<img src=\"" + gifSource + "\" />").select("img").first();
        linkInserter.wrapGifImage(image);
        String linkWrap = image.parent().select("a").attr("href");
        ImageActionLink actionLink = new ImageActionLink(linkWrap);
        assertThat(actionLink.containsAction(), is(true));
        assertThat(actionLink.getGifImageSource(), is(gifSource));
    }

    @Test
    public void testEmbeddedImageActionLink() {
        String filterSource = "filter.png";
        String mainSource = "main.gif";

        Element wrappedFiltered = linkInserter.getWrappedEmbeddedImage(filterSource, mainSource);
        testEmbeddedImageActionLink(wrappedFiltered.parent().select("a").attr("href"), filterSource, mainSource);

        Element wrapped = linkInserter.getWrappedEmbeddedImage(mainSource);
        testEmbeddedImageActionLink(wrapped.parent().select("a").attr("href"), "", mainSource);
    }

    private void testEmbeddedImageActionLink(String linkWrap, String filterSource, String mainSource) {
        ImageActionLink actionLink = new ImageActionLink(linkWrap);
        assertThat(actionLink.containsAction(), is(true));
        assertNull(actionLink.getGifImageSource());

        ImageActionLink.EmbeddedImageActions emb = ImageActionLink.EmbeddedImageActions.forLink(linkWrap);
        assertThat(emb.getFilterImage(), is(filterSource));
        assertThat(emb.getSourceImage(), is(mainSource));
    }
}
