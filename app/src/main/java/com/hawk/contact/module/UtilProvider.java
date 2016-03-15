package com.hawk.contact.module;

import android.content.Context;

import com.hawk.contact.qualifier.ApplicationContext;
import com.hawk.contact.util.AndroidLogger;
import com.hawk.contact.util.AndroidStringFetcher;
import com.hawk.contact.util.Logger;
import com.hawk.contact.util.StringFetcher;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by heyong on 16/3/11.
 */
@Module(
        includes = ContextProvider.class,
        library = true
)
public class UtilProvider {

    @Provides @Singleton
    public Bus provideEventBus() {
        return new Bus();
    }

    @Provides @Singleton
    public Logger provideLogger() {
        return new AndroidLogger();
    }

    @Provides @Singleton
    public StringFetcher provideStringFetcher(@ApplicationContext Context context) {
        return new AndroidStringFetcher(context);
    }

}
