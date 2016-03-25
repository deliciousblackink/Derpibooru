package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;

import org.xml.sax.XMLReader;

/**
 * @author http://stackoverflow.com/a/4062318/1726690
 */
class CustomFormattingTagHandler implements Html.TagHandler {
    private Context mContext;

    CustomFormattingTagHandler(Context context) {
        mContext = context;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("spoiler")) {
            processSpoiler(opening, output);
        }
    }

    private void processSpoiler(boolean opening, Editable output) {
        int length = output.length();
        if (opening) {
            output.setSpan(new SpoilerSpan(mContext), length, length, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLastSpan(output, SpoilerSpan.class);
            int where = output.getSpanStart(obj);

            output.removeSpan(obj);

            if (where != length) {
                output.setSpan(new SpoilerSpan(mContext), where, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private Object getLastSpan(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }
}