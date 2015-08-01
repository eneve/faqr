package com.faqr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.faqr.R;
import com.faqr.fragment.PreferencesFragment;

/**
 * Created by stephen on 7/27/15.
 */
public class PreferencesActivity extends BaseActivity {

    private Bundle extras;

    private String fromActivity = "";
    private String fromActivityMeta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        extras = getIntent().getExtras();
        if (extras != null && extras.getString("fromActivity") != null && !TextUtils.isEmpty(extras.getString("fromActivity"))) {
            fromActivity = extras.getString("fromActivity");
            fromActivityMeta = extras.getString("fromActivityMeta");
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PreferencesFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, FaqActivity.class);
                if (fromActivity.equalsIgnoreCase("My FAQs")) {
                    intent = new Intent(this, FaqsActivity.class);
                }
                if (fromActivity.equalsIgnoreCase("My FAQmarks")) {
                    intent = new Intent(this, FaqmarksActivity.class);
                }
                if (fromActivity.equalsIgnoreCase("SearchResults")) {
                    intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("game", fromActivityMeta);
                }

                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }
}
