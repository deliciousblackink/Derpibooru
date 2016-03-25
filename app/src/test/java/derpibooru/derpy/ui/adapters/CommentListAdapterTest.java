package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.data.internal.CommentReplyItem;
import derpibooru.derpy.data.server.DerpibooruComment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@Config(sdk = 19, manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class CommentListAdapterTest {
    Context context;

    CommentListAdapter adapter;
    RecyclerView recyclerView;

    List<DerpibooruComment> dummyInitialItems;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        fillInitialItems();
    }

    @Test
    public void testInitialization() {
        adapter = new CommentListAdapter(context, Collections.<DerpibooruComment>emptyList(), null) {
            @Override
            protected void fetchCommentReply(CommentReplyItem replyItem) { }

            @Override
            protected void scrollToPosition(int adapterPosition) { }

            @Override
            protected void onNewCommentsAdded(int commentsAdded) {
                fail("when the adapter is initialized and the items are first added, onNewCommentsAdded(int) is not expected to be called");
            }
        };
        bindAdapterToRecyclerView();
    }

    @Test
    public void testAddingNewComments() {
        int newItems = 2;
        testItemAddition(newItems);
    }

    @Test
    public void testAddingMoreNewCommentsThanIsCurrentlyDisplayed() {
        int newItems = dummyInitialItems.size() + 1;
        testItemAddition(newItems);
    }

    private void testItemAddition(final int itemsToAdd) {
        final MethodCallTest callTest = new MethodCallTest();

        adapter = new CommentListAdapter(context, Collections.<DerpibooruComment>emptyList(), null) {
            @Override
            protected void fetchCommentReply(CommentReplyItem replyItem) { }

            @Override
            protected void scrollToPosition(int adapterPosition) { }

            @Override
            protected void onNewCommentsAdded(int commentsAdded) {
                assertThat(commentsAdded, is(itemsToAdd));
                callTest.called();
            }
        };
        bindAdapterToRecyclerView();

        List<DerpibooruComment> newItems = new ArrayList<>(dummyInitialItems);
        newItems.addAll(0, getDummyItems(itemsToAdd, (dummyInitialItems.size())));
        adapter.resetItems(newItems);

        if (!callTest.wasCalled) {
            fail("onNewCommentsAdded(int) was not called");
        }
    }

    private void fillInitialItems() {
        dummyInitialItems = new ArrayList<>(10);
        dummyInitialItems.addAll(getDummyItems(10, 0));
    }

    private List<DerpibooruComment> getDummyItems(int items, int startWithIndex) {
        List<DerpibooruComment> out = new ArrayList<>(items);
        for (int i = startWithIndex; i < (startWithIndex + items); i++) {
            out.add(new DerpibooruComment(i, "", "", "", ""));
        }
        return out;
    }

    /*private void initializeRecyclerViewWithAdapter(@Nullable final ArgRunnable fetchCommentReply,
                                                   @Nullable final ArgRunnable scrollToPosition,
                                                   @Nullable final ArgRunnable onNewCommentsAdded) {

        bindAdapterToRecyclerView();
    }*/

    private void bindAdapterToRecyclerView() {
        adapter.resetItems(dummyInitialItems);
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private class MethodCallTest {
        boolean wasCalled;

        public void called() {
            wasCalled = true;
        }
    }
}