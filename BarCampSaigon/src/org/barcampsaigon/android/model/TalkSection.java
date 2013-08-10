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

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class TalkSection {
    private String mHourSection;
    private String mRoomName;
    private String mTitle;
    private String mAuthorName;
    private String mAuthorProfileUrl;

    public TalkSection(String inputString, String hourSection, String roomName) {
        setHourSection(hourSection);
        setRoomName(roomName);
        setTitle(inputString);
    }

    /**
     * @return the hourSection
     */
    public String getHourSection() {
        return mHourSection;
    }

    /**
     * @param hourSection
     *            the hourSection to set
     */
    public void setHourSection(String hourSection) {
        mHourSection = hourSection;
    }

    /**
     * @return the roomName
     */
    public String getRoomName() {
        return mRoomName;
    }

    /**
     * @param roomName
     *            the roomName to set
     */
    public void setRoomName(String roomName) {
        mRoomName = roomName;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        return mAuthorName;
    }

    /**
     * @param authorName
     *            the authorName to set
     */
    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    /**
     * @return the authorProfileUrl
     */
    public String getAuthorProfileUrl() {
        return mAuthorProfileUrl;
    }

    /**
     * @param authorProfileUrl
     *            the authorProfileUrl to set
     */
    public void setAuthorProfileUrl(String authorProfileUrl) {
        mAuthorProfileUrl = authorProfileUrl;
    }

}
