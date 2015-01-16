/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.faqr.R;
import com.faqr.activity.base.BaseActivity;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * This Activity determines which activity to forward to on app start
 * 
 * @author eneve
 */
public class MainActivity extends BaseActivity {

    private String currFaq = "";
    private String[] currFaqMeta = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        setContentView(R.layout.activity_help);

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        // ActionBar actionBar = getSupportActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // }


        // get the current FAQ
        if (!TextUtils.isEmpty(prefs.getString("curr_faq", ""))) {

            // auto open
            if (prefs.getBoolean("auto_open_new", getResources().getBoolean(R.bool.auto_open_default))) {
                Intent intent = new Intent(this, FaqActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, MyFaqsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {

            // what to do if there are no faqs
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Intent intent = new Intent(this, MyFaqsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        }
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
