/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.faqr.FaqrApp;
import com.faqr.R;
import com.faqr.model.FaqMeta;
import com.faqr.view.ObservableWebView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Faqr - The ultimate GameFAQs reader application for Android 5 Trillion
 *
 * DON'T FAQ AROUND!!!!
 *
 * @author eneve
 */
public class FaqActivity extends BaseActivity {

    // immersive
    private static final int INITIAL_HIDE_DELAY = 2200;
    private View mDecorView;

    // loading
    private LinearLayout loading;
    private LinearLayout error;

    private String lines[] = new String[] {};
    private String origLines[] = new String[] {};

    // list view
    private ListView listView;
    private FaqAdapter adapter;

    // search view
    private SearchView searchView;

    // find
    private Boolean filtered = false;
    private Boolean find = false;
    private String findString = "";
    private int currFindPos = 0;

    private Menu menu;
    private Boolean hideWebViewMenuOptions = false;

    private MenuItem faqmarksItem;

    private long prevCurrTime;
    private int prevFirstVisibleItem;

    private long prevCurrTime2;

    private Boolean goTo = false;
    // private int goToCounter = 0;
    private int currGotoPos = 0;

    // quit dialog
    protected AlertDialog quitDialog;

    // our webview
    private ObservableWebView webView;
    private boolean webViewActive = false;
    private CookieManager cookieManager;

    private long webViewThrottleTime;
    private boolean webViewReloadSavedPos = true;


    private float autoMonoFontSize = -1.0f;

    // current faq info
    private String currFaq = "";
    private FaqMeta currFaqMeta = new FaqMeta();

    // faqmark
    private Integer faqmarkPos = -1;

    private Toolbar toolbar;
    private Boolean toolbarAnim = false;

