package com.hawk.contact.module;

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
public class InjectModule {
    public final Injector mInjector;

    public InjectModule(Injector injector) {
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Provides
    public Injector provideInjector() {
        return mInjector;
    }

}
