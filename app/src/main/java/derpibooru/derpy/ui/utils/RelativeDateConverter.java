package derpibooru.derpy.ui.utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RelativeDateConverter {
    public static final String DATE_FORMAT_RETURNED_BY_DERPIBOORU = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIMEZONE_RETURNED_BY_DERPIBOORU = "UTC";

    private String mFormat;
    private String mTimezone;

    public RelativeDateConverter(String expectedDateTimeFormat, String expectedTimeZone) {
        mFormat = expectedDateTimeFormat;
        mTimezone = expectedTimeZone;
    }

    protected long getCurrentTimeInMilliseconds() {
        return new Date().getTime();
    }

    public String getRelativeDate(String date) {
        SimpleDateFormat f = new SimpleDateFormat(mFormat, Locale.ENGLISH);
        f.setTimeZone(TimeZone.getTimeZone(mTimezone));
        try {
            long nowInMilliseconds = getCurrentTimeInMilliseconds();
            long commentInMilliseconds = f.parse(date).getTime();
            /* SECOND_IN_MILLIS to display "x seconds ago" */
            return DateUtils.getRelativeTimeSpanString(commentInMilliseconds,
                                                       nowInMilliseconds, DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            Log.e("ImageCommentsAdapter", "error parsing date string", e);
        }
        return "";
    }
}
