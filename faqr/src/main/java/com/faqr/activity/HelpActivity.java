/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.faqr.R;
import com.faqr.activity.base.BaseActivity;

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

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // }

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