    /** Called when the activity is first created. */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faq);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        RelativeLayout bg = (RelativeLayout) findViewById(R.id.bg);

        // theme goodness
        toolbar.getRootView().setBackgroundColor(themeBackgroundColor);

        listView = (ListView) findViewById(R.id.list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setDisplayUseLogoEnabled(false);

        extras = getIntent().getExtras();
        if (extras != null) {
            faqmarkPos = extras.getInt("FAQmarkPosition", -1);
        }

        adapter = new FaqAdapter();
        listView.setAdapter(adapter);

        if (prefs.getBoolean("use_fast_scroll", getResources().getBoolean(R.bool.use_fast_scroll_default))) {
            listView.setFastScrollEnabled(true);
            if (prefs.getBoolean("fast_scroll_left", getResources().getBoolean(R.bool.fast_scroll_left_default))) {
                listView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
            }
        } else {
            listView.setFastScrollEnabled(false);
        }

        // loading indicator
        loading = (LinearLayout) findViewById(R.id.loading);
        error = (LinearLayout) findViewById(R.id.error);

        /** called when a list item is clicked */
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                    if (!getFind() && !goTo) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                            if (visible && !getFind()) {
                                hideSystemUI();
                            } else {
                                showSystemUI();
                            }
                        } else {
                            if (getSupportActionBar().isShowing() && !getFind()) {
                                hideSystemUI();
                            } else {
                                showSystemUI();
                            }
                        }
                    }
                }
            }
        });

        /** called when a list item is long clicked */
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (filtered) {
                    // find position in orig list
                    int realpos = 0;
                    String theline = lines[position];
                    for (int i = 0; i < origLines.length; i++) {
                        String currline = origLines[i];
                        if (currline.equals(theline)) {
                            new SavedPosTask().execute(new String[] { Integer.valueOf(i).toString() });
                            realpos = i;
                            break;
                        }
                    }
                } else {
                    new SavedPosTask().execute(new String[] { Integer.valueOf(position).toString() });
                }

                // save the postiion
                if (prefs.getBoolean("highlight_saved_pos", getResources().getBoolean(R.bool.highlight_saved_position_default))) {
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                }

                return true;
            }
        });

        listView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               // do nothing
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // do nothing
            }
        });

        // quit dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setMessage("This will quit the application.").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        quitDialog = dialogBuilder.create();
        // quitDialog.setTitle("Are you sure?");

        // auto font size reset
        autoMonoFontSize = -1.0f;

        // webview
        webView = (ObservableWebView) findViewById(R.id.webview);
        webView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t, int oldl, int oldt){
                //Do stuff
                if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                    long currTime = Calendar.getInstance().getTimeInMillis();
                    if (((currTime - webViewThrottleTime) > 1000) && t > oldt && getSupportActionBar().isShowing() && !getFind()) {
                        hideSystemUI();
                        webViewThrottleTime = Calendar.getInstance().getTimeInMillis();

                    } else if (((currTime - webViewThrottleTime) > 1000) && t < oldt && !getSupportActionBar().isShowing()) {
                        showSystemUI();
                        webViewThrottleTime = Calendar.getInstance().getTimeInMillis();
                    }
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(".gamefaqs.com", "css_color=" + themeCssColor + "; Domain=.gamefaqs.com");
        CookieSyncManager.getInstance().sync();

        webView.setInitialScale(1);

        // caching - larger for newer devices
        webView.getSettings().setAppCacheMaxSize(10 * 1024 * 1024); // 10MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            // native loading widget
            public void onProgressChanged(WebView view, int progress) {
                // do nothing
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.i(TAG, cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                // do nothing
            }

            @Override
            public void onPageFinished(WebView view, final String url) {

                // set it to try later and try here anyway if perchance menu isn't null
                hideWebViewMenuOptions = true;
                if (null != menu) {
                    MenuItem opt = menu.findItem(R.id.action_search);
                    opt = menu.findItem(R.id.menu_display_options);
                    opt = menu.findItem(R.id.menu_faqmarks);
                    opt.setVisible(false);
                }

                loading.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);

                String currFaqURL = currFaqMeta.getUrl();

                if (view != null && view.getTitle() != null) {
                    String title = view.getTitle().trim();
                    // downloaded images in webview with have about:blank title
                    if (!title.equals("about:blank")) {
                        getSupportActionBar().setTitle(title);
                        getSupportActionBar().setSubtitle(currFaqURL.replaceAll("http://", "").replaceAll("https://", ""));
                    }
                }

                // set last read date
                SharedPreferences.Editor editor = prefs.edit();
                String key = currFaqMeta.getUrl() + "curr_url";
                editor.putString(key, url);
                editor.commit();

                String test = prefs.getString(currFaqMeta.getUrl() + "curr_url", "");
                getSupportActionBar().setIcon(android.R.color.transparent);

                // restore current webview locations
                if (webViewReloadSavedPos) {
                    assert view != null;
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (url.contains("#")) {
                                webView.loadUrl(url);
                            }
                            webViewReloadSavedPos = false;
                        }
                        // Delay the scrollTo to make it work
                    }, 300);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("Webview Error", "error code:" + errorCode);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            // set true to catch when urls are loaded
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // do what you need to do when the URL is about to load
            @Override
            public void onLoadResource(WebView view, String url) {
                // Toast.makeText(TAG, url, Toast.LENGTH_SHORT);
                if (url.equals("someUrl")) {
                    // do whatever you want
                }
            }
        });
        webView.setVisibility(View.GONE);

        // handle some padding at the top for when action bar is hidden
        if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
            // don't set any margins in immersive
        }

        // get the current FAQ
        if (!TextUtils.isEmpty(prefs.getString("curr_faq", ""))) {
            currFaq = prefs.getString("curr_faq", "");
            currFaqMeta = new FaqMeta(prefs.getString(prefs.getString("curr_faq", ""), ""));

            // set last read date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(prefs.getString("curr_faq", "") + "___last_read", sdf.format(new Date()));

            Log.i(TAG, "-----------------------------");
            Log.i(TAG, "currFaq " + currFaq);
            Log.i(TAG, "faqMeta " + currFaqMeta);
            Log.i(TAG, "-----------------------------");

            int curr_pos = prefs.getInt(prefs.getString("curr_faq", "") + "curr_pos", -1);
            int saved_pos = prefs.getInt(prefs.getString("curr_faq", "") + "saved_pos", -1);
            new GetFaqTask().execute(new String[] {});
        } else {
            Intent intent = new Intent(this, FaqsActivity.class);
            startActivity(intent);
            finish();
        }

        configureOrientation(this.getResources().getConfiguration().orientation);

        // immersive webview
        mDecorView = getWindow().getDecorView();
        if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int flags) {
                    boolean visible = (flags & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                    if (visible) {
                        showSystemUI();
                    } else {
//                        showSystemUI();
                    }
                }
            });

            final GestureDetector clickDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                    if (visible && !getFind()) {
                        hideSystemUI();
                    } else {
                        showSystemUI();
                    }
                    return true;
                }
            });
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                    if (visible) {
                        return clickDetector.onTouchEvent(motionEvent);
                    } else {
                        return false;
                    }
                }
            });

            showSystemUI();
        }
    }

    /** Called when the activity will start interacting with the user. */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webView.canGoBack() == true) {
                webView.goBack();
                return true;
            } else {
                // Save the web scroll position
                if (webViewActive) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(webView.getUrl() + "curr_pos", webView.getScrollY());
                    editor.commit();
                }

                if (extras == null || (extras != null && extras.getBoolean("from_search") != true)) {
                    Intent intent = new Intent(this, FaqsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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

    /** Called when a button is clicked */
//    public void onClick(View view) {
//        if (view == find_bar_prev) {
//            // find_prev();
//            new FindPrevTask().execute(new String[] {});
//        } else if (view == find_bar_next) {
//            // find_next();
//            new FindNextTask().execute(new String[] {});
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        final Menu finalMenu = menu;

        getMenuInflater().inflate(R.menu.activity_faq, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.find_hint));
        searchView.setIconifiedByDefault(true);

        LinearLayout searchText = (LinearLayout) searchView.findViewById(R.id.search_plate);
        int pL = searchText.getPaddingLeft();
        int pT = searchText.getPaddingTop();
        int pR = searchText.getPaddingRight();
        int pB = searchText.getPaddingBottom();
//        searchText.setBackgroundDrawable(getResources().getDrawable(R.drawable.textfield_activated_holo_dark));
        searchText.setPadding(pL, pT, pR, pB);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFind(true);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {

                if (query.length() == 0 || query.trim().length() > 2) {
                    if (webViewActive) {
                        webView.findAll(query);
                    } else {
                        findString = query;
                        new FindNextTask().execute(new String[]{query});
                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (webViewActive) {
                    webView.findAll(query);
                } else {
                    findString = query;
                    new FindNextTask().execute(new String[]{query});
                }
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (webViewActive) {
                    webView.findAll("");
                }

                MenuItem prev = finalMenu.findItem(R.id.menu_prev);
                prev.setVisible(false);
                MenuItem next = finalMenu.findItem(R.id.menu_next);
                next.setVisible(false);
                MenuItem opt;
                opt = finalMenu.findItem(R.id.menu_faqmarks);
                opt.setVisible(true);

                opt = finalMenu.findItem(R.id.menu_display_options);
                opt.setVisible(true);
                opt = finalMenu.findItem(R.id.menu_browser);
                opt.setVisible(true);
                opt = finalMenu.findItem(R.id.menu_settings);
                opt.setVisible(true);
                opt = finalMenu.findItem(R.id.menu_about);
                opt.setVisible(true);

                getSupportActionBar().setIcon(android.R.color.transparent);

                setFind(false);
                currFindPos = 0;

                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setFind(true);
                Log.i(TAG, "onMenuItemActionExpand " + item.getItemId());

                return true;
            }
        });

        MenuItem findItem = menu.findItem(R.id.action_search);
        faqmarksItem = menu.findItem(R.id.menu_faqmarks);
        if (hideWebViewMenuOptions) {
            faqmarksItem.setVisible(false);
        }

        // if faqmarks
        try {
            if (!TextUtils.isEmpty(prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", ""))) {
                // do nothing
            } else {
                faqmarksItem.setEnabled(false);
            }
        } catch (Exception e) {
            faqmarksItem.setEnabled(false);
        }

        MenuItem prev = menu.findItem(R.id.menu_prev);
        prev.setVisible(false);
        MenuItem next = menu.findItem(R.id.menu_next);
        next.setVisible(false);

        if (!prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
            MenuItem opt = menu.findItem(R.id.menu_faqmarks);
            opt.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        // Save the web scroll position
        if (webViewActive) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(webView.getUrl() + "curr_pos", webView.getScrollY());
            editor.commit();
        }

        switch (item.getItemId()) {
        case R.id.menu_display_options:
            View menuItemView = findViewById(R.id.bg); // SAME ID AS MENU ID
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int width = Math.round(FaqrApp.convertDpToPixel(200, getApplicationContext()));
            PopupWindow popup = new PopupWindow(inflater.inflate(R.layout.view_display_options, null, false), width, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // SETBACKGROUNDDRAWABLE IS VERY IMPORTANT FOR POPUP DISMISSAL //
            popup.setBackgroundDrawable(new BitmapDrawable());
            // SETBACKGROUNDDRAWABLE IS VERY IMPORTANT FOR POPUP DISMISSAL //

            popup.setAnimationStyle(R.style.OptionsAnimationPopup);
            popup.showAtLocation(menuItemView, Gravity.TOP | Gravity.RIGHT, 20, getStatusBarHeight() + getActionBarHeight());

            Spinner theme = (Spinner) popup.getContentView().findViewById(R.id.theme_spinner);
            ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.theme_titles, android.R.layout.simple_spinner_item);
            themeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            theme.setAdapter(themeAdapter);

            Integer themeSetting = Integer.valueOf(prefs.getString("theme", "1"));
            theme.setSelection(themeSetting - 1);
            theme.setOnItemSelectedListener(new OnItemSelectedListener() {
                int spinnerCount = 0;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (spinnerCount > 0) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("theme", new Integer(position + 1).toString());
                        editor.commit();

                        setThemeColors();
                        toolbar.getRootView().setBackgroundColor(themeBackgroundColor);
                        adapter.notifyDataSetChanged();

                        // set webView css
                        cookieManager.setCookie(".gamefaqs.com", "css_color=" + themeCssColor + "; Domain=.gamefaqs.com");
                        if (webViewActive) {
                            webView.reload();
                        }
                    }
                    spinnerCount++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            Spinner typeface = (Spinner) popup.getContentView().findViewById(R.id.typeface_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> typefaceAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.typeface_list_titles, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            typefaceAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            // spinner.MODE_DIALOG
            typeface.setAdapter(typefaceAdapter);

            Integer typefaceSetting = Integer.valueOf(prefs.getString("typeface", "1"));
            typeface.setSelection(typefaceSetting - 1);
            typeface.setOnItemSelectedListener(new OnItemSelectedListener() {
                int spinnerCount = 0;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (spinnerCount > 0) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("typeface", new Integer(position + 1).toString());
                        editor.commit();
                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                    spinnerCount++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });

            TextView fontSizeMinus = (TextView) popup.getContentView().findViewById(R.id.text_smaller);
            fontSizeMinus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Integer fontSize = prefs.getInt("font_size_v2", getResources().getInteger(R.integer.font_size_default));
                        fontSize = fontSize > -6 ? fontSize - 1 : fontSize;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("font_size_v2", fontSize);
                        editor.commit();
                    } else {
                        Integer fontSize = prefs.getInt("font_size_v2_land", getResources().getInteger(R.integer.font_size_default));
                        fontSize = fontSize > -6 ? fontSize - 1 : fontSize;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("font_size_v2_land", fontSize);
                        editor.commit();
                    }

                    adapter.notifyDataSetChanged();
                }
            });

            TextView fontSizePlus = (TextView) popup.getContentView().findViewById(R.id.text_larger);
            fontSizePlus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Integer fontSize = prefs.getInt("font_size_v2", getResources().getInteger(R.integer.font_size_default));
                        fontSize = fontSize < 8 ? fontSize + 1 : fontSize;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("font_size_v2", fontSize);
                        editor.commit();

                    } else {
                        Integer fontSize = prefs.getInt("font_size_v2_land", getResources().getInteger(R.integer.font_size_default));
                        fontSize = fontSize < 8 ? fontSize + 1 : fontSize;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("font_size_v2_land", fontSize);
                        editor.commit();
                    }

                    adapter.notifyDataSetChanged();
                }
            });

            Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), getResources().getString(R.string.glyphicons_file));
            fontSizeMinus.setTypeface(tf);
            fontSizePlus.setTypeface(tf);

            final TextView leftJustify = (TextView) popup.getContentView().findViewById(R.id.left_justify);
            final TextView centerJustify = (TextView) popup.getContentView().findViewById(R.id.center_justify);
            final TextView rightJustify = (TextView) popup.getContentView().findViewById(R.id.right_justify);

            leftJustify.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer justify = prefs.getInt("justify_v2", getResources().getInteger(R.integer.justify_default));
