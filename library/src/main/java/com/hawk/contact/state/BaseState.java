package com.hawk.contact.state;

import com.hawk.contact.model.ContactAccount;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface BaseState {

    public static class UiCauseEvent {
        public final int callingid;

        public UiCauseEvent(int callingid) {
            this.callingid = callingid;
        }
    }

    public String getUsername();

    public ContactAccount getCurrentAccount();

    public void registerForEvents(Object receiver);

    public void unregisterForEvents(Object receiver);

}
