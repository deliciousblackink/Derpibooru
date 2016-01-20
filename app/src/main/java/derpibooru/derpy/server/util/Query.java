package derpibooru.derpy.server.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.HttpsURLConnection;

import java.net.URL;

import derpibooru.derpy.server.util.parsers.ServerResponseParser;

/**
 * Provides network connectivity and server response parsing
 * running on a background thread.
 * Should not be used outside of .server package.
 */
public class Query {
    private static final int INPUT_STREAM_READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;

    private QueryResultHandler mQueryResultHandler;
    private Context mContext;

    public Query(Context context, QueryResultHandler listener) {
        mContext = context;
        mQueryResultHandler = listener;
    }

    public void executeQuery(URL query, ServerResponseParser parser) {
        if (query == null) {
            mQueryResultHandler.onQueryFailed();
        } else {
            if (isConnected()) {
                new RequestTask().execute(new RequestTaskParameters(query, parser));
            } else {
                mQueryResultHandler.onQueryFailed();
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class RequestTask extends AsyncTask<RequestTaskParameters, Void, Object> {
        @Override
        protected Object doInBackground(RequestTaskParameters... params) {
            try {
                String response = getResponse(params[0].getTargetUrl());
                return params[0].getParser().parseResponse(response);
            } catch (Exception e) {
                Log.e("Derpy Query", "error: ", e);
                mQueryResultHandler.onQueryFailed();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                mQueryResultHandler.onQueryExecuted(result);
            }
        }
    }

    private class RequestTaskParameters {
        private URL mTarget;
        private ServerResponseParser mParser;

        public RequestTaskParameters(URL target,
                                     ServerResponseParser parser) {
            mTarget = target;
            mParser = parser;
        }

        public URL getTargetUrl() {
            return mTarget;
        }

        public ServerResponseParser getParser() {
            return mParser;
        }
    }

    private String getResponse(URL url) throws IOException {
        InputStream is = null;
        try {
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            c.setReadTimeout(INPUT_STREAM_READ_TIMEOUT);
            c.setConnectTimeout(CONNECTION_TIMEOUT);
            c.setRequestMethod("GET");
            c.setDoInput(true);

            c.connect();
            is = c.getInputStream();
            return readInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String readInputStream(java.io.InputStream is) {
        /* http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string */
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
