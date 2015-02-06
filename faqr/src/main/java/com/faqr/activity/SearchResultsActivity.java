/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.faqr.FaqrApp;
import com.faqr.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * This Activity provides a help screen for the app
 * 
 * @author eneve
 */
public class SearchResultsActivity extends BaseActivity {

    private StickyListHeadersListView listView;
    private SearchResultsListAdapter adapter;

    // private ArrayList adapterData = new ArrayList();

    private ArrayList data = new ArrayList();
    private ArrayList allData = new ArrayList();
    private ArrayList titles = new ArrayList();

    private LinearLayout loading;
    private LinearLayout noResults;

    private Bundle extras;

    private String game = "";
    private String url = "";

    private Integer myFaqsScrollPos;

    private SearchView searchView;

    // *** THIS IS THE ACTIVE URL AND THE ONE THE APP WILL USE ***

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        // theme goodness
        toolbar.getRootView().setBackgroundColor(themeBackgroundColor);
        if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                // setTheme(R.style.AppBlackOverlayTheme);

            }
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
//                setTheme(R.style.AppBlackOverlayTheme);
            }


        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
//                setTheme(R.style.AppDarkOverlayTheme);
            }
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
            // RelativeLayout bg = (RelativeLayout) findViewById(R.id.bg);
            // bg.setBackgroundColor(0xFFECE1CA);
            // themeColor = getResources().getColor(R.color.sepia_theme_color);

        }

        // make sure the keyboard only pops up when a user clicks into an EditText
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // }

        extras = getIntent().getExtras();
        if (extras != null && extras.getString("game") != null && !TextUtils.isEmpty(extras.getString("game"))) {
            game = extras.getString("game");
        }

        String title = FaqrApp.toTitleCase(game);
        setTitle(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setTitle("");
        }

        if (extras != null && extras.getString("url") != null && !TextUtils.isEmpty(extras.getString("url"))) {
            url = extras.getString("url");
        }

        // set the list adapter
        adapter = new SearchResultsListAdapter();
        // setListAdapter(adapter);
        listView = (StickyListHeadersListView) findViewById(R.id.list);
        // listView.setOnItemClickListener(adapter.itemClickListener);
        listView.setAdapter(adapter);
        // listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // String line = lines[position];

                // name
                // TextView nameView = (TextView) view.findViewById(R.id.name);

                String text = allData.get(position).toString();

                String url = "";

                String[] textSplit = text.split("---");
                url = textSplit[textSplit.length - 1].trim();

                if ((textSplit.length == 6) || (textSplit.length == 7)) {

                    String title = textSplit[0].trim();
                    String date = textSplit[1].trim();
                    String author = textSplit[2].trim();
                    String version = textSplit[3].trim();
                    String size = textSplit[4].trim();
                    String href = textSplit[5].trim();

                    // String

                    // String faqsMeta = prefs.getString("downloaded_faqs_meta", "");
                    // if (!TextUtils.isEmpty(faqsMeta))
                    // faqsMeta = faqsMeta + " === ";

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("curr_faq", FaqrApp.validFileName(href));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                    editor.putString(FaqrApp.validFileName(href) + "___last_read", sdf.format(new Date()));

                    Log.i(TAG, FaqrApp.validFileName(href) + " " + title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href);
                    // Toast.makeText(getApplicationContext(), Faqr.validFileName(href) + " " + title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href, Toast.LENGTH_SHORT).show();
                    // we might have it already
                    if (TextUtils.isEmpty(prefs.getString(FaqrApp.validFileName(href), "")))
                        editor.putString(FaqrApp.validFileName(href), title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href);
                    editor.commit();
                    // sectionData.add(title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href);
                }

                String[] split = url.split("/");
                String last = "";
                String secondToLast = "";
                if (split.length > 2) {
                    last = split[split.length - 1];
                    secondToLast = split[split.length - 2];
                }

                // external url
                if (!url.contains(getResources().getString(R.string.GAMEFAQS_URL))) {

                    Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from_search", true);
                    startActivity(intent);

                } else if (secondToLast.equals("faqs") && !TextUtils.isEmpty(last)) {
                    // Log.w(TAG, url);
                    // Log.w(TAG, last);
                    // Log.w(TAG, secondToLast);

                    Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from_search", true);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from_search_results", true);
                    intent.putExtra("game", game);
                    intent.putExtra("url", url);
                    startActivity(intent);
