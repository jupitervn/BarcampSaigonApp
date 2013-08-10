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

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class HourSectionLoader extends CursorLoader {
    private final String mHourString;

    public HourSectionLoader(Context context, String hourString) {
        super(context);
        mHourString = hourString;
    }

    @Override
    protected Cursor onLoadInBackground() {
        SqlCacheHelper sqlHelper = SqlCacheHelper.getInstance(getContext());
        return sqlHelper.getAllHourSection(mHourString);
    }

}
