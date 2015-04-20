package com.hanshenrik.gronsleth_hasamishogi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;


import java.util.List;


public class SettingsActivity extends PreferenceActivity {
    private static final boolean ALWAYS_SIMPLE_PREFS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == android.R.id.home || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        CheckBoxPreference daiVersionCheckbox = (CheckBoxPreference) findPreference(getString(R.string.pref_key_dai_version));
        final ListPreference capturesToWinList = (ListPreference) findPreference(getString(R.string.pref_key_captures_to_win));

        daiVersionCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int defaultIndex;
                if (newValue == true) {
                    defaultIndex = getResources().getStringArray(R.array.pref_captures_to_win_list_values_dai).length - 1;

                    // update list entries and values
                    capturesToWinList.setEntries(R.array.pref_captures_to_win_list_entries_dai);
                    capturesToWinList.setEntryValues(R.array.pref_captures_to_win_list_values_dai);

                    // set default value to be last entry
                    capturesToWinList.setValueIndex(defaultIndex);
                } else {
                    defaultIndex = getResources().getStringArray(R.array.pref_captures_to_win_list_values_normal).length - 1;

                    // update list entries and values
                    capturesToWinList.setEntries(R.array.pref_captures_to_win_list_entries_normal);
                    capturesToWinList.setEntryValues(R.array.pref_captures_to_win_list_values_normal);

                    // set default value to be last entry
                    capturesToWinList.setValueIndex(defaultIndex);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}
