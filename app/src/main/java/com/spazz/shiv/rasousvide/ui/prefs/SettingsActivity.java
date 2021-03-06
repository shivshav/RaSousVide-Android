package com.spazz.shiv.rasousvide.ui.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import com.spazz.shiv.rasousvide.R;
import com.spazz.shiv.rasousvide.rest.RestClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity  {
    private static final String TAG = "SettingsAActivity";
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    public static final String KEY_PREF_GENERAL_ADV_VIEW = "advanced_switch";
    public static final String KEY_PREF_GENERAL_TEMP_UNITS = "temperature_switch";
    public static final String KEY_PREF_PI_USE_DOMAIN = "pi_use_domain";
    public static final String KEY_PREF_PI_DOMAIN_NAME = "pi_domain_name";
    public static final String KEY_PREF_PI_IP = "pi_ip";
    public static final String KEY_PREF_PI_PORT = "pi_port";
    public static final String KEY_PREF_PI_TEMP_REFRESH = "pi_on_refresh_rate";
    public static final String KEY_PREF_PI_MODE_REFRESH = "pi_off_refresh_rate";
    public static final String KEY_PREF_NOTIFICATIONS_ENABLE = "notifications_enable";
    public static final String KEY_PREF_NOTIFICATIONS_RINGTONE = "notifications_ringtone";
    public static final String KEY_PREF_NOTIFICATIONS_VIBRATE = "notifications_vibrate";
    public static final String KEY_PREF_NOTIFICATIONS_NOTIFY_ON = "notifications_notify_on";


    public static final int DEFAULT_PREF_PI_TEMP_REFRESH = 5;
    public static final int DEFAULT_PREF_PI_MODE_REFRESH = 60;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    @Override
    protected boolean isValidFragment (String fragmentName) {
        return true;
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        //Add Raspberry Pi preferences
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_pi);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_pi_settings);

        // Add 'notifications' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

//        // Add 'data and sync' preferences, and a corresponding header.
//        fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_data_sync);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_data_sync);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference("example_text"));

//        bindPreferenceSummaryToValue(findPreference(KEY_PREF_GENERAL_ADV_VIEW));
        bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_DOMAIN_NAME));
        bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_IP));
        bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_PORT));

        bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_TEMP_REFRESH));
        bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_MODE_REFRESH));


//        bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_ENABLE));
        bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_RINGTONE));
//        bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_VIBRATE));
        bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_NOTIFY_ON));

//        bindPreferenceSummaryToValue(findPreference("example_list"));
//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        bindPreferenceSummaryToValue(findPreference("sync_frequency"));

        setupPiSettingsScreen();
    }

    private void setupPiSettingsScreen() {

        //TODO: Port these changes over to small screen variant
        //TODO: Find an easier way to do this
        final SwitchPreference domainSwitch = (SwitchPreference) findPreference(KEY_PREF_PI_USE_DOMAIN);

        final EditTextPreference domainText = (EditTextPreference) findPreference(KEY_PREF_PI_DOMAIN_NAME);
        final EditTextPreference ipAddr = (EditTextPreference) findPreference(KEY_PREF_PI_IP);
        final EditTextPreference port = (EditTextPreference) findPreference(KEY_PREF_PI_PORT);

        domainText.setEnabled(domainSwitch.isChecked());
        ipAddr.setEnabled(!domainSwitch.isChecked());
        port.setEnabled(!domainSwitch.isChecked());

        domainSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preference instanceof SwitchPreference) {
                    boolean useDomain = ((SwitchPreference) preference).isChecked();

                    ipAddr.setEnabled(!useDomain);
                    port.setEnabled(!useDomain);
                    domainText.setEnabled(useDomain);

                    return true;
                }
                return false;
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if(preference instanceof MultiSelectListPreference) {
                MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                Set<String> chosenSet = (Set<String>) value;

                StringBuilder sb = new StringBuilder(chosenSet.size() * 2);//Because we will add at most a comma for every entry - 1

                int idx = 0;
                for(Iterator<String> it = chosenSet.iterator(); it.hasNext();) {
                    String currentEntry;
                    String index = it.next();

                    try{
                        idx = Integer.parseInt(index);
                        currentEntry = listPreference.getEntries()[idx].toString();
                    } catch (ClassCastException cce) {
                        currentEntry = listPreference.getEntries()[idx].toString();
                        idx++;
                    }

                    sb.append(currentEntry);
                    if(it.hasNext()) {
                        sb.append(", ");
                    }
                }
                preference.setSummary(sb.toString());

            } else if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        Object preferenceVal = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

        try {
            String myString = preferences.getString(preference.getKey(), "");
            preferenceVal = myString;

        } catch (ClassCastException cce) {
            try {
                Integer myInt = preferences.getInt(preference.getKey(), DEFAULT_PREF_PI_TEMP_REFRESH);
                preferenceVal = myInt;
            } catch (ClassCastException cce_inner) {
                Set<String> myStringSet = preferences.getStringSet(preference.getKey(), null);
                preferenceVal = myStringSet;
            }
        }

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, preferenceVal);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PiPreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_pi_settings);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_IP));
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_PORT));

            bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_DOMAIN_NAME));

            bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_TEMP_REFRESH));
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_PI_MODE_REFRESH));

            setupPiSettingsFragment();

        }

        private void setupPiSettingsFragment() {
            final SwitchPreference domainSwitch = (SwitchPreference) findPreference(KEY_PREF_PI_USE_DOMAIN);

            final EditTextPreference domainText = (EditTextPreference) findPreference(KEY_PREF_PI_DOMAIN_NAME);
            final EditTextPreference ipAddr = (EditTextPreference) findPreference(KEY_PREF_PI_IP);
            final EditTextPreference port = (EditTextPreference) findPreference(KEY_PREF_PI_PORT);

            domainText.setEnabled(domainSwitch.isChecked());
            ipAddr.setEnabled(!domainSwitch.isChecked());
            port.setEnabled(!domainSwitch.isChecked());

            domainSwitch.setOnPreferenceClickListener(preference -> {
                if(preference instanceof SwitchPreference) {
                    boolean useDomain = ((SwitchPreference) preference).isChecked();

                    ipAddr.setEnabled(!useDomain);
                    port.setEnabled(!useDomain);
                    domainText.setEnabled(useDomain);

                    return true;
                }
                return false;
            });
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_ENABLE));
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_RINGTONE));
//            bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_VIBRATE));
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_NOTIFICATIONS_NOTIFY_ON));
        }
    }
}
