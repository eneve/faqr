/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import com.faqr.FaqrApp;
import com.faqr.R;
import com.faqr.model.FaqMeta;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

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
    private List data = new ArrayList();
    private List titles = new ArrayList();

    private LinearLayout loading;
    private LinearLayout noResults;

    private Menu menu;

    private SearchView searchView;
    private ArrayAdapter<String> searchViewAdapter;

    // dialogs
    protected AlertDialog faqDialog;
    protected AlertDialog deleteConfirmDialog;
    protected AlertDialog clearConfirmDialog;
    protected AlertDialog deleteAllConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_faqs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        toolbar.getRootView().setBackgroundColor(themeBackgroundColor);

        // set the list adapter
        adapter = new MyFaqsListAdapter();
        listView = (StickyListHeadersListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        // loading indicator
        loading = (LinearLayout) findViewById(R.id.loading);

        // no results
        noResults = (LinearLayout) findViewById(R.id.no_results);
        noResults.setVisibility(View.GONE);

        TextView noResultsText = (TextView) findViewById(R.id.no_results_text);
        noResultsText.setTextColor(themeTextColor);

        /** called when a list item is clicked */
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

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

                FaqMeta faqMeta = new FaqMeta(prefs.getString(metaView.getText().toString(), ""));

                // delete dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyFaqsActivity.this, R.style.AppCompatAlertDialogStyle);
                dialogBuilder.setMessage("Are you sure you want to delete " + faqMeta.getTitle() + "?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = prefs.edit();

                        File file = null;
                        File[] files = FaqrApp.getFaqrFiles(getFilesDir().listFiles());
                        for (int i = 0; i < files.length; i++) {
                            File f = files[i];
                            if (f.getName().equals(metaView.getText().toString())) {
                                file = f;
                            }
                        }

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

        // clear search history confirm dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyFaqsActivity.this, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setMessage("Are you sure you want to clear your search history?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("recent_searches", "");
                editor.commit();

                String recentSearches = prefs.getString("recent_searches", "");
                String[] split = recentSearches.split(" --- ");
                final List<String> list = new ArrayList<String>();
                Collections.addAll(list, split);
                list.remove("");

                final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
                searchViewAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_dropdown_item_1line_faqr, list);
                searchAutoComplete.setAdapter(searchViewAdapter);

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        clearConfirmDialog = dialogBuilder.create();

        // delete all confirm dialog
        dialogBuilder = new AlertDialog.Builder(MyFaqsActivity.this, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setMessage("Are you sure you want to delete all of your saved FAQs?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getMenuInflater().inflate(R.menu.activity_my_faqs, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconifiedByDefault(true);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "onclick", Toast.LENGTH_SHORT).show();
            }
        });

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
                searchItem.collapseActionView();

                if (null != query && !query.equals("")) {
                    String recentSearches = prefs.getString("recent_searches", "");
                    String[] split = recentSearches.split(" --- ");
                    final List<String> list = new ArrayList<String>();
                    Collections.addAll(list, split);
                    list.remove(query.toString().trim());
                    String newRecentSearches = "";
                    newRecentSearches += query.toString().trim();
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
                        intent.putExtra("game", query.trim());

                        editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
                        editor.commit();

                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter search terms.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setThreshold(0);
        String recentSearches = prefs.getString("recent_searches", "");
        String[] split = recentSearches.split(" --- ");
        final List<String> list = new ArrayList<String>();
        Collections.addAll(list, split);
        list.remove("");

        searchViewAdapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line_faqr, list);
        searchAutoComplete.setAdapter(searchViewAdapter);
        searchAutoComplete.setOnItemClickListener(new OnItemClickListener() {

            /**
             * Implements OnItemClickListener
             */
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                searchAutoComplete.setText(textView.getText());

                searchItem.collapseActionView();

                if (!isNetworkAvailable()) {
                    connectionDialog.show();
                } else {

                    SharedPreferences.Editor editor = prefs.edit();

                    if (null != searchView.getQuery().toString() && searchView.getQuery().toString().equals("")) {

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

                        editor.putString("recent_searches", newRecentSearches);
                        editor.commit();
                    }

                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra("game", textView.getText());
                    editor.putInt("my_faqs_pos", listView.getFirstVisiblePosition());
                    editor.commit();
                    startActivity(intent);
                    finish();
                }
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
        case R.id.action_search: // check connectivity
            return true;
        case R.id.menu_clear:
            clearConfirmDialog.show();
            return true;
        case R.id.menu_delete:
            deleteAllConfirmDialog.show();
            return true;
        case R.id.menu_settings:
            intent = new Intent(this, PreferencesActivity.class);
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
            return true;
        default:
            return false;
        }
    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

            FaqMeta faqMeta = new FaqMeta(prefs.getString(file.getName(), ""));


            // name
            TextView nameView = (TextView) view.findViewById(R.id.name);
            TextView authorView = (TextView) view.findViewById(R.id.author);
            TextView versionView = (TextView) view.findViewById(R.id.version);
            TextView sizeView = (TextView) view.findViewById(R.id.size);

            // for passing hidden information not shown
            TextView metaView = (TextView) view.findViewById(R.id.meta);
            metaView.setText(file.getName());

            String dateFix = faqMeta.getDate();
            if (dateFix.startsWith("0"))
                dateFix = dateFix.substring(1);

            String versionAndSize = "v" + faqMeta.getVersion() + "/" + faqMeta.getSize().replaceAll("K", "k");
            if (faqMeta.getVersion().trim().equals("") && !faqMeta.getVersion().equalsIgnoreCase("Final"))
                versionAndSize = faqMeta.getSize().replaceAll("K", "k");

            nameView.setText(faqMeta.getTitle());
            authorView.setText(faqMeta.getAuthor());

            // curr faq gets an indicator
            if (prefs.getString("curr_faq", "").equalsIgnoreCase(file.getName())) {
                nameView.setText("\u2605 " + nameView.getText());
            }

            versionView.setText(dateFix);
            sizeView.setText(versionAndSize);

            // theme goodness
            nameView.setTextColor(themeColor);
            authorView.setTextColor(themeTextColor);
            versionView.setTextColor(themeTextColor);
            sizeView.setTextColor(themeTextColor);

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
            view.setBackgroundColor(primaryColor);

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
    private class InitTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            File[] files = FaqrApp.getFaqrFiles(getFilesDir().listFiles());
            allData = Arrays.asList(files);

            // current sorting
            Collections.sort(allData, new MyAlphaComparable());

            titles = new ArrayList();
            data = new ArrayList();

            // titles loop
            for (int i = 0; i < allData.size(); i++) {
                File file = (File) allData.get(i);
                FaqMeta faqMeta = new FaqMeta(prefs.getString(file.getName(), ""));
                String title = faqMeta.getGameTitle();
                if (!titles.contains(title)) {
                    titles.add(title);
                }
            }

            // data loop
            for (int i = 0; i < titles.size(); i++) {
                ArrayList<File> sectionFiles = new ArrayList<File>();
                for (int j = 0; j < allData.size(); j++) {
                    File file = (File) allData.get(j);

                    FaqMeta faqMeta = new FaqMeta(prefs.getString(file.getName(), ""));
                    String title = faqMeta.getGameTitle();
                    if (title.equals(titles.get(i))) {
                        sectionFiles.add(allData.get(j));
                    }
                }
                data.add(sectionFiles);
            }

            return result;
        }

        protected void onPostExecute(String result) {

            // fancy animations
            listView.setVisibility(View.VISIBLE);
//            loading.setVisibility(View.GONE);

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

            int my_faqs_pos = prefs.getInt("my_faqs_pos", 0);
            listView.setSelection(my_faqs_pos);

            adapter.notifyDataSetChanged();

            // even tho we aren't using this adapter we can still check it here??
            if (allData.size() < 1) {
                noResults.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * Save the current position
     *
     * @author eneve
     */
    private class DeleteAllTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            SharedPreferences.Editor editor = prefs.edit();
            File[] files = FaqrApp.getFaqrFiles(getFilesDir().listFiles());
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

            Intent intent = new Intent(getApplicationContext(), MyFaqsActivity.class);

            editor = prefs.edit();
            editor.putInt("my_faqs_pos", 0);
            editor.commit();

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // COMPARABLES

    public class MyAlphaComparable implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            // TODO there is a NPE here when the size is 6
            // Final Fantasy IV FAQ/Walkthrough --- 09/20/11 --- Johnathan 'Zy' Sawyer --- 1.02 --- 1267K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/62211
            try {
//                String[] faqsMeta1 = prefs.getString(o1.getName(), "").split(" --- ");
//                String[] faqsMeta2 = prefs.getString(o2.getName(), "").split(" --- ");

                FaqMeta faqMeta1 = new FaqMeta(prefs.getString(o1.getName(), ""));
                FaqMeta faqMeta2 = new FaqMeta(prefs.getString(o2.getName(), ""));

//                String o1Name = faqsMeta1[6];
//                String o2Name = faqsMeta2[6];
//                String o1Name = faqsMeta1[6];
//                String o2Name = faqsMeta2[6];
                return (faqMeta1.getGameTitle().compareTo(faqMeta2.getGameTitle()));
            } catch (Exception e) {
                // shouldn't go here anymore now that index is 0 and not 6

                Log.d(TAG, "Problem with FaqMeta and MyAlphaComparable");
                return (o1.getName().compareTo(o2.getName()));
            }
        }
    }
}
