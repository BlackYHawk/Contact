package com.hawk.contact.util;

import java.util.Collection;

/**
 * Created by Administrator on 2016/3/8.
 */
public class CollectionsUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static int getSize(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

}
