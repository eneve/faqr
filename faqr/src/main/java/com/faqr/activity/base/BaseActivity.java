/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.faqr.R;
import com.faqr.activity.SearchActivity;

/**
 * This Activity provides a base Activity
 * 
 * @author eneve
 */
public class BaseActivity extends SherlockActivity {

    protected static final String TAG = "FAQr";

    protected static LayoutInflater inflater = null;

    // private ShakeListener mShaker;

    protected SharedPreferences prefs;

    protected Bundle extras;

    // connectivity dialog
    protected AlertDialog connectionDialog;

    protected int themeColor;
    protected int themeBackgroundColor;
    protected Drawable themeDrawable;
    protected String themeCssColor;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflater will give us elements in the hotlist_item layout
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // extras
        extras = getIntent().getExtras();

        if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
            setTheme(R.style.Appnewlighttheme);
            themeColor = getResources().getColor(R.color.gamefaqs_dark_color);
            themeBackgroundColor = getResources().getColor(R.color.gamefaqs_color);
            themeDrawable = getResources().getDrawable(R.drawable.faqr_saved_light_bg_small_light_corner);
            themeCssColor = "default";
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
            setTheme(R.style.AppBlackTheme);
            themeColor = getResources().getColor(R.color.gamefaqs_light_color);
            themeBackgroundColor = getResources().getColor(R.color.gamefaqs_light_color);
            themeDrawable = getResources().getDrawable(R.drawable.faqr_saved_light_bg_small_dark_corner);
            themeCssColor = "dark-blue";
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
            setTheme(R.style.AppDarkTheme);
            themeColor = getResources().getColor(R.color.gamefaqs_light_color);
            themeBackgroundColor = getResources().getColor(R.color.gamefaqs_light_color);
            themeDrawable = getResources().getDrawable(R.drawable.faqr_saved_light_bg_small_dark_corner);
            themeCssColor = "dark-blue";
            // themeCssColor = "cloudy";
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
            setTheme(R.style.Appnewlighttheme);

            themeColor = getResources().getColor(R.color.gamefaqs_dark_color);
            themeBackgroundColor = getResources().getColor(R.color.gamefaqs_color);
            // themeColor = getResources().getColor(R.color.sepia_theme_color);
            // themeBackgroundColor = getResources().getColor(R.color.sepia_theme_color);
            themeDrawable = getResources().getDrawable(R.drawable.faqr_saved_light_bg_small_sepia_corner);
            themeCssColor = "sepia";
        }

        // keep screen on
        if (prefs.getBoolean("keep_screen_on", getResources().getBoolean(R.bool.keep_screen_on_default))) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // orientation lock
        if (prefs.getString("auto_rotate_screen", "1").equals("1")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        } else if (prefs.getString("auto_rotate_screen", "1").equals("2")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (prefs.getString("auto_rotate_screen", "1").equals("3")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // connection dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("There is no internet connection available. Please connect to wifi or your data plan and try again.").setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        connectionDialog = dialogBuilder.create();
        // connectionDialog.setTitle("Warning!");
        // connectionDialog.setIcon(R.drawable.ic_dialog_alert);

        // connectionDialog.setIcon(R.drawable.ic_dialog_alert);
    }

    /** The final call you receive before your activity is destroyed. */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /** Called when the system is about to start resuming a previous activity. */
    @Override
    protected void onPause() {
        super.onPause();

        // mShaker.pause();
    }

    /** Called when the activity will start interacting with the user. */
    @Override
    protected void onResume() {
        super.onResume();

        // hide notification bar
        // if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("1")) {
        // showStatusBar();
        // } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("2")) {
        // hideStatusBar();
        // } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("3")) {
        // int orientation = getScreenOrientation();
        // if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
        // hideStatusBar();
        // } else {
        // showStatusBar();
        // }
        // }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // hide notification bar
        // if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("1")) {
        // showStatusBar();
        // } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("2")) {
        // hideStatusBar();
        // } else if (prefs.getString("hide_notification_bar", getResources().getString(R.string.hide_notification_bar_default)).equals("3")) {
        // int orientation = getScreenOrientation();
        // if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
        // hideStatusBar();
        // } else {
        // showStatusBar();
        // }
        // }

    }

    /** Called when activity start-up is complete (after onStart() and onRestoreInstanceState(Bundle) have been called). */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_SEARCH)) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(searchIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    /** Get the version of the application */
    protected String getApplicationVersion() {
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // do nothing
        }
        return versionName;
    }

    /** Check for network connectivity */
    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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

    /** return the height of the action bar in pixels **/
    protected int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else if (getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /** return the height of the status bar in pixels **/
    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // navigation bar (at the bottom of the screen on a Nexus device)
    public int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}