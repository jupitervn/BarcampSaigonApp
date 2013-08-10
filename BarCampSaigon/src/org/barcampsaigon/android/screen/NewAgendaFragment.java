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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.barcampsaigon.android.R;
import org.barcampsaigon.android.service.BackgroundService;
import org.barcampsaigon.android.ui.ExtendedHorizontalScrollView;
import org.barcampsaigon.android.ui.ExtendedHorizontalScrollView.OnScrollStoppedListener;
import org.barcampsaigon.android.util.Config;

import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * New Agenda fragment using view pager and horizontal scroll view.
 * 
 * @author Cao Duy Vu (vu.cao.duy@gmail.com)
 * 
 */
public class NewAgendaFragment extends BaseFragment implements
        OnPageChangeListener,
        OnScrollStoppedListener,
        OnTouchListener, OnGlobalLayoutListener, OnClickListener {
    private View mRootView;
    private ViewPager mSectionPager;
    private ExtendedHorizontalScrollView mHourScrollView;
    private LinearLayout mHourInnerLayout;
    private Button mRefreshButton;
    private AgendaTaskReceiver mReceiver;
    private SectionAdapter mAdapter;
    private TextView mSelectedTextView;
    private View mRefreshView;
    private int mCurrentSelectedItem = 0;
    private static final int HOUR_SLOTS = 5;
    private boolean isFetchSuccess = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_new_agenda_fragment, container, false);
        mSectionPager = (ViewPager) mRootView.findViewById(R.id.section_pager);
        mHourScrollView = (ExtendedHorizontalScrollView) mRootView.findViewById(R.id.hour_scroll_view);
        mHourInnerLayout = (LinearLayout) mRootView.findViewById(R.id.hour_inner_view);
        mHourScrollView.setOnScrollStoppedListener(this);
        mHourScrollView.setOnTouchListener(this);
        mSelectedTextView = (TextView) mRootView.findViewById(R.id.hour_selected_view);
        mHourScrollView.setVisibility(View.VISIBLE);
        mSelectedTextView.setVisibility(View.VISIBLE);
        mRefreshButton = (Button) mRootView.findViewById(R.id.btn_refresh);
        mRefreshButton.setOnClickListener(this);
        mRefreshView = mRootView.findViewById(R.id.refresh_panel);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalBroadcastManager localMgr = LocalBroadcastManager.getInstance(getActivity());
        if (mReceiver == null) {
            mReceiver = new AgendaTaskReceiver(this);
        }
        IntentFilter filter = new IntentFilter(BackgroundService.ACTION_FETCH_AGENDA_SUCCESS);
        filter.addAction(BackgroundService.ACTION_FETCH_ERROR);
        localMgr.registerReceiver(mReceiver, filter);
        showAgendaRefresh();
        getAgenda();
    }

    private void showAgendaRefresh() {
        mRefreshView.setVisibility(View.VISIBLE);
    }

    private void hideAgendaRefresh() {
        mRefreshView.setVisibility(View.GONE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager localMgr = LocalBroadcastManager.getInstance(getActivity());
        if (mReceiver != null) {
            localMgr.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFetchSuccess) {
            getAgenda();
        }
    }

    private void getAgenda() {
        Intent getAgendaIntent = new Intent(getActivity(), BackgroundService.class);
        getAgendaIntent.setAction(BackgroundService.ACTION_FETCH_AGENDA);
        getActivity().startService(getAgendaIntent);
    }

    @Override
    public void updateWithObject(Context context, Object object, String tag) {

    }

    private void onFetchAgendaSuccess(Set<String> hourSections) {
        hideLoadingDialog();
        hideAgendaRefresh();
        renderHours(hourSections);
        mAdapter = new SectionAdapter(getChildFragmentManager(), hourSections);
        mSectionPager.setAdapter(mAdapter);
        mSectionPager.setOnPageChangeListener(this);
        mSectionPager.setCurrentItem(mCurrentSelectedItem);
        Log.d("D.Vu", "CURRENT ITEM " + mCurrentSelectedItem);
        mHourScrollView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        //        setSelectedItem(mCurrentSelectedItem);
        isFetchSuccess = true;
        mRefreshButton.setVisibility(View.GONE);
    }

    private void onFetchAgendaFailed() {
        hideLoadingDialog();
        hideAgendaRefresh();
        Set<String> hourString = Config.getHourData(getActivity());
        Toast.makeText(getActivity(), R.string.agenda_connection_error, Toast.LENGTH_LONG).show();
        isFetchSuccess = false;
        if (hourString != null) {
            onFetchAgendaSuccess(hourString);
        } else {
            mRefreshButton.setVisibility(View.VISIBLE);
        }

    }

    private void setSelectedItem(int position) {
        scrollToColumn(position);
    }

    private TextView mPreviousSelected;

    private void scrollToColumn(int position) {
        TextView prevChildView = (TextView) mHourInnerLayout.getChildAt(position);

        if (prevChildView != null) {
            int childLeft = prevChildView.getLeft();
            Log.d("D.Vu", "SCROLL TO " + childLeft + " " + position);
            mHourScrollView
                    .smoothScrollTo(childLeft - (mHourScrollView.getHorizontalFadingEdgeLength() / 2), 0);
        }
        if (position >= mHourInnerLayout.getChildCount() - 5) {
            position = mHourInnerLayout.getChildCount() - 5;
        }
        if (position >= 0 && position <= mHourInnerLayout.getChildCount() - 2) {
            prevChildView = (TextView) mHourInnerLayout.getChildAt(position + 2);
            if (mPreviousSelected != null) {
                mPreviousSelected.setSelected(false);
            }
            prevChildView.setSelected(true);
            mPreviousSelected = prevChildView;
        }
    }

    private void renderHours(Set<String> hourSections) {
        mPreviousSelected = null;
        mHourInnerLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels / HOUR_SLOTS;
        FrameLayout.LayoutParams selectedLp = (android.widget.FrameLayout.LayoutParams) mSelectedTextView
                .getLayoutParams();
        selectedLp.width = width;
        mSelectedTextView.setLayoutParams(selectedLp);
        int index = 0;
        for (String hourSection : hourSections) {
            TextView hourTextView = (TextView) inflater.inflate(R.layout.hour_item_layout, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
            hourTextView.setWidth(width);
            int separatorIndex = hourSection.indexOf('-');
            if (separatorIndex != -1) {
                hourSection = hourSection.substring(0, separatorIndex);
            }
            hourTextView.setText(hourSection);
            hourTextView.setOnClickListener(this);
            hourTextView.setTag(Integer.valueOf(index));
            mHourInnerLayout.addView(hourTextView, lp);
            index++;
        }
        for (int i = 0; i < 2; i++) {
            TextView hourTextView = (TextView) inflater.inflate(R.layout.hour_item_layout, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
            hourTextView.setWidth(width);
            mHourInnerLayout.addView(hourTextView, i, lp);
        }
        for (int i = 0; i < 2; i++) {
            TextView hourTextView = (TextView) inflater.inflate(R.layout.hour_item_layout, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
            hourTextView.setWidth(width);
            mHourInnerLayout.addView(hourTextView, lp);
        }
        mHourScrollView.setVisibility(View.VISIBLE);
        mSelectedTextView.setVisibility(View.VISIBLE);

    }

    private static class AgendaTaskReceiver extends BroadcastReceiver {
        private final WeakReference<NewAgendaFragment> mFragment;

        public AgendaTaskReceiver(NewAgendaFragment fragment) {
            mFragment = new WeakReference<NewAgendaFragment>(fragment);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                NewAgendaFragment fragment = mFragment.get();
                if (action.equals(BackgroundService.ACTION_FETCH_AGENDA_SUCCESS)) {
                    TreeSet<String> hourSections = (TreeSet<String>) intent
                            .getSerializableExtra(BackgroundService.EXTRA_LIST_OF_HOUR_SECTIONS);
                    Log.d("D.Vu", "FETCH SUCCESS " + hourSections.size());
                    if (fragment != null) {
                        fragment.onFetchAgendaSuccess(hourSections);
                    }
                } else if (action.equals(BackgroundService.ACTION_FETCH_ERROR)) {
                    if (fragment != null) {
                        fragment.onFetchAgendaFailed();
                    }
                }
            }

        }
    }

    private static class SectionAdapter extends FragmentStatePagerAdapter {
        private List<String> mHourSections;

        public SectionAdapter(FragmentManager fm, Set<String> hourSections) {
            super(fm);
            setHourSections(new ArrayList<String>());
            getHourSections().addAll(hourSections);
        }

        @Override
        public Fragment getItem(int position) {
            String hourString = getHourSections().get(position);
            Log.d("D.Vu", "GET ITEM " + position);
            return HourSectionFragment.newInstance(hourString);
        }

        @Override
        public int getCount() {
            return (getHourSections() == null) ? 0 : getHourSections().size();
        }

        public List<String> getHourSections() {
            return mHourSections;
        }

        public void setHourSections(List<String> hourSections) {
            mHourSections = hourSections;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    private int mScrollY = 0;

    public void notifyScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    @Override
    public void onPageSelected(int position) {
        //        HourSectionFragment currentFragmenst = (HourSectionFragment) mAdapter.getItem(mCurrentSelectedItem);
        //        int scrollY = currentFragment.getScrollY();
        mCurrentSelectedItem = position;
        HourSectionFragment nextFragment = (HourSectionFragment) mAdapter.getItem(mCurrentSelectedItem);
        if (nextFragment != null && !nextFragment.isRemoving()) {
            nextFragment.updateScrollY(mScrollY);
        }
        scrollToColumn(position);

    }

    @Override
    public void onScrollStopped() {
        checkAndScroll();
    }

    private boolean checkAndScroll() {
        if (mHourInnerLayout.getChildCount() > 0) {
            for (int i = 0; i < mHourInnerLayout.getChildCount(); i++) {
                View prevChildView = mHourInnerLayout.getChildAt(i);
                View nextChildView = mHourInnerLayout.getChildAt(i + 1);
                if (prevChildView != null && nextChildView != null) {
                    int scrollX = (mHourScrollView.getScrollX());
                    if (scrollX < 0) {
                        scrollX = 0;
                    }
                    if (prevChildView.getLeft() <= scrollX
                            && scrollX <= nextChildView.getLeft()) {
                        int leftOffset = scrollX - prevChildView.getLeft();
                        int rightOffset = nextChildView.getLeft() - scrollX;
                        if (leftOffset >= rightOffset) {
                            mSectionPager.setCurrentItem(i + 1, true);
                            scrollToColumn(i + 1);
                        } else {
                            mSectionPager.setCurrentItem(i, true);
                            scrollToColumn(i);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            mHourScrollView.startScrollerTask();
        }
        return false;
    }

    @Override
    public void onGlobalLayout() {
        setSelectedItem(mCurrentSelectedItem);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_refresh:
            if (!isFetchSuccess) {
                getAgenda();
            }
            break;
        default:
            Integer col = (Integer) view.getTag();
            if (col != null) {
                setSelectedItem(col);
                mSectionPager.setCurrentItem(col);
            }
            break;
        }

    }
}
