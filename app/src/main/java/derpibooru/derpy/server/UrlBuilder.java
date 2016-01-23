package derpibooru.derpy.server;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruImageListType;

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

    public static URL generateSearchUrl(Map<String, String> params, String query) {
        String request = DERPIBOORU_DOMAIN;
        try {
            request += "search.json?utf8=✓";
            request += "&sbq=" + URLEncoder.encode(query, "UTF-8");

            for (Map.Entry<String, String> entry : params.entrySet()) {
                request += String.format("&%s=%s", entry.getKey(), entry.getValue());
            }

            return new URL(request);
        } catch (Exception e) {
            Log.e("UrlBuilder", "generateSearchUrl", e);
            return null;
        }
    }
}
