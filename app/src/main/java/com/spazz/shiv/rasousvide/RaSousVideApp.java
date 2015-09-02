package com.spazz.shiv.rasousvide;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orm.SugarApp;
import com.spazz.shiv.rasousvide.rest.RestClient;
import com.spazz.shiv.rasousvide.rest.model.ShivVideAPI;
import com.spazz.shiv.rasousvide.ui.prefs.SettingsActivity;

/**
 * Created by shivneil on 9/1/15.
 * TODO: Add legal stuff
 * TODO: Add javadocs stuff
 */
public class RaSousVideApp extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO:
        RestClient.initAPI(getEndpointPreference(this));
    }

    private String getEndpointPreference(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useDomain = preferences.getBoolean(SettingsActivity.KEY_PREF_PI_USE_DOMAIN, false);

        if(useDomain) {
            return preferences.getString(SettingsActivity.KEY_PREF_PI_DOMAIN_NAME, RestClient.getApiEndpoint());
        }
        else {
            String ip = preferences.getString(SettingsActivity.KEY_PREF_PI_IP, "http://localhost");
            String port = preferences.getString(SettingsActivity.KEY_PREF_PI_PORT, "5000");

            //TODO: Handle HTTP/HTTPS being present or not
            //TODO: Handle trailing slash being present or not

            return "http://".concat(ip).concat(":").concat(port);
        }

    }

}
