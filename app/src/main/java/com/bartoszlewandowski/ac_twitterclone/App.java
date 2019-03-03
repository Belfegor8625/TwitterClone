/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.ac_twitterclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("WauD72D7zCHNBWYkxabxtya5fF4gZx5Q6zDYR3Fd")
                .clientKey("DHa93SZVR4e1FxQa9TnWJGRMi6tq4gYkmnaBcFd6")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
