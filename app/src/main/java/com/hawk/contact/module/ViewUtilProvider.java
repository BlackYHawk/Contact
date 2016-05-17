package com.hawk.contact.module;

import android.content.Context;
import android.content.res.AssetManager;

import com.hawk.contact.adapters.ContactSectionedListAdapter;
import com.hawk.contact.module.library.ContextProvider;
import com.hawk.contact.module.library.UtilProvider;
import com.hawk.contact.qualifier.ApplicationContext;
import com.hawk.contact.util.TypefaceManager;
import com.hawk.contact.widget.FontTextView;

import java.text.DateFormat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by heyong on 16/5/9.
 */
@Module(
        includes =  {
                ContextProvider.class,
                UtilProvider.class
        },
        injects = {
                FontTextView.class,
                ContactSectionedListAdapter.class
        }
)
public class ViewUtilProvider {

    @Provides @Singleton
    public TypefaceManager provideTypefaceManager(AssetManager assetManager) {
        return new TypefaceManager(assetManager);
    }

    @Provides @Singleton
    public DateFormat provideMediumDateFormat(@ApplicationContext Context context) {
        return android.text.format.DateFormat.getMediumDateFormat(context);
    }

}
