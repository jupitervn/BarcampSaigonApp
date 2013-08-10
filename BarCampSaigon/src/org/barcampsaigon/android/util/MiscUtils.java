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

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.barcampsaigon.android.R;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class MiscUtils {

    private static final long HOUR_INTERVAL = 3600 * 1000;
    private static final long DAY_INTERVAl = HOUR_INTERVAL * 24;
    public static final String SPECIAL_CHARACTERS = "ạảãàáâậầấẩẫăắằặẳẵóòọõỏôộổỗồốơờớợởỡéèẻẹẽêếềệểễúùụủũưựữửừứíìịỉĩýỳỷỵỹđ";

    public static final String REPLACEMENTS = "aaaaaaaaaaaaaaaaaoooooooooooooooooeeeeeeeeeeeuuuuuuuuuuuiiiiiyyyyyd";

    public static String convertToVietnamesWithoutSpecialChars(String input) {
        String result = null;
        if (input != null) {
            String normalizedStr = input.toLowerCase();
            StringBuilder builder = new StringBuilder();
            for (char c : normalizedStr.toCharArray()) {
                int index = SPECIAL_CHARACTERS.indexOf(c);
                if (index != -1) {
                    builder.append(REPLACEMENTS.charAt(index));
                } else {
                    builder.append(c);
                }
            }
            result = builder.toString();
        }
        return result;
    }

    /**
     * Return true if a string is null or full of spaces.
     * 
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        return (text == null || text.trim().length() == 0);
    }

    private static String[] EXCLUDE_STRINGS = new String[] {
            "http://",
            "https://",
            "facebook.com/",
            "\\?ref=tn_tnmn",
            "facebook.com/",
            "www",
            "twitter.com/",
            "^@",
            "^/"
    };

    /**
     * Normalize facebook/Social id from spreadsheet.
     * 
     * @param input
     * @return
     */
    public static String getSocialId(String input) {
        String result = input;
        for (String excludeStr : EXCLUDE_STRINGS) {
            result = result.replaceAll(excludeStr, "");
        }
        return result;
    }

    /**
     * 
     * @param input
     * @return
     */
    public static boolean isValidId(String input) {
        if (!isEmpty(input) && !input.contains(" ") && !input.contains("@")) {
            return true;
        }
        return false;
    }

    /**
     * Get facebook profile url.
     * 
     * @param fbId
     * @return
     */
    public static String getFacebookProfileUrl(String fbId) {
        if (fbId != null) {
            return String.format(Config.FACEBOOK_PROFILE_URL, fbId);
        } else {
            return null;
        }
    }

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Parses a ISO8601-compliant date/time string.
     * 
     * @param text
     *            the date/time string to be parsed
     * @return a <code>Calendar</code>, or <code>null</code> if the input could not be parsed
     */
    public static Date convertGoogleDate(String text) {
        // parse time zone designator (Z or +00:00 or -00:00)
        // and build time zone id (GMT or GMT+00:00 or GMT-00:00)
        String tzID = "GMT"; // Zulu, i.e. UTC/GMT (default)
        int tzPos = text.indexOf('Z');
        if (tzPos == -1) {
            // not Zulu, try +
            tzPos = text.indexOf('+');
            if (tzPos == -1) {
                // not +, try -, but remember it might be used within first
                // 8 charaters for separating year, month and day, yyyy-mm-dd
                tzPos = text.indexOf('-', 8);
            }
            if (tzPos == -1) {
                // no time zone specified, assume Zulu
            } else {
                // offset to UTC specified in the format +00:00/-00:00
                tzID += text.substring(tzPos);
                text = text.substring(0, tzPos);
            }
        } else {
            // Zulu, i.e. UTC/GMT
            text = text.substring(0, tzPos);
        }

        TimeZone tz = TimeZone.getTimeZone(tzID);
        SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT);
        format.setLenient(false);
        format.setTimeZone(tz);
        Date date = format.parse(text, new ParsePosition(0));
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance(tz);
        cal.setTime(date);
        return cal.getTime();
    }

    /**
     * Common date to string convert method.
     * 
     * @param date
     * @return
     */
    public static String convertCommonTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        return sdf.format(date);
    }

    public static String convertToTweetTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        return sdf.format(date);
    }

    /**
     * Convert tweet time to user friendly string.
     * 
     * @param createdTime
     * @return
     */
    public static String convertTweetTime(Context context, String createdTime) {
        String result = convertCommonTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        try {
            Date createdDate = sdf.parse(createdTime);
            Date currentDate = new Date();
            long elapsedTime = currentDate.getTime() - createdDate.getTime();
            if (elapsedTime <= HOUR_INTERVAL) {
                int minutes = (int) (elapsedTime / (60 * 1000));
                result = context.getResources().getQuantityString(R.plurals.minute_string, minutes, minutes);
            } else if (elapsedTime <= DAY_INTERVAl) {
                int hours = (int) (elapsedTime / HOUR_INTERVAL);
                result = context.getResources().getQuantityString(R.plurals.hour_string, hours, hours);
            } else {
                result = convertCommonTime(createdDate);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }

    /**
     * Format tweet name.
     * 
     * @param context
     * @param fullname
     * @param nickname
     * @return
     */
    public static Spanned convertTweetAuthor(Context context, String fullname, String nickname) {
        Spanned result = new SpannableString(fullname + " @" + nickname);
        if (context != null) {
            String fullnameColor = "#000000";
            result = Html.fromHtml(context.getString(R.string.tweet_author_string, fullnameColor, fullname, nickname));
        }
        return result;
    }
}
