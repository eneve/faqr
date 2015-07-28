package com.faqr.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.faqr.R;

/**
 * Created by stephen on 7/27/15.
 */
public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /* get preference */
        Preference preference = findPreference(key);

        /* update summary */
        if (key.equals("list_0")) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }


        if (key.equals("typeface")) {
//            if (preferences.getString("typeface", "1").equals("1")) {
//                getPreferenceScreen().findPreference("typeface").setSummary("Mono");
//            } else if (preferences.getString("typeface", "1").equals("2")) {
//                getPreferenceScreen().findPreference("typeface").setSummary("Variable Sans");
//            } else if (preferences.getString("typeface", "1").equals("3")) {
//                getPreferenceScreen().findPreference("typeface").setSummary("Variable Serif");
//            }

            preference.setSummary(((ListPreference) preference).getEntry());
        }

        if (key.equals("saved_pos_preview")) {

            preference.setSummary(((ListPreference) preference).getEntry());

//            String previewSize = preferences.getString("saved_pos_preview", "3");
//            getPreferenceScreen().findPreference("saved_pos_preview").setSummary(previewSize);

//            if (preferences.getString("saved_pos_preview", "6").equals("6")) {
//                getPreferenceScreen().findPreference("saved_pos_preview").setSummary("Short");
//            } else if (preferences.getString("saved_pos_preview", "6").equals("12")) {
//                getPreferenceScreen().findPreference("saved_pos_preview").setSummary("Medium");
//            } else if (preferences.getString("saved_pos_preview", "6").equals("24")) {
//                getPreferenceScreen().findPreference("saved_pos_preview").setSummary("Long");
//            }
        }

        if (key.equals("highlight_faqmark")) {

            preference.setSummary(((ListPreference) preference).getEntry());

//            if (preferences.getString("highlight_faqmark", "1").equals("1")) {
//                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight text");
//            } else if (preferences.getString("highlight_faqmark", "1").equals("2")) {
//                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight corner fold");
//            } else if (preferences.getString("highlight_faqmark", "1").equals("3")) {
//                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight text and corner fold");
//            } else if (preferences.getString("highlight_faqmark", "1").equals("4")) {
//                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Do not highlight");
//            }
        }

        if (key.equals("auto_rotate_screen")) {

            preference.setSummary(((ListPreference) preference).getEntry());

//            if (preferences.getString("auto_rotate_screen", "1").equals("1")) {
//                getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Use system setting");
////                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//            } else if (preferences.getString("auto_rotate_screen", "1").equals("2")) {
//                getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Lock portrait");
////                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            } else if (preferences.getString("auto_rotate_screen", "1").equals("3")) {
//                getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Lock landscape");
////                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
        }


        if (key.equals("theme")) {

            preference.setSummary(((ListPreference) preference).getEntry());

//            if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
//                getPreferenceScreen().findPreference("theme").setSummary("Day");
//            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
//                getPreferenceScreen().findPreference("theme").setSummary("Night");
//            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
//                getPreferenceScreen().findPreference("theme").setSummary("Dark");
//            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
//                getPreferenceScreen().findPreference("theme").setSummary("Sepia");
//            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("5")) {
//                getPreferenceScreen().findPreference("theme").setSummary("Transparent");
//            }
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
