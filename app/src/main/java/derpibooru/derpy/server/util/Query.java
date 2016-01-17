package derpibooru.derpy.server.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public abstract class Query {
    protected QueryHandler mQueryHandler;
    private Context mContext;

    public Query(Context context, QueryHandler listener) {
        mContext = context;
        mQueryHandler = listener;
    }

    protected abstract void processResponse(String response);

    protected void executeQuery(URL from) {
        if (isConnected()) {
            new RequestTask().execute(from);
        } else {
            mQueryHandler.queryFailed();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class RequestTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... url) {
            try {
                return getResponse(url[0]);
            } catch (IOException e) {
                mQueryHandler.queryFailed();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            processResponse(response);
        }
    }

    private String getResponse(URL url) throws IOException {
        InputStream is = null;

        try {
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            c.setReadTimeout(10000);
            c.setConnectTimeout(15000);
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
