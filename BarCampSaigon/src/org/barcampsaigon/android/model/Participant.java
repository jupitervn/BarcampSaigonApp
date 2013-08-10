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
package org.barcampsaigon.android.model;

import java.io.Serializable;

import org.barcampsaigon.android.cache.SqlCacheHelper;

import android.database.Cursor;

/**
 * Participant model.
 * 
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class Participant implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3540436916906157687L;
    private String name;
    private String facebookUrl;
    private String twiterAcc;
    private String position;
    private String company;
    private String topicWannaPresent;
    private String topicWannaHear;

    public Participant() {

    }

    public Participant(Cursor cursor) {
        if (cursor != null) {
            String attendeeName = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_COLUMN_NAME));
            String attendeePosition = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_COLUMN_POSITION));
            String facebookId = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_COLUMN_FB));
            String twitterId = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_COLUMN_TW));
            String company = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_COLUMN_COMPANY));
            String topicWannaHear = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_WANNA_HEAR));
            String topicWannaPresent = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.ATTENDEE_WANNA_PRESENT));
            setName(attendeeName);
            setPosition(attendeePosition);
            setFacebookUrl(facebookId);
            setTwiterAcc(twitterId);
            setCompany(company);
            setTopicWannaHear(topicWannaHear);
            setTopicWannaPresent(topicWannaPresent);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTwiterAcc() {
        return twiterAcc;
    }

    public void setTwiterAcc(String twiterAcc) {
        this.twiterAcc = twiterAcc;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTopicWannaHear() {
        return topicWannaHear;
    }

    public void setTopicWannaHear(String topicWannaHear) {
        this.topicWannaHear = topicWannaHear;
    }

    public String getTopicWannaPresent() {
        return topicWannaPresent;
    }

    public void setTopicWannaPresent(String topicWannaPresent) {
        this.topicWannaPresent = topicWannaPresent;
    }

}
