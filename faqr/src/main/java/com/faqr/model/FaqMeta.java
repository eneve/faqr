package com.faqr.model;

import android.util.Log;

import java.util.Arrays;

/**
 * This will help reduce errors and parsing logic and general understanding of the FAQMeta
 * B/C I always forget
 *
 * Created by stephen on 7/6/15.
 *
 * Examples of FAQ Meta
 *
 *  Interlude FAQ/Walkthrough --- 09/20/11 --- A Backdated Future --- 1.04 --- 275K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/62307 --- Final Fantasy IV: The Complete Collection (PSP) Interlude FAQ/Walkthrough by A Backdated Future
 *  Final Fantasy IV FAQ/Walkthrough --- 09/20/11 --- A Backdated Future --- 1.02 --- 1265K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/62211 --- Final Fantasy IV: The Complete Collection (PSP) Final Fantasy IV FAQ/Walkthrough by A Backdated Future
 *  The After Years FAQ/Walkthrough --- 01/22/12 --- gamingrat --- 1 --- 477K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/63680 --- Final Fantasy IV: The Complete Collection (PSP) The After Years FAQ/Walkthrough by gamingrat
 *  Interlude FAQ/Walkthrough --- 09/20/11 --- A Backdated Future --- 1.04 --- 275K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/62307 --- Final Fantasy IV: The Complete Collection (PSP) Interlude FAQ/Walkthrough by A Backdated Future
 *  FAQ/Walkthrough --- 10/06/14 --- noz3r0 --- 1.00 --- 1273K --- http://m.gamefaqs.com/ps3/735143-kingdom-hearts-hd-25-remix/faqs/70271 --- Kingdom Hearts HD 2.5 ReMIX (PS3) FAQ/Walkthrough by noz3r0
 *  The After Years FAQ/Walkthrough --- 01/22/12 --- gamingrat --- 1 --- 477K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/63680 --- Final Fantasy IV: The Complete Collection (PSP) The After Years FAQ/Walkthrough by gamingrat
 *  FAQ/Walkthrough --- 10/06/14 --- noz3r0 --- 1.00 --- 1273K --- http://m.gamefaqs.com/ps3/735143-kingdom-hearts-hd-25-remix/faqs/70271 --- Kingdom Hearts HD 2.5 ReMIX (PS3) FAQ/Walkthrough by noz3r0
 *  Interlude FAQ/Walkthrough --- 09/20/11 --- A Backdated Future --- 1.04 --- 275K --- http://m.gamefaqs.com/psp/615911-final-fantasy-iv-the-complete-collection/faqs/62307 --- Final Fantasy IV: The Complete Collection (PSP) Interlude FAQ/Walkthrough by A Backdated Future
 *  FAQ/Walkthrough (X360) --- 08/15/11 --- etjester --- Final --- 410K --- http://m.gamefaqs.com/ps3/689056-mass-effect/faqs/51033 --- Mass Effect FAQ/Walkthrough for PlayStation 3 by etjester - GameFAQs
 *  FAQ/Walkthrough *new*  <HTML> --- 06/08/15 --- Suikosun --- 0.1 --- 49K --- http://m.gamefaqs.com/ps4/702760-the-witcher-3-wild-hunt/faqs/71878 --- The Witcher 3: Wild Hunt FAQ/Walkthrough for PlayStation 4 by Suikosun - GameFAQs --- TYPE=HTML
 *
 */
public class FaqMeta {

    private String[] faqMetaSplit;

    /**
     * FaqMeta constructor
     *
     */
    public FaqMeta() {
        faqMetaSplit = new String[]{};
    }

    /**
     * FaqMeta constructor
     *
     * @param faqMeta
     */
    public FaqMeta(String faqMeta) {
        faqMetaSplit = faqMeta.split("---");
    }


    /**
     * FAQ Title
     *
     * @return
     */
    public String getTitle() {

        String faqTitle = "";

        try {
            faqTitle = faqMetaSplit[0].trim();
        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);
        }

        return faqTitle;
    }


    /**
     * FAQ DAte
     *
     * @return
     */
    public String getDate() {

        String date = "";

        try {
            date = faqMetaSplit[1].trim();
        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);
        }

        return date;
    }


    /**
     * FAQ Author
     *
     * @return
     */
    public String getAuthor() {

        String author = "";

        try {
            author = faqMetaSplit[2].trim();
        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);
        }

        return author;
    }


    /**
     * FAQ Version
     *
     * @return
     */
    public String getVersion() {

        String version = "";

        try {
            version = faqMetaSplit[3].trim();
        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);
        }

        return version;
    }


    /**
     * FAQ Size
     *
     * @return
     */
    public String getSize() {

        String size = "";

        try {
            size = faqMetaSplit[4].trim();
        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);
        }

        return size;
    }


    /**
     * FAQ URL
     *
     * @return
     */
    public String getUrl() {

        String url = "";

        try {
            url = faqMetaSplit[5].trim();
        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);
        }

        return url;
    }


    /**
     * FAQ Game Title
     *
     * @return
     */
    public String getGameTitle() {

        String gameTitle = "";

        try {
            gameTitle = faqMetaSplit[6].trim();

            if (gameTitle.indexOf(getTitle()) >= 0) {
                gameTitle = gameTitle.split(getTitle())[0].trim();

            } else if (gameTitle.indexOf(getTitle().split("\\(|<|\\*")[0].trim()) != -1) {
                gameTitle = gameTitle.substring(0, gameTitle.indexOf(getTitle().split("\\(|<|\\*")[0].trim())).trim();

            }

        } catch (Exception e) {
            Log.e("FAQr", e.getMessage(), e);

            gameTitle = getTitle();
        }
        return gameTitle;
    }

    /**
     * FAQ Type
     *
     * NOT SET ON ASCII FAQS *****
     *
     * TYPE=IMAGE
     * TYPE=HTML
     *
     *
     * @return
     */
    public String getType() {

        String type = "";

        try {
            type = faqMetaSplit[7].trim();
        } catch (Exception e) {
            Log.w("FAQr", e.getMessage());
        }

        return type;
    }


    @Override
    public String toString() {
        return "FaqMeta=" + Arrays.toString(faqMetaSplit);
    }
}
