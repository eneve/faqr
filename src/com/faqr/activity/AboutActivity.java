/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.faqr.R;
import com.faqr.activity.base.BaseActivity;
import com.google.analytics.tracking.android.EasyTracker;

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

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // }

        // donate
        TextView donate = (TextView) findViewById(R.id.donate_link);
        Linkify.addLinks(donate, Linkify.ALL);

        // contact
        TextView contact = (TextView) findViewById(R.id.contact_link);
        Linkify.addLinks(contact, Linkify.ALL);
        donate.setLinkTextColor(themeColor);
        contact.setLinkTextColor(themeColor);

        // version
        TextView version = (TextView) findViewById(R.id.version);
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // do nothing
        }
        version.setText(versionName);

        // theme goodness
        ((TextView) findViewById(R.id.section_1_title)).setTextColor(themeColor);
        ((TextView) findViewById(R.id.section_2_title)).setTextColor(themeColor);
        ((TextView) findViewById(R.id.section_3_title)).setTextColor(themeColor);
        ((TextView) findViewById(R.id.section_4_title)).setTextColor(themeColor);
        ((TextView) findViewById(R.id.section_5_title)).setTextColor(themeColor);
        ((TextView) findViewById(R.id.section_6_title)).setTextColor(themeColor);
        ((TextView) findViewById(R.id.section_7_title)).setTextColor(themeColor);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this); // Add this method.
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
