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
package org.barcampsaigon.android.cache;

import java.util.List;

import org.barcampsaigon.android.model.Event;
import org.barcampsaigon.android.model.Participant;
import org.barcampsaigon.android.model.TalkSection;
import org.barcampsaigon.android.util.MiscUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class SqlCacheHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "barcampsg.db";
    private static final int DB_VERSION = 7;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "name";
    public static final String COLUMN_HOUR_SECTION = "hour";
    public static final String COLUMN_ROOM_NAME = "room";
    public static final String COLUMN_AUTHOR_NAME = "author";
    public static final String COLUMN_AUTHOR_PROFILE_URL = "author_profile";

    public static final String ATTENDEE_COLUMN_ID = "_id";
    public static final String ATTENDEE_COLUMN_NAME = "name";
    public static final String ATTENDEE_COLUMN_FB = "facebook";
    public static final String ATTENDEE_COLUMN_TW = "twitter";
    public static final String ATTENDEE_COLUMN_POSITION = "position";
    public static final String ATTENDEE_COLUMN_COMPANY = "company";
    public static final String ATTENDEE_WANNA_HEAR = "wanna_hear";
    public static final String ATTENDEE_WANNA_PRESENT = "wanna_present";
    public static final String ATTENDEE_COLUMN_SEACH_VALUE = "search_value";

    public static final String TWEET_COLUMN_ID = "_id";
    public static final String TWEET_COLUMN_TEXT = "text";
    public static final String TWEET_COLUMN_FULLNAME = "name";
    public static final String TWEET_COLUMN_NICK = "nick";
    public static final String TWEET_COLUMN_AVATAR = "avatar";
    public static final String TWEET_COLUMN_DATE = "created_date";

    private static final String TABLE_NAME = "AGENDA";
    private static final String TABLE_ATTENDEE = "ATTENDEE";
    private static final String TABLE_TWEETS = "TWEETS";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + "(\""
            + COLUMN_ID + "\" int auto increment, \""
            + COLUMN_TITLE + "\" text, \""
            + COLUMN_HOUR_SECTION + "\" text not null, \""
            + COLUMN_ROOM_NAME + "\" text not null, \""
            + COLUMN_AUTHOR_NAME + "\" text, \""
            + COLUMN_AUTHOR_PROFILE_URL + "\" text "
            + ");";

    private static final String CREATE_ATTENDEE_TABLE = "create table if not exists " + TABLE_ATTENDEE + "(\""
            + ATTENDEE_COLUMN_ID + "\" int auto increment, \""
            + ATTENDEE_COLUMN_FB + "\" text, \""
            + ATTENDEE_COLUMN_TW + "\" text, \""
            + ATTENDEE_COLUMN_COMPANY + "\" text, \""
            + ATTENDEE_WANNA_HEAR + "\" text, \""
            + ATTENDEE_WANNA_PRESENT + "\" text, \""
            + ATTENDEE_COLUMN_NAME + "\" text not null, \""
            + ATTENDEE_COLUMN_SEACH_VALUE + "\" text not null, \""
            + ATTENDEE_COLUMN_POSITION + "\" text "
            + ");";

    private static final String CREATE_TWEETS_TABLE = "create table if not exists " + TABLE_TWEETS + "(\""
            + TWEET_COLUMN_ID + "\" int, \""
            + TWEET_COLUMN_TEXT + "\" text not null, \""
            + TWEET_COLUMN_FULLNAME + "\" text not null, \""
            + TWEET_COLUMN_NICK + "\" text not null, \""
            + TWEET_COLUMN_AVATAR + "\" text, \""
            + TWEET_COLUMN_DATE + "\" text "
            + ");";

    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME + ";";
    private static final String DROP_ATTENDEE = "drop table if exists " + TABLE_ATTENDEE + ";";
    private static final String DROP_TWEETS = "drop table if exists " + TABLE_TWEETS + ";";

    private static Object lock = new Object();
    private static SqlCacheHelper _instance;

    /**
     * Default constructor.
     * 
     * @param context
     */
    public SqlCacheHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_ATTENDEE_TABLE);
        db.execSQL(CREATE_TWEETS_TABLE);
        Log.d("D.Vu", "CREATE TABLE " + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(DROP_TABLE);
            db.execSQL(DROP_ATTENDEE);
            db.execSQL(DROP_TWEETS);
            onCreate(db);
        }

    }

    private boolean addNewEvent(SQLiteDatabase db, Event event, String roomName) {
        ContentValues values = new ContentValues();
        long rowId = db.insert(TABLE_NAME, null, values);
        return rowId != -1;
    }

    private boolean addTalkSection(SQLiteDatabase db, TalkSection section) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NAME, section.getRoomName());
        values.put(COLUMN_HOUR_SECTION, section.getHourSection());
        values.put(COLUMN_TITLE, section.getTitle());
        values.put(COLUMN_AUTHOR_NAME, section.getAuthorName());
        values.put(COLUMN_AUTHOR_PROFILE_URL, section.getAuthorProfileUrl());
        long rowId = db.insert(TABLE_NAME, null, values);
        return rowId != -1;
    }

    /**
     * Drop all old cached and update new data.
     * 
     * @param talkSections
     * @return
     */
    public boolean updateAllDatabase(List<TalkSection> talkSections) {
        SQLiteDatabase db = getWritableDatabase();
        boolean result = true;
        try {
            if (talkSections != null && talkSections.size() > 0) {
                db.execSQL(DROP_TABLE);
                db.execSQL(CREATE_TABLE);
                db.beginTransaction();
                for (TalkSection talkSection : talkSections) {
                    addTalkSection(db, talkSection);
                }
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * Search attendee list.
     * 
     * @param keyword
     * @return
     */
    public Cursor searchAttendeeList(CharSequence keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ATTENDEE, null, ATTENDEE_COLUMN_SEACH_VALUE + " LIKE " + "'%" + keyword + "%'", null,
                null, null, null);
    }

    /**
     * Update attendee cache.
     * 
     * @param attendees
     * @return
     */
    public boolean updateAttendeeList(List<Participant> attendees) {
        SQLiteDatabase db = getWritableDatabase();
        boolean result = true;
        try {
            if (attendees != null && attendees.size() > 0) {
                db.execSQL(DROP_ATTENDEE);
                db.execSQL(CREATE_ATTENDEE_TABLE);
                db.beginTransaction();
                for (Participant attendee : attendees) {
                    addAttendee(db, attendee);
                }
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    private boolean addAttendee(SQLiteDatabase db, Participant attendee) {
        ContentValues values = new ContentValues();
        values.put(ATTENDEE_COLUMN_NAME, attendee.getName());
        values.put(ATTENDEE_COLUMN_FB, attendee.getFacebookUrl());
        values.put(ATTENDEE_COLUMN_POSITION, attendee.getPosition());
        values.put(ATTENDEE_COLUMN_TW, attendee.getTwiterAcc());
        values.put(ATTENDEE_COLUMN_COMPANY, attendee.getCompany());
        values.put(ATTENDEE_WANNA_HEAR, attendee.getTopicWannaHear());
        values.put(ATTENDEE_WANNA_PRESENT, attendee.getTopicWannaPresent());
        values.put(ATTENDEE_COLUMN_SEACH_VALUE,
                MiscUtils.convertToVietnamesWithoutSpecialChars(attendee.getName() + " " + attendee.getCompany()));
        long rowId = db.insert(TABLE_ATTENDEE, null, values);
        return rowId != -1;
    }

    public Cursor getAllHourSection(String hourString) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, COLUMN_HOUR_SECTION + " = \"" + hourString + "\"", null, null, null, null);
    }

    public Cursor getParticipants() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ATTENDEE, null, null, null, null, null, null);
    }

    public Cursor getAllRowModels() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);

    }

    /**
     * Get SqlCacheHelper instance.
     * 
     * @param context
     * @return
     */
    public static SqlCacheHelper getInstance(Context context) {
        if (_instance == null) {
            synchronized (lock) {
                _instance = new SqlCacheHelper(context);
            }
        }
        return _instance;
    }
}
