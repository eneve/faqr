package com.faqr.fragment;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.faqr.R;

/**
 * Created by stephen on 7/27/15.
 */
public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /* get preference */
        Preference preference = findPreference(key);

        /* update summary */
        if (key.equals("typeface")) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        if (key.equals("saved_pos_preview")) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        if (key.equals("highlight_faqmark")) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        if (key.equals("auto_rotate_screen")) {
            preference.setSummary(((ListPreference) preference).getEntry());

            if (((ListPreference) preference).getValue().equals("1")) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else if (((ListPreference) preference).getValue().equals("2")) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (((ListPreference) preference).getValue().equals("3")) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        if (key.equals("theme")) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}
