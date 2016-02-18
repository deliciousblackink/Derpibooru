package derpibooru.derpy.server;

import android.content.Context;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AsynchronousFormRequest extends AsynchronousRequest {
    private Map<String, String> mForm;
    private String mHttpMethod;

    public AsynchronousFormRequest(Context context, String url, Map<String, String> form, int successResponseCode,
                                   String httpMethod) {
        super(context, null, url, successResponseCode);
        mForm = form;
        mHttpMethod = httpMethod;
    }

    @Override
    protected Request generateRequest() {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> formItem : mForm.entrySet()) {
            formBody.add(formItem.getKey(), formItem.getValue());
        }
        return new Request.Builder().url(mUrl).method(mHttpMethod, formBody.build()).build();
    }
}
