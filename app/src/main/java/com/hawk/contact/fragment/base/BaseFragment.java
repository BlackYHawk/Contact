package com.hawk.contact.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hawk.contact.BaseActivity;
import com.hawk.contact.Display;
import com.hawk.contact.R;

/**
 * Created by heyong on 16/3/14.
 */
public abstract class BaseFragment extends Fragment {

    private Toolbar mToolbar;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    protected void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar, true);
    }

    protected final void setSupportActionBar(Toolbar toolbar, boolean handleBackground) {
        ((BaseActivity)getActivity()).setSupportActionBar(toolbar, handleBackground);
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }

    protected Display getDisplay() {
        return ((BaseActivity)getActivity()).getDisplay();
    }
}
