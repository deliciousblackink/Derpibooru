package derpibooru.derpy.ui.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.EnumSet;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.server.requesters.ImageInteractionRequester;
import derpibooru.derpy.ui.presenters.mocks.MockImageInteractionRequester;
import derpibooru.derpy.ui.views.AccentColorIconButton;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Config(sdk = 19, manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ImageInteractionPresenterTest {
    Context context;

    AccentColorIconButton scoreButton;
    AccentColorIconButton faveButton;
    AccentColorIconButton upvoteButton;
    AccentColorIconButton downvoteButton;

    ImageInteractionPresenter presenter;

    MockImageInteractionRequester requesterMock;
    EnumSet<DerpibooruImageInteraction.InteractionType> interactions;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        scoreButton = new AccentColorIconButton(context);
        faveButton = new AccentColorIconButton(context);
        upvoteButton = new AccentColorIconButton(context);
        downvoteButton = new AccentColorIconButton(context);
        interactions = EnumSet.noneOf(DerpibooruImageInteraction.InteractionType.class);
        presenter = new ImageInteractionPresenter(0, scoreButton, faveButton, upvoteButton, downvoteButton) {
            @Override
            protected ImageInteractionRequester getNewInstanceOfRequester(Context context, ImageInteractionPresenter.InteractionRequestHandler handler) {
                requesterMock = new MockImageInteractionRequester(context, handler);
                return requesterMock;
            }

            @Override
            protected void onInteractionFailed() {

            }

            @NonNull
            @Override
            protected EnumSet<DerpibooruImageInteraction.InteractionType> getInteractionsSet() {
                return interactions;
            }
        };
    }

    @Test
    public void testInitialState() {
        assertThat(scoreButton.isEnabled(), is(false));
        assertThat(faveButton.isEnabled(), is(false));
        assertThat(upvoteButton.isEnabled(), is(false));
        assertThat(downvoteButton.isEnabled(), is(false));
        presenter.enableInteractions(context);
        assertThat(scoreButton.isEnabled(), is(false));
        assertThat(faveButton.isEnabled(), is(true));
        assertThat(upvoteButton.isEnabled(), is(true));
        assertThat(downvoteButton.isEnabled(), is(true));
    }

    @Test
    public void testInteractions() {
        presenter.enableInteractions(context);
        testInteraction(DerpibooruImageInteraction.InteractionType.Fave, new Runnable() {
            @Override
            public void run() {
                assertThat(faveButton.isActive(), is(true));
                assertThat(upvoteButton.isActive(), is(true));
                assertThat(downvoteButton.isActive(), is(false));
            }
        });
        faveButton.performClick();
        testInteraction(DerpibooruImageInteraction.InteractionType.ClearFave, new Runnable() {
            @Override
            public void run() {
                assertThat(faveButton.isActive(), is(false));
                assertThat(upvoteButton.isActive(), is(true));
                assertThat(downvoteButton.isActive(), is(false));
            }
        });
        faveButton.performClick();
        testInteraction(DerpibooruImageInteraction.InteractionType.Downvote, new Runnable() {
            @Override
            public void run() {
                assertThat(faveButton.isActive(), is(false));
                assertThat(upvoteButton.isActive(), is(false));
                assertThat(downvoteButton.isActive(), is(true));
            }
        });
        downvoteButton.performClick();
        testInteraction(DerpibooruImageInteraction.InteractionType.Upvote, new Runnable() {
            @Override
            public void run() {
                assertThat(faveButton.isActive(), is(false));
                assertThat(upvoteButton.isActive(), is(true));
                assertThat(downvoteButton.isActive(), is(false));
            }
        });
        upvoteButton.performClick();
        testInteraction(DerpibooruImageInteraction.InteractionType.ClearVote, new Runnable() {
            @Override
            public void run() {
                assertThat(faveButton.isActive(), is(false));
                assertThat(upvoteButton.isActive(), is(false));
                assertThat(downvoteButton.isActive(), is(false));
            }
        });
        upvoteButton.performClick();
    }

    private void testInteraction(final DerpibooruImageInteraction.InteractionType type, final Runnable assertAfter) {
        requesterMock.returnNext(new DerpibooruImageInteraction(5, 3, 1, type));
        requesterMock.beforeReturning(new Runnable() {
            @Override
            public void run() {
                assertThat(requesterMock.requestedType, is(type));
            }
        });
        requesterMock.afterReturning(new Runnable() {
            @Override
            public void run() {
                /* wait for the ui to refresh */
                scoreButton.post(new Runnable() {
                    @Override
                    public void run() {
                        assertThat(Integer.parseInt(scoreButton.getText().toString()), is(2));
                        assertThat(Integer.parseInt(faveButton.getText().toString()), is(5));
                        assertThat(Integer.parseInt(upvoteButton.getText().toString()), is(3));
                        assertThat(Integer.parseInt(downvoteButton.getText().toString()), is(1));
                        assertAfter.run();
                    }
                });
            }
        });
    }
}
