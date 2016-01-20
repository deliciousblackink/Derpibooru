package derpibooru.derpy.server.util;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import derpibooru.derpy.data.server.DerpibooruImageListType;
import derpibooru.derpy.server.ImageListProvider;

public class UrlBuilder {
    private static final String DERPIBOORU_DOMAIN = "https://trixiebooru.org/";

    public static URL generateListUrl(DerpibooruImageListType type, String time) {
        String request = DERPIBOORU_DOMAIN;
        switch (type) {
            case TopScoring:
                request += "lists/top_scoring.json";
                break;
            case MostCommented:
                request += "lists/top_commented.json";
                break;
        }
        request += "?last=" + time;
        try {
            return new URL(request);
        } catch (MalformedURLException e) {
            Log.e("UrlBuilder", "generateListUrl", e);
            return null;
        }
    }

    public static URL generateImageUrl(int imageId) {
        String request = String.format("%simages/%d", DERPIBOORU_DOMAIN, imageId);
        try {
            return new URL(request);
        } catch (MalformedURLException e) {
            Log.e("UrlBuilder", "generateImageUrl", e);
            return null;
        }
    }
}
