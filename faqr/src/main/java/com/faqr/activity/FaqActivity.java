/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.faqr.FaqrApp;
import com.faqr.R;

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
public class FaqActivity extends BaseActivity implements OnClickListener {

    // immersive
    private static final int INITIAL_HIDE_DELAY = 2700;
    private View mDecorView;

    // loading
    private LinearLayout loading;
    private LinearLayout error;

    // find bar
    private LinearLayout find_bar;
    private Button find_bar_prev;
    private Button find_bar_next;
    private Button find_bar_close;
    private EditText find_bar_text;

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
    private WebView webView;
    private boolean webViewActive = false;

    private float autoMonoFontSize = -1.0f;

    // current faq info
    private String currFaq = "";
    private String[] currFaqMeta = new String[] {};

    // faqmark
    private Integer faqmarkPos = -1;

    /** Called when the activity is first created. */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // theme stuff

        // DO NOT DELETE YET DO NOT DELETE YET DO NOT DELETE YET

        // if (prefs.getBoolean("use_dark_theme", getResources().getBoolean(R.bool.use_dark_theme_default))) {
        // if (prefs.getBoolean("use_true_black", getResources().getBoolean(R.bool.use_true_black_default))) {
        // if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
        // setTheme(R.style.AppBlackOverlayTheme);
        // }
        // } else {
        // if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
        // setTheme(R.style.AppDarkOverlayTheme);
        // }
        // }
        // } else {
        // if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
        // setTheme(R.style.AppLightOverlayTheme);
        // }
        // }

        setContentView(R.layout.activity_faq);

