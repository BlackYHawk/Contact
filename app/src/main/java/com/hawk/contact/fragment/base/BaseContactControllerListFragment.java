package com.hawk.contact.fragment.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.hawk.contact.Constants;
import com.hawk.contact.R;
import com.hawk.contact.base.ContactApplication;
import com.hawk.contact.controller.UserController;

/**
 * Created by heyong on 16/4/23.
 */
public abstract class BaseContactControllerListFragment<E extends AbsListView, T> extends
        ListFragment<E> implements UserController.BaseUserListUi<T>, AbsListView.OnScrollListener{
    private static final String LOG_TAG = BaseContactControllerListFragment.class.getSimpleName();
    private UserController.UserUiCallbacks mCallbacks;

    private int mFirstVisiblePosition;

    private int mLoadMoreRequestedItemCount;
    private boolean mLoadMoreIsAtBottom;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().attachUi(this);
    }

    @Override
    public void onPause() {
        getController().detachUi(this);
        super.onPause();
    }

    private void saveListViewPosition() {
        E listView = getListView();

        mFirstVisiblePosition = listView.getFirstVisiblePosition();

        if(mFirstVisiblePosition != AdapterView.INVALID_POSITION && listView.getChildCount() > 0) {
            mFirstVisiblePosition = listView.getChildAt(0).getTop();
        }
    }

    protected void moveListViewToSavedPositions() {
        final E listView = getListView();

        if(mFirstVisiblePosition != AdapterView.INVALID_POSITION && listView.getChildCount() <= 0) {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(mFirstVisiblePosition);
                }
            });
        }
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        if(visible) {
            setListShown(false);
        }
        else {
            setListShown(true);
        }
    }

    @Override
    public void showError(UserController.Error error) {
        setListShown(true);

        switch (error) {
            case BAD_AUTH:
                setEmptyText(getString(R.string.bad_account_auth, getTitle()));
                break;
            case BAD_CREATE:
                setEmptyText(getString(R.string.bad_account_create, getTitle()));
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mLoadMoreIsAtBottom = (totalItemCount > mLoadMoreRequestedItemCount) &&
                (firstVisibleItem + visibleItemCount == totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
            if(onScrolledToBottom()) {
                mLoadMoreRequestedItemCount = view.getCount();
                mLoadMoreIsAtBottom = false;
            }
        }
    }

    protected boolean onScrolledToBottom() {
        if(Constants.DEBUG) {
            Log.d(LOG_TAG, "onScrolledToBottom");
        }
        if(hasCallbacks()) {
            getCallbacks().onScrolledToBottom();
            return true;
        }
        return false;
    }

    protected final boolean hasCallbacks() {
        return mCallbacks != null;
    }

    protected final UserController.UserUiCallbacks getCallbacks() {
        return mCallbacks;
    }

    @Override
    public void setCallback(UserController.UserUiCallbacks userUiCallbacks) {
        mCallbacks = userUiCallbacks;
    }

    private String getTitle() {
        if (hasCallbacks()) {
            return getCallbacks().getUiTitle();
        }
        return null;
    }

    private UserController getController() {
        return ContactApplication.from(getActivity()).getMainController().getUserController();
    }
}
