package derpibooru.derpy.server;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsynchronousPostRequest extends AsynchronousRequest {
    private  HashMap<String, String> mPostFormData;

    public AsynchronousPostRequest(Context context,
                               ServerResponseParser parser,
                               String url, HashMap<String, String> postFormItems,
                               RequestHandler requestHandler) {
        super(context, parser, url, requestHandler);
        mPostFormData = postFormItems;
    }

    @Override
    protected Request generateRequest() {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> formItem : mPostFormData.entrySet()) {
            formBody.add(formItem.getKey(), formItem.getValue());
        }


        return new Request.Builder()
                .url(mUrl)
                .addHeader("cookie", mCookieStorage.getCookie())
                .method("POST", formBody.build())
                .build();
    }

    protected Object parseResponse(Response response) {
        /* HTTP 302 indicates successful authentication */
        return (response.code() == 302);
    }
}