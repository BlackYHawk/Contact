package com.hawk.contact.module.library;

import android.content.Context;

import com.hawk.contact.qualifier.ApplicationContext;
import com.hawk.contact.qualifiers.ForDatabase;
import com.hawk.contact.qualifiers.GeneralPurpose;
import com.hawk.contact.util.AndroidLogger;
import com.hawk.contact.util.AndroidStringFetcher;
import com.hawk.contact.util.BackgroundExecutor;
import com.hawk.contact.util.ContactBackgroundExecutor;
import com.hawk.contact.util.ImageHelper;
import com.hawk.contact.util.Logger;
import com.hawk.contact.util.StringFetcher;
import com.squareup.otto.Bus;

import java.util.concurrent.Executors;

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
    public ImageHelper provideImageHelper() {
        return new ImageHelper();
    }

    @Provides @Singleton @GeneralPurpose
    public BackgroundExecutor provideMultiThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new ContactBackgroundExecutor(Executors.newFixedThreadPool(numberCores * 2 + 1));
    }

    @Provides @Singleton @ForDatabase
    public BackgroundExecutor provideDatabaseThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new ContactBackgroundExecutor(Executors.newFixedThreadPool(numberCores * 2 + 1));
    }

    @Provides @Singleton
    public StringFetcher provideStringFetcher(@ApplicationContext Context context) {
        return new AndroidStringFetcher(context);
    }

}
