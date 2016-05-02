package com.hawk.contact.base;

import android.app.Application;
import android.content.Context;

import com.hawk.contact.controller.MainController;
import com.hawk.contact.module.ApplicationModule;
import com.hawk.contact.module.library.ContextProvider;
import com.hawk.contact.module.library.InjectorModule;
import com.hawk.contact.module.library.UtilProvider;
import com.hawk.contact.util.Injector;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Created by heyong on 16/3/11.
 */
public class ContactApplication extends Application implements Injector {

    public static ContactApplication from(Context context) {
        return (ContactApplication)context.getApplicationContext();
    }

    @Inject
    MainController mainController;

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new ContextProvider(this),
                new ApplicationModule(),
                new InjectorModule(this),
                new UtilProvider());
        objectGraph.inject(this);
    }

    public MainController getMainController() {
        return mainController;
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override
    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
