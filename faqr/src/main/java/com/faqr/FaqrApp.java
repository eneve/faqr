/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr;

import android.app.Application;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This provides data and service methods for FAQr
 * 
 * @author eneve
 */
public class FaqrApp extends Application {

    // these should be resources
    public static final String GAMEFAQS_URL = "http://m.gamefaqs.com";
    public static final String SEARCH_URL = GAMEFAQS_URL + "/search/index.html?game=";


    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public static enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
//                    : analytics.newTracker(R.xml.ecommerce_tracker);
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    /**
     * Sort the FAQr data files from other meta data in the files dir
     * 
     * @param files
     * @return
     */
    public static File[] getFaqrFiles(File[] files) {
        List<File> faqrFiles = new ArrayList<File>();
        for (File file : files) {
            if (file.getName().contains("http___m_gamefaqs_com"))
                faqrFiles.add(file);
        }
        return faqrFiles.toArray(new File[] {});
    }

    /*
     * Helper for saving files to internal storage
     */
    public static void writeData(FileOutputStream fOut, String data) throws IOException {
        // FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        osw.write(data);
        osw.flush();
        // /getting fuktup
        osw.close();
    }

    /*
     * Helper for reading files from internal storage
     */
    public static String readSavedData(FileInputStream fIn) throws IOException {
        String datax = "";
        // FileInputStream fIn = openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fIn);
        BufferedReader r = new BufferedReader(isr);
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line + "\n");
        }
        isr.close();

        datax = total.toString();

        return datax;
    }

    /*
     * Helper to create the internal data structure needed from a file on internal storage
     */
    public static String[] getLinesFile(String content) {
        List<String> currated = new ArrayList<String>();
        String[] lines = content.split("\\n\\n");
        for (String line : lines) {
            // Log.w(TAG, line);

            while (line.startsWith("\n")) {
                line = line.replaceFirst("\\n", "");
            }

            if (!TextUtils.isEmpty(line)) {

                if (line.length() > 10000) {
                    // Log.w(TAG, " " + line.length());
                    // handle large lines
                    int count = 0;
                    StringBuffer sb = new StringBuffer();
                    String[] sublines = line.split("\\n");
                    for (String subline : sublines) {
                        sb.append(replaceTabs(subline));
                        // underscores or 10 lines first
                        if ((allUnderscores(subline) && (count != 1)) || count == 50) {
                            currated.add(sb.toString());
                            sb = new StringBuffer();
                            count = 0;
                        } else {
                            sb.append("\n");
                        }
                        count++;
                    }
                    // anything that might still be left in the buffer
                    currated.add(sb.toString());

                } else if (line.contains("\n __\n")) {
                    // THIS IS A SPCIAL CASE FOR THE FINAL FANTASY V FAQ I'M USING ;-)
                    String[] sublines = line.split("\\n __\\n");
                    currated.add(replaceTabs(sublines[0]));
                    currated.add(replaceTabs(" __\n" + sublines[1]));

                } else {

                    // attempt to fix the centering problem???
                    // String[] sublines = line.split("\\n");
                    //
                    // StringBuffer sb = new StringBuffer();
                    // for (String subline : sublines) {
                    // while (subline.length() < 79) {
                    // subline += " ";
                    // }
                    // sb.append(replaceTabs(subline));
                    // sb.append("\n");
                    // }
                    // currated.add(sb.toString());

                    currated.add(line);
                }
            }
        }
        return (String[]) currated.toArray(new String[currated.size()]);
    }

    /*
     * Helper to creat the internal data structure needed from a file on the web
     */
    @SuppressWarnings("unchecked")
    public static String[] getLines(String content) {
        List currated = new ArrayList();

        String[] lines = content.split("\\r?\\n\\r?\\n");
        for (String line : lines) {
            // Log.w(TAG, " " + line.length());

            while (line.startsWith("\r\n")) {
                line = line.replaceFirst("\\r?\\n", "");
            }

            if (!TextUtils.isEmpty(line)) {
                if (line.length() > 10000) {
                    // Log.w(TAG, " " + line.length());
                    // handle large lines
                    int count = 0;
                    StringBuffer sb = new StringBuffer();
                    String[] sublines = line.split("\\r?\\n");
                    for (String subline : sublines) {
                        sb.append(replaceTabs(subline));
                        // underscores or 10 lines first
                        if ((allUnderscores(subline) && (count != 1)) || count == 50) {
                            currated.add(sb.toString());
                            sb = new StringBuffer();
                            count = 0;
                        } else {
                            sb.append("\n");
                        }
                        count++;
                    }
                    // anything that might still be left in the buffer
                    currated.add(sb.toString());

                } else if (line.contains("\r\n __\r\n")) {
                    // THIS IS A SPCIAL CASE FOR THE FINAL FANTASY V FAQ I'M USING ;-)
                    String[] sublines = line.split("\\r\\n __\\r\\n");
                    currated.add(replaceTabs(sublines[0]));
                    currated.add(replaceTabs(" __\n" + sublines[1]));

                } else {
                    currated.add(replaceTabs(line));
                }
            }
        }
        return (String[]) currated.toArray(new String[currated.size()]);
    }

    public static boolean allUnderscores(String content) {
        boolean underscores = true;
        content = content.trim();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) != ' ' && content.charAt(i) != '_' && content.charAt(i) != '-' && content.charAt(i) != '=') {
                underscores = false;
                break;
            }
        }
        return underscores;
    }

    public static String replaceTabs(String content) {
        int count = 8;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\t') {
                while (count > 0) {
                    sb.append(" ");
                    count--;
                }
                count = 8;
            } else if (content.charAt(i) == '\n') {
                sb.append(content.charAt(i));
                count = 8;
            } else {
                sb.append(content.charAt(i));
                count--;
                if (count == 0) {
                    count = 8;
                }
            }
        }
        return sb.toString();
    }

    /*
     * helper to determine if we should be using a fixed width font on this section
     */
    public static boolean useFixedWidthFont(String content) {
        boolean useFixedWidthFont = false;

        // four or more special characters in a row is a good clue
        if (content.contains("----") || content.contains("====") || content.contains("____") || content.contains("....") || content.contains("    ")) { // || content.contains("����")) {
            useFixedWidthFont = true;
        }

        // seemED to work well
        // if (!useFixedWidthFont) {
        // if (countOccurrences(content, '-') > 10 || countOccurrences(content,
        // '=') > 10 || countOccurrences(content, '_') > 10 ||
        // countOccurrences(content, '|') > 10) {
        // useFixedWidthFont = true;
        // }
        // }

        // try a ratio
        if (!useFixedWidthFont) {
            int alphaNumericChars = countAlphaNumericCharacters(content);
            double ratio = (alphaNumericChars * 1.0) / content.length();
            // OK WHO WANTS TO FUKING GUESS THE MAGIC RATIO
            if (ratio < 0.6) {
                useFixedWidthFont = true;
            }
        }

        return useFixedWidthFont;
    }

    /*
     * count number of alpha numerics in a string
     */
    public static int countAlphaNumericCharacters(String content) {
        int count = 0;
        for (int i = 0; i < content.length(); i++) {
            if (Character.isLetterOrDigit(content.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    /*
     * Helper to count the number of times a character appears in a string
     */
    public static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', '.' };

    /*
     * Helper that will create valid file names for writing to internal storage
     */
    public static String validFileName(String title) {
        String s = title;
        int len = s.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);

            boolean found = false;
            for (char currch : ILLEGAL_CHARACTERS) {
                if (ch == currch) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                sb.append(ch);
            } else {
                sb.append('_');
            }

        }
        return sb.toString().replaceAll(" ", "_");
    }

    /** string join impl */
    public static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    /**
     * Convert GameFAQs representation to full name
     * 
     * @param name
     *            The shortened symbol
     * @return The full name of the console
     */
    public static String getConsoleFullName(String name) {
        if (name.equalsIgnoreCase("AND")) {
            return "Android";
        } else if (name.equalsIgnoreCase("DS")) {
            return "Nintendo DS";
        } else if (name.equalsIgnoreCase("IP")) {
            return "iPhone";
        } else if (name.equalsIgnoreCase("PS")) {
            return "PlayStation";
        } else if (name.equalsIgnoreCase("PS2")) {
            return "PlayStation 2";
        } else if (name.equalsIgnoreCase("PS3")) {
            return "PlayStation 3";
        } else if (name.equalsIgnoreCase("SNES")) {
            return "Super Nintendo";
        } else if (name.equalsIgnoreCase("GBA")) {
            return "Game Boy Advance";
        } else if (name.equalsIgnoreCase("WII")) {
            return "Nintendo Wii";
        } else if (name.equalsIgnoreCase("PSP")) {
            return "PlayStation Portable";
        } else if (name.equalsIgnoreCase("VITA")) {
            return "PlayStation Vita";
        } else if (name.equalsIgnoreCase("3DS")) {
            return "Nintendo 3DS";
        } else if (name.equalsIgnoreCase("BB")) {
            return "BlackBerry";
        } else if (name.equalsIgnoreCase("GB")) {
            return "Game Boy";
        } else if (name.equalsIgnoreCase("GC")) {
            return "Game Cube";
        } else if (name.equalsIgnoreCase("GBC")) {
            return "Game Boy Color";
        } else if (name.equalsIgnoreCase("MOBILE")) {
            return "Mobile";
        } else if (name.equalsIgnoreCase("WSC")) {
            return "Wonder Swan Color";
        } else if (name.equalsIgnoreCase("X360")) {
            return "Xbox 360";
        } else if (name.equalsIgnoreCase("XBOX")) {
            return "Xbox";
        } else if (name.equalsIgnoreCase("DC")) {
            return "Dreamcast";
        } else if (name.equalsIgnoreCase("N64")) {
            return "Nintendo 64";
        } else if (name.equalsIgnoreCase("MAC")) {
            return "Mac";
        } else if (name.equalsIgnoreCase("GEN")) {
            return "Genesis";
        } else {
            return name;
        }

    }

    /**
     * toTitleCase - also detects roman numerals
     * 
     * @param givenString
     * @return the title cased string
     */
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            boolean romanNumeral = false;
            if (arr[i].toUpperCase().matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")) {
                romanNumeral = true;
            }
            if (romanNumeral) {
                sb.append(arr[i].toUpperCase()).append(" ");
            } else {
                sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Check if a string is a version (e.g. 1.1, 23.74.48, etc.)
     * 
     * @param str
     * @return
     */
    public static boolean isVersionString(String str) {
        for (int i = 0; i < str.length(); i++) {

            // If we find a non-digit character we return false.
            if (!Character.isLetter(str.charAt(i)))
                return false;
        }
        return true;
    }

}