//                    finish();
                }

            }
        });

        // loading indicator
        loading = (LinearLayout) findViewById(R.id.loading);
        noResults = (LinearLayout) findViewById(R.id.no_results);

        // check connectivity
        if (!isNetworkAvailable()) {
            // connectionDialog.show();
            Toast.makeText(getApplicationContext(), "There is no internet connection available, Please connect to wifi or data plan and try again.", Toast.LENGTH_LONG).show();
            loading.setVisibility(View.GONE);
        } else {
            // execute the task in another thread
            new SearchTask().execute(new String[] {});
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // if (this.getCurrentFocus() != null)
        // this.getCurrentFocus().clearFocus();
        //
        // if (searchView != null)
        // searchView.clearFocus();
    }

    /** Called when the activity will start interacting with the user. */
    @Override
    protected void onResume() {
        super.onResume();

        // prevent the searchView from getting focus
        listView.requestFocus();

//        if (this.getCurrentFocus() != null)
//            this.getCurrentFocus().clearFocus();
//
//        if (searchView != null)
//            searchView.clearFocus();
    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // back if we came from search
            // if (extras != null && extras.getBoolean("from_search") == true) {
            // finish();
            // } else {
            // quitDialog.show();
            // }
            // return true;

            if (extras == null || (extras != null && extras.getBoolean("from_search_results") != true)) {
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Intent intent = new Intent(this, MyFaqsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // }
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        // EasyTracker.getInstance(this).activityStart(this); // Add this method.

        // Get tracker.
        Tracker t = ((FaqrApp) getApplication()).getTracker();
        // Set screen name.
        t.setScreenName(getClass().getName());
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        if (searchView != null)
            searchView.clearFocus();
    }

    @Override
    public void onStop() {
        super.onStop();
//        EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_search_results, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconifiedByDefault(false);
        // searchItem.expandActionView();
        LinearLayout searchText = (LinearLayout) searchView.findViewById(R.id.search_plate);
        LinearLayout searchTextFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        // searchText.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // searchTextFrame.setBackgroundDrawable(getResources().getDrawable(R.drawable.textfield_activated_holo_dark));

        int pL = searchText.getPaddingLeft();
        int pT = searchText.getPaddingTop();
        int pR = searchText.getPaddingRight();
        int pB = searchText.getPaddingBottom();
        searchText.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        searchText.setPadding(pL, pT, pR, pB);

        pL = searchTextFrame.getPaddingLeft();
        pT = searchTextFrame.getPaddingTop();
        pR = searchTextFrame.getPaddingRight();
        pB = searchTextFrame.getPaddingBottom();
        searchTextFrame.setBackgroundDrawable(getResources().getDrawable(R.drawable.textfield_activated_holo_dark));
        searchTextFrame.setPadding(pL, pT, pR, pB);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // close the search view
                // Toast.makeText(getApplicationContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();

                if (null != query && !query.equals("")) {

                    // saved searches
                    String recentSearches = prefs.getString("recent_searches", "");
                    String[] split = recentSearches.split(" --- ");
                    final List<String> list = new ArrayList<String>();
                    Collections.addAll(list, split);
                    list.remove(query.trim());
                    String newRecentSearches = "";
                    newRecentSearches += query.trim();
                    for (int i = 0; i < list.size(); i++) {
                        newRecentSearches += " --- " + list.get(i);
                        if (i > 18)
                            break;
                    }
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("recent_searches", newRecentSearches);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra("game", query.trim());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter search terms.", Toast.LENGTH_SHORT).show();
                }

                // Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                // intent.putExtra("game", searchView.getQuery().toString());
                // startActivity(intent);

                return true;
            }
        });

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        // searchView.findViewById(abs__search_src_text)
        searchAutoComplete.setThreshold(0);
        String recentSearches = prefs.getString("recent_searches", "");
        String[] split = recentSearches.split(" --- ");
        final List<String> list = new ArrayList<String>();
        Collections.addAll(list, split);
        list.remove("");
