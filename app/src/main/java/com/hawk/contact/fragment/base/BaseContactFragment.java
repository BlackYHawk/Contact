package com.hawk.contact.fragment.base;

import com.hawk.contact.base.ContactApplication;
import com.hawk.contact.controller.UserController;

/**
 * Created by heyong on 16/3/14.
 */
public abstract class BaseContactFragment extends BaseFragment implements UserController.UserUi {

    private UserController.UserUiCallbacks mUserUiCallbacks;

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

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showError(UserController.Error error) {

    }

    @Override
    public boolean isModal() {
        return false;
    }


    protected final boolean hasCallback() {
        return mUserUiCallbacks != null;
    }

    protected final UserController.UserUiCallbacks getUserCallback() {
        return mUserUiCallbacks;
    }

    @Override
    public void setCallback(UserController.UserUiCallbacks userUiCallbacks) {
        this.mUserUiCallbacks = userUiCallbacks;
    }

    private UserController getController() {
        return ContactApplication.from(getActivity()).getMainController().getUserController();
    }
}
