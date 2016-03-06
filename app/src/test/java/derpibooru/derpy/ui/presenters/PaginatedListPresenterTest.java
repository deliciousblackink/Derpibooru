package derpibooru.derpy.ui.presenters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.ui.adapters.RecyclerViewPaginationAdapter;
import derpibooru.derpy.ui.test_utils.MockIntegerParcelablePaginationAdapter;
import derpibooru.derpy.ui.test_utils.MockIntegerParcelable;
import derpibooru.derpy.ui.test_utils.MockIntegerParcelablePaginationProvider;

@Config(sdk = 19, manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class PaginatedListPresenterTest {
    List<MockIntegerParcelable> dummyInitialItems;

    MockIntegerParcelablePaginationProvider mockProvider;
    MockIntegerParcelablePaginationAdapter mockAdapter;

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    Context context;

    PaginatedListPresenter<MockIntegerParcelable> presenter;

    @Before
    public void setUp() {
        context =  RuntimeEnvironment.application.getApplicationContext();

        refreshLayout = new SwipeRefreshLayout(context);
        recyclerView = new RecyclerView(context);

        dummyInitialItems = new ArrayList<>(10);
        dummyInitialItems.addAll(getListOfTenMockItems(0));

        mockAdapter = new MockIntegerParcelablePaginationAdapter(context, dummyInitialItems);
        presenter = new PaginatedListPresenter<MockIntegerParcelable>(refreshLayout, recyclerView) {
            @Override
            public RecyclerViewPaginationAdapter<MockIntegerParcelable, ?> getNewInstanceOfListAdapter(
                    List<MockIntegerParcelable> initialItems) {
                return mockAdapter;
            }

            @Override
            public void onEndlessScrollBottomReached() {
                super.onEndlessScrollBottomReached();
            }
        };

        mockProvider = new MockIntegerParcelablePaginationProvider(context, presenter.new PaginatedListProviderHandler());
        mockProvider.returnNext(Collections.<MockIntegerParcelable>emptyList());
        presenter.initializeWithProvider(mockProvider);
    }

    @Test
    public void testInitialItems() {
        assertAdapterItemsAreEqualTo(dummyInitialItems);
    }

    @Test
    public void testNextPage() {
        List<MockIntegerParcelable> nextPageItems =
                getListOfTenMockItems(dummyInitialItems.size() - 1);

        final List<MockIntegerParcelable> expectedItems = new ArrayList<>();
        expectedItems.addAll(dummyInitialItems);
        expectedItems.addAll(nextPageItems);

        ((MockIntegerParcelablePaginationProvider) presenter.getListProvider())
                .returnNext(nextPageItems)
                .afterReturning(new Runnable() {
                    @Override
                    public void run() {
                        assertAdapterItemsAreEqualTo(expectedItems);
                    }
                });
        /* emulate scrolling to the bottom */
        presenter.onEndlessScrollBottomReached();
    }

    @Test
    public void testRefresh() {
        final List<MockIntegerParcelable> newItems =
                getListOfTenMockItems(dummyInitialItems.size() - 1);

        ((MockIntegerParcelablePaginationProvider) presenter.getListProvider())
                .returnNext(newItems)
                .beforeReturning(new Runnable() {
                    @Override
                    public void run() {
                        assertThat(refreshLayout.isRefreshing(), is(true));
                    }
                })
                .afterReturning(new Runnable() {
                    @Override
                    public void run() {
                        assertAdapterItemsAreEqualTo(newItems);
                        assertThat(refreshLayout.isRefreshing(), is(false));
                    }
                });

        assertThat(refreshLayout.isRefreshing(), is(false));
        presenter.refreshList();
    }

    @Test
    public void testStateRecovery() {
        saveAndRestorePresenterState();
        assertAdapterItemsAreEqualTo(dummyInitialItems);
        assertThat(presenter.getListProvider().getCurrentPage(), is(1));
        /* navigate to third page to test current page recovery */
        List<MockIntegerParcelable> expectedItems = generatePagesTo(3);
        saveAndRestorePresenterState();
        assertAdapterItemsAreEqualTo(expectedItems);
        assertThat(presenter.getListProvider().getCurrentPage(), is(3));
    }

    private List<MockIntegerParcelable> generatePagesTo(int targetPage) {
        List<MockIntegerParcelable> expectedItems = new ArrayList<>();
        expectedItems.addAll(dummyInitialItems);
        for (int i = 1; i < targetPage; i++) {
            List<MockIntegerParcelable> nextPageItems =
                    getListOfTenMockItems(dummyInitialItems.size() - 1);
            expectedItems.addAll(nextPageItems);
            ((MockIntegerParcelablePaginationProvider) presenter.getListProvider())
                    .returnNext(nextPageItems);
            presenter.onEndlessScrollBottomReached();
        }
        return expectedItems;
    }

    private void saveAndRestorePresenterState() {
        recyclerView.setLayoutManager(getMockLayoutManager());
        Bundle savedInstanceState = new Bundle();
        presenter.onSaveInstanceState(savedInstanceState);
        presenter.initializeWithProvider(presenter.getListProvider(), savedInstanceState);
    }

    private LinearLayoutManager getMockLayoutManager() {
        final int expectedItemPosition = 20;
        LinearLayoutManager mockLayoutManager = mock(LinearLayoutManager.class);
        when(mockLayoutManager.findFirstVisibleItemPosition()).thenReturn(expectedItemPosition);
        /* assert that the presenter restores RecyclerView item position */
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                assertThat((int) invocation.getArguments()[0], is(expectedItemPosition));
                return null;
            }
        }).when(mockLayoutManager).scrollToPosition(anyInt());
        return mockLayoutManager;
    }

    private void assertAdapterItemsAreEqualTo(List<MockIntegerParcelable> items) {
        assertThat(mockAdapter.getItemCount(), is(items.size()));
        for (int i = 0; i < mockAdapter.getItemCount(); i++) {
            assertThat(mockAdapter.getItems().get(i), samePropertyValuesAs(items.get(i)));
        }
    }

    private List<MockIntegerParcelable> getListOfTenMockItems(int startWithIndex) {
        List<MockIntegerParcelable> out = new ArrayList<>(10);
        for (int i = startWithIndex; i < (startWithIndex + 10); i++) {
            out.add(new MockIntegerParcelable(i + 1));
        }
        return out;
    }
}
