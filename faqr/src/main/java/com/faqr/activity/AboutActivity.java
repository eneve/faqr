/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.TextView;

import com.faqr.FaqrApp;
import com.faqr.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * This Activity provides an about screen for the app
 *
 * @author eneve
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // donate
        TextView donate = (TextView) findViewById(R.id.donate_link);
        Linkify.addLinks(donate, Linkify.ALL);

        // contact
        TextView contact = (TextView) findViewById(R.id.contact_link);
        Linkify.addLinks(contact, Linkify.ALL);

        // version
        TextView version = (TextView) findViewById(R.id.version);
        TextView versionTitle = (TextView) findViewById(R.id.version_title);

        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // do nothing
        }
        version.setText(versionName);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get tracker.
        Tracker t = ((FaqrApp) getApplication()).getTracker();
        // Set screen name.
        t.setScreenName(getClass().getName());
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onStop() {
        super.onStop();
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
