package derpibooru.derpy.server.requesters;

import android.content.Context;

import derpibooru.derpy.server.QueryHandler;

public abstract class AuthenticatedApiRequester extends Requester {
    public AuthenticatedApiRequester(Context context, QueryHandler handler) {
        super(context, handler);
    }


}
