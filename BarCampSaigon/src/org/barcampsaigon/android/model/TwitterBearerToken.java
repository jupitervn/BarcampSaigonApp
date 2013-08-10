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

import com.google.api.client.util.Key;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class TwitterBearerToken {
    @Key("token_type")
    private String mTokenType;
    @Key("access_token")
    private String mAccessToken;
    public String getTokenType() {
        return mTokenType;
    }
    public void setTokenType(String tokenType) {
        mTokenType = tokenType;
    }
    public String getAccessToken() {
        return mAccessToken;
    }
    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
