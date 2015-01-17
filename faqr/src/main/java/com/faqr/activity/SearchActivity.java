/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.faqr.FaqrApp;
import com.faqr.R;

import java.io.File;

/**
 * This Activity provides a help screen for the app
 * 
 * @author eneve
 */
public class SearchActivity extends BaseActivity {

    private AutoCompleteTextView searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String currFaq = prefs.getString("curr_faq", "");

        ActionBar actionBar = getSupportActionBar();

        File[] files = FaqrApp.getFaqrFiles(getFilesDir().listFiles());
        if (files.length > 0)
            // if (!TextUtils.isEmpty(currFaq)) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        // }

        // search text

        // String recentSearches = prefs.getString("recent_searches", "");
        // String[] split = recentSearches.split(" --- ");
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, split);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchText.setThreshold(0);

        // searchText.setAdapter(adapter);
        //
        // searchText.requestFocus();

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        // test input
        // searchText.setText("final fantasy iv");

        searchText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        // Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // intent.putExtra("game", searchText.getText().toString());
                        // startActivity(intent);
                        // return true;

                        if (null != searchText.getText().toString() && !searchText.getText().toString().equals("")) {
                            // save search
                            String newRecentSearches = "";
                            String recentSearches = prefs.getString("recent_searches", "");
                            if (!recentSearches.contains(searchText.getText().toString())) {
                                newRecentSearches += searchText.getText().toString();
                                String[] split = recentSearches.split(" --- ");
                                for (int i = 0; i < split.length; i++) {
                                    newRecentSearches += " --- " + split[i];
                                    if (i > 18)
                                        break;
                                }
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("recent_searches", newRecentSearches);
                                editor.commit();
                            }

                            Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                            intent.putExtra("game", searchText.getText().toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter search terms.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                // otherwise use default handling of key
                return false;
            }
        });

        // select an auto complete option
        // searchText.setOnItemClickListener(new OnItemClickListener() {
        //
        // /**
        // * Implements OnItemClickListener
        // */
        // public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // // if (DBG)
        // Log.w(TAG, "onItemClick() position " + position);
        // // onItemClicked(position, KeyEvent.KEYCODE_UNKNOWN, null);
        //
        // TextView textView = (TextView) view.findViewById(android.R.id.text1);
        //
        // searchText.setText(textView.getText());
        //
        // // String recentSearches = prefs.getString("recent_searches", "");
        // // String[] split = recentSearches.split(" --- ");
        //
        // // searchItem.collapseActionView();
        //
        // Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        // intent.putExtra("game", textView.getText());
        // startActivity(intent);
        // }
        // });

        // search button
        Button searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // intent.putExtra("game", searchText.getText().toString());
                // startActivity(intent);
                // return true;

                if (null != searchText.getText().toString() && !searchText.getText().toString().equals("")) {
                    // save search
                    String newRecentSearches = "";
                    String recentSearches = prefs.getString("recent_searches", "");
                    if (!recentSearches.contains(searchText.getText().toString())) {
                        newRecentSearches += searchText.getText().toString();
                        String[] split = recentSearches.split(" --- ");
                        for (int i = 0; i < split.length; i++) {
                            newRecentSearches += " --- " + split[i];
                            if (i > 18)
                                break;
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("recent_searches", newRecentSearches);
                        editor.commit();
                    }

                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra("game", searchText.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter search terms.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /** Called when the activity will start interacting with the user. */
    @Override
    protected void onResume() {
        super.onResume();

        String recentSearches = prefs.getString("recent_searches", "");
        String[] split = recentSearches.split(" --- ");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line_faqr_blacktext, split);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchText.setThreshold(1);
        searchText.setAdapter(adapter);

        searchText.requestFocus();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.activity_help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, MyFaqsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            Intent intent = new Intent(this, MyFaqsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_SEARCH)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