//        if (split.length == 1 && split[0].equals("")) {
//            split = new String[] {};
//        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line_faqr, list);
        searchAutoComplete.setAdapter(adapter);

        searchAutoComplete.setText(game);

        searchAutoComplete.setOnItemClickListener(new OnItemClickListener() {

            /**
             * Implements OnItemClickListener
             */
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // if (DBG)
                // Log.w(TAG, "onItemClick() position " + position);
                // onItemClicked(position, KeyEvent.KEYCODE_UNKNOWN, null);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                searchAutoComplete.setText(textView.getText());

                // saved searches
                String recentSearches = prefs.getString("recent_searches", "");
                String[] split = recentSearches.split(" --- ");
                final List<String> list = new ArrayList<String>();
                Collections.addAll(list, split);
                list.remove(searchView.getQuery().toString().trim());
                String newRecentSearches = "";
                newRecentSearches += searchView.getQuery().toString().trim();
                for (int i = 0; i < list.size(); i++) {
                    newRecentSearches += " --- " + list.get(i);
                    if (i > 18)
                        break;
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("recent_searches", newRecentSearches);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("game", textView.getText());
                startActivity(intent);
                finish();
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // find = true;
                Log.i(TAG, "onMenuItemActionExpand " + item.getItemId());
                return true;
            }
        });

        // searchView.setSuggestionsAdapter(new FaqSearchAdapter(this, searchManager.getSearchableInfo(getComponentName()), searchView));

        // }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_settings:
            Intent intent;
            intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("fromActivity", "SearchResults");
            intent.putExtra("fromActivityMeta", game);
            startActivity(intent);
            finish();
            return true;
        case R.id.menu_about:

            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        case android.R.id.home:
            // finish();

            if (extras == null || (extras != null && extras.getBoolean("from_search_results") != true)) {
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                intent = new Intent(this, MyFaqsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // }
            }
            finish();

            return true;
        default:
            return false;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIST ADAPTER

    public class SearchResultsListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        @Override
        public int getCount() {
            return allData.size();
        }

        @Override
        public Object getItem(int position) {
            return allData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (extras != null && extras.getString("url") != null && !TextUtils.isEmpty(extras.getString("url"))) {
                view = inflater.inflate(R.layout.search_result_item_2, parent, false);
            } else {
                view = inflater.inflate(R.layout.search_result_item, parent, false);
            }

            // String line = lines[position];
            //

            String item = (String) allData.get(position);

            if (item.split(" --- ").length == 3) {

                // name
                TextView nameView = (TextView) view.findViewById(R.id.name);

                // platform
                TextView platformView = (TextView) view.findViewById(R.id.platform);

                platformView.setText(item.split(" --- ")[0]);
                nameView.setText(item.split(" --- ")[1]);

                // theme goodness
                nameView.setTextColor(themeColor);
                platformView.setTextColor(themeTextColor);

            } else if (item.split(" --- ").length == 6) {

                // name
                TextView nameView = (TextView) view.findViewById(R.id.name);

                // platform
                // TextView dateView = (TextView) view.findViewById(R.id.date);
                TextView authorView = (TextView) view.findViewById(R.id.author);
                TextView versionView = (TextView) view.findViewById(R.id.version);
                TextView sizeView = (TextView) view.findViewById(R.id.size);

                String dateFix = item.split(" --- ")[1];
                if (dateFix.startsWith("0"))
                    dateFix = dateFix.substring(1);

                // boolean isVersion = Fqqr.isVersionString(item.split(" --- ")[3]);
                String versionAndSize = "v" + item.split(" --- ")[3] + "/" + item.split(" --- ")[4].replaceAll("K", "k");
                if (item.split(" --- ")[3].trim().equals("") && !item.split(" --- ")[3].trim().equalsIgnoreCase("Final"))
                    versionAndSize = item.split(" --- ")[4].replaceAll("K", "k");

                nameView.setText(item.split(" --- ")[0]);
                // dateView.setText(item.split(" --- ")[1]);
                authorView.setText(item.split(" --- ")[2]);
                // if (!item.split(" --- ")[3].isEmpty() && !item.split(" --- ")[3].startsWith("v"))
                versionView.setText(dateFix);
                sizeView.setText(versionAndSize);

                // data.add(title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href);

                // theme goodness
                nameView.setTextColor(themeColor);
                authorView.setTextColor(themeTextColor);
                versionView.setTextColor(themeTextColor);
                sizeView.setTextColor(themeTextColor);


            } else if (item.split(" --- ").length == 7) {
                // IT HAS 7 IF IT HAS AN IMAGE LIKE STAR, CIRCLE, HALFCIRCLE ETC.

                // name
                TextView nameView = (TextView) view.findViewById(R.id.name);

                // platform
                // TextView dateView = (TextView) view.findViewById(R.id.date);
                TextView authorView = (TextView) view.findViewById(R.id.author);
                TextView versionView = (TextView) view.findViewById(R.id.version);
                TextView sizeView = (TextView) view.findViewById(R.id.size);

                // the unicode image
                String star = "\u2605 ";
                String circle = "\u25CF ";
                String halfCircle = "\u25D2 ";
                // unicode image urls
                String starSrc = "http://img.gamefaqs.net/images/default/rec.gif";
                String circleSrc = "http://img.gamefaqs.net/images/default/s3.gif";
                String halfCircleSrc = "http://img.gamefaqs.net/images/default/s2.gif";

                String marker = "";
                String imgSrc = item.split(" --- ")[6];
                if (imgSrc.equals(starSrc)) {
                    marker = star;
                }
                // else if (imgSrc.equals(circleSrc)) {
                // marker = circle;
                // } else if (imgSrc.equals(halfCircleSrc)) {
                // marker = halfCircle;
                // }

                nameView.setText(marker + item.split(" --- ")[0]);
                // dateView.setText(item.split(" --- ")[1]);
                authorView.setText(item.split(" --- ")[2]);
                // if (!item.split(" --- ")[3].isEmpty() && !item.split(" --- ")[3].startsWith("v"))
                versionView.setText(item.split(" --- ")[3]);
                sizeView.setText(item.split(" --- ")[4]);

                // data.add(title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href);


                // theme goodness
                nameView.setTextColor(themeColor);
                authorView.setTextColor(themeTextColor);
                versionView.setTextColor(themeTextColor);
                sizeView.setTextColor(themeTextColor);

            }



            return view;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.search_result_header, null);
            TextView textView = (TextView) view.findViewById(R.id.name);

            int count = 0;
            int sectionCount = 0;
            for (int i = 0; i < allData.size(); i++) {
                count += ((ArrayList) data.get(i)).size();

                if (position < count) {
                    break;
                }

                sectionCount += 1;
            }

            textView.setText(titles.get(sectionCount).toString());

            view.setBackgroundColor(primaryColor);

            // if (currentBookings.size() == 0) {
            // text.setText("Past Reservations");
            // } else if (pastBookings.size() == 0) {
            // text.setText("Current Reservations");
            // } else {
            // if (section == 0)
            // text.setText("Current Reservations");
            // else
            // text.setText("Past Reservations");
            // }
            return view;
        }

        @Override
        public long getHeaderId(int position) {
            int count = 0;
            int sectionCount = 0;
            for (int i = 0; i < allData.size(); i++) {
                count += ((ArrayList) data.get(i)).size();

                if (position < count) {
                    break;
                }

                sectionCount += 1;
            }

            return sectionCount;
        }

    }



    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TASKS

    /**
     * Save the current position
     * 
     * @author eneve
     */
    private class SearchTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            noResults.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = "0";

            try {

                Log.w(TAG, "===============================================");
                Log.w(TAG, "FETCHING FROM WEB " + url);

                String gameParam = game.replace(" ", "\\+");

                Document doc;
                if (extras != null && extras.getString("url") != null && !TextUtils.isEmpty(extras.getString("url"))) {
                    // String userAgent = new WebView(getApplicationContext()).getSettings().getUserAgentString();
                    // Document doc = Jsoup.connect(url).userAgent(userAgent).referrer("http://www.google.com").timeout(10000).get();
                    doc = Jsoup.connect(url).timeout(10000).get();
                } else {
                    // String userAgent = new WebView(getApplicationContext()).getSettings().getUserAgentString();
                    // Document doc = Jsoup.connect(SEARCH_URL + gameParam).userAgent(userAgent).referrer("http://www.google.com").timeout(10000).get();
                    doc = Jsoup.connect(getResources().getString(R.string.SEARCH_URL) + gameParam).timeout(10000).get();
                }

                String[] split = url.split("/");
                String last = split[split.length - 1];
                // String secondToLast = split[split.length - 2];

                if (last.equals("faqs")) {
                    // ///////////////////////////
                    // SECOND SEARCH RESULTS PAGE
                    int stop = 0;
                    Element mainElem = doc.select("#content").get(0);
                    for (Element innerElem : mainElem.children()) {
                        String tagName = innerElem.tagName();
                        if (innerElem.tagName().equals("div") && innerElem.className().equals("pod")) {
                            stop++;
                        }
                        if (innerElem.tagName().equals("h2")) {
                            break;
                        }
                    }

                    int podNumber = 1;
                    Elements podElems = doc.select(".pod");
                    for (Element podElem : podElems) {
                        String sectionTitle = podElem.select(".title").text().trim();
                        ArrayList sectionData = new ArrayList();
                        Elements rowElems = podElem.select("tr");
                        for (Element rowElem : rowElems) {
                            int count = 0;
                            String img = "";
                            String href = "";
                            String title = "";
                            String date = "";
                            String author = "";
                            String version = "";
                            String size = "";
                            Elements tdElems = rowElem.select("td");
                            for (Element tdElem : tdElems) {
                                if (sectionTitle.equals("Video Walkthroughs")) {
                                    if (count == 0) {
                                        href = tdElem.select("a").attr("href");
                                        if (href.startsWith("/"))
                                            href = getResources().getString(R.string.GAMEFAQS_URL) + href;
                                    } else if (count == 1) {
                                        title = tdElem.select(".faqtitle").text();
                                        title += " by " + tdElem.select(".faqauthor").text();
                                        author = tdElem.select(".faqinfo").text();
                                        ;
                                    }
                                } else {
                                    if (count == 0) {
                                        title = tdElem.text();
                                        Elements imgs = tdElem.select("img");
                                        if (imgs.size() == 1 && imgs.get(0).attr("alt").equals("(HTML)")) {
                                            title += "  <HTML>";
                                        } else if (imgs.size() == 2 && imgs.get(1).attr("alt").equals("(HTML)")) {
                                            title += "  <HTML>";
                                        }
                                        img = tdElem.select("img").attr("src");
                                        href = tdElem.select("a").attr("href");
                                        if (href.startsWith("/"))
                                            href = getResources().getString(R.string.GAMEFAQS_URL) + href;
                                    } else if (count == 1) {
                                        date = tdElem.text();
                                    } else if (count == 2) {
                                        author = tdElem.select("a").text();
                                    } else if (count == 3) {
                                        version = tdElem.text();
                                    } else if (count == 4) {
                                        size = tdElem.text();
                                    }
                                }
                                count++;
                            }

                            // <td>
                            // <span class="faqtitle"><a href="/pc/615805-the-elder-scrolls-v-skyrim/videofaq/gf_video-26/part-1">Video Walkthrough</a></span> by <span class="faqauthor"><a
                            // href="/users/horror_spooky/contributions">horror_spooky</a></span>
                            // <br>
                            // <span class="faqinfo">GameFAQs Video, Updated 3/25/13, 6 parts, 1h15m34s</span></td>

                            if (!TextUtils.isEmpty(href)) {
                                sectionData.add(title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href + " --- " + img);
                                // adapterData.add(title + " --- " + date + " --- " + author + " --- " + version + " --- " + size + " --- " + href);
                            }

                        }
                        if (sectionData.size() > 0 && !sectionTitle.equalsIgnoreCase("Log In to GameFAQs")) {
                            titles.add(sectionTitle);
                            data.add(sectionData);
                            allData.addAll(sectionData);
                        }
                        if (podNumber == stop) {
                            break;
                        }
                        podNumber++;
                    }

                } else {
                    // //////////////////////////
                    // FIRST SEARCH RESULTS PAGE
                    Elements podElems = doc.select(".pod");
                    for (Element podElem : podElems) {
                        String sectionTitle = podElem.select(".title").text();
                        ArrayList sectionData = new ArrayList();
                        Elements rowElems = podElem.select("tr");
                        for (Element rowElem : rowElems) {
                            int count = 0;
                            String platform = "";
                            String title = "";
                            String href = "";
                            Elements tdElems = rowElem.select("td");
                            for (Element tdElem : tdElems) {
                                if (count == 0) {
                                    platform = tdElem.text();
                                } else if (count == 1) {
                                    title = tdElem.select("a").get(0).text();
                                } else if (count == 2) {
                                    href = getResources().getString(R.string.GAMEFAQS_URL) + tdElem.select("a").attr("href");
                                }
                                count++;
                            }
                            if (!TextUtils.isEmpty(href)) {
                                sectionData.add(FaqrApp.getConsoleFullName(platform) + " --- " + title + " --- " + href);
                                // adapterData.add(platform + " --- " + title + " --- " + href);
                            }
                        }
                        if (sectionData.size() > 0) {
                            titles.add(sectionTitle);
                            data.add(sectionData);
                            allData.addAll(sectionData);
                        }
                    }

                }

                // Log.w(TAG, data.toString());

            } catch (Exception e) {

                e.printStackTrace();
                result = "-1";
            }

            return result;
        }

        protected void onPostExecute(String result) {

            if (result.equals("-1")) {
                Toast.makeText(getApplicationContext(), "Sorry an error occured. Please try again.", Toast.LENGTH_SHORT).show();
            } else {

                if (data.size() == 0) {
                    // Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_SHORT).show();

                    noResults.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);

                    // Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim);
                    // Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_anim);
                    // fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    // @Override
                    // public void onAnimationStart(Animation animation) {
                    //
                    // }
                    //
                    // @Override
                    // public void onAnimationEnd(Animation animation) {
                    // loading.setVisibility(View.GONE);
                    // }
                    //
                    // @Override
                    // public void onAnimationRepeat(Animation animation) {
                    //
                    // }
                    // });
                    // // Now Set your animation
                    // noResults.startAnimation(fadeInAnimation);
                    //
                    // loading.startAnimation(fadeOutAnimation);

                } else {

                    // fancy animations
                    listView.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);

                    Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim);
                    Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_anim);
                    fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    // Now Set your animation
                    listView.startAnimation(fadeInAnimation);

                    loading.startAnimation(fadeOutAnimation);

                    // loading.setVisibility(View.GONE);
                    // listView.setVisibility(View.VISIBLE);
                }

                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            }

        }

    };

}
