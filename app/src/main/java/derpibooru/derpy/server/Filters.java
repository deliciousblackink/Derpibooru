package derpibooru.derpy.server;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.parsers.FilterListParser;
import derpibooru.derpy.server.parsers.ServerResponseParser;
import derpibooru.derpy.storage.UserDataStorage;

public class Filters {
    private UserDataStorage mStorage;
    private FiltersRequestHandler mHandler;
    private Context mContext;

    public Filters(Context context, FiltersRequestHandler handler) {
        mContext = context;
        mHandler = handler;
        mStorage = new UserDataStorage(context);
    }

    public DerpibooruFilter getCurrentFilter() {
        /* sending an asynchronous request to the server would be an overkill there */
        return mStorage.getUserData().getCurrentFilter();
    }

    public void fetchAvailableFilters() {
        FilterListProvider flp = new FilterListProvider(mContext, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                mHandler.onAvailableFiltersFetched((ArrayList<DerpibooruFilter>) result);
            }

            @Override
            public void onRequestFailed() { }
        });
        flp.fetch();
    }

    public void changeCurrentFilterTo(final DerpibooruFilter newFilter) {
        AuthenticityToken authToken =
                new AuthenticityToken(mContext, new ProviderRequestHandler() {
                    @Override
                    public void onRequestCompleted(Object result) {
                        requestFilterChange((String) result, newFilter);
                    }

                    @Override
                    public void onRequestFailed() {

                    }
                }, AuthenticityToken.TokenAction.ChangeFilter);
        authToken.fetch();
    }

    private void requestFilterChange(String token, final DerpibooruFilter newFilter) {
        HashMap<String, String> form = new HashMap<>();
        form.put("_method", "patch");
        form.put("authenticity_token", token);
        FilterChangeProvider provider = new FilterChangeProvider(mContext, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                mStorage.setCurrentFilter(newFilter);
                onFilterChangeResponseReceived((boolean) result);
            }

            @Override
            public void onRequestFailed() {
                mHandler.onNetworkError();
            }
        }, form, newFilter.getId());
        provider.fetch();
    }

    private void onFilterChangeResponseReceived(boolean successful) {
        if (successful) {
            mHandler.onFilterChangedSuccessfully();
        } else {
            /* in case of changing filters, a non-HTTP302 response is
             * a network error, since there's no user input involved. */
            mHandler.onNetworkError();
        }
    }

    public interface FiltersRequestHandler {
        void onAvailableFiltersFetched(ArrayList<DerpibooruFilter> filters);

        void onFilterChangedSuccessfully();

        void onNetworkError();
    }

    private class FilterListProvider extends Provider {
        public FilterListProvider(Context context, ProviderRequestHandler handler) {
            super(context, handler);
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN);
            sb.append("filters.json");
            return sb.toString();
        }

        @Override
        public void fetch() {
            super.executeQuery(generateUrl(), new FilterListParser());
        }
    }

    private class FilterChangeProvider extends Provider {
        private HashMap<String, String> mForm;
        private int mFilterId;

        public FilterChangeProvider(Context context,
                                    ProviderRequestHandler handler,
                                    HashMap<String, String> form,
                                    int filterId) {
            super(context, handler);
            mForm = form;
            mFilterId = filterId;
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN).append("filters/select");
            sb.append("?id=").append(mFilterId);
            return sb.toString();
        }

        @Override
        public void fetch() {
            executeQuery(generateUrl(), null);
        }

        @Override
        protected void executeQuery(String url, ServerResponseParser parser) {
            Handler threadHandler = new Handler();
            AsynchronousPostRequest requestThread =
                    new AsynchronousPostRequest(mContext, url, mForm,
                                                new AsynchronousRequest.RequestHandler() {
                                                    Handler uiThread = new Handler(Looper.getMainLooper());

                                                    @Override
                                                    public void onRequestCompleted(Object parsedResponse) {
                                                        uiThread.post(new UiThreadMessageSender(parsedResponse, false));
                                                    }

                                                    @Override
                                                    public void onRequestFailed() {
                                                        uiThread.post(new UiThreadMessageSender(null, true));
                                                    }
                                                });
            threadHandler.post(requestThread);
        }
    }
}