        // theme goodness
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
            RelativeLayout bg = (RelativeLayout) findViewById(R.id.bg);
            bg.setBackgroundColor(0xFFECE1CA);
            themeColor = getResources().getColor(R.color.sepia_theme_color);

        }

        // show back if we came from search
        // if (extras != null && extras.getBoolean("from_search") == true) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setIcon(android.R.color.transparent);
        // }=
        actionBar.setDisplayUseLogoEnabled(false);

        extras = getIntent().getExtras();
        if (extras != null) {
            faqmarkPos = extras.getInt("FAQmarkPosition", -1);
            // int my_faqs_pos = prefs.getInt("my_faqs_pos", 0);
            // listView.setSelection(my_faqs_pos);
        }

        // set the list adapter
        adapter = new FaqAdapter();
        // setListAdapter(adapter);
        // ListView listView = listView;

        listView = (ListView) findViewById(R.id.list);
        // listView.setOnItemClickListener(adapter.itemClickListener);
        listView.setAdapter(adapter);

        // listView.setTextFilterEnabled(true);

        // loading indicator
        loading = (LinearLayout) findViewById(R.id.loading);
        error = (LinearLayout) findViewById(R.id.error);

        // Android < 3.0 find bar
        find_bar = (LinearLayout) findViewById(R.id.find_bar);
        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        find_bar_prev = (Button) findViewById(R.id.btn_prev);
        find_bar_prev.setOnClickListener(this);
        find_bar_next = (Button) findViewById(R.id.btn_next);
        find_bar_next.setOnClickListener(this);
        find_bar_close = (Button) findViewById(R.id.btn_close);
        find_bar_close.setOnClickListener(this);
        find_bar_text = (EditText) findViewById(R.id.find_text);
        find_bar_text.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // do nothing
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // find_text_changed(s.toString());
                new FindTextChangedTask().execute(new String[] { s.toString() });
            }
        });
        find_bar_text.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(find_bar_text.getWindowToken(), 0);
                    return true;
                } else {
                    return false;
                }
            }
        });
        // }
        find_bar.setVisibility(View.GONE);

        /** called when a list item is clicked */
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                    if (!find && !goTo) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                            if (visible) {
                                hideSystemUI();
                            } else {
                                showSystemUI();
                            }
                        } else {

                            // boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0;
                            // if (visible) {
                            // hideSystemUI();
                            // } else {
                            // showSystemUI();
                            // }

                            if (getSupportActionBar().isShowing()) {
                                hideSystemUI();
                            } else {
                                showSystemUI();
                            }
                            // }
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

                // show action bar
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // if (!getSupportActionBar().isShowing() && !find)
                // getSupportActionBar().show();
                // }

                // save the postiion
                if (prefs.getBoolean("highlight_saved_pos", getResources().getBoolean(R.bool.highlight_saved_position_default))) {
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                }

                return true;
            }
        });

        listView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
                // long currTime = System.currentTimeMillis();
                // if (currTime - prevCurrTime > 200) {
                // if (prevFirstVisibleItem != 0) {
                // if (firstVisibleItem > prevFirstVisibleItem && !find && !goTo) {
                // if (getSupportActionBar().isShowing())
                // getSupportActionBar().hide();
                // } else if (firstVisibleItem < prevFirstVisibleItem) {
                // if (!getSupportActionBar().isShowing())
                // getSupportActionBar().show();
                // }
                // }
                // prevFirstVisibleItem = firstVisibleItem;
                // prevCurrTime = currTime;
                // }
                // }
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // turn down the lights
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    // if (prefs.getBoolean("use_lights_out", getResources().getBoolean(R.bool.use_lights_out_default))) {
                    // if (listView.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LOW_PROFILE) {
                    // listView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    // }
                    // }
                }
            }
        });

        // quit dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        // fit the width of screen
        // webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // remove a weird white line on the right size
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        // webView.getSettings().setSupportZoom(true);
        // webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        // webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
        }

        // Cookie sessionCookie = myapp.cookie;
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        // if (sessionCookie != null) {
        // cookieManager.removeSessionCookie();
        // String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain=" + sessionCookie.getDomain();
        cookieManager.setCookie(".gamefaqs.com", "css_color=" + themeCssColor + "; Domain=.gamefaqs.com");
        CookieSyncManager.getInstance().sync();
        // }

        webView.setInitialScale(1);

        // caching - larger for newer devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setAppCacheMaxSize(10 * 1024 * 1024); // 10MB
        } else {
            webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        }
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
            public void onPageFinished(WebView view, String url) {
                // new SleepyTask().execute(new String[] {});

                // set it to try later and try here anyway if perchance menu isn't null
                hideWebViewMenuOptions = true;
                if (null != menu) {
                    MenuItem opt = menu.findItem(R.id.menu_find);
                    opt.setVisible(false);
                    // opt = menu.findItem(R.id.menu_goto);
                    // opt.setVisible(false);
                    opt = menu.findItem(R.id.menu_faqmarks);
                    opt.setVisible(false);
                }

                loading.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);

                String faqMeta = prefs.getString(prefs.getString("curr_faq", ""), "");
                String[] currFaqMeta = faqMeta.split(" --- ");
                // String faqMeta = prefs.getString(Faqr.validFileName(cxurrFaqURL), "");
                String currFaqURL = currFaqMeta[5];

                if (view != null && view.getTitle() != null) {
                    String title = view.getTitle();
                    title = title.replaceAll(" - GameFAQs", "");
                    title = title.replaceAll("GameFAQs: ", "");
                    title = title.replaceAll(" - GameSpot.com", "");
                    title = title.replaceAll("GameSpot.com: ", "");
                    title = title.replaceAll("GameSpot", "");
                    title = title.replaceAll("GameFAQs", "");
                    title = title.replaceAll("-", "");
                    setTitle(title.trim());

                    getSupportActionBar().setTitle(title.trim());
                    getSupportActionBar().setSubtitle(currFaqURL.replaceAll("http://", "").replaceAll("https://", ""));
                }

                getSupportActionBar().setIcon(android.R.color.transparent);
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

        // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default)) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams();
        // params.setMargins(0, getActionBarHeight(), 0, 0);
        // webView.setLayoutParams(params);
        // }

        // get the current FAQ
        if (!TextUtils.isEmpty(prefs.getString("curr_faq", ""))) {
            currFaq = prefs.getString("curr_faq", "");
            // currFaq example
            // http___m_gamefaqs_com_psp_615911-final-fantasy-iv-the-complete-collection_faqs_62211
            String faqMeta = prefs.getString(prefs.getString("curr_faq", ""), "");

            // set last read date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(prefs.getString("curr_faq", "") + "___last_read", sdf.format(new Date()));

            // faqMeta example
            // Final Fantasy IV FAQ/Walkthrough --- 09/20/11 --- Johnathan 'Zy' Sawyer --- 1.02 --- 1267K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/62211
            Log.w(TAG, "-----------------------------");
            Log.w(TAG, currFaq);
            Log.w(TAG, faqMeta);
            Log.w(TAG, "-----------------------------");
            currFaqMeta = faqMeta.split(" --- ");

            int curr_pos = prefs.getInt(prefs.getString("curr_faq", "") + "curr_pos", -1);
            int saved_pos = prefs.getInt(prefs.getString("curr_faq", "") + "saved_pos", -1);
            // Toast.makeText(getApplicationContext(), faqMeta + " " + curr_pos + " " + saved_pos, Toast.LENGTH_LONG).show();
            new GetFaqTask().execute(new String[] {});
        } else {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();
        }

        // immersive webview
        mDecorView = getWindow().getDecorView();
        if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int flags) {
                    boolean visible = (flags & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                    // controlsView.animate()
                    // .alpha(visible ? 1 : 0)
                    // .translationY(visible ? 0 : controlsView.getHeight());
                }
            });
            // webView.setClickable(true);
            final GestureDetector clickDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                    if (visible) {
                        hideSystemUI();
                    } else {
                        showSystemUI();
                    }
                    return true;
                    // }

                    // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    // boolean visible = getSupportActionBar().isShowing();
                    // if (visible) {
                    // hideSystemUI();
                    // } else {
                    // showSystemUI();
                    // }
                    // return true;
                    // } else {
                    // boolean visible = getSupportActionBar().isShowing();
                    // if (visible) {
                    // hideSystemUI();
                    // } else {
                    // showSystemUI();
                    // }
                    // return true;
                    // }
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
                    // } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    // boolean visible = getSupportActionBar().isShowing();
                    // if (visible) {
                    // return clickDetector.onTouchEvent(motionEvent);
                    // } else {
                    // return false;
                    // }
                    // } else {
                    // boolean visible = getSupportActionBar().isShowing();
                    // if (visible) {
                    // return clickDetector.onTouchEvent(motionEvent);
                    // } else {
                    // return false;
                    // }
                    // }

                    // boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                    // if (visible) {
                    // return clickDetector.onTouchEvent(motionEvent);
                    // } else {
                    // return false;
                    // }
                }
            });

            showSystemUI();
        }
    }

    /** Called when the activity will start interacting with the user. */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();

        // low profile
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        //
        // if (prefs.getBoolean("use_lights_out", getResources().getBoolean(R.bool.use_lights_out_default))) {
        // listView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        // }
        // }

    }

    /** Called when phone hard keys are pressed */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webView.canGoBack() == true) {
                webView.goBack();
                return true;
            } else {
                if (goTo) {
                    goTo = false;
                    MenuItem gotoPrev = menu.findItem(R.id.menu_goto_prev);
                    gotoPrev.setVisible(false);
                    MenuItem gotoNext = menu.findItem(R.id.menu_goto_next);
                    gotoNext.setVisible(false);
                    MenuItem gotoClose = menu.findItem(R.id.menu_goto_close);
                    gotoClose.setVisible(false);
                    // new GotoTimeoutTask().execute(new String[] {});
                    MenuItem opt;
                    // MenuItem opt = menu.findItem(R.id.menu_goto);
                    // opt.setVisible(true);
                    // opt = menu.findItem(R.id.menu_lock);
                    // opt.setVisible(false);

                    if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
                        opt = menu.findItem(R.id.menu_faqmarks);
                        opt.setVisible(true);
                    }
                    opt = menu.findItem(R.id.menu_find);
                    opt.setVisible(true);
                    opt = menu.findItem(R.id.menu_browser);
                    opt.setVisible(true);
                    opt = menu.findItem(R.id.menu_settings);
                    opt.setVisible(true);
                    opt = menu.findItem(R.id.menu_about);
                    opt.setVisible(true);
                }

                if (extras == null || (extras != null && extras.getBoolean("from_search") != true)) {
                    Intent intent = new Intent(this, MyFaqsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        } else if ((keyCode == KeyEvent.KEYCODE_SEARCH)) {
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchMenuItem = menu.findItem(R.id.menu_find);
            searchView = (SearchView) searchMenuItem.getActionView();
            searchMenuItem.expandActionView();

            if (!getSupportActionBar().isShowing())
                getSupportActionBar().show();
            MenuItem prev = menu.findItem(R.id.menu_prev);
            prev.setVisible(true);
            MenuItem next = menu.findItem(R.id.menu_next);
            next.setVisible(true);
            MenuItem opt = menu.findItem(R.id.menu_faqmarks);
            opt.setVisible(false);
            // opt = menu.findItem(R.id.menu_goto);
            // opt.setVisible(false);
            opt = menu.findItem(R.id.menu_browser);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_settings);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_about);
            opt.setVisible(false);

            // } else {
            // // Android < 3.0 Find
            // getSupportActionBar().hide();
            // find_bar.setVisibility(View.VISIBLE);
            // find = true;
            // }
            return true;
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
//        EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
//        EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }

    @Override
    /** Called when the phone orientation is changed */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // forces the recalculation of the mono font size
        autoMonoFontSize = -1.0f;
    }

    /** Called when a button is clicked */
    public void onClick(View view) {
        if (view == find_bar_prev) {
            // find_prev();
            new FindPrevTask().execute(new String[] {});
        } else if (view == find_bar_next) {
            // find_next();
            new FindNextTask().execute(new String[] {});
        } else if (view == find_bar_close) {
            // Android < 3.0 Find
            find_bar.setVisibility(View.GONE);
            getSupportActionBar().show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(find_bar_text.getWindowToken(), 0);
            find_bar_text.setText("");
            findString = "";
            find = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        final Menu finalMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_faq, menu);

        // Get the SearchView and set the searchable configuration
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_find);
        searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.find_hint));
            searchView.setIconifiedByDefault(true);

            LinearLayout searchText = (LinearLayout) searchView.findViewById(R.id.search_plate);
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
                    new FindTextChangedTask().execute(new String[] { s });
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // close the search view
                    // Toast.makeText(getApplicationContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                    // finalMenu.findItem(R.id.menu_search).collapseActionView();
                    return true;
                }
            });

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Log.i(TAG, "onMenuItemActionCollapse " + item.getItemId());
                    MenuItem prev = finalMenu.findItem(R.id.menu_prev);
                    prev.setVisible(false);
                    MenuItem next = finalMenu.findItem(R.id.menu_next);
                    next.setVisible(false);
                    // MenuItem opt = finalMenu.findItem(R.id.menu_downloads);
                    // opt.setVisible(true);
                    // MenuItem opt = finalMenu.findItem(R.id.menu_search);
                    // opt.setVisible(true);
                    MenuItem opt;
                    // MenuItem opt = finalMenu.findItem(R.id.menu_goto);
                    // opt.setVisible(true);
                    // opt = finalMenu.findItem(R.id.menu_lock);
                    // opt.setVisible(true);
                    // if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
                    opt = finalMenu.findItem(R.id.menu_faqmarks);
                    opt.setVisible(true);
                    // }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        opt = finalMenu.findItem(R.id.menu_display_options);
                        opt.setVisible(true);
                    }
                    opt = finalMenu.findItem(R.id.menu_browser);
                    opt.setVisible(true);
                    opt = finalMenu.findItem(R.id.menu_settings);
                    opt.setVisible(true);
                    opt = finalMenu.findItem(R.id.menu_about);
                    opt.setVisible(true);

                    getSupportActionBar().setIcon(android.R.color.transparent);

                    find = false;
                    currFindPos = 0;

                    return true; // Return true to collapse action view
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    find = true;
                    Log.i(TAG, "onMenuItemActionExpand " + item.getItemId());
                    return true;
                }
            });
        }

        MenuItem findItem = menu.findItem(R.id.menu_find);
        // MenuItem gotoItem = menu.findItem(R.id.menu_goto);
        faqmarksItem = menu.findItem(R.id.menu_faqmarks);
        if (hideWebViewMenuOptions) {
            findItem.setVisible(false);
            // gotoItem.setVisible(false);
            faqmarksItem.setVisible(false);
        }

        // if faqmarks
        if (!TextUtils.isEmpty(prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", ""))) {

        } else {
            faqmarksItem.setEnabled(false);
        }

        // initialize the orientation lock
        // if (prefs.getInt("orientation_locked", -1) >= 0) {
        // MenuItem lockMenuItem = menu.findItem(R.id.menu_lock);
        // lockMenuItem.setTitle("Unlock Orientation");
        // }

        MenuItem prev = menu.findItem(R.id.menu_prev);
        prev.setVisible(false);
        MenuItem next = menu.findItem(R.id.menu_next);
        next.setVisible(false);

        MenuItem gotoPrev = menu.findItem(R.id.menu_goto_prev);
        gotoPrev.setVisible(false);
        MenuItem gotoNext = menu.findItem(R.id.menu_goto_next);
        gotoNext.setVisible(false);
        MenuItem gotoClose = menu.findItem(R.id.menu_goto_close);
        gotoClose.setVisible(false);

        if (!prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
            MenuItem opt = menu.findItem(R.id.menu_faqmarks);
            opt.setVisible(false);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            MenuItem opt;
            opt = finalMenu.findItem(R.id.menu_display_options);
            opt.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

        case R.id.menu_display_options:

            View menuItemView = findViewById(R.id.bg); // SAME ID AS MENU ID

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.display_options, null, false),600, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bottom_solid_faqr_dark));
            // The code below assumes that the root container has an id called 'main'
            pw.setAnimationStyle(R.style.OptionsAnimationPopup);

            pw.showAtLocation(menuItemView, Gravity.TOP | Gravity.RIGHT, 20, getStatusBarHeight() + getActionBarHeight());

            Spinner theme = (Spinner) pw.getContentView().findViewById(R.id.theme_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.theme_titles, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            themeAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_faqr);
            // Apply the adapter to the spinner
            // spinner.MODE_DIALOG
            theme.setAdapter(themeAdapter);
            // theme.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs__spinner_ab_default_holo_dark));

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
                        Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    spinnerCount++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            Spinner typeface = (Spinner) pw.getContentView().findViewById(R.id.typeface_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> typefaceAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.typeface_list_titles, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            typefaceAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_faqr);
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

            Spinner fontSize = (Spinner) pw.getContentView().findViewById(R.id.font_size_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.font_size_list_titles, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            fontSizeAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_faqr);
            // Apply the adapter to the spinner
            // spinner.MODE_DIALOG
            fontSize.setAdapter(fontSizeAdapter);

            String fontSizeSetting = prefs.getString("mono_font_size", "Auto");
            String[] mono = getResources().getStringArray(R.array.font_size_list_values);
            int pos = 0;
            for (int i = 0; i < mono.length; i++) {
                if (mono[i].equals(fontSizeSetting)) {
                    pos = i;
                    break;
                }
            }
            fontSize.setSelection(pos);

            fontSize.setOnItemSelectedListener(new OnItemSelectedListener() {
                int spinnerCount = 0;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (spinnerCount > 0) {
                        SharedPreferences.Editor editor = prefs.edit();
                        String[] mono = getResources().getStringArray(R.array.font_size_list_values);
                        editor.putString("mono_font_size", mono[position]);
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

            Spinner varfontSize = (Spinner) pw.getContentView().findViewById(R.id.var_font_size_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> varfontSizeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.font_size_list_titles, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            varfontSizeAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_faqr);
            // Apply the adapter to the spinner
            // spinner.MODE_DIALOG
            varfontSize.setAdapter(fontSizeAdapter);

            String varfontSizeSetting = prefs.getString("variable_font_size", "Auto");
            String[] var = getResources().getStringArray(R.array.font_size_list_values);
            int varpos = 0;
            for (int i = 0; i < var.length; i++) {
                if (var[i].equals(varfontSizeSetting)) {
                    varpos = i;
                }
            }

            varfontSize.setSelection(varpos);

            varfontSize.setOnItemSelectedListener(new OnItemSelectedListener() {

                int spinnerCount = 0;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    if (spinnerCount > 0) {
                        SharedPreferences.Editor editor = prefs.edit();
                        String[] var = getResources().getStringArray(R.array.font_size_list_values);
                        editor.putString("variable_font_size", var[position]);
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

            // quit dialog
            // AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            // dialogBuilder.setMessage("This will quit the application.").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int id) {
            // // do nothing
            // finish();
            // }
            // }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int id) {
            // // do nothing
            // }
            // });
            // dialogBuilder.show();

            return true;

        case R.id.menu_prev:
            // find_prev();
            new FindPrevTask().execute(new String[] {});
            return true;
        case R.id.menu_next:
            // find_next();
            new FindNextTask().execute(new String[] {});
            return true;
        case R.id.menu_goto_prev:
            // find_prev();
            // new FindPrevTask().execute(new String[] {});

            new GotoPrevTask().execute(new String[] {});
            // new GotoTimeoutTask().execute(new String[] {});

            return true;
        case R.id.menu_goto_next:
            // find_next();

            new GotoNextTask().execute(new String[] {});
            // new GotoTimeoutTask().execute(new String[] {});

            return true;

        case R.id.menu_goto_close:
            goTo = false;
            MenuItem gotoPrev = menu.findItem(R.id.menu_goto_prev);
            gotoPrev.setVisible(false);
            MenuItem gotoNext = menu.findItem(R.id.menu_goto_next);
            gotoNext.setVisible(false);
            MenuItem gotoClose = menu.findItem(R.id.menu_goto_close);
            gotoClose.setVisible(false);
            // new GotoTimeoutTask().execute(new String[] {});
            MenuItem opt;
            // MenuItem opt = menu.findItem(R.id.menu_goto);
            // opt.setVisible(true);
            // opt = menu.findItem(R.id.menu_lock);
            // opt.setVisible(false);

            if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
                opt = menu.findItem(R.id.menu_faqmarks);
                opt.setVisible(true);
            }
            opt = menu.findItem(R.id.menu_find);
            opt.setVisible(true);
            opt = menu.findItem(R.id.menu_browser);
            opt.setVisible(true);
            opt = menu.findItem(R.id.menu_settings);
            opt.setVisible(true);
            opt = menu.findItem(R.id.menu_about);
            opt.setVisible(true);

            return true;

        case R.id.menu_faqmarks:

            if (!prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", "").equals("")) {

                intent = new Intent(this, FaqmarksActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No FAQmarks. Long press to save one.", Toast.LENGTH_SHORT).show();

            }

            return true;

        case R.id.menu_find:
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (!getSupportActionBar().isShowing())
                getSupportActionBar().show();

            getSupportActionBar().setIcon(R.drawable.ic_launcher);

            MenuItem prev = menu.findItem(R.id.menu_prev);
            prev.setVisible(true);
            MenuItem next = menu.findItem(R.id.menu_next);
            next.setVisible(true);
            opt = menu.findItem(R.id.menu_display_options);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_faqmarks);
            opt.setVisible(false);
            // opt = menu.findItem(R.id.menu_goto);
            // opt.setVisible(false);
            opt = menu.findItem(R.id.menu_browser);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_settings);
            opt.setVisible(false);
            opt = menu.findItem(R.id.menu_about);
            opt.setVisible(false);
            return true;
            // case R.id.menu_goto:
            // if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
            // goTo = true;
            // currGotoPos = 0;
            //
            // if (!prefs.getString(Faqr.validFileName(currFaqMeta[5]) + "multi_saved_pos", "").equals("")) {
            // if (!getSupportActionBar().isShowing())
            // getSupportActionBar().show();
            // // new GotoNextTask().execute(new String[] {});
            // gotoPrev = menu.findItem(R.id.menu_goto_prev);
            // gotoPrev.setVisible(true);
            // gotoNext = menu.findItem(R.id.menu_goto_next);
            // gotoNext.setVisible(true);
            // // gotoClose = menu.findItem(R.id.menu_goto_close);
            // // gotoClose.setVisible(true);
            // // new GotoTimeoutTask().execute(new String[] {});
            // opt = menu.findItem(R.id.menu_faqmarks);
            // opt.setVisible(false);
            // opt = menu.findItem(R.id.menu_goto);
            // opt.setVisible(false);
            // // opt = menu.findItem(R.id.menu_lock);
            // // opt.setVisible(false);
            // opt = menu.findItem(R.id.menu_find);
            // opt.setVisible(false);
            // opt = menu.findItem(R.id.menu_settings);
            // opt.setVisible(false);
            // opt = menu.findItem(R.id.menu_about);
            // opt.setVisible(false);
            // } else {
            // Toast.makeText(getApplicationContext(), "No FAQmarks. Long press to save one.", Toast.LENGTH_SHORT).show();
            // goTo = false;
            // }
            //
            // }
            //
            // return true;
        case R.id.menu_browser:
            String url = currFaqMeta[5];
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        case R.id.menu_settings:
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
            return true;
        case R.id.menu_about:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        case android.R.id.home:
            if (goTo) {
                goTo = false;
                gotoPrev = menu.findItem(R.id.menu_goto_prev);
                gotoPrev.setVisible(false);
                gotoNext = menu.findItem(R.id.menu_goto_next);
                gotoNext.setVisible(false);
                gotoClose = menu.findItem(R.id.menu_goto_close);
                gotoClose.setVisible(false);
                // new GotoTimeoutTask().execute(new String[] {});
                // opt = menu.findItem(R.id.menu_goto);
                // opt.setVisible(true);
                // opt = menu.findItem(R.id.menu_lock);
                // opt.setVisible(false);

                if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
                    opt = menu.findItem(R.id.menu_faqmarks);
                    opt.setVisible(true);
                }
                opt = menu.findItem(R.id.menu_find);
                opt.setVisible(true);
                opt = menu.findItem(R.id.menu_browser);
                opt.setVisible(true);
                opt = menu.findItem(R.id.menu_settings);
                opt.setVisible(true);
                opt = menu.findItem(R.id.menu_about);
                opt.setVisible(true);

                return true;
            }

            if (extras == null || (extras != null && extras.getBoolean("from_search") != true)) {
                intent = new Intent(this, MyFaqsActivity.class);
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
            View view = inflater.inflate(R.layout.faq_item, parent, false);

            String line = lines[position];

            // name
            TextView nameView = (TextView) view.findViewById(R.id.name);

            if (prefs.getString("typeface", getResources().getString(R.string.typeface_default)).equals("1") || FaqrApp.useFixedWidthFont(line)) {

                // if (!prefs.getBoolean("use_variable_font", getResources().getBoolean(R.bool.use_variable_font_default)) || Faqr.useFixedWidthFont(line)) {
                // //////////
                // MONO FONT

                // nameView.setTextScaleX(1.3f);
                // nameView.setTypeface(tf);
                nameView.setTextAppearance(getApplicationContext(), R.style.MonoText);

                String monoFontSize = prefs.getString("mono_font_size", getResources().getString(R.string.mono_font_size_default));

                // auto font-size
                if (monoFontSize.equalsIgnoreCase("auto") && autoMonoFontSize == -1.0f) {
                    // Log.i(TAG, "CALCULATING FONT SIZE --------------------");

                    int measuredWidth = 0;
                    int measuredHeight = 0;
                    Point size = new Point();
                    WindowManager w = getWindowManager();

                    // account for padding on both sides
                    // Resources r = getResources();
                    // float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
                    // px = px * 2;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        w.getDefaultDisplay().getSize(size);
                        measuredWidth = Math.round(size.x);
                        measuredHeight = size.y;
                    } else {
                        Display d = w.getDefaultDisplay();
                        measuredWidth = Math.round(d.getWidth());
                        measuredHeight = d.getHeight();
                    }

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
                    nameView.setTextSize(Float.valueOf(autoMonoFontSize));
                    // make bold for small fonts
                    if (autoMonoFontSize <= 7.0f) {
                        nameView.setTextAppearance(getApplicationContext(), R.style.MonoTextBold);
                    }
                } else {
                    nameView.setTextSize(Float.valueOf(monoFontSize));
                }

                // Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DejaVuSansMono.ttf");
                // nameView.setTypeface(tf);

                // if (!line.startsWith("         ")) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) nameView.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                nameView.setLayoutParams(lp);
                // }

                // nameView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                // nameView.setGravity( Gravity.CENTER | Gravity.CENTER);
                view.setPadding(0, 10, 0, 10);

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

                    if (autoMonoFontSize <= 10.0f) {
                        nameView.setTextSize(Float.valueOf("13.0f"));
                    } else if (autoMonoFontSize >= 15.0f) {
                        nameView.setTextSize(Float.valueOf("15.0f"));
                    } else {
                        nameView.setTextSize(Float.valueOf("14.0f"));
                    }

                } else {
                    nameView.setTextSize(Float.valueOf(variableFontSize));
                }

                view.setPadding(10, 10, 10, 10);
            }

            // set the text
            nameView.setText(line);

            // handle some padding at the top for when action bar is hidden

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (position == 0 && prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                    nameView.setPadding(view.getPaddingLeft(), getActionBarHeight() + getStatusBarHeight(), view.getPaddingRight(), view.getPaddingBottom());
                } else if (position == lines.length - 1 && prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                    nameView.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), getNavigationBarHeight());
                }
                // else if (position == 0 && prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
                // nameView.setPadding(view.getPaddingLeft(), getActionBarHeight(), view.getPaddingRight(), view.getPaddingBottom());
                // }
            }

            // sepia text color
            if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
                nameView.setTextColor(0xFF645032);
            } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("5")) {
            }

            // saved pos highlighting
            // if (prefs.getBoolean("highlight_saved_pos", getResources().getBoolean(R.bool.highlight_saved_position_default))) {
            // if (prefs.getBoolean("multi_saved_pos_new", getResources().getBoolean(R.bool.multi_saved_position_default))) {
            // String savedPosMulti = prefs.getString(Faqr.validFileName(currFaqMeta[5]) + "multi_saved_pos", "");
            // String[] savedPosMultiArray = savedPosMulti.split(",");
            // if (Arrays.asList(savedPosMultiArray).contains(Integer.valueOf(position).toString())) {
            //
            // // saved pos background
            // if (prefs.getBoolean("saved_pos_background", getResources().getBoolean(R.bool.saved_position_background_default))) {
            // int pL = view.getPaddingLeft();
            // int pT = view.getPaddingTop();
            // int pR = view.getPaddingRight();
            // int pB = view.getPaddingBottom();
            // view.setBackgroundDrawable(themeDrawable);
            // view.setPadding(pL, pT, pR, pB);
            // nameView.setTextColor(themeColor);
            // }
            //
            // }
            // }
            // }

            String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", "");
            String[] savedPosMultiArray = savedPosMulti.split(",");
            if (prefs.getString("highlight_faqmark", "1").equals("1")) {
                if (Arrays.asList(savedPosMultiArray).contains(Integer.valueOf(position).toString())) {
                    nameView.setTextColor(themeColor);
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
                    nameView.setTextColor(themeColor);
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
                        // Get the EditText's internal text storage

                        str.setSpan(new BackgroundColorSpan(0xFFff9632), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        // theme stuff
                        if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("1")) {
                        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("2")) {
                            str.setSpan(new ForegroundColorSpan(0xFF000000), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("3")) {
                            str.setSpan(new ForegroundColorSpan(0xFF000000), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("4")) {
                            themeColor = getResources().getColor(R.color.gamefaqs_light_color);
                            themeDrawable = getResources().getDrawable(R.drawable.faqr_saved_light_bg_small);
                        } else if (prefs.getString("theme", getResources().getString(R.string.theme_default)).equals("5")) {
                            str.setSpan(new ForegroundColorSpan(0xFF000000), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

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
                currFaqURL = currFaqMeta[5];
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return "-999";
            }

            try {
                Log.w(TAG, "===============================================");
                Log.w(TAG, "READING FROM FILE " + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());

                if (currFaqMeta.length == 8 && currFaqMeta[7].trim().equals("TYPE=IMAGE")) {

                    // IMAGE FAQ
                    return "4";

                } else if (currFaqMeta.length == 8 && currFaqMeta[7].trim().equals("TYPE=HTML")) {

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
                        if (currFaqMeta[5].contains("m.gamefaqs.com")) {

                            Log.w(TAG, "===============================================");
                            Log.w(TAG, "FETCHING FROM WEB " + currFaqURL);

                            if (!currFaqURL.startsWith("http")) {
                                currFaqURL = "http://" + currFaqURL.trim();
                            }

                            // download the faq from the web
                            Document doc = Jsoup.connect(currFaqURL).timeout(10000).get();

                            Elements titleElem = doc.select("title");
                            for (Element link : titleElem) {
                                title += link.text();
                            }
                            title = title.replace("GameFAQs: ", "");

                            SharedPreferences.Editor editor = prefs.edit();
                            // editor.putString("faq_title", title);
                            // editor.commit();

                            String faqMeta = prefs.getString(FaqrApp.validFileName(currFaqURL), "");
                            Log.i(TAG, FaqrApp.validFileName(currFaqURL) + " " + faqMeta);
                            // Toast.makeText(getApplicationContext(), Faqr.validFileName(faqURL) + " " + faqMeta, Toast.LENGTH_SHORT).show();
                            if (faqMeta.split(" --- ").length == 6)
                                faqMeta = faqMeta + " --- " + title;
                            editor.putString(FaqrApp.validFileName(currFaqURL), faqMeta);
                            editor.commit();

                            // update our copy of faqr metadata
                            currFaqMeta = faqMeta.split(" --- ");

                            // parse the content
                            Elements pre = doc.select("pre");
                            for (Element elem : pre) {
                                // text node prevents the removal of leading and trailing whitespace
                                List<TextNode> nodes = elem.textNodes();
                                for (TextNode node : nodes)
                                    content += node.getWholeText();
                                // content += elem.text();
                            }

                            // checking for ASCII FAQ determined by amount of stuff in <pre> tags
                            if (content.length() < 10000) {

                                if (pre.size() > 0) {
                                    Elements children = pre.get(0).children();

                                    // WE HAVE FOUND AN IMAGE
                                    if (children.size() == 1 && children.get(0).tagName().equals("img")) {

                                        String imagePath = children.get(0).attr("src");
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
                        if (e.getMessage() != null)
                            Log.e(TAG, e.getMessage());
                        result = "-1";
                    } catch (OutOfMemoryError e) {
                        // memory exception on device
                        if (e.getMessage() != null)
                            Log.e(TAG, e.getMessage());
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

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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

                // this will create a place holder file even tho we aren't using the fucking web archive which sux!
                // webView.saveWebArchive(getFileStreamPath(Faqr.validFileName(currFaqURL)).getAbsolutePath());

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
                webView.loadUrl(currFaqMeta[5].trim());

                // this will create a place holder file even tho we aren't using the fucking web archive which sux!
                webView.saveWebArchive(getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath());

                webViewActive = true;

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
                Toast.makeText(getApplicationContext(), "There is no internet connection available, Please connect to wifi or data plan and try again.", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("-2")) {

                // :'-(
                // error in the web access
                Toast.makeText(getApplicationContext(), "Sorry an out of memory exception occured. ", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("-1")) {

                // :'-(
                // error in the web access
                Toast.makeText(getApplicationContext(), "Sorry an error occured. Please try again.", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);

            } else if (result.equals("3") || result.equals("4")) {

                // :-)
                // we got an image downloaded to show and we are very happy
                if (result == "3") {
                    Toast.makeText(getApplicationContext(), "Successfully saved image to device.", Toast.LENGTH_LONG).show();
                } else {

                }
                // if (result == "4")
                // Toast.makeText(getApplicationContext(), "Opening saved image in WebView.", Toast.LENGTH_LONG).show();

                webViewActive = true;

                String title = "";
                String subtitle = "";
                if (currFaqMeta.length > 6) {
                    // setTitle(currFaqMeta[6]);

                    String[] titleParts = currFaqMeta[6].split("\\(");

                    title = titleParts[0].trim();
                    if (titleParts.length > 1) {
                        subtitle = currFaqMeta[6].split("\\)")[1].trim();
                        if (title.indexOf(currFaqMeta[0].split("\\(|<")[0].trim()) != -1) {
                            title = title.substring(0, title.indexOf(currFaqMeta[0].split("\\(|<")[0].trim())).trim();
                        }
                        if (subtitle.startsWith("Final Fantasy IV ")) {
                            subtitle = subtitle.replaceAll("Final Fantasy IV ", "");
                        }

                        getSupportActionBar().setSubtitle(subtitle);
                    }
                    getSupportActionBar().setTitle(title);
                }
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
                html = ("<html><title>" + title + "</title><body style=\"background-color:" + bgColor + ";\"><img src=\"file://" + getFileStreamPath(FaqrApp.validFileName(currFaqURL)).getAbsolutePath() + "\" align=left></body></html>");

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

                // webview time!
                webView.loadUrl(currFaqMeta[5].trim());

            } else {

                // write to device status (not a fatal error)
                if (result == "1") {
                    Toast.makeText(getApplicationContext(), "Successfully saved FAQ to device.", Toast.LENGTH_LONG).show();
                } else if (result == "2") {
                    Toast.makeText(getApplicationContext(), "Error saving FAQ to device.", Toast.LENGTH_LONG).show();
                }

                // :-)
                // we got a faq to show and we are very happy!
                if (currFaqMeta.length > 6) {
                    // setTitle(currFaqMeta[6]);

                    String[] titleParts = currFaqMeta[6].split("\\(");

                    String title = titleParts[0].trim();
                    if (titleParts.length > 1) {
                        String subtitle = currFaqMeta[6].split("\\)")[1].trim();
                        if (title.indexOf(currFaqMeta[0].split("\\(|<")[0].trim()) != -1) {
                            title = title.substring(0, title.indexOf(currFaqMeta[0].split("\\(|<")[0].trim())).trim();
                        }
                        if (subtitle.startsWith("Final Fantasy IV ")) {
                            subtitle = subtitle.replaceAll("Final Fantasy IV ", "");
                        }

                        getSupportActionBar().setSubtitle(subtitle);
                    }
                    getSupportActionBar().setTitle(title);
                }

                getSupportActionBar().setIcon(android.R.color.transparent);

                // set the listview position
                if (faqmarkPos > -1) {
                    // if there is a faqmark we are returning to
                    if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                        listView.setSelectionFromTop(faqmarkPos, getActionBarHeight() + getStatusBarHeight());
                    }
                    // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
                    // listView.setSelectionFromTop(faqmarkPos, getActionBarHeight());
                    // }
                    else {
                        listView.setSelection(faqmarkPos);
                    }

                    String plusOne = new Integer(faqmarkPos + 1).toString();
                    // double percentage = (new Double(plusOne) / new Double(lines.length)) * 100.0;
                    // DecimalFormat df = new DecimalFormat("#");
                    Toast.makeText(getApplicationContext(), "Location " + plusOne + "/" + lines.length + " - FAQmark", Toast.LENGTH_SHORT).show();

                } else {
                    // otherwise use the curr_pos
                    if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                        listView.setSelectionFromTop(prefs.getInt(FaqrApp.validFileName(currFaqMeta[5]) + "curr_pos", 0), getActionBarHeight() + getStatusBarHeight());
                    }
                    // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default))) {
                    // listView.setSelectionFromTop(prefs.getInt(Faqr.validFileName(currFaqMeta[5]) + "curr_pos", 0), getActionBarHeight());
                    // }
                    else {
                        listView.setSelection(prefs.getInt(FaqrApp.validFileName(currFaqMeta[5]) + "curr_pos", 0));
                    }

                }

                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                // lights out!
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // if (prefs.getBoolean("use_lights_out", getResources().getBoolean(R.bool.use_lights_out_default))) {
                // listView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                // }
                // }
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
            editor.putInt(FaqrApp.validFileName(currFaqMeta[5]) + "curr_pos", Integer.valueOf(strings[0]).intValue());
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

                String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", "");
                // Log.w(TAG, "BEFORE " + savedPosMulti);

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
                // editor.putString(Faqr.validFileName(currFaqMeta[5]) + "multi_saved_pos", savedPosMulti + "," + strings[0]);
                editor.putString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", savedPosMulti);
                editor.commit();

                // Log.w(TAG, "AFTER " + savedPosMulti);

            } else {
                editor.putInt(FaqrApp.validFileName(currFaqMeta[5]) + "saved_pos", Integer.valueOf(strings[0]).intValue());
                editor.commit();
            }
            return result;
        }

        protected void onPostExecute(String result) {

            String savedPosPlusOne = new Integer(Integer.valueOf(savedPos) + 1).toString();

            // if faqmarks
            if (!TextUtils.isEmpty(prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", ""))) {
                faqmarksItem.setEnabled(true);
            } else {
                faqmarksItem.setEnabled(false);
            }

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "Saved Location " + savedPosPlusOne + "/" + lines.length + " - FAQmark.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Removed Location " + savedPosPlusOne + "/" + lines.length + " - FAQmark.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Find onTextChanged Task
     * 
     * @author eneve
     */
    private class FindTextChangedTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {
            findString = strings[0];

            // ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            if (!TextUtils.isEmpty(findString)) {
                int count = 0;
                int position = -1;
                for (String line : lines) {
                    int startIndex = line.toLowerCase().indexOf(findString.toLowerCase());
                    if (startIndex != -1 && count > prefs.getInt("curr_pos", 0)) {
                        position = count;
                        break;
                    }
                    count++;
                }
                if (position > -1) {
                    // listView.setSelection(position);
                    currFindPos = position;

                    return Integer.valueOf(position).toString();
                } else {
                    if (!TextUtils.isEmpty(findString)) {
                        count = lines.length - 1;
                        position = -1;
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
                            // listView.setSelection(position);
                            currFindPos = position;
                            return Integer.valueOf(position).toString();
                        }
                    }
                }
            }
            return "-1";
        }

        protected void onPostExecute(String result) {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            // if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
            // listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            // } else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight());
            // } else {
            // listView.setSelection(Integer.valueOf(result).intValue());
            // }
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

            int currentPosition = listView.getFirstVisiblePosition() + 1;

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
            }
            // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight());
            // }
            else {
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

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {

            int currentPosition = listView.getFirstVisiblePosition() + 1;

            if (!TextUtils.isEmpty(findString)) {
                int count = 0;
                int position = -1;
                for (String line : lines) {
                    int startIndex = line.toLowerCase().indexOf(findString.toLowerCase());
                    if (startIndex != -1 && count != currFindPos && count > currentPosition) {
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
            }
            // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight());
            // }
            else {
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

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {

            int position = listView.getFirstVisiblePosition() + 1;

            String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", "");

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

            // String plusOne = new Integer(Integer.valueOf(result) + 1).toString();
            // double percentage = (new Double(plusOne) / new Double(lines.length)) * 100.0;
            // DecimalFormat df = new DecimalFormat("#");
            // Toast.makeText(getApplicationContext(), "Location " + plusOne + "/" + lines.length + " - " + df.format(percentage) + "%", Toast.LENGTH_SHORT).show();

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            }
            // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight());
            // }
            else {
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

        protected void onPreExecute() {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {

            int position = listView.getFirstVisiblePosition() + 1;

            String savedPosMulti = prefs.getString(FaqrApp.validFileName(currFaqMeta[5]) + "multi_saved_pos", "");

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
                    // lastGotoPos = 0;
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

            // String plusOne = new Integer(Integer.valueOf(result) + 1).toString();
            // double percentage = (new Double(plusOne) / new Double(lines.length)) * 100.0;
            // DecimalFormat df = new DecimalFormat("#");
            // Toast.makeText(getApplicationContext(), "Location " + plusOne + "/" + lines.length + " - " + df.format(percentage) + "%", Toast.LENGTH_SHORT).show();

            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

            if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
                listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight() + getStatusBarHeight());
            }
            // else if (prefs.getBoolean("hide_action_bar", getResources().getBoolean(R.bool.hide_action_bar_default)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // listView.setSelectionFromTop(Integer.valueOf(result).intValue(), getActionBarHeight());
            // }
            else {
                listView.setSelection(Integer.valueOf(result).intValue());
            }
        }
    };

    // //////////////////////
    // / IMMERSIVE STUFF

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // When the window loses focus (e.g. the action overflow is shown),
        // cancel any pending hide action. When the window gains focus,
        // hide the system UI.
        if (prefs.getBoolean("use_immersive_mode", getResources().getBoolean(R.bool.use_immersive_mode_default))) {
            if (!find && !goTo) {
                if (hasFocus) {
                    delayedHide(INITIAL_HIDE_DELAY);
                } else {
                    mHideHandler.removeMessages(0);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !webViewActive) {

            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
            // mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);

            // mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            getSupportActionBar().hide();
            // ActionBar actionBar = getSupportActionBar();
            // actionBar.hide();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && webViewActive) {

            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);

        } else if (!webViewActive) {

            // mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            hideStatusBar();
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            mDecorView.setSystemUiVisibility(0);

            getSupportActionBar().show();
            // ActionBar actionBar = getSupportActionBar();
            // actionBar.show();
        } else {

            // mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            showStatusBar();
            ActionBar actionBar = getSupportActionBar();
            actionBar.show();
        }
    }

    private final Handler mHideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideSystemUI();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeMessages(0);
        mHideHandler.sendEmptyMessageDelayed(0, delayMillis);
    }

}
