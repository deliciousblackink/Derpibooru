package derpibooru.derpy.server;

import android.content.Context;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

class AsynchronousFormRequest extends AsynchronousRequest {
    private Map<String, String> mForm;

    public AsynchronousFormRequest(Context context, String url, Map<String, String> form,
                                   RequestHandler requestHandler) {
        super(context, null, url, requestHandler);
        mForm = form;
    }

    @Override
    protected Request generateRequest() {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> formItem : mForm.entrySet()) {
            formBody.add(formItem.getKey(), formItem.getValue());
        }
        return new Request.Builder().url(mUrl).method("POST", formBody.build()).build();
    }

    protected Object parseResponse(Response response) {
        /* HTTP 302 indicates successful authentication */
        return (response.code() == 302); /* TODO: 200 in vote */
    }
}
