package com.hawk.contact.util;

import com.hawk.contact.controller.UserController;
import com.hawk.contact.lib.R;

/**
 * Created by heyong on 16/3/15.
 */
public class StringManager {

    public static int getStringResId(UserController.ContactTab tabUi) {
        switch (tabUi) {
            case DIAL:
                return R.string.dial_title;
            case PEOPLE:
                return R.string.people_title;
        }
        return 0;
    }

}
