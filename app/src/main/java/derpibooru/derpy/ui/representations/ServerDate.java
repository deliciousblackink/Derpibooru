package derpibooru.derpy.ui.representations;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents date and time received from the server.
 */
public class ServerDate {
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String SERVER_TIMEZONE = "UTC";
    private static final long TIME_NOT_PARSED = -1L;

    private final long mServerTimeInMillis;

    /**
     * Instantiates a date object from a server date string. If the string provided
     * does not match the server format, the exception is logged, but not thrown.
     */
    public ServerDate(String serverDateResponse) {
        mServerTimeInMillis = convertServerDateToMillis(serverDateResponse);
    }

    /**
     * @return a string representation of date returned by server relative to the current device date,
     * or an empty string if the date returned by server has not been succesfully parsed.
     */
    public String getRelativeTimeSpanString() {
        return (mServerTimeInMillis != TIME_NOT_PARSED)
               ? DateUtils.getRelativeTimeSpanString(mServerTimeInMillis, getCurrentTimeInMillis(), DateUtils.SECOND_IN_MILLIS)
                       .toString()
               : "";
    }

    /**
     * If the specified format cannot be applied to the date returned by server, the exception
     * is logged, but not thrown, and an empty string is returned.
     *
     * @return a string representation of date returned by server in the specified date format,
     * or an empty string if the date returned by server has not been succesfully parsed.
     */
    public String getFormattedTimeString(@NonNull String dateFormat, @NonNull Locale dateLocale) {
        try {
            return new SimpleDateFormat(dateFormat, dateLocale).format(mServerTimeInMillis);
        } catch (IllegalArgumentException e) {
            Log.e("ServerDate", "error formatting server date with the specified args", e);
            return "";
        }
    }

    private long convertServerDateToMillis(String serverDate) {
        try {
            SimpleDateFormat f = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.ENGLISH);
            f.setTimeZone(TimeZone.getTimeZone(SERVER_TIMEZONE));
            return f.parse(serverDate).getTime();
        } catch (ParseException e) {
            Log.e("ServerDate", "error parsing date string", e);
            return TIME_NOT_PARSED;
        }
    }

    private long getCurrentTimeInMillis() {
        return new Date().getTime();
    }
}
