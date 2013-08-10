/**
 * Copyright (C) 2013 BARCAMP SG. All rights reserved.
 * 
 * 
 * BARCAMP SG MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BARCAMP SG SHALL NOT BE LIABLE FOR ANY
 * LOSSES OR DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 */
package org.barcampsaigon.android.util;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Config Util.
 * 
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class Config {

    /**
     * Twitter consumer key.
     */
    public static final String TWITTER_CONSUMER_KEY = "";
    /**
     * Twitter consumer secret.
     */
    public static final String TWITTER_CONSUMER_SECRET = "";
    /**
     * Twitter search tag.
     */
    public static final String BARCAMP_TWITTER_TAG = "";

    /**
     * Parse application key.
     */
    public static final String PARSE_APP_KEY = "";
    /**
     * Parse client key.
     */
    public static final String PARSE_CLIENT_KEY = "";
    /**
     * Agenda url.
     */
    public static final String AGENDA_URL = "";
    /**
     * Participants url.
     */
    public static final String PARTICIPANTS_URL = "";
    /**
     * Participants csv url.
     */
    public static final String PARTICIPANTS_CSV_URL = "";
    /**
     * Twitter search url.
     */
    public static final String TWITTER_URL = "https://api.twitter.com/1.1/search/tweets.json";
    /**
     * Twitter oauth token.
     */
    public static final String TWITTER_OAUTH_URL = "https://api.twitter.com/oauth2/token";
    public static final String TWITTER_AUTH_KEY = "";
    /**
     * Participants name column index.
     */
    public static final int NAME_COL = 2;
    /**
     * Twitter col index.
     */
    public static final int TWITTER_COL = 6;
    /**
     * Participants facebook column index.
     */
    public static final int FACEBOOK_COL = 7;
    /**
     * Participants position column index.
     */
    public static final int POSITION_COL = 4;
    /**
     * Facebook profile url.
     */
    public static final String FACEBOOK_PROFILE_URL = "http://graph.facebook.com/%s/picture?width=300&height=300";
    /**
     * Twitter profile url.
     */
    public static final String TWITTER_PROFILE_URL = "http://api.twitter.com/1/users/profile_image/%s.png&size=bigger";
    /**
     * Key for common data.
     */
    public static final String COMMON_DATA_PREF = "barcamp-common-data";
    /**
     * Key for hour sections.
     */
    public static final String HOUR_DATA_PREF = "hour-sections-data";
    /**
     * Key for agenda date pref.
     */
    public static final String AGENDA_DATE_PREF = "agenda-date-pref";
    /**
     * Key for attendee date pref.
     */
    public static final String ATTENDEE_DATE_PREF = "attendee-date-pref";
    /**
     * Key for agenda alarm time.
     */
    public static final String AGENDA_ALARM_TIME = "agenda_alarm_time";

    public static final String TWITTER_ACCESS_TOKEN_PREF = "twitter_access_token_pref";
    public static final String TWITTER_SECRET_TOKEN_PREF = "twitter_secret_token_pref";
    /**
     * Key for attendee alarm time.
     */
    public static final String ATTENDEE_ALARM_TIME = "attendee_alarm_time";
    /**
     * Key for tweet alarm time.
     */
    public static final String TWEET_ALARM_TIME = "tweet_alarm_time";

    /**
     * Default alarm time.
     */
    public static final long DEFAULT_ALARM_TIME = 15 * 60 * 1000;
    /**
     * Max alarm time.
     */
    public static final long MAX_ALARM_TIME = 12 * 60 * 60 * 1000;

    public static final double RMIT_LATITUDE = 10.729241;
    public static final double RMIT_LONGITUDE = 106.695389;
    public static final int COMPANY_COL = 5;
    public static final int TOPIC_WANNA_HEAR_COL = 10;
    public static final int TOPIC_WANNA_PRESENT_COL = 9;

    /**
     * Store hour data to pref.
     * 
     * @param context
     * @param hourStrings
     */
    public static void setHourData(Context context, Set<String> hourStrings) {
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            sharedPref.edit().putString(HOUR_DATA_PREF, gson.toJson(hourStrings)).commit();
        }
    }

    /**
     * Get hour data.
     * 
     * @param context
     * @return
     */
    public static Set<String> getHourData(Context context) {
        TreeSet<String> result = null;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            String hourString = sharedPref.getString(HOUR_DATA_PREF, null);
            if (hourString != null) {
                Gson gson = new Gson();
                result = new TreeSet<String>();
                String[] hours = gson.fromJson(hourString, String[].class);
                result.addAll(Arrays.asList(hours));
            }

        }
        return result;
    }

    public static long getLastAgendaUpdateTime(Context context) {
        long result = -1;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            result = sharedPref.getLong(AGENDA_DATE_PREF, -1);
        }
        return result;
    }

    public static long getLastAttendeeUpdateTime(Context context) {
        long result = -1;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            result = sharedPref.getLong(ATTENDEE_DATE_PREF, -1);
        }
        return result;
    }

    public static void setLastAgendaUpdateTime(Context context, long value) {
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            sharedPref.edit().putLong(AGENDA_DATE_PREF, value).commit();
        }
    }

    public static void setLastAttendeeUpdateTime(Context context, long value) {
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            sharedPref.edit().putLong(ATTENDEE_DATE_PREF, value).commit();
        }
    }

    public static long getAlarmTime(Context context, String key) {
        long result = DEFAULT_ALARM_TIME;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            result = sharedPref.getLong(key, DEFAULT_ALARM_TIME);
        }
        return result;
    }

    public static void setAlarmTime(Context context, String key, long alarmTime) {
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            sharedPref.edit().putLong(key, alarmTime).commit();
        }
    }

    public static void setTwitterAccessToken(Context context, String accessToken) {
        if (context != null && accessToken != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            sharedPref.edit().putString(TWITTER_ACCESS_TOKEN_PREF, accessToken).commit();
        }
    }

    public static void setTwitterSecretToken(Context context, String secretToken) {
        if (context != null && secretToken != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            sharedPref.edit().putString(TWITTER_SECRET_TOKEN_PREF, secretToken).commit();
        }
    }

    public static String getTwitterAccessToken(Context context) {
        String result = null;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            result = sharedPref.getString(TWITTER_ACCESS_TOKEN_PREF, null);
        }
        return result;
    }

    public static String getTwitterSecretToken(Context context) {
        String result = null;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(COMMON_DATA_PREF, Context.MODE_PRIVATE);
            result = sharedPref.getString(TWITTER_SECRET_TOKEN_PREF, null);
        }
        return result;
    }
}
