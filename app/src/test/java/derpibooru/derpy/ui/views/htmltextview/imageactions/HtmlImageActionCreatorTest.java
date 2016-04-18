package derpibooru.derpy.ui.views.htmltextview.imageactions;

import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HtmlImageActionCreatorTest {
    @Test
    public void testGetImageActionElement() {
        String link = "{action_string_representation}";
        Element element = HtmlImageActionCreator.getImageActionElement(link);

        assertThat(element.tag().getName(), is("a"));
        assertThat(element.attr("href"), is(link));
        assertThat(element.children().size(), is(1));
        assertThat(element.children().first().tag().getName(), is("img"));
        assertThat(element.children().first().attr("src"), is(link));
    }
}
