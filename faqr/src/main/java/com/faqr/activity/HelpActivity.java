/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.faqr.FaqrApp;
import com.faqr.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * This Activity provides a help screen for the app
 * 
 * @author eneve
 */
public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // }

    }

    @Override
    public void onStart() {
        super.onStart();
        // EasyTracker.getInstance(this).activityStart(this); // Add this method.

        // Get tracker.
        Tracker t = ((FaqrApp) getApplication()).getTracker(FaqrApp.TrackerName.GLOBAL_TRACKER);
        // Set screen name.
        t.setScreenName(getClass().getName());
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onStop() {
        super.onStop();
//        EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return false;
        }
    }

}
