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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshWebView;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class AboutFragment extends BaseFragment implements
        AnimationListener,
        OnClickListener {
    private View mRootView = null;
    private ImageView mVespaView;
    private PullToRefreshWebView mMainContent;
    private View mMainContentWrapped;
    private RelativeLayout mParentView;
    //    private TextView mTextView;
    private ImageView mInfoBtn;
    private ImageView mMapBtn;
    private long start;
    private long end;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.tab_about_content, container, false);
        mMainContentWrapped = mRootView.findViewById(R.id.ll_main_content_wrapped);
        mVespaView = (ImageView) mRootView.findViewById(R.id.vespa_girl_view);
        mMainContent = (PullToRefreshWebView) mRootView.findViewById(R.id.ll_main_content);
        mParentView = (RelativeLayout) mRootView.findViewById(R.id.rl_parent);
        mInfoBtn = (ImageView) mRootView.findViewById(R.id.btn_info);
        mInfoBtn.setOnClickListener(this);
        mMapBtn = (ImageView) mRootView.findViewById(R.id.btn_map);
        mMapBtn.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainContent.getRefreshableView().getSettings().setJavaScriptEnabled(true);
        mMainContent.getRefreshableView().loadUrl(
                "https://dl.dropboxusercontent.com/u/43997154/barcamp_mobile/barcamp_mobile.html");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(
        //                R.id.fm_map_fragment);
        //        mapFragment = (SupportMapFragment) (getActivity()).getSupportFragmentManager().findFragmentById(
        //                R.id.fm_map_fragment);
        //        if (mapFragment != null) {
        //            mMap = mapFragment.getMap();
        //
        //            setupMap();
        //        }
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.vespa_animation);
        //        mVespaView.startAnimation(mRotateAnimation);
        //        mVespaView.startAnimation(mTranslateAnimation);

        a.setAnimationListener(this);
        AnimationSet set = new AnimationSet(false);
        Animation rotateAnim = new RotateAnimation(0, 3, RotateAnimation.RELATIVE_TO_SELF, 0.4f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setStartOffset(0);
        rotateAnim.setRepeatCount(RotateAnimation.INFINITE);
        rotateAnim.setRepeatMode(RotateAnimation.REVERSE);
        rotateAnim.setDuration(100);
        Animation translateAnim = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        translateAnim.setDuration(2500);
        translateAnim.setStartOffset(700);
        set.addAnimation(rotateAnim);
        set.addAnimation(translateAnim);
        set.setFillAfter(true);
        mVespaView.startAnimation(set);
        translateAnim.setAnimationListener(this);
        //        set.setAnimationListener(this);
    }

    @Override
    public void updateWithObject(Context context, Object object, String tag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        start = System.currentTimeMillis();
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Animation currentAnimation = mVespaView.getAnimation();
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mParentView.setBackgroundColor(Color.TRANSPARENT);
            mainActivity.showTab(true);
            mMainContentWrapped.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        end = System.currentTimeMillis();
        start = end;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_info:
            Intent showInfoActivity = new Intent(getActivity(), InfoActivity.class);
            startActivity(showInfoActivity);
            break;
        case R.id.btn_map:
            Intent showMapActivity = new Intent(getActivity(), InternalMapActivity.class);
            showMapActivity.putExtra(InternalMapActivity.EXTRA_MAP_POSITION, 0);
            startActivity(showMapActivity);
            break;
        }
    }

}
