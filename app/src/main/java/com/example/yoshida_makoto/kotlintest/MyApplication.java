package com.example.yoshida_makoto.kotlintest;

import android.app.Application;

import com.example.yoshida_makoto.kotlintest.dagger.AppComponent;
import com.example.yoshida_makoto.kotlintest.dagger.DaggerAppComponent;
import com.example.yoshida_makoto.kotlintest.dagger.RootModule;

/**
 * Created by yoshida_makoto on 2016/10/26.
 */

public class MyApplication extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().rootModule(new RootModule(this)).build();
    }

    public AppComponent getApplicationComponent(){
        return mAppComponent;
    }
}
