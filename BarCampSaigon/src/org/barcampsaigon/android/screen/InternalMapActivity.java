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

import org.barcampsaigon.android.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class InternalMapActivity extends RoboActivity implements OnClickListener {
    @InjectView(R.id.btn_left)
    private ImageView mPrevButton;
    @InjectView(R.id.btn_right)
    private ImageView mNextButton;
    @InjectView(R.id.iv_map_view)
    private ImageView mMapView;
    @InjectView(R.id.tv_header)
    private TextView mHeaderView;

    public static final String EXTRA_MAP_POSITION = "extra-map-position";
    private int mCurrentFloor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_map_layout);
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentFloor = intent.getIntExtra(EXTRA_MAP_POSITION, 0);
        }
        changeMapToFloor(mCurrentFloor);
        mPrevButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
    }

    private void changeMapToFloor(int position) {
        mPrevButton.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
        switch (position) {
        case 0:
            mHeaderView.setText(R.string.map_first_floor);
            mMapView.setImageResource(R.drawable.first_floor);
            mPrevButton.setVisibility(View.INVISIBLE);
            break;
        case 1:
            mHeaderView.setText(R.string.map_second_floor);
            mMapView.setImageResource(R.drawable.second_floor);
            break;
        case 2:
            mHeaderView.setText(R.string.map_third_floor);
            mMapView.setImageResource(R.drawable.third_floor);
            mNextButton.setVisibility(View.INVISIBLE);
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            if (mCurrentFloor > 0) {
                mCurrentFloor--;
            }
            changeMapToFloor(mCurrentFloor);
            break;
        case R.id.btn_right:
            if (mCurrentFloor < 2) {
                mCurrentFloor++;
            }
            changeMapToFloor(mCurrentFloor);
            break;
        }

    }
}
