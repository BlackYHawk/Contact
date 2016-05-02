package com.hawk.contact.module.library;

import com.google.common.base.Preconditions;
import com.hawk.contact.util.Injector;

import dagger.Module;
import dagger.Provides;

/**
 * Created by heyong on 16/3/11.
 */
@Module(
        library = true
)
public class InjectorModule {
    public final Injector mInjector;

    public InjectorModule(Injector injector) {
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Provides
    public Injector provideInjector() {
        return mInjector;
    }

}
