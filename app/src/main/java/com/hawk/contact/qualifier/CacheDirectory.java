package com.hawk.contact.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by heyong on 16/3/11.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDirectory {
}