//                    Toast.makeText(getApplicationContext(), "leftJustify" + 0, Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("justify_v2", 0);
                    editor.commit();

                    leftJustify.setTextColor(getResources().getColor(R.color.day_background));
                    centerJustify.setTextColor(getResources().getColor(R.color.dark_background));
                    rightJustify.setTextColor(getResources().getColor(R.color.dark_background));

                    adapter.notifyDataSetChanged();
                }
            });

            centerJustify.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer justify = prefs.getInt("justify_v2", getResources().getInteger(R.integer.justify_default));
//                    Toast.makeText(getApplicationContext(), "centerJustify" + 1, Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("justify_v2", 1);
                    editor.commit();

                    leftJustify.setTextColor(getResources().getColor(R.color.dark_background));
                    centerJustify.setTextColor(getResources().getColor(R.color.day_background));
                    rightJustify.setTextColor(getResources().getColor(R.color.dark_background));

                    adapter.notifyDataSetChanged();
                }
            });

            rightJustify.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer justify = prefs.getInt("justify_v2", getResources().getInteger(R.integer.justify_default));
//                    Toast.makeText(getApplicationContext(), "rightJustify" + 2, Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("justify_v2", 2);
                    editor.commit();

                    leftJustify.setTextColor(getResources().getColor(R.color.dark_background));
                    centerJustify.setTextColor(getResources().getColor(R.color.dark_background));
                    rightJustify.setTextColor(getResources().getColor(R.color.day_background));

                    adapter.notifyDataSetChanged();
                }
            });

            Integer justify = prefs.getInt("justify_v2", getResources().getInteger(R.integer.justify_default));
            if (justify == 0) {
                leftJustify.setTextColor(getResources().getColor(R.color.day_background));
            } else if (justify == 1) {
                centerJustify.setTextColor(getResources().getColor(R.color.day_background));
            } else {
                rightJustify.setTextColor(getResources().getColor(R.color.day_background));
            }

            leftJustify.setTypeface(tf);
            centerJustify.setTypeface(tf);
            rightJustify.setTypeface(tf);


            if (webViewActive) {
                TextView typefaceTextView = (TextView)  popup.getContentView().findViewById(R.id.typeface);
                Spinner typefaceSpinner = (Spinner)  popup.getContentView().findViewById(R.id.typeface_spinner);
                LinearLayout textSizeLayout = (LinearLayout)  popup.getContentView().findViewById(R.id.do_text_size);
                LinearLayout justifyLayout = (LinearLayout)  popup.getContentView().findViewById(R.id.do_justify);
                typefaceTextView.setVisibility(View.GONE);
                typefaceSpinner.setVisibility(View.GONE);
                textSizeLayout.setVisibility(View.GONE);
                justifyLayout.setVisibility(View.GONE);
            }

            return true;

        case R.id.menu_prev:
            if (webViewActive) {
                webView.findNext(false);
            } else {
                new FindPrevTask().execute(new String[]{});
            }
            return true;
        case R.id.menu_next:
            if (webViewActive) {
                webView.findNext(true);
            } else {
                new FindNextTask().execute(new String[]{});
            }
            return true;

        case R.id.menu_faqmarks:

            try {
                if (!prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", "").equals("")) {
                    intent = new Intent(this, FaqmarksActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No FAQmarks. Long press to save one.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "No FAQmarks. Long press to save one.", Toast.LENGTH_SHORT).show();
            }

            return true;

        case R.id.action_search:
            if (!getSupportActionBar().isShowing())
                getSupportActionBar().show();

            getSupportActionBar().setIcon(R.drawable.ic_launcher);

            MenuItem prev = menu.findItem(R.id.menu_prev);
            prev.setVisible(true);
            MenuItem next = menu.findItem(R.id.menu_next);
            next.setVisible(true);
            MenuItem opt;
            opt = menu.findItem(R.id.menu_display_options);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_faqmarks);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_browser);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_settings);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_about);
            opt.setVisible(false);
            return true;

        case R.id.menu_browser:
            String url = currFaqMeta.getUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        case R.id.menu_settings:
            intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            finish();
            return true;
        case R.id.menu_about:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        case android.R.id.home:
            if (extras == null || (extras != null && extras.getBoolean("from_search") != true)) {
                intent = new Intent(this, FaqsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();
            return true;
        default:
            return false;
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case DialogInterface.BUTTON_POSITIVE:

                // check connectivity
                if (!isNetworkAvailable()) {
                    connectionDialog.show();
                } else {
                    new GetFaqTask().execute(new String[] { "reload" });
                }
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                // No button clicked
                break;
            }
        }
    };

    /**
     * The Faqr list adapter thats smart about displaying ASCII
     *
     * @author eneve
     */
    public class FaqAdapter extends BaseAdapter {

        private final Object mLock = new Object();

        public FaqAdapter() {
        }

        public int getCount() {
            return lines.length;
        }

        public Object getItem(int position) {
            return lines[position];
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("NewApi")
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_item_faq, parent, false);

            // theme
            if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
                // Day
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_day));
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
                // Night
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_night));
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
                // Dark
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_dark));
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_sepia));
            }

            String line = lines[position];

            // name
            TextView nameView = (TextView) view.findViewById(R.id.name);

            if (prefs.getString("typeface", getResources().getString(R.string.typeface_default)).equals("1") || FaqrApp.useFixedWidthFont(line)) {

                // if (!prefs.getBoolean("use_variable_font", getResources().getBoolean(R.bool.use_variable_font_default)) || Faqr.useFixedWidthFont(line)) {
                // //////////
                // MONO FONT

                nameView.setTextAppearance(getApplicationContext(), R.style.MonoText);

                String monoFontSize = prefs.getString("mono_font_size", getResources().getString(R.string.mono_font_size_default));

                // auto font-size
                if (monoFontSize.equalsIgnoreCase("auto") && autoMonoFontSize == -1.0f) {
                    // Log.i(TAG, "CALCULATING FONT SIZE --------------------");

                    int measuredWidth = 0;
                    int measuredHeight = 0;
                    Point size = new Point();
                    WindowManager w = getWindowManager();
                    w.getDefaultDisplay().getSize(size);
                    measuredWidth = Math.round(size.x);
                    measuredHeight = size.y;

                    int totalCharstoFit = nameView.getPaint().breakText(getResources().getString(R.string.standard_width), true, measuredWidth, null);
                    int count = 0;
                    autoMonoFontSize = 12.0f;
                    // 79 characters/line is a classic standard for text files
                    while (totalCharstoFit != 79) {
                        if (totalCharstoFit < 79) {
                            autoMonoFontSize = Float.valueOf(autoMonoFontSize) - 0.5f;
                            nameView.setTextSize(Float.valueOf(autoMonoFontSize));
                            if (autoMonoFontSize <= 6.0f)
                                break;
                        } else if (totalCharstoFit > 79) {
                            autoMonoFontSize = Float.valueOf(autoMonoFontSize) + 0.5f;
                            nameView.setTextSize(Float.valueOf(autoMonoFontSize));
                            if (autoMonoFontSize >= 15.0f)
                                break;
                        } else {
                            break;
                        }
                        totalCharstoFit = nameView.getPaint().breakText(getResources().getString(R.string.standard_width), true, measuredWidth, null);
                        count++;
                        if (count > 10)
                            break;
                    }
                }
                if (monoFontSize.equalsIgnoreCase("auto")) {

                    // V2 font size
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Integer fontSize = prefs.getInt("font_size_v2", getResources().getInteger(R.integer.font_size_default));
                        nameView.setTextSize(Float.valueOf(autoMonoFontSize + fontSize));
                    } else {
                        Integer fontSize = prefs.getInt("font_size_v2_land", getResources().getInteger(R.integer.font_size_default));
                        nameView.setTextSize(Float.valueOf(autoMonoFontSize + fontSize));
                    }

                    // make bold for small fonts
                    if (autoMonoFontSize <= 7.0f) {
                        nameView.setTextAppearance(getApplicationContext(), R.style.MonoTextBold);
                    }
                } else {
                    nameView.setTextSize(Float.valueOf(monoFontSize));
                }

                Integer justify = prefs.getInt("justify_v2", getResources().getInteger(R.integer.justify_default));
                if (justify == 0) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) nameView.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.setMargins(16, 0, 0, 0);
                    nameView.setLayoutParams(lp);
                } else if (justify == 1) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) nameView.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    nameView.setLayoutParams(lp);
                } else {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) nameView.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.setMargins(0, 0, 16, 0);
                    nameView.setLayoutParams(lp);
                }
                view.setPadding(0, 8, 0, 8);

            } else if (prefs.getString("typeface", getResources().getString(R.string.typeface_default)).equals("2") || prefs.getString("typeface", getResources().getString(R.string.typeface_default)).equals("3")) {

                // //////////////
                // VARIABLE FONT
                // //////////////

                // nameView.setTypeface(tf2);
                if (prefs.getString("typeface", getResources().getString(R.string.typeface_default)).equals("3")) {
                    nameView.setTextAppearance(getApplicationContext(), R.style.SerifText);
                } else {
                    nameView.setTextAppearance(getApplicationContext(), R.style.SansText);
                }

                line = line.replaceAll("\\n", "");

                String variableFontSize = prefs.getString("variable_font_size", getResources().getString(R.string.variable_font_size_default));

                // auto font size
                if (variableFontSize.equalsIgnoreCase("auto")) {

                    Float varAutoMonoFontSize = autoMonoFontSize + 2.5f;

                    // V2 font size
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Integer fontSize = prefs.getInt("font_size_v2", getResources().getInteger(R.integer.font_size_default));
                        nameView.setTextSize(varAutoMonoFontSize + fontSize);
                    } else {
                        Integer fontSize = prefs.getInt("font_size_v2_land", getResources().getInteger(R.integer.font_size_default));
                        nameView.setTextSize(varAutoMonoFontSize + fontSize);
                    }

                } else {
                    nameView.setTextSize(Float.valueOf(variableFontSize));
                }

                view.setPadding(16, 8, 16, 8);
            }

            // set the text
            nameView.setText(line);

            // handle some padding at the top for when action bar is hidden
            if (position == 0 && prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                nameView.setPadding(view.getPaddingLeft(), getActionBarHeight() + getStatusBarHeight(), view.getPaddingRight(), view.getPaddingBottom());
            } else if (position == lines.length - 1 && prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                nameView.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), getNavigationBarHeight());
            }

            // theme goodness
            nameView.setTextColor(themeTextColor);

            String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", "");
            String[] savedPosMultiArray = savedPosMulti.split(",");
            if (prefs.getString("highlight_faqmark", "1").equals("1")) {
                if (Arrays.asList(savedPosMultiArray).contains(Integer.valueOf(position).toString())) {
                    nameView.setTextColor(themeAccentColor);
                }
            } else if (prefs.getString("highlight_faqmark", "1").equals("2")) {
                if (Arrays.asList(savedPosMultiArray).contains(Integer.valueOf(position).toString())) {
                    int pL = view.getPaddingLeft();
                    int pT = view.getPaddingTop();
                    int pR = view.getPaddingRight();
                    int pB = view.getPaddingBottom();
                    view.setBackgroundDrawable(themeDrawable);
                    view.setPadding(pL, pT, pR, pB);
                }
            } else if (prefs.getString("highlight_faqmark", "1").equals("3")) {
                if (Arrays.asList(savedPosMultiArray).contains(Integer.valueOf(position).toString())) {
                    int pL = view.getPaddingLeft();
                    int pT = view.getPaddingTop();
                    int pR = view.getPaddingRight();
                    int pB = view.getPaddingBottom();
                    view.setBackgroundDrawable(themeDrawable);
                    view.setPadding(pL, pT, pR, pB);
                    nameView.setTextColor(themeAccentColor);
                }
            } else if (prefs.getString("highlight_faqmark", "1").equals("4")) {
                // do nothing
            }

            // find highlighting
            if (find && !TextUtils.isEmpty(findString)) {
                String text = nameView.getText().toString().toLowerCase();

                // find all occurrences forward
                List<Integer> findPosList = new ArrayList<Integer>();
                for (int i = -1; (i = text.indexOf(findString, i + 1)) != -1;) {
                    findPosList.add(i);
                } // prints "4", "13", "22"

                Spannable str = new SpannableString(nameView.getText());
                for (Integer index : findPosList) {

                    int startIndex = index;
                    int endIndex = index + findString.length();
                    if (startIndex != -1) {

                        str.setSpan(new BackgroundColorSpan(0xFFff9632), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        str.setSpan(new ForegroundColorSpan(0xFF000000), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                nameView.setText(str);
            }

            // curr position task
            long currTime = System.currentTimeMillis();
            if (currTime - prevCurrTime2 > 10) {
                new CurrPosTask().execute(new String[] { Integer.valueOf(listView.getFirstVisiblePosition()).toString() });
                prevCurrTime2 = currTime;
            }

            return view;
        }
    }

    /**
     * Main Async task that loads the FAQS - trys to read from disk then web then then save the file if necessary
     *
     * @author eneve
     */
    private class GetFaqTask extends AsyncTask<String, Void, String> {

        String title = "";
        String content = "";
        String currFaqURL = "";

        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            setTitle(TAG);
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = "0";
            boolean success = false;

            // THIS IS WHERE WE CRASH IF THE METADATA IS CORRUPTED??
            // HOW AND WHY THE METADATA IS CORRUPTED IS CURRENTLY UNKNOWN
            try {
                currFaqURL = currFaqMeta.getUrl();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return "-999";
            }

            try {
                Log.w(TAG, "===============================================");
                Log.w(TAG, "READING FROM FILE " + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());

                if (currFaqMeta.getType().equals("TYPE=IMAGE")) {

                    // IMAGE FAQ
                    return "4";

                } else if (currFaqMeta.getType().equals("TYPE=HTML")) {

                    // HTML FAQ
                    return "5";

                } else {

                    // ASCII FAQ

                    String filecontent = FaqrApp.readSavedData(openFileInput(FaqrApp.validFileName(currFaq)));
                    if (!TextUtils.isEmpty(filecontent)) {
                        lines = FaqrApp.getLinesFile(filecontent);
                        origLines = new String[lines.length];
                        System.arraycopy(lines, 0, origLines, 0, lines.length);
                        success = true;
                    }
                    Log.w(TAG, "FILE LINES.SIZE " + lines.length);
                }
                Log.w(TAG, "===============================================");
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
            }

            if (!success) {
                if (isNetworkAvailable()) {
                    // try get from web
                    try {

                        // valid gamefaqs url
                        if (currFaqMeta.getUrl().contains("m.gamefaqs.com")) {

                            Log.w(TAG, "===============================================");
                            Log.w(TAG, "FETCHING FROM WEB " + currFaqURL);

                            if (!currFaqURL.startsWith("http")) {
                                currFaqURL = "http://" + currFaqURL.trim();
                            }

                            // download the faq from the web
                            Document doc = Jsoup.connect(currFaqURL)
                                .header("Accept-Encoding", "gzip, deflate")
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                                .maxBodySize(0)
                                .timeout(600000)
                                .get();

                            Elements titleElem = doc.select(".page-title a");
                            for (Element elem : titleElem) {
                                title += elem.text();
                            }
                            title = title.replace("GameFAQs: ", "");
                            Log.i(TAG, "Got the page.title a as " + title);

                            SharedPreferences.Editor editor = prefs.edit();

                            String faqMeta = prefs.getString(FaqrApp.validFileName(currFaqURL), "");
                            Log.i(TAG, FaqrApp.validFileName(currFaqURL) + " " + faqMeta);
                            // Toast.makeText(getApplicationContext(), Faqr.validFileName(faqURL) + " " + faqMeta, Toast.LENGTH_SHORT).show();
                            if (faqMeta.split(" --- ").length == 6)
                                faqMeta = faqMeta + " --- " + title;
                            editor.putString(FaqrApp.validFileName(currFaqURL), faqMeta);
                            editor.commit();

                            // update our copy of faqr metadata
                            currFaqMeta = new FaqMeta(prefs.getString(FaqrApp.validFileName(currFaqURL), ""));

                            // parse the content
                            Elements pre = doc.select("pre");
                            for (Element elem : pre) {
                                // text node prevents the removal of leading and trailing whitespace
                                List<TextNode> nodes = elem.textNodes();
                                for (TextNode node : nodes) {
                                    content += node.getWholeText();
                                }
                            }

                            // checking for ASCII FAQ determined by amount of stuff in <pre> tags
                            if (content.length() < 10000) {

                                Elements imgresize = doc.select(".imgmain img.imgresize");

                                if (imgresize.size() > 0) {
                                    String imagePath = imgresize.get(0).attr("src");
                                    String[] imagePathSplit = imagePath.split("/");
                                    String imageName = imagePathSplit[imagePathSplit.length - 1];

                                    InputStream in = new URL(imagePath).openConnection().getInputStream();

                                    File fileUri = new File(getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                                    FileOutputStream outStream = null;
                                    outStream = new FileOutputStream(fileUri);

                                    byte[] buffer = new byte[1024];
                                    int len1 = 0;
                                    while ((len1 = in.read(buffer)) > 0) {
                                        outStream.write(buffer, 0, len1);
                                    }
                                    outStream.close();

                                    // write the image to the disk
                                    Log.w(TAG, "===============================================");
                                    Log.w(TAG, "WRITING FILE " + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                                    Log.w(TAG, "===============================================");

                                    // append the image information to the url;
                                    if (faqMeta.split(" --- ").length == 7)
                                        faqMeta = faqMeta + " --- " + "TYPE=IMAGE";
                                    editor.putString(FaqrApp.validFileName(currFaqURL), faqMeta);
                                    editor.commit();

                                    // return a new status code
                                    return "3";
                                }


                                // we didn't get an image apparently
                                return "-99";
                            }

                            lines = FaqrApp.getLines(content);
                            origLines = new String[lines.length];
                            System.arraycopy(lines, 0, origLines, 0, lines.length);

                            Log.w(TAG, "WEB LINES.SIZE " + lines.length);
                            Log.w(TAG, "===============================================");

                            success = true;

                        } else {

                            // not a gamefaqs url
                            return "-98";
                        }

                    } catch (Exception e) {
                        // error reading from web
//                        if (e.getMessage() != null)
                        Log.e(TAG, e.getMessage(), e);
                        result = "-1";
                    } catch (OutOfMemoryError e) {
                        // memory exception on device
//                        if (e.getMessage() != null)
                        Log.e(TAG, e.getMessage(), e);
                        result = "-2";
                    }

                    // only try to write if we got a file
                    if (success) {
                        try {
                            Log.w(TAG, "===============================================");
                            Log.w(TAG, "WRITING FILE " + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                            Log.w(TAG, "===============================================");

                            // delete and refresh
                            // getFileStreamPath(validFileName(prefs.getString("faq_title", ""))).delete();
                            FaqrApp.writeData(openFileOutput(FaqrApp.validFileName(currFaqURL), Context.MODE_PRIVATE), content);

                            result = "1";

                        } catch (Exception e) {
                            // error writing the file
                            Log.e(TAG, e.getMessage());
                            result = "2";
                        }
                    }
                } else {
                    // no network available
                    result = "-3";
                }
            }

            return result;
        }

        protected void onPostExecute(String result) {
            if (result.equals("-999")) {

                // :'-(
                // we have a big problem because it seems the faq meta data is corrupted somehow
                Toast.makeText(getApplicationContext(), "Sorry an unknown error occured. You may want to delete and re-download this FAQ.", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("-99")) {

                // :-|
                // web based faq

                webView.loadUrl(currFaqMeta.getUrl().trim()); // + "?single=1");
                // this will create a place holder file even tho we aren't using the fucking web archive which sux!
                try {
                    webView.saveWebArchive(getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                } catch (Exception e) {
                    // java.lang.NoSuchMethodError: android.webkit.WebView.saveWebArchive ???
                }

                webViewActive = true;

                File file = new File(getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                if (!file.exists())
                    try {
                        FaqrApp.writeData(new FileOutputStream(file), "");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }

                Log.w(TAG, "===============================================");
                Log.w(TAG, "WRITING FILE " + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                Log.w(TAG, "===============================================");

                String faqMeta = prefs.getString(FaqrApp.validFileName(currFaqURL), "");
                if (faqMeta.split(" --- ").length == 7)
                    faqMeta = faqMeta + " --- " + "TYPE=HTML";
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(FaqrApp.validFileName(currFaqURL), faqMeta);
                editor.commit();

            } else if (result.equals("-98")) {

                // :-\
                // not a gamefaqs url we might do something different here

                // webview time!
                webView.loadUrl(currFaqMeta.getUrl().trim());

                // this will create a place holder file even tho we aren't using the fucking web archive which sux!
                webView.saveWebArchive(getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());

                webViewActive = true;

                getSupportActionBar().setTitle(currFaqMeta.getGameTitle());
                getSupportActionBar().setSubtitle(currFaqMeta.getTitle() + " by " + currFaqMeta.getAuthor());
                getSupportActionBar().setIcon(android.R.color.transparent);

                Log.w(TAG, "===============================================");
                Log.w(TAG, "WRITING FILE " + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());
                Log.w(TAG, "===============================================");

                String faqMeta = prefs.getString(FaqrApp.validFileName(currFaqURL), "");
                if (faqMeta.split(" --- ").length == 7)
                    faqMeta = faqMeta + " --- " + "TYPE=HTML";
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(FaqrApp.validFileName(currFaqURL), faqMeta);
                editor.commit();

            } else if (result.equals("-3")) {

                // :-(
                // no connectivity
                // connectionDialog.show();
                Log.e(TAG, "Result -3: No connectivity");
                Toast.makeText(getApplicationContext(), "There is no internet connection available, Please connect to wifi or data plan and try again.", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("-2")) {

                // :'-(
                // error in the web access
                Log.e(TAG, "Result -2: Error in the web access");
                Toast.makeText(getApplicationContext(), "Sorry an out of memory exception occured. ", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("-1")) {

                // :'-(
                // error in the web access
                Log.e(TAG, "Result -1: Error in the web access");
                Toast.makeText(getApplicationContext(), "Sorry an error occured. Please try again.", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("3") || result.equals("4")) {

                // :-)
                // we got an image downloaded to show and we are very happy
                if (result == "3") {
                    Toast.makeText(getApplicationContext(), "Successfully saved image to device.", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Successfully saved image to device.");
                } else {

                }
                webViewActive = true;

                getSupportActionBar().setTitle(currFaqMeta.getGameTitle());
                getSupportActionBar().setSubtitle(currFaqMeta.getTitle() + " by " + currFaqMeta.getAuthor());
                getSupportActionBar().setIcon(android.R.color.transparent);

                // theme stuff
                String html = "";
                String bgColor = "#FFFFFF";
                if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
                } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
                    bgColor = "#000000";
                } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
                    bgColor = "#2F2F2F";
                } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
                    bgColor = "#ECE1CA";
                }

                String paddingTop = "64px";
                if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                    Integer paddingImmersive = getStatusBarHeight() + getActionBarHeight();
                    paddingTop = paddingImmersive.toString() + "px";
                }

                html = ("<html><title>" + title + "</title><body style=\"background-color:" + bgColor + ";\"><div style=\"width:100%\"><img style=\"display:block;margin:0 auto;padding-top:" + paddingTop + ";\" src=\"file://" + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath() + "\"></body></html>");
                webView.loadDataWithBaseURL(getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath(), html, "text/html", "utf-8", "");

                hideWebViewMenuOptions = true;
                loading.setVisibility(View.GONE);
                error.setVisibility(View.GONE);

            } else if (result == "5") {

                webViewActive = true;

                // we attempt to load the page from the cache if no network connection
                if (!isNetworkAvailable()) {
                    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                }

                String url = currFaqMeta.getUrl().trim();
                if (!prefs.getString(currFaqMeta.getUrl().trim() + "curr_url", "").equals("")) {
                    url = prefs.getString(currFaqMeta.getUrl().trim() + "curr_url", "");
//                    Toast.makeText(getApplicationContext(), "Load URL -- " + url, Toast.LENGTH_LONG).show();
                }

                webView.loadUrl(url);

            } else {

                // write to device status (not a fatal error)
                if (result == "1") {
                    Toast.makeText(getApplicationContext(), "Successfully saved FAQ to device.", Toast.LENGTH_LONG).show();
                } else if (result == "2") {
                    Toast.makeText(getApplicationContext(), "Error saving FAQ to device.", Toast.LENGTH_LONG).show();
                }

                // :-)
                // we got a faq to show and we are very happy!
                getSupportActionBar().setTitle(currFaqMeta.getGameTitle());
                getSupportActionBar().setSubtitle(currFaqMeta.getTitle() + " by " + currFaqMeta.getAuthor());
                getSupportActionBar().setIcon(android.R.color.transparent);

                // set the listview position
                if (faqmarkPos > -1) {
                    // if there is a faqmark we are returning to
                    if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                        listView.setSelectionFromTop(faqmarkPos, getActionBarHeight() + getStatusBarHeight());
                    } else {
                        listView.setSelection(faqmarkPos);
                    }

                    String plusOne = new Integer(faqmarkPos + 1).toString();
                    // double percentage = (new Double(plusOne) / new Double(lines.length)) * 100.0;
                    // DecimalFormat df = new DecimalFormat("#");
                    Toast.makeText(getApplicationContext(), "FAQMark " + plusOne + "/" + lines.length, Toast.LENGTH_SHORT).show();

                } else {
                    // otherwise use the curr_pos
                    if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                        listView.setSelectionFromTop(prefs.getInt(FaqrApp.validFileName(currFaqMeta.getUrl()) + "curr_pos", 0), getActionBarHeight() + getStatusBarHeight());
                    } else {
                        listView.setSelection(prefs.getInt(FaqrApp.validFileName(currFaqMeta.getUrl()) + "curr_pos", 0));
                    }
                }

                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);

                // fancy animations
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
                error.setVisibility(View.GONE);
            }

        }
    };

    /**
     * Save the current position
     *
     * @author eneve
     */
    private class CurrPosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FaqrApp.validFileName(currFaqMeta.getUrl()) + "curr_pos", Integer.valueOf(strings[0]).intValue());
            editor.commit();
            return "";
        }
    };

    /**
     * Save the saved position
     *
     * @author eneve
     */
    private class SavedPosTask extends AsyncTask<String, Void, String> {

        String savedPos = "-1";

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences.Editor editor = prefs.edit();

            String result = "0";
            if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {

                String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", "");

                savedPos = strings[0];

                boolean found = false;
                String[] savedPosMultiList = savedPosMulti.split(",");
                StringBuffer newSavedPosMulti = new StringBuffer();
                for (int i = 0; i < savedPosMultiList.length; i++) {
                    if (savedPosMultiList[i].equals(strings[0])) {
                        found = true;
                    } else {
                        newSavedPosMulti.append(savedPosMultiList[i] + ",");
                    }
                }

                if (found) {
                    savedPosMulti = newSavedPosMulti.toString();
                    result = "1";
                } else {
                    savedPosMulti = savedPosMulti + strings[0] + ",";
                }

                editor.putString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", savedPosMulti);
                editor.commit();

            } else {
                editor.putInt(FaqrApp.validFileName(currFaqMeta.getUrl()) + "saved_pos", Integer.valueOf(strings[0]).intValue());
                editor.commit();
            }
            return result;
        }

        protected void onPostExecute(String result) {

            // vibrate dat
            if (prefs.getBoolean("vibrate", getResources().getBoolean(R.bool.vibrate_default))) {
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100); // vibrate for 3 seconds (e.g 3000 milliseconds)
            }

            String savedPosPlusOne = new Integer(Integer.valueOf(savedPos) + 1).toString();

            // if faqmarks
            if (!TextUtils.isEmpty(prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", ""))) {
                faqmarksItem.setEnabled(true);
            } else {
                faqmarksItem.setEnabled(false);
            }

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "Saved FAQMark " + savedPosPlusOne + "/" + lines.length, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Removed FAQMark " + savedPosPlusOne + "/" + lines.length, Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * Find Prev Task
     *
     * @author eneve
     */
    private class FindPrevTask extends AsyncTask<String, Void, String> {

        private boolean showToast = false;

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {

            if (!TextUtils.isEmpty(findString)) {
                int count = lines.length - 1;
                int position = -1;
                for (int i = lines.length - 1; i > 0; i--) {
                    String line = lines[i];
                    int startIndex = line.toLowerCase().indexOf(findString.toLowerCase());
                    if (startIndex != -1 && count < currFindPos) {
                        position = count;
                        break;
                    }
                    count--;
                }
                if (position > -1) {
                    currFindPos = position;
                    return Integer.valueOf(position).toString();
                } else {
                    count = lines.length - 1;
                    position = -1;
                    for (int i = lines.length - 1; i > 0; i--) {
                        String line = lines[i];
                        int startIndex = line.toLowerCase().indexOf(findString.toLowerCase());
                        if (startIndex != -1 && count < lines.length - 1) {
                            position = count;
                            break;
                        }
                        count--;
                    }
                    if (position > -1 && position != currFindPos) {
                        showToast = true;
                        currFindPos = position;
                        return Integer.valueOf(position).toString();
                    }
                }
            }
            return "-1";
        }

        protected void onPostExecute(String result) {
            if (showToast)
                Toast.makeText(getApplicationContext(), "Wrapped Search.", Toast.LENGTH_SHORT).show();
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            } else {
                listView.setSelection(Integer.valueOf(result).intValue());
            }
        }
    };

    /**
     * Find Next Task
     *
     * @author eneve
     */
    private class FindNextTask extends AsyncTask<String, Void, String> {

        private boolean showToast = false;
        private int mCurrentPosition = 0;

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            mCurrentPosition = listView.getFirstVisiblePosition() + 1;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (!TextUtils.isEmpty(findString)) {
                int count = 0;
                int position = -1;
                for (String line : lines) {
                    int startIndex = line.toLowerCase().indexOf(findString.toLowerCase());
                    if (startIndex != -1 && count != currFindPos && count > mCurrentPosition) {
                        position = count;
                        break;
                    }
                    count++;
                }
                if (position > -1) {
                    currFindPos = position;
                    return Integer.valueOf(position).toString();
                } else {
                    count = 0;
                    position = -1;
                    for (String line : lines) {
                        int startIndex = line.toLowerCase().indexOf(findString.toLowerCase());
                        if (startIndex != -1 && count > 0) {
                            position = count;
                            break;
                        }
                        count++;
                    }
                    if (position > -1 && position != currFindPos) {
                        showToast = true;
                        currFindPos = position;
                        return Integer.valueOf(position).toString();
                    }
                }
            }
            return "-1";
        }

        protected void onPostExecute(String result) {
            if (showToast)
                Toast.makeText(getApplicationContext(), "Wrapped Search.", Toast.LENGTH_SHORT).show();

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            } else {
                listView.setSelection(Integer.valueOf(result).intValue());
            }
        }
    };

    /**
     * Goto Prev Task
     *
     * @author eneve
     */
    private class GotoPrevTask extends AsyncTask<String, Void, String> {

        private boolean showToast = false;

        private int position = 0;

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            position = listView.getFirstVisiblePosition() + 1;
        }

        @Override
        protected String doInBackground(String... strings) {


            String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", "");

            if (!savedPosMulti.equals("")) {
                String[] savedPosMultiList = savedPosMulti.split(",");

                Integer[] ints = new Integer[savedPosMultiList.length];
                for (int i = 0; i < savedPosMultiList.length; i++) {
                    ints[i] = Integer.valueOf(savedPosMultiList[i]);
                }

                Arrays.sort(ints, Collections.reverseOrder());

                for (int i = 0; i < ints.length; i++) {
                    if (Integer.valueOf(ints[i]) < position && Integer.valueOf(ints[i]) < currGotoPos) {
                        // Log.w(TAG, "POSITION " + position + " PREV " + ints[i]);
                        currGotoPos = Integer.valueOf(ints[i]);
                        return new Integer(ints[i]).toString();
                    }
                }

                // if we didn't return then we wrap and start from beginning
                showToast = true;
                for (int i = 0; i < ints.length; i++) {
                    currGotoPos = Integer.valueOf(ints[i]);
                    return new Integer(ints[i]).toString();
                }
            }

            return "-1";
        }

        protected void onPostExecute(String result) {
            if (result.equals("-1"))
                Toast.makeText(getApplicationContext(), "No FAQmark Found.", Toast.LENGTH_SHORT).show();
            else if (showToast)
                Toast.makeText(getApplicationContext(), "Wrapped Search.", Toast.LENGTH_SHORT).show();

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            } else {
                listView.setSelection(Integer.valueOf(result).intValue());
            }
        }
    };

    /**
     * Goto Next Task
     *
     * @author eneve
     */
    private class GotoNextTask extends AsyncTask<String, Void, String> {

        private boolean showToast = false;

        private int position = 0;

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            position = listView.getFirstVisiblePosition() + 1;
        }

        @Override
        protected String doInBackground(String... strings) {
            String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta.getUrl()) + "multi_saved_pos", "");

            if (!savedPosMulti.equals("")) {
                String[] savedPosMultiList = savedPosMulti.split(",");

                Integer[] ints = new Integer[savedPosMultiList.length];
                for (int i = 0; i < savedPosMultiList.length; i++) {
                    ints[i] = Integer.valueOf(savedPosMultiList[i]);
                }

                Arrays.sort(ints);

                for (int i = 0; i < ints.length; i++) {
                    if (Integer.valueOf(ints[i]) > position && Integer.valueOf(ints[i]) > currGotoPos) {
                        currGotoPos = Integer.valueOf(ints[i]);
                        return new Integer(ints[i]).toString();
                    }
                }

                // if we didn't return then we wrap and start from beginning
                showToast = true;
                for (int i = 0; i < ints.length; i++) {
                    currGotoPos = Integer.valueOf(ints[i]);
                    return new Integer(ints[i]).toString();
                }
            }

            return "-1";
        }

        protected void onPostExecute(String result) {
            if (result.equals("-1"))
                Toast.makeText(getApplicationContext(), "No FAQmark Found.", Toast.LENGTH_SHORT).show();
            else if (showToast)
                Toast.makeText(getApplicationContext(), "Wrapped Search.", Toast.LENGTH_SHORT).show();

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            } else {
                listView.setSelection(Integer.valueOf(result).intValue());
            }
        }
    };

    // Calculate the % of scroll progress in the actual web page content
    // http://stackoverflow.com/questions/6855715/maintain-webview-content-scroll-position-on-orientation-change
    private float calculateProgression(WebView content) {
        float positionTopView = content.getTop();
        float contentHeight = content.getContentHeight();
        float currentScrollPosition = content.getScrollY();
        float percentWebview = (currentScrollPosition - positionTopView) / contentHeight;
        return percentWebview;
    }



    // //////////////////////
    // / IMMERSIVE STUFF

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // When the window loses focus (e.g. the action overflow is shown),
        // cancel any pending hide action. When the window gains focus,
        // hide the system UI.
        if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
            if (!getFind() && !goTo) {
                if (hasFocus) {
                    delayedHide(INITIAL_HIDE_DELAY);
                } else {
                    mHideHandler.removeMessages(0);
                }
            }
        }
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE);

            Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_anim);
            if (!toolbarAnim)
                toolbar.startAnimation(slide);
            slide.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0) {
                    toolbarAnim = true;
                    toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    toolbarAnim = false;
                    getSupportActionBar().hide();
                }
            });
        } else {
            if (!webViewActive) {
                mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_anim);
                if (!toolbarAnim)
                    toolbar.startAnimation(slide);

                slide.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationStart(Animation arg0) {
                        toolbarAnim = true;
                        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        toolbarAnim = false;
                        getSupportActionBar().hide();
                    }
                });

            } else if (webViewActive) {
                mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_anim);
                if (!toolbarAnim)
                    toolbar.startAnimation(slide);

                slide.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationStart(Animation arg0) {
                        toolbarAnim = true;
                        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        toolbarAnim = false;
                        getSupportActionBar().hide();
                    }
                });
            }
        }
    }

    private void showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_anim);
            if (!toolbarAnim)
                toolbar.startAnimation(slide);

            slide.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0) {
                    toolbarAnim = true;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    toolbarAnim = false;
                    getSupportActionBar().show();
                }
            });

        } else  {
            mDecorView.setSystemUiVisibility(0);

            Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_anim);
            if (!toolbarAnim)
                toolbar.startAnimation(slide);

            slide.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0) {
                    toolbarAnim = true;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    toolbarAnim = false;
                    getSupportActionBar().show();
                }
            });
        }
    }

    private final Handler mHideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!getFind() && !goTo)
                hideSystemUI();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeMessages(0);
        mHideHandler.sendEmptyMessageDelayed(0, delayMillis);
    }

    @Override
    /** Called when the phone orientation is changed */
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        // forces the recalculation of the mono font size
        autoMonoFontSize = -1.0f;
        configureOrientation(config.orientation);
    }

    public void configureOrientation(int orientation) {
        switch(orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                setupLandscape();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                setupPortrait();
                break;
        }
    }

    /**
     * Called when Portrait
     */
    public void setupPortrait() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
                params.setMargins(0, getStatusBarHeight(), 0, 0);
                toolbar.setLayoutParams(params);
            } else {
                RelativeLayout background = (RelativeLayout) findViewById(R.id.bg);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) background.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.toolbar);
                background.setLayoutParams(params);
            }
        } else {
            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                // nothing
            } else {
                RelativeLayout background = (RelativeLayout) findViewById(R.id.bg);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) background.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.toolbar);
                background.setLayoutParams(params);
            }
        }
    }

    /**
     * Called when landscape
     */
    public void setupLandscape() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
                params.setMargins(0, getStatusBarHeight(), 0, 0);

                // fix the toolbar width when nav bar on right
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                int fullscreenheight = metrics.heightPixels;
                int fullscreenwidth = metrics.widthPixels;
                params.width = fullscreenwidth;

                toolbar.setLayoutParams(params);
            }else {
                RelativeLayout background = (RelativeLayout) findViewById(R.id.bg);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) background.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.toolbar);
                background.setLayoutParams(params);
            }
        } else {
            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                // nothing
            } else {
                RelativeLayout background = (RelativeLayout) findViewById(R.id.bg);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) background.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.toolbar);
                background.setLayoutParams(params);
            }
        }
    }

    public boolean getFind() {
        return find;
    }

    public void setFind(boolean status) {
        find = status;
    }

}
