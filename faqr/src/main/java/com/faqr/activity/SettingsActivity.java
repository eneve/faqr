/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.faqr.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented as a single list. On tablets, settings are split by category, with category headers shown to the left of the list of
 * settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design guidelines and the <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for more
 * information on developing a Settings UI.
 */
public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
    /**
     * Determines whether to always show the simplified settings UI, where settings are presented in a single list. When false, settings are shown as a master/detail two-pane view on tablets. When true, a single pane is shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = true;

    private SharedPreferences prefs;

    private Bundle extras;

    private String fromActivity = "";
    private String fromActivityMeta = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // }

        extras = getIntent().getExtras();
        if (extras != null && extras.getString("fromActivity") != null && !TextUtils.isEmpty(extras.getString("fromActivity"))) {
            fromActivity = extras.getString("fromActivity");
            fromActivityMeta = extras.getString("fromActivityMeta");
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        // orientation lock
        if (prefs.getString("auto_rotate_screen", "1").equals("1")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        } else if (prefs.getString("auto_rotate_screen", "1").equals("2")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (prefs.getString("auto_rotate_screen", "1").equals("3")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // hide notification bar
//        if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("1")) {
//            showStatusBar();
//        } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("2")) {
//            hideStatusBar();
//        } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("3")) {
//            int orientation = getScreenOrientation();
//            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//                hideStatusBar();
//            } else {
//                showStatusBar();
//            }
//        }

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
//        EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // if (preferences.getBoolean("highlight_saved_pos", true)) {
        // getPreferenceScreen().findPreference("saved_pos_background").setEnabled(true);
        // } else {
        // getPreferenceScreen().findPreference("saved_pos_background").setEnabled(false);
        // }

        // if (preferences.getBoolean("use_variable_font", false)) {
        // getPreferenceScreen().findPreference("use_serif_font").setEnabled(true);
        // getPreferenceScreen().findPreference("variable_font_size").setEnabled(true);
        // } else {
        // getPreferenceScreen().findPreference("use_serif_font").setEnabled(false);
        // getPreferenceScreen().findPreference("variable_font_size").setEnabled(false);
        // }

        // disable dark theme
        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        // getPreferenceScreen().findPreference("use_lights_out").setSummary("Only available in Android 3.0+");
        // getPreferenceScreen().findPreference("use_lights_out").setEnabled(false);
        // }

        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        // getPreferenceScreen().findPreference("use_immersive_mode").setSummary("Only available in Android 4.4+");
        // getPreferenceScreen().findPreference("use_immersive_mode").setEnabled(false);
        // }

        // typeface
        if (preferences.getString("typeface", "1").equals("1")) {
            getPreferenceScreen().findPreference("typeface").setSummary("Mono");
        } else if (preferences.getString("typeface", "1").equals("2")) {
            getPreferenceScreen().findPreference("typeface").setSummary("Variable Sans");
        } else if (preferences.getString("typeface", "1").equals("3")) {
            getPreferenceScreen().findPreference("typeface").setSummary("Variable Serif");
        }

        // if (key.equals("mono_font_size")) {
        String monoFont = preferences.getString("mono_font_size", "1");
        if (monoFont.equals("Auto"))
            getPreferenceScreen().findPreference("mono_font_size").setSummary(monoFont);
        else
            getPreferenceScreen().findPreference("mono_font_size").setSummary(monoFont + " pt");
        // }

        // if (key.equals("variable_font_size")) {
        String variableFont = preferences.getString("variable_font_size", "1");
        if (variableFont.equals("Auto"))
            getPreferenceScreen().findPreference("variable_font_size").setSummary(variableFont);
        else
            getPreferenceScreen().findPreference("variable_font_size").setSummary(variableFont + " pt");
        // }

        // auto-rotate screen set summary
        if (preferences.getString("highlight_faqmark", "1").equals("1")) {
            getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight text");
        } else if (preferences.getString("highlight_faqmark", "1").equals("2")) {
            getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight corner fold");
        } else if (preferences.getString("highlight_faqmark", "1").equals("3")) {
            getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight text and corner fold");
        } else if (preferences.getString("highlight_faqmark", "1").equals("4")) {
            getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Do not highlight");
        }

        String previewSize = preferences.getString("saved_pos_preview", "3");
        getPreferenceScreen().findPreference("saved_pos_preview").setSummary(previewSize + " lines");

        // auto-rotate screen set summary
        if (preferences.getString("auto_rotate_screen", "1").equals("1")) {
            getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Use system setting");
        } else if (preferences.getString("auto_rotate_screen", "1").equals("2")) {
            getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Lock portrait");
        } else if (preferences.getString("auto_rotate_screen", "1").equals("3")) {
            getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Lock landscape");

        }

        // hide notification bar set summary
//        if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("1")) {
//            getPreferenceScreen().findPreference("hide_notification_bar").setSummary("Always show");
//        } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("2")) {
//            getPreferenceScreen().findPreference("hide_notification_bar").setSummary("Always hide");
//        } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("3")) {
//            getPreferenceScreen().findPreference("hide_notification_bar").setSummary("Hide in landscape");
//        }

        // hide notification bar set summary
        if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
            getPreferenceScreen().findPreference("theme").setSummary("Day");
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
            getPreferenceScreen().findPreference("theme").setSummary("Night");
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
            getPreferenceScreen().findPreference("theme").setSummary("Dark");
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
            getPreferenceScreen().findPreference("theme").setSummary("Sepia");
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("5")) {
            getPreferenceScreen().findPreference("theme").setSummary("Transparent");
        }

        // disable dark theme
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            getPreferenceScreen().findPreference("theme").setSummary("Only available in Android 3.0+");
            getPreferenceScreen().findPreference("theme").setEnabled(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // hide notification bar
        if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("1")) {
            showStatusBar();
        } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("2")) {
            hideStatusBar();
        } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("3")) {
            int orientation = getScreenOrientation();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                hideStatusBar();
            } else {
                showStatusBar();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, FaqActivity.class);
            if (fromActivity.equalsIgnoreCase("My FAQs")) {
                intent = new Intent(this, MyFaqsActivity.class);
            }
            if (fromActivity.equalsIgnoreCase("My FAQmarks")) {
                intent = new Intent(this, FaqmarksActivity.class);
            }
            if (fromActivity.equalsIgnoreCase("SearchResults")) {
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("game", fromActivityMeta);
            }
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        default:
            return false;
        }
    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this, FaqActivity.class);
            if (fromActivity.equalsIgnoreCase("My FAQs")) {
                intent = new Intent(this, MyFaqsActivity.class);
            }
            if (fromActivity.equalsIgnoreCase("My FAQmarks")) {
                intent = new Intent(this, FaqmarksActivity.class);
            }
            if (fromActivity.equalsIgnoreCase("SearchResults")) {
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("game", fromActivityMeta);
            }
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Shows the simplified settings UI if the device configuration if the device configuration dictates that a simplified, single-pane UI should be shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        // PreferenceCategory generalHeader = new PreferenceCategory(this);
        // generalHeader.setTitle(R.string.pref_header_general);
        // getPreferenceScreen().addPreference(generalHeader);
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory savedPosHeader = new PreferenceCategory(this);
        savedPosHeader.setTitle(R.string.pref_header_saved_pos);
        getPreferenceScreen().addPreference(savedPosHeader);
        addPreferencesFromResource(R.xml.pref_saved_pos);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory screenHeader = new PreferenceCategory(this);
        screenHeader.setTitle(R.string.pref_header_screen);
        getPreferenceScreen().addPreference(screenHeader);
        addPreferencesFromResource(R.xml.pref_screen);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fontsHeader = new PreferenceCategory(this);
        fontsHeader.setTitle(R.string.pref_header_fonts);
        getPreferenceScreen().addPreference(fontsHeader);
        addPreferencesFromResource(R.xml.pref_fonts);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        // bindPreferenceSummaryToValue(findPreference("faq_url"));
        // bindPreferenceSummaryToValue(findPreference("user_dark_theme"));
        // bindPreferenceSummaryToValue(findPreference("mono_font_size"));
        // bindPreferenceSummaryToValue(findPreference("variable_font_size"));

    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device doesn't have newer APIs like {@link PreferenceFragment}, or the device doesn't have an
     * extra-large screen. In these cases, a single-pane "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof EditTextPreference) {

                // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // intent.putExtra("reload", true);
                // startActivity(intent);
                // return true;

            } else if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                // ListPreference listPreference = (ListPreference) preference;
                // int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                // preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                // preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary (line of text below the preference title) is updated to reflect the value. The summary is also immediately updated upon
     * calling this method. The exact display format is dependent on the type of preference.
     * 
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        // sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the activity is showing a two-pane settings UI.
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
            // bindPreferenceSummaryToValue(findPreference("faq_url"));
            // bindPreferenceSummaryToValue(findPreference("mono_font_size"));
            // bindPreferenceSummaryToValue(findPreference("variable_font_size"));

            // bindPreferenceSummaryToValue(findPreference("mono_font_size"));
            // bindPreferenceSummaryToValue(findPreference("variable_font_size"));
        }
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("highlight_saved_pos")) {

            if (preferences.getBoolean("highlight_saved_pos", true)) {
                getPreferenceScreen().findPreference("saved_pos_background").setEnabled(true);
            } else {
                getPreferenceScreen().findPreference("saved_pos_background").setEnabled(false);
            }

        }

        if (key.equals("mono_font_size")) {
            String typeface = preferences.getString("mono_font_size", "1");

            if (typeface.equals("Auto"))
                getPreferenceScreen().findPreference("mono_font_size").setSummary(typeface);
            else
                getPreferenceScreen().findPreference("mono_font_size").setSummary(typeface + " pt");
        }

        if (key.equals("variable_font_size")) {
            String typeface = preferences.getString("variable_font_size", "1");

            if (typeface.equals("Auto"))
                getPreferenceScreen().findPreference("variable_font_size").setSummary(typeface);
            else
                getPreferenceScreen().findPreference("variable_font_size").setSummary(typeface + " pt");
        }

        // if (key.equals("use_variable_font")) {
        //
        // if (preferences.getBoolean("use_variable_font", false)) {
        // getPreferenceScreen().findPreference("use_serif_font").setEnabled(true);
        // getPreferenceScreen().findPreference("variable_font_size").setEnabled(true);
        // } else {
        // getPreferenceScreen().findPreference("use_serif_font").setEnabled(false);
        // getPreferenceScreen().findPreference("variable_font_size").setEnabled(false);
        // }
        // }

        if (key.equals("typeface")) {
            if (preferences.getString("typeface", "1").equals("1")) {
                getPreferenceScreen().findPreference("typeface").setSummary("Mono");
            } else if (preferences.getString("typeface", "1").equals("2")) {
                getPreferenceScreen().findPreference("typeface").setSummary("Variable Sans");
            } else if (preferences.getString("typeface", "1").equals("3")) {
                getPreferenceScreen().findPreference("typeface").setSummary("Variable Serif");
            }
        }

        if (key.equals("saved_pos_preview")) {
            String previewSize = preferences.getString("saved_pos_preview", "3");
            getPreferenceScreen().findPreference("saved_pos_preview").setSummary(previewSize + " lines");
        }

        if (key.equals("highlight_faqmark")) {
            if (preferences.getString("highlight_faqmark", "1").equals("1")) {
                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight text");
            } else if (preferences.getString("highlight_faqmark", "1").equals("2")) {
                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight corner fold");
            } else if (preferences.getString("highlight_faqmark", "1").equals("3")) {
                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Highlight text and corner fold");
            } else if (preferences.getString("highlight_faqmark", "1").equals("4")) {
                getPreferenceScreen().findPreference("highlight_faqmark").setSummary("Do not highlight");
            }
        }

        if (key.equals("auto_rotate_screen")) {
            if (preferences.getString("auto_rotate_screen", "1").equals("1")) {
                getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Use system setting");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else if (preferences.getString("auto_rotate_screen", "1").equals("2")) {
                getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Lock portrait");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (preferences.getString("auto_rotate_screen", "1").equals("3")) {
                getPreferenceScreen().findPreference("auto_rotate_screen").setSummary("Lock landscape");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        if (key.equals("hide_notification_bar")) {
            if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("1")) {
                getPreferenceScreen().findPreference("hide_notification_bar").setSummary("Always show");
                showStatusBar();
            } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("2")) {
                getPreferenceScreen().findPreference("hide_notification_bar").setSummary("Always hide");
                hideStatusBar();
            } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("3")) {
                getPreferenceScreen().findPreference("hide_notification_bar").setSummary("Hide in landscape");
                int orientation = getScreenOrientation();
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    hideStatusBar();
                }
            }
        }

        if (key.equals("theme")) {
            if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
                getPreferenceScreen().findPreference("theme").setSummary("Day");
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
                getPreferenceScreen().findPreference("theme").setSummary("Night");
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
                getPreferenceScreen().findPreference("theme").setSummary("Dark");
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
                getPreferenceScreen().findPreference("theme").setSummary("Sepia");
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("5")) {
                getPreferenceScreen().findPreference("theme").setSummary("Transparent");
            }
        }
    }

    /** Get screen orientation */
    protected int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Surface.ROTATION_270:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            }
        }

        return orientation;
    }

}
