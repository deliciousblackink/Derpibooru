package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public abstract class FragmentAdapter {
    private FragmentManager mFragmentManager;
    private int mFragmentLayoutId;

    public FragmentAdapter(FragmentManager fragmentManager, int fragmentLayoutId) {
        mFragmentManager = fragmentManager;
        mFragmentLayoutId = fragmentLayoutId;
    }

    protected abstract void setFragmentCallbackHandlers(@Nullable Fragment target);

    /**
     * Displays a fragment from the back stack and restores its callback handlers.
     *
     * @return {@code true} if there was a fragment in the back stack, {@code false} otherwise.
     */
    public boolean restoreFragmentFromBackStack() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            for (Fragment fragment : mFragmentManager.getFragments()) {
                setFragmentCallbackHandlers(fragment);
            }
        }
        return mFragmentManager.popBackStackImmediate();
    }

    /**
     * Returns the currently displayed fragment.
     *
     * @return {@code null} if there is no framgent being displayed.
     */
    @Nullable
    public Fragment getCurrentFragment() {
        return mFragmentManager.findFragmentById(mFragmentLayoutId);
    }

    /**
     * Replaces the currently displayed fragment with a new one.
     *
     * @param fragment new fragment to be displayed
     */
    protected void display(Fragment fragment) {
        commitSafely(mFragmentManager
                             .beginTransaction()
                             .replace(mFragmentLayoutId, fragment));
    }

    /**
     * Replaces the currently displayed fragment with a new one, adding the transaction to the back stack.
     *
     * @param fragment new fragment to be displayed
     * @param backStackTag an optional tag for the transaction in the back stack
     */
    protected void display(Fragment fragment, @Nullable String backStackTag) {
        commitSafely(mFragmentManager
                             .beginTransaction()
                             .addToBackStack(backStackTag)
                             .replace(mFragmentLayoutId, fragment));
    }

    /**
     * Replaces the currently displayed fragment with a new one, adding the transaction to the back stack.
     * Binds corresponding animation resources to the fragment transcation.
     *
     * @param fragment new fragment to be displayed
     * @param backStackTag an optional tag for the transaction in the back stack
     */
    protected void display(Fragment fragment, @Nullable String backStackTag,
                           @AnimRes int enterAnim, @AnimRes int exitAnim,
                           @AnimRes int popEnterAnim, @AnimRes int popExitAnim) {
        commitSafely(mFragmentManager
                             .beginTransaction()
                             .addToBackStack(backStackTag)
                             .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
                             .replace(mFragmentLayoutId, fragment));
    }

    /**
     * Somewhat dirty workaround for the FragmentTransaction
     * <a href="http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html">state loss</a>,
     * which can take place due to {@link derpibooru.derpy.server.providers.Provider} callback occuring after
     * the activity's view is destroyed.
     * <br>
     * Unlike {@link FragmentTransaction#commitAllowingStateLoss()}, the method does <strong>not</strong> commit the
     * transaction after onSaveInstanceState. Moreover, it logs the exception should it be required for debugging.
     */
    private int commitSafely(FragmentTransaction transaction) {
        try {
            return transaction.commit();
        } catch (IllegalStateException e) {
            Log.e("FragmentAdapter", "commitSafely: failed to commit transaction", e);
            return -1;
        }
    }
}
