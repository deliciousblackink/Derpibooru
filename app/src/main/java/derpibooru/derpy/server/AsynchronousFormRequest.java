package derpibooru.derpy.server;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Map;

import derpibooru.derpy.server.parsers.ServerResponseParser;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AsynchronousFormRequest<T> extends AsynchronousRequest<T> {
    private Map<String, String> mForm;
    private String mHttpMethod;

    public AsynchronousFormRequest(Context context, @Nullable ServerResponseParser<T> parser,
                                   String url, Map<String, String> form,
                                   int successResponseCode, String httpMethod) {
        super(context, parser, url, successResponseCode);
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
