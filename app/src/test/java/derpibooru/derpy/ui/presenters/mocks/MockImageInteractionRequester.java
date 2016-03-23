package derpibooru.derpy.ui.presenters.mocks;

import android.content.Context;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.ImageInteractionRequester;

public class MockImageInteractionRequester extends ImageInteractionRequester {
    private DerpibooruImageInteraction mReturn;
    private Runnable mDoAfter;
    private Runnable mDoBefore;

    public DerpibooruImageInteraction.InteractionType requestedType;

    public MockImageInteractionRequester(Context context, QueryHandler<DerpibooruImageInteraction> handler) {
        super(context, handler);
    }

    @Override
    public ImageInteractionRequester interaction(DerpibooruImageInteraction.InteractionType type) {
        requestedType = type;
        return this;
    }

    public MockImageInteractionRequester returnNext(DerpibooruImageInteraction interaction) {
        mReturn = interaction;
        return this;
    }

    public MockImageInteractionRequester beforeReturning(Runnable action) {
        mDoBefore = action;
        return this;
    }

    public MockImageInteractionRequester afterReturning(Runnable action) {
        mDoAfter = action;
        return this;
    }

    @Override
    public void fetch() {
        if (mDoBefore != null) {
            mDoBefore.run();
        }
        mHandler.onQueryExecuted(mReturn);
        if (mDoAfter != null) {
            mDoAfter.run();
        }
    }
}
