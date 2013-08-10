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
package org.barcampsaigon.android.screen;

import java.util.Date;

import org.barcampsaigon.android.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class InfoActivity extends RoboActivity implements OnClickListener {
    @InjectView(R.id.tv_barcamp_link)
    private TextView mBarcampUrlTextView;
    @InjectView(R.id.tv_contact_email)
    private TextView mContactTextView;
    @InjectView(R.id.tv_bug_email)
    private TextView mBugTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_layout);
        mBarcampUrlTextView.setOnClickListener(this);
        mContactTextView.setOnClickListener(this);
        mBugTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_barcamp_link:
            Intent showUrl = new Intent(Intent.ACTION_VIEW);
            showUrl.setData(Uri.parse(getString(R.string.barcamp_url)));
            startActivity(showUrl);
            break;
        case R.id.tv_contact_email:
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getString(R.string.contact_email), null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            break;
        case R.id.tv_bug_email:
            emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getString(R.string.bug_email), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "BUG REPORT " + (new Date()).toString());
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            break;
        }
    }

}
