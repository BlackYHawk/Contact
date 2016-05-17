package com.hawk.contact.display;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.hawk.contact.Display;
import com.hawk.contact.R;
import com.hawk.contact.fragment.ContactTabFragment;
import com.hawk.contact.model.ColorScheme;

/**
 * Created by heyong on 16/3/11.
 */
public class AndroidDisplay implements Display {

    private AppCompatActivity mActivity;

    private static final TypedValue sTypedValue = new TypedValue();
    private ColorScheme mColorScheme;
    private int mColorPrimaryDark;

    private Toolbar mToolbar;
    private boolean mCanChangeToolbarBackground;

    public AndroidDisplay(AppCompatActivity mActivity) {
        this.mActivity = Preconditions.checkNotNull(mActivity, "Activity cannot be null");

        mActivity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, sTypedValue, true);
        mColorPrimaryDark = sTypedValue.data;
    }

    @Override
    public boolean hasMainFragment() {
        return mActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_main) != null;
    }

    @Override
    public void showMainFragment() {
        showFragment(new ContactTabFragment());
    }

    @Override
    public void startAddAccountActivity() {

    }

    private void showFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void setColorScheme(ColorScheme colorScheme) {
        if (Objects.equal(mColorScheme, colorScheme)) {
            // ColorScheme hasn't changed, ignore
            return;
        }

        mColorScheme = colorScheme;

        setToolbarBackground(mColorScheme.primaryColor);
        mColorPrimaryDark = mColorScheme.secondaryColor;
    }

    @Override
    public void setActionBarTitle(CharSequence title) {
        final ActionBar actionBar = mActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setActionBarSubtitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(title);
        }
    }

    @Override
    public void showUpNavigation(boolean show) {
        final ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setHomeAsUpIndicator(show ? R.drawable.ic_back : R.drawable.ic_menu);
        }
    }

    @Override
    public void setSupportActionBar(Object toolbar, boolean handleBackground) {
        mToolbar = (Toolbar) toolbar;
        mCanChangeToolbarBackground = handleBackground;

        if (mColorScheme != null) {
            setToolbarBackground(mColorScheme.primaryColor);
        }

        if (mToolbar != null) {
            final ActionBar ab = mActivity.getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeButtonEnabled(true);
            }
        }
    }

    private void setToolbarBackground(int color) {
        if (mCanChangeToolbarBackground && mToolbar != null) {
            mToolbar.setBackgroundColor(color);
        }
    }

}
