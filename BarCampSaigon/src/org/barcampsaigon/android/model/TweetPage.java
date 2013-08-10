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
import java.util.List;

import com.google.api.client.util.Key;

/**
 * @author "Jupiter"
 * 
 */
public class TweetPage implements Serializable {
    @Key("statuses")
    private List<Tweet> mTweets;

    public List<Tweet> getTweets() {
        return mTweets;
    }

    public void setTweets(List<Tweet> tweets) {
        mTweets = tweets;
    }

}
