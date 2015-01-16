/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import com.faqr.Faqr;
import com.faqr.R;
import com.faqr.activity.base.BaseActivity;
import com.faqr.adapter.SectionListAdapter.IndexPath;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * This Activity provides a list of the saved FAQs
 * 
 * @author eneve
 */
public class MyFaqsActivity extends BaseActivity {

    // list view
    private MyFaqsListAdapter adapter;
    private List<File> allData = new ArrayList<File>();

    // section list view
    private StickyListHeadersListView listView;
    // private MyFaqsSectionListAdapter sectionAdapter;
    private List data = new ArrayList();
    private List titles = new ArrayList();

    private LinearLayout loading;
    private LinearLayout noResults;

    private Menu menu;

    private SearchView searchView;

    // dialogs
    protected AlertDialog faqDialog;
    protected AlertDialog deleteConfirmDialog;
    protected AlertDialog deleteAllConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_faqs);

        ActionBar actionBar = getSupportActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);

        // theme goodness
        if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                // setTheme(R.style.AppBlackOverlayTheme);
            }
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                setTheme(R.style.AppBlackOverlayTheme);
            }
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                setTheme(R.style.AppDarkOverlayTheme);
            }
        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
            // RelativeLayout bg = (RelativeLayout) findViewById(R.id.bg);
            // bg.setBackgroundColor(0xFFECE1CA);
            // themeColor = getResources().getColor(R.color.sepia_theme_color);

        }

        // set the list adapter
        adapter = new MyFaqsListAdapter();
        // setListAdapter(adapter);

        // sectionAdapter = new MyFaqsSectionListAdapter();
        // setListAdapter(sectionAdapter);
        listView = (StickyListHeadersListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        // extras = getIntent().getExtras();
        // if (extras != null) {
        // Integer myFaqsScrollPos = extras.getInt("MyFaqsScrollPosition", 0);
        // int my_faqs_pos = prefs.getInt("my_faqs_pos", 0);
        // listView.setSelection(my_faqs_pos);
        // }

        // loading indicator
        loading = (LinearLayout) findViewById(R.id.loading);

        // no results
        noResults = (LinearLayout) findViewById(R.id.no_results);
        noResults.setVisibility(View.GONE);

        /** called when a list item is clicked */
        // listView.setOnItemClickListener(sectionAdapter.itemClickListener);
        // listView.setOnLongClickListener(sectionAdapter.i)

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // final File file = (File) adapterData.get(position);
                // final String faqsMeta = prefs.getString(file.getName(), "");
                // final String faqsMetaLastRead = prefs.getString(file.getName() + "___last_read", "");
                // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                //
                // SharedPreferences.Editor editor = prefs.edit();
                // editor.putString("curr_faq", file.getName());
                // editor.putString(file.getName() + "___last_read", sdf.format(new Date()));
                // editor.commit();
                // Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
                // // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // startActivity(intent);
                // // finish();

                final File file = (File) allData.get(position);
                final String faqsMeta = prefs.getString(file.getName(), "");
                final String faqsMetaLastRead = prefs.getString(file.getName() + "___last_read", "");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("curr_faq", file.getName());
                editor.putString(file.getName() + "___last_read", sdf.format(new Date()));
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), FaqActivity.class);

                editor = prefs.edit();
                editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
                editor.commit();

                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        /** called when a list item is long clicked */
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final TextView metaView = (TextView) view.findViewById(R.id.meta);

                // final File file = (File) adapterData.get(position);
                final String faqsMeta = prefs.getString(metaView.getText().toString(), "");

                // delete dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyFaqsActivity.this);
                dialogBuilder.setMessage("Are you sure you want to delete " + faqsMeta.split(" --- ")[6] + "?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = prefs.edit();

                        File file = null;
                        File[] files = Faqr.getFaqrFiles(getFilesDir().listFiles());
                        for (int i = 0; i < files.length; i++) {
                            File f = files[i];
                            if (f.getName().equals(metaView.getText().toString())) {
                                file = f;
                            }
                        }

                        // File file = (File) adapterData.get(position);

                        String currFaq = prefs.getString("curr_faq", "");

                        // more than 1 faq
                        if (files.length > 1) {
                            // if deleting the curr faq
                            if (currFaq.equalsIgnoreCase(file.getName())) {
                                if (allData.size() == 1) {
                                    editor.putString("curr_faq", "");
                                } else if (position == 0) {
                                    File nextFile = (File) allData.get(1);
                                    editor.putString("curr_faq", nextFile.getName());
                                } else {
                                    File zeroFile = (File) allData.get(0);
                                    editor.putString("curr_faq", zeroFile.getName());
                                }
                            }
                            editor.remove(file.getName());
                            editor.remove(file.getName() + "curr_pos");
                            editor.remove(file.getName() + "saved_pos");
                            editor.commit();
                            file.delete();
                            Intent intent = new Intent(getApplicationContext(), MyFaqsActivity.class);

                            editor = prefs.edit();
                            editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
                            editor.commit();

                            // intent.putExtra("MyFaqsScrollPosition", listView.getFirstVisiblePosition());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            editor.remove("curr_faq");
                            editor.remove(file.getName());
                            editor.remove(file.getName() + "curr_pos");
                            editor.remove(file.getName() + "saved_pos");
                            editor.commit();
                            file.delete();
                            // we deleted the only faq
                            Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
                            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                faqDialog = dialogBuilder.create();
                faqDialog.show();

                return true;
            }
        });

        // delete all confirm dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyFaqsActivity.this);
        dialogBuilder.setMessage("Are you sure you want to delete all of your saved FAQs.").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // start the delete all task
                new DeleteAllTask().execute(new String[] {});
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        deleteAllConfirmDialog = dialogBuilder.create();
        // deleteAllConfirmDialog.setTitle("Delete All FAQs?");

        // init the faq list view
        new InitTask().execute(new String[] {});
    }

    /** Called when the activity will start interacting with the user. */
    @Override
    protected void onResume() {
        super.onResume();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getSupportMenuInflater().inflate(R.menu.activity_my_faqs, menu);
        // MenuItem dateItem = menu.findItem(R.id.menu_sort_date);
        // dateItem.setTitle("\u2605 Sort By Date Added");

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        // final MenuItem searchItemOld = menu.findItem(R.id.menu_search_old);

        // Get the SearchView and set the searchable configuration
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconifiedByDefault(true);
        LinearLayout searchText = (LinearLayout) searchView.findViewById(R.id.abs__search_plate);
        // searchText.setBackgroundDrawable(getResources().getDrawable(R.drawable.textfield_activated_holo_dark));

        int pL = searchText.getPaddingLeft();
        int pT = searchText.getPaddingTop();
        int pR = searchText.getPaddingRight();
        int pB = searchText.getPaddingBottom();
        searchText.setBackgroundDrawable(getResources().getDrawable(R.drawable.textfield_activated_holo_dark));
        searchText.setPadding(pL, pT, pR, pB);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                // find_text_changed(s);
                // new FindTextChangedTask().execute(new String[] { s });
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // close the search view
                // Toast.makeText(getApplicationContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                // finalMenu.findItem(R.id.menu_search).collapseActionView();

                searchItem.collapseActionView();

                if (null != searchView.getQuery().toString() && !searchView.getQuery().toString().equals("")) {

                    // save search
                    // String newRecentSearches = "";
                    // String recentSearches = prefs.getString("recent_searches", "");
                    // if (!recentSearches.contains(searchView.getQuery().toString())) {
                    // newRecentSearches += searchView.getQuery().toString();
                    // String[] split = recentSearches.split(" --- ");
                    // for (int i = 0; i < split.length; i++) {
                    // newRecentSearches += " --- " + split[i];
                    // if (i > 18)
                    // break;
                    // }
                    // SharedPreferences.Editor editor = prefs.edit();
                    // editor.putString("recent_searches", newRecentSearches);
                    // editor.commit();
                    // }

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

                    if (!isNetworkAvailable()) {
                        connectionDialog.show();
                    } else {

                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                        intent.putExtra("game", searchView.getQuery().toString());

                        editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
                        editor.commit();

                        // intent.putExtra("MyFaqsScrollPosition", listView.getFirstVisiblePosition());
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter search terms.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        // Android < 3.0 enter key
        // searchView.setOnKeyListener(new OnKeyListener() {
        // public boolean onKey(View v, int keyCode, KeyEvent event) {
        // if (event.getAction() == KeyEvent.ACTION_DOWN) {
        // switch (keyCode) {
        // case KeyEvent.KEYCODE_DPAD_CENTER:
        // case KeyEvent.KEYCODE_ENTER:
        // Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        // intent.putExtra("game", searchView.getQuery().toString());
        // startActivity(intent);
        // finish();
        //
        // return true;
        // default:
        // break;
        // }
        // }
        // return false;
        // }
        // });

        final SearchAutoComplete searchAutoComplete = (SearchAutoComplete) searchView.findViewById(R.id.abs__search_src_text);
        // searchView.findViewById(abs__search_src_text)
        searchAutoComplete.setThreshold(0);
        String recentSearches = prefs.getString("recent_searches", "");
        String[] split = recentSearches.split(" --- ");
        if (split.length == 1 && split[0].equals("")) {
            split = new String[] {};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line_faqr, split);
        searchAutoComplete.setAdapter(adapter);

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

                // String recentSearches = prefs.getString("recent_searches", "");
                // String[] split = recentSearches.split(" --- ");

                searchItem.collapseActionView();

                if (!isNetworkAvailable()) {
                    connectionDialog.show();
                } else {

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
                    intent.putExtra("game", textView.getText());
                    editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
                    editor.commit();
                    startActivity(intent);
                    finish();
                }
            }
        });

        searchItem.setOnActionExpandListener(new OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Log.i(TAG, "onMenuItemActionCollapse " + item.getItemId());
                // MenuItem prev = finalMenu.findItem(R.id.menu_prev);
                // prev.setVisible(false);
                // MenuItem next = finalMenu.findItem(R.id.menu_next);
                // next.setVisible(false);
                // MenuItem opt = finalMenu.findItem(R.id.menu_downloads);
                // opt.setVisible(true);
                // MenuItem opt = finalMenu.findItem(R.id.menu_search);
                // opt.setVisible(true);
                // MenuItem opt = finalMenu.findItem(R.id.menu_goto);
                // opt.setVisible(true);
                // opt = finalMenu.findItem(R.id.menu_lock);
                // opt.setVisible(true);
                // opt = finalMenu.findItem(R.id.menu_settings);
                // opt.setVisible(true);
                // opt = finalMenu.findItem(R.id.menu_about);
                // opt.setVisible(true);

                // find = false;
                // currFindPos = 0;

                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // find = true;
                // Log.i(TAG, "onMenuItemActionExpand " + item.getItemId());
                return true;
            }
        });

        // searchView.setSuggestionsAdapter(new FaqSearchAdapter(this, searchManager.getSearchableInfo(getComponentName()), searchView));

        // }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // MenuItem alphaItem = menu.findItem(R.id.menu_sort_alpha);
        // MenuItem dateItem = menu.findItem(R.id.menu_sort_date);
        // MenuItem sizeItem = menu.findItem(R.id.menu_sort_size);
        Intent intent;
        switch (item.getItemId()) {
        // case R.id.menu_search_old: // check connectivity
        // if (!isNetworkAvailable()) {
        // connectionDialog.show();
        // // Toast.makeText(getApplicationContext(),
        // // "There is no internet connection available, Please connect to wifi or data plan and try again.",
        // // Toast.LENGTH_LONG).show();
        // } else {
        // // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        // // intent = new Intent(this, SearchActivity.class);
        // // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // //
        // // SharedPreferences.Editor editor = prefs.edit();
        // // editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
        // // editor.commit();
        // //
        // // startActivity(intent);
        // // finish();
        // // }
        // }
        // return true;
        case R.id.menu_search: // check connectivity
            if (!isNetworkAvailable()) {
                // connectionDialog.show();
                // Toast.makeText(getApplicationContext(),
                // "There is no internet connection available, Please connect to wifi or data plan and try again.",
                // Toast.LENGTH_LONG).show();
            } else {
                // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                // intent = new Intent(this, SearchActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // startActivity(intent);
                // finish();
                // }
            }
            return true;
        case R.id.menu_delete:
            deleteAllConfirmDialog.show();
            return true;
            // case R.id.menu_sort:
            // AlertDialog.Builder builder = new Builder(this);
            // // builder.setTitle("Sort FAQs");
            // String[] types = { "By Last Read", "By Title", "By Date Added", "By Size" };
            // Integer currSort = Integer.valueOf(prefs.getString("curr_my_faqs_sort", "1"));
            // types[currSort] = "\u2605 " + types[currSort];
            // builder.setItems(types, new OnClickListener() {
            // @Override
            // public void onClick(DialogInterface dialog, int which) {
            // SharedPreferences.Editor editor = prefs.edit();
            // dialog.dismiss();
            // switch (which) {
            // case 0:
            // // Collections.sort(adapterData, new MyLastComparable());
            // // adapter.notifyDataSetChanged();
            // // sectionAdapter.notifyDataSetChanged();
            // //
            // // editor.putInt("my_faqs_pos", 0);
            // // editor.commit();
            // // new InitTask().execute(new String[] {});
            //
            // Intent intent = new Intent(getApplicationContext(), MyFaqsActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //
            // editor = prefs.edit();
            // editor.putString("curr_my_faqs_sort", "0");
            // editor.putInt("my_faqs_pos", 0);
            // editor.commit();
            //
            // startActivity(intent);
            // finish();
            //
            // break;
            // case 1:
            // // Collections.sort(adapterData, new MyAlphaComparable());
            // // adapter.notifyDataSetChanged();
            // // sectionAdapter.notifyDataSetChanged();
            // // editor.putString("curr_my_faqs_sort", "1");
            // // editor.putInt("my_faqs_pos", 0);
            // // editor.commit();
            // // new InitTask().execute(new String[] {});
            //
            // intent = new Intent(getApplicationContext(), MyFaqsActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //
            // editor = prefs.edit();
            // editor.putString("curr_my_faqs_sort", "1");
            // editor.putInt("my_faqs_pos", 0);
            // editor.commit();
            //
            // startActivity(intent);
            // finish();
            //
            // break;
            // case 2:
            // // Collections.sort(adapterData, new MyDateComparable());
            // // adapter.notifyDataSetChanged();
            // // sectionAdapter.notifyDataSetChanged();
            // // editor.putString("curr_my_faqs_sort", "2");
            // // editor.putInt("my_faqs_pos", 0);
            // // editor.commit();
            // // new InitTask().execute(new String[] {});
            //
            // intent = new Intent(getApplicationContext(), MyFaqsActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //
            // editor = prefs.edit();
            // editor.putString("curr_my_faqs_sort", "2");
            // editor.putInt("my_faqs_pos", 0);
            // editor.commit();
            //
            // startActivity(intent);
            // finish();
            //
            // break;
            // case 3:
            // // Collections.sort(adapterData, new MySizeComparable());
            // // adapter.notifyDataSetChanged();
            // // sectionAdapter.notifyDataSetChanged();
            // // editor.putString("curr_my_faqs_sort", "3");
            // // editor.putInt("my_faqs_pos", 0);
            // // editor.commit();
            // // new InitTask().execute(new String[] {});
            //
            // intent = new Intent(getApplicationContext(), MyFaqsActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //
            // editor = prefs.edit();
            // editor.putString("curr_my_faqs_sort", "3");
            // editor.putInt("my_faqs_pos", 0);
            // editor.commit();
            //
            // startActivity(intent);
            // finish();
            //
            // break;
            // }
            // }
            // });
            //
            // builder.show();
            // return true;

            // case R.id.menu_sort_date:
            // Collections.sort(adapterData, new MyDateComparable());
            // adapter.notifyDataSetChanged();
            // alphaItem.setTitle("Sort By Title");
            // dateItem.setTitle("\u2605 Sort By Date Added");
            // sizeItem.setTitle("Sort By Size");
            // return true;
            // case R.id.menu_sort_size:
            // Collections.sort(adapterData, new MySizeComparable());
            // adapter.notifyDataSetChanged();
            // alphaItem.setTitle("Sort By Title");
            // dateItem.setTitle("Sort By Date Added");
            // sizeItem.setTitle("\u2605 Sort By Size");
            // return true;
        case R.id.menu_settings:
            intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("fromActivity", "My FAQs");

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
            editor.commit();

            startActivity(intent);
            finish();
            return true;
        case R.id.menu_about:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        case android.R.id.home:
            // intent = new Intent(this, FaqActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // startActivity(intent);
            // finish();
            return true;
        default:
            return false;
        }
    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        // Intent searchIntent = new Intent(this, FaqActivity.class);
        // searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // startActivity(searchIntent);
        // finish();
        // return true;
        // }
        if ((keyCode == KeyEvent.KEYCODE_SEARCH)) {
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
            searchView = (SearchView) searchMenuItem.getActionView();
            searchMenuItem.expandActionView();
            // } else {
            // Intent searchIntent = new Intent(this, SearchActivity.class);
            // searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // startActivity(searchIntent);
            // finish();
            // return true;
            // }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIST ADAPTER

    public class MyFaqsListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        public MyFaqsListAdapter() {
            // do nothing
        }

        public int getCount() {
            return allData.size();
        }

        public Object getItem(int position) {
            return allData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.search_result_item_2, parent, false);

            File file = (File) allData.get(position);
            String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

            // name
            TextView nameView = (TextView) view.findViewById(R.id.name);
            TextView authorView = (TextView) view.findViewById(R.id.author);
            TextView versionView = (TextView) view.findViewById(R.id.version);
            TextView sizeView = (TextView) view.findViewById(R.id.size);

            // for passing hidden information not shown
            TextView metaView = (TextView) view.findViewById(R.id.meta);
            metaView.setText(file.getName());

            String dateFix = faqsMeta[1];
            if (dateFix.startsWith("0"))
                dateFix = dateFix.substring(1);

            String versionAndSize = "v" + faqsMeta[3] + "/" + faqsMeta[4].replaceAll("K", "k");
            if (faqsMeta[3].trim().equals("") && !faqsMeta[3].equalsIgnoreCase("Final"))
                versionAndSize = faqsMeta[4].replaceAll("K", "k");

            // curr faq gets an indictator
            // if (prefs.getString("curr_faq", "").equalsIgnoreCase(file.getName())) {
            // nameView.setText("\u2605 " + faqsMeta[6].split("\\(")[0].trim());
            // } else {
            nameView.setText(faqsMeta[6].split("\\(")[0].trim());
            // }
            authorView.setText(faqsMeta[0] + " by " + faqsMeta[2]);

            // sort by title
            if (Integer.valueOf(prefs.getString("curr_my_faqs_sort", "1")) == 1) {
                nameView.setText(faqsMeta[0]);
                authorView.setText(faqsMeta[2]);
            }

            // sort by size
            if (Integer.valueOf(prefs.getString("curr_my_faqs_sort", "1")) == 3) {
                versionAndSize = faqsMeta[4].replaceAll("K", "k");
            }

            if (prefs.getString("curr_faq", "").equalsIgnoreCase(file.getName())) {
                nameView.setText("\u2605 " + nameView.getText());
            }

            // if (!item.split(" --- ")[3].isEmpty() && !item.split(" --- ")[3].startsWith("v"))
            versionView.setText(dateFix);
            sizeView.setText(versionAndSize);

            // theme goodness
            nameView.setTextColor(themeColor);

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

            // theme goodness
            view.setBackgroundColor(themeBackgroundColor);

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

    public class MyFaqsSectionListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        public MyFaqsSectionListAdapter() {
            // delegate = this;
        }

        public int sectionCount() {
            return titles.size();
            // return 0;
        }

        public int rowsInSection(int section) {
            return ((ArrayList) data.get(section)).size();

            // return 0;
        }

        public View viewForRowAtIndexPath(IndexPath path, ViewGroup parent) {
            View view = inflater.inflate(R.layout.search_result_item_2, parent, false);

            // File file = (File) adapterData.get(position);

            File file = (File) ((List) data.get(path.section)).get(path.row);

            String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

            // name
            TextView nameView = (TextView) view.findViewById(R.id.name);
            TextView authorView = (TextView) view.findViewById(R.id.author);
            TextView versionView = (TextView) view.findViewById(R.id.version);
            TextView sizeView = (TextView) view.findViewById(R.id.size);

            // for passing hidden information not shown
            TextView metaView = (TextView) view.findViewById(R.id.meta);
            metaView.setText(file.getName());

            String dateFix = faqsMeta[1];
            if (dateFix.startsWith("0"))
                dateFix = dateFix.substring(1);

            String versionAndSize = "v" + faqsMeta[3] + "/" + faqsMeta[4].replaceAll("K", "k");
            if (faqsMeta[3].trim().equals("") && !faqsMeta[3].equalsIgnoreCase("Final"))
                versionAndSize = faqsMeta[4].replaceAll("K", "k");

            // curr faq gets an indictator
            // if (prefs.getString("curr_faq", "").equalsIgnoreCase(file.getName())) {
            // nameView.setText("\u2605 " + faqsMeta[6].split("\\(")[0].trim());
            // } else {
            nameView.setText(faqsMeta[6].split("\\(")[0].trim());
            // }
            authorView.setText(faqsMeta[0] + " by " + faqsMeta[2]);

            // sort by title
            if (Integer.valueOf(prefs.getString("curr_my_faqs_sort", "1")) == 1) {
                nameView.setText(faqsMeta[0]);
                authorView.setText(faqsMeta[2]);
            }

            // sort by size
            if (Integer.valueOf(prefs.getString("curr_my_faqs_sort", "1")) == 3) {
                versionAndSize = faqsMeta[4].replaceAll("K", "k");
            }

            if (prefs.getString("curr_faq", "").equalsIgnoreCase(file.getName())) {
                nameView.setText("\u2605 " + nameView.getText());
            }

            // if (!item.split(" --- ")[3].isEmpty() && !item.split(" --- ")[3].startsWith("v"))
            versionView.setText(dateFix);
            sizeView.setText(versionAndSize);

            // theme goodness
            nameView.setTextColor(themeColor);

            return view;
        }

        public View viewForHeaderInSection(int section) {
            View view = inflater.inflate(R.layout.search_result_header, null);
            TextView textView = (TextView) view.findViewById(R.id.name);
            textView.setText(titles.get(section).toString());

            // theme goodness
            view.setBackgroundColor(themeColor);

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

        public void itemSelectedAtIndexPath(IndexPath path) {
            // String line = lines[position];

            final File file = (File) ((ArrayList) data.get(path.section)).get(path.row);
            final String faqsMeta = prefs.getString(file.getName(), "");
            final String faqsMetaLastRead = prefs.getString(file.getName() + "___last_read", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("curr_faq", file.getName());
            editor.putString(file.getName() + "___last_read", sdf.format(new Date()));
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), FaqActivity.class);

            editor = prefs.edit();
            editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
            editor.commit();

            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getHeaderId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
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
    private class InitTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            File[] files = Faqr.getFaqrFiles(getFilesDir().listFiles());
            allData = Arrays.asList(files);

            // current sorting
            Integer currSort = Integer.valueOf(prefs.getString("curr_my_faqs_sort", "1"));
            switch (currSort) {
            case 0:
                Collections.sort(allData, new MyLastComparable());
                break;
            case 1:
                Collections.sort(allData, new MyAlphaComparable());
                break;
            case 2:
                Collections.sort(allData, new MyDateComparable());
                break;
            case 3:
                Collections.sort(allData, new MySizeComparable());
                break;
            }

            // HARD CIRCUIT THE SORT HERE !!!!
            currSort = 1;
            // section list adapter
            switch (currSort) {
            case 0:

                String[] dateViewedTitles = new String[] { "Most Recently Read" }; // , "Read Past Week", "Read Past Month", "Read Past Year" };
                List[] dateViewedData = new List[] { new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList() };

                for (int i = 0; i < allData.size(); i++) {
                    File file = (File) allData.get(i);
                    String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

                    String faqLastRead1 = prefs.getString(file.getName() + "___last_read", "");
                    Date d = new Date();
                    try {
                        d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse(faqLastRead1);
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }

                    // Calendar cal = Calendar.getInstance();
                    // cal.add(Calendar.DATE, -3);
                    // Date pastDay = cal.getTime();
                    // cal = Calendar.getInstance();
                    // cal.add(Calendar.DATE, -7);
                    // Date pastWeek = cal.getTime();
                    // cal = Calendar.getInstance();
                    // cal.add(Calendar.DATE, -30);
                    // Date pastMonth = cal.getTime();
                    //
                    // Log.w(TAG, "FILE " + d.toString());
                    // Log.w(TAG, "DAY " + pastDay.toString() + " " + d.compareTo(pastDay));
                    // Log.w(TAG, "WEEK " + pastWeek.toString() + " " + d.compareTo(pastWeek));
                    // Log.w(TAG, "MONTH " + pastMonth.toString() + " " + d.compareTo(pastMonth));

                    // if (d.compareTo(pastDay) == 1) {
                    dateViewedData[0].add(file);
                    // } else if (d.compareTo(pastWeek) == 1) {
                    // dateViewedData[1].add(file);
                    // } else if (d.compareTo(pastMonth) == 1) {
                    // dateViewedData[2].add(file);
                    // } else {
                    // dateViewedData[3].add(file);
                    // }

                }

                titles = Arrays.asList(dateViewedTitles);
                data = Arrays.asList(dateViewedData);

                break;
            case 1:

                titles = new ArrayList();
                data = new ArrayList();

                // titles loop
                for (int i = 0; i < allData.size(); i++) {
                    File file = (File) allData.get(i);
                    String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

                    // Log.w(TAG, "TITLE - " + faqsMeta[6].split("\\(")[0].trim());
                    // Log.w(TAG, "FAQS META - " + faqsMeta.toString());

                    String title = faqsMeta[6].split("\\(")[0].trim();
                    if (title.indexOf(faqsMeta[0].split("\\(|<")[0].trim()) != -1) {
                        title = title.substring(0, title.indexOf(faqsMeta[0].split("\\(|<")[0].trim())).trim();
                    }

                    if (!titles.contains(title)) {
                        titles.add(title);
                    }

                }

                // data loop
                for (int i = 0; i < titles.size(); i++) {
                    ArrayList<File> sectionFiles = new ArrayList<File>();
                    for (int j = 0; j < allData.size(); j++) {
                        File file = (File) allData.get(j);
                        String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

                        String title = faqsMeta[6].split("\\(")[0].trim();
                        if (title.indexOf(faqsMeta[0].split("\\(|<")[0].trim()) != -1) {
                            title = title.substring(0, title.indexOf(faqsMeta[0].split("\\(|<")[0].trim())).trim();
                        }

                        if (title.equals(titles.get(i))) {
                            sectionFiles.add(allData.get(j));
                        }
                    }
                    data.add(sectionFiles);
                }

                break;
            case 2:

                String[] dateAddedTitles = new String[] { "Most Recently Added" }; // , "Added Past Week", "Added Past Month", "Added Past Year" };
                List[] dateAddedData = new List[] { new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList() };

                for (int i = 0; i < allData.size(); i++) {
                    File file = (File) allData.get(i);
                    String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

                    // Log.w(TAG, prefs.getString(file.getName(), ""));

                    Date d = new Date(file.lastModified());

                    // Calendar cal = Calendar.getInstance();
                    // cal.add(Calendar.DATE, -3);
                    // Date pastDay = cal.getTime();
                    // cal = Calendar.getInstance();
                    // cal.add(Calendar.DATE, -7);
                    // Date pastWeek = cal.getTime();
                    // cal = Calendar.getInstance();
                    // cal.add(Calendar.DATE, -30);
                    // Date pastMonth = cal.getTime();

                    // Log.w(TAG, "FILE " + d.toString());
                    // Log.w(TAG, "DAY " + pastDay.toString() + " " + d.compareTo(pastDay));
                    // Log.w(TAG, "WEEK " + pastWeek.toString() + " " + d.compareTo(pastWeek));
                    // Log.w(TAG, "MONTH " + pastMonth.toString() + " " + d.compareTo(pastMonth));

                    // if (d.compareTo(pastDay) == 1) {
                    dateAddedData[0].add(file);
                    // } else if (d.compareTo(pastWeek) == 1) {
                    // dateAddedData[1].add(file);
                    // } else if (d.compareTo(pastMonth) == 1) {
                    // dateAddedData[2].add(file);
                    // } else {
                    // dateAddedData[3].add(file);
                    // }
                }

                titles = Arrays.asList(dateAddedTitles);
                data = Arrays.asList(dateAddedData);

                break;
            case 3:
                Collections.sort(allData, new MySizeComparable());

                String[] sizeTitles = new String[] { "Largest File Size" }; // ">1000k", ">500k", ">200k", ">50k", "<50k" };

                List[] sizeData = new List[] { new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList() };

                // titles loop
                for (int i = 0; i < allData.size(); i++) {
                    File file = (File) allData.get(i);
                    String[] faqsMeta = prefs.getString(file.getName(), "").split(" --- ");

                    Integer size = Integer.valueOf(faqsMeta[4].replaceAll("K", "").trim());
                    // if (size > 1000) {
                    sizeData[0].add(file);
                    // } else if (size > 500) {
                    // sizeData[1].add(file);
                    // } else if (size > 200) {
                    // sizeData[2].add(file);
                    // } else if (size > 50) {
                    // sizeData[3].add(file);
                    // } else {
                    // sizeData[4].add(file);
                    // }
                }

                titles = Arrays.asList(sizeTitles);
                data = Arrays.asList(sizeData);

                break;
            }

            return result;
        }

        protected void onPostExecute(String result) {

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

            int my_faqs_pos = prefs.getInt("my_faqs_pos", 0);
            listView.setSelection(my_faqs_pos);

            adapter.notifyDataSetChanged();
            // sectionAdapter.notifyDataSetChanged();

            // adapter.notifyDataSetChanged();
            // listView.smoothScrollBy(1000, 1000);

            // even tho we aren't using this adapter we can still check it here??
            if (allData.size() < 1) {

                // if android >= 3.0 then stay otherwise force to search
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    noResults.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "No saved FAQs. Please search.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {

            }
        }
    };

    /**
     * Save the current position
     * 
     * @author eneve
     */
    private class DeleteAllTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            // do nothing
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            SharedPreferences.Editor editor = prefs.edit();
            File[] files = Faqr.getFaqrFiles(getFilesDir().listFiles());
            for (File file : files) {
                // kill the metadata
                editor.remove(file.getName());
                editor.remove(file.getName() + "curr_pos");
                editor.remove(file.getName() + "saved_pos");
                editor.commit();
                // kill the files
                file.delete();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Deleted all saved FAQs.", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("curr_faq");
            editor.commit();

            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // noResults.setVisibility(View.VISIBLE);
            // } else {
            // intent
            Intent intent = new Intent(getApplicationContext(), MyFaqsActivity.class);

            editor = prefs.edit();
            editor.putInt("my_faqs_pos", 0);
            editor.commit();

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            // }
        }
    };

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // COMPARABLES

    public class MyLastComparable implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                // start them old if their date hasn't been set yet
                date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse("1970-01-01 00:00:00.000+0000");
                date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse("1970-01-01 00:00:00.000+0000");
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
            // now parse the dates
            try {
                String faqLastRead1 = prefs.getString(o1.getName() + "___last_read", "");
                date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse(faqLastRead1);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
            // now parse the dates
            try {
                String faqLastRead2 = prefs.getString(o2.getName() + "___last_read", "");
                date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse(faqLastRead2);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
            return (date2.compareTo(date1));
        }
    }

    public class MyAlphaComparable implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            String[] faqsMeta1 = prefs.getString(o1.getName(), "").split(" --- ");
            String o1Name = faqsMeta1[6];
            String[] faqsMeta2 = prefs.getString(o2.getName(), "").split(" --- ");
            String o2Name = faqsMeta2[6];
            return (o1Name.compareTo(o2Name));
        }
    }

    public class MyDateComparable implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return Long.valueOf(((File) o2).lastModified()).compareTo(Long.valueOf(((File) o1).lastModified()));
        }
    }

    public class MySizeComparable implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return Long.valueOf(((File) o2).length()).compareTo(Long.valueOf(((File) o1).length()));
        }
    }

}
