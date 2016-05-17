package com.hawk.contact.controller;

import com.google.common.base.Preconditions;
import com.hawk.contact.Constants;
import com.hawk.contact.Display;
import com.hawk.contact.state.BaseState;
import com.hawk.contact.util.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

/**
 * Created by heyong on 16/3/8.
 */
public abstract class BaseUIController<U extends BaseUIController.Ui<UC>, UC> extends BaseController {

    public interface Ui<UC> {
        void setCallback(UC uc);

        boolean isModal();
    }

    public interface SubUi {

    }

    @Inject Logger mLogger;
    private final Set<U> mUis;
    private final Set<U> mUnModifiableUis;

    public BaseUIController() {
        mUis = new CopyOnWriteArraySet<>();
        mUnModifiableUis = Collections.unmodifiableSet(mUis);
    }

    public synchronized final void attachUi(U ui) {
        Preconditions.checkArgument(ui != null, "UI can not be null");
        Preconditions.checkState(!mUis.contains(ui), "UI is already attached");

        mUis.add(ui);
        ui.setCallback(createUICallback(ui));

        if(isInited()) {
            if(!ui.isModal() && !(ui instanceof SubUi)) {
                updateDisplayTitle(getUiTitle(ui));
            }
            onUiAttached(ui);
            populateUi(ui);
        }
    }

    public synchronized final void detachUi(U ui) {
        Preconditions.checkArgument(ui != null, "UI can not be null");
        Preconditions.checkState(mUis.contains(ui), "UI is not attached");

        onUiDetached(ui);
        ui.setCallback(null);
        mUis.remove(ui);
    }

    protected abstract UC createUICallback(U ui);

    protected void onInited() {
        if(!mUis.isEmpty()) {
            for(U ui : mUis) {
                onUiAttached(ui);
                populateUi(ui);
            }
        }
    }

    protected final Set<U> getUis() {
        return mUnModifiableUis;
    }

    protected String getUiTitle(U ui) {
        return null;
    }

    protected final void updateDisplayTitle(U ui) {
        updateDisplayTitle(getUiTitle(ui));
    }

    protected final void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if(display != null) {
            display.setActionBarTitle(title);
        }
    }

    protected void onUiAttached(U ui) {

    }

    protected void onUiDetached(U ui) {

    }

    protected synchronized final void populateUis() {
        if(Constants.DEBUG) {
            mLogger.d(getClass().getSimpleName(), "populateUis");
        }
        for(U ui : mUis) {
            populateUi(ui);
        }
    }

    protected void populateUi(U ui) {

    }

    protected int getId(U ui) {
        return ui.hashCode();
    }

    protected synchronized U findUi(final int id) {
        for(U ui : mUis) {
            if(getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    protected final void populateUiFromEvent(BaseState.UiCauseEvent event) {
        Preconditions.checkArgument(event != null, "event can not be null");

        final U ui = findUi(event.callingid);
        if(ui != null) {
            populateUi(ui);
        }
    }

}
