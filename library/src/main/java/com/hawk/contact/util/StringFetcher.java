package com.hawk.contact.util;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface StringFetcher {

    public String getString(int id);
    public String getString(int id, Object... format);

}
