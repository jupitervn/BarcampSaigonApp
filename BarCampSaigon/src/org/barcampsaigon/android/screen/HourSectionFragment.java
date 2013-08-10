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
import org.barcampsaigon.android.cache.HourSectionLoader;
import org.barcampsaigon.android.cache.SqlCacheHelper;
import org.barcampsaigon.android.service.BackgroundService;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class HourSectionFragment extends BaseFragment implements
        OnRefreshListener<ListView>,
        LoaderCallbacks<Cursor>,
        OnScrollListener, OnItemClickListener {
    private View mRootView;
    private PullToRefreshListView mListView;
    private HourSectionAdapter mAdapter;
    private int mScrollY = 0;
    /**
     * Extra for hour section string.
     */
    public static final String EXTRA_HOUR_SECTION_STRING = "hour-section";

    /**
     * Create new instance of Hour Section Fragment.
     * 
     * @param hourString
     * @return
     */
    public static HourSectionFragment newInstance(String hourString) {
        HourSectionFragment f = new HourSectionFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_HOUR_SECTION_STRING, hourString);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.hour_section_fragment_layout, container, false);
        mListView = (PullToRefreshListView) mRootView.findViewById(R.id.section_list);
        mListView.getRefreshableView().setDividerHeight(
                getResources().getDimensionPixelSize(R.dimen.talk_list_divider_height));
        mListView.setOnRefreshListener(this);
        mListView.setOnScrollListener(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().restartLoader(0, getArguments(), this);
    }

    @Override
    public void updateWithObject(Context context, Object object, String tag) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        Intent getAgendaIntent = new Intent(getActivity(), BackgroundService.class);
        getAgendaIntent.setAction(BackgroundService.ACTION_FETCH_AGENDA);
        getActivity().startService(getAgendaIntent);
    }

    private void showSection(Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new HourSectionAdapter(getActivity(), cursor, 0);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.swapCursor(cursor);
            mAdapter.notifyDataSetChanged();
        }
        mListView.setOnItemClickListener(this);
        mListView.getRefreshableView().scrollTo(0, mScrollY);
        mListView.onRefreshComplete();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
        String hourString = null;
        if (bundle != null) {
            hourString = bundle.getString(EXTRA_HOUR_SECTION_STRING);
        }
        HourSectionLoader cursorLoader = new HourSectionLoader(getActivity(), hourString);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        showSection(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {

    }

    private static class HourSectionAdapter extends CursorAdapter {
        private static final int[] ROOM_COLORS = new int[] {
                R.color.room_red_bgr,
                R.color.room_green_bgr,
                R.color.room_blue_bgr };

        public HourSectionAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public void bindView(View convertView, Context context, Cursor cursor) {
            String title = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.COLUMN_TITLE));
            String roomName = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.COLUMN_ROOM_NAME));
            String sectionAuthor = cursor.getString(cursor.getColumnIndex(SqlCacheHelper.COLUMN_AUTHOR_NAME));
            TextView titleTextView = (TextView) convertView.findViewById(R.id.talk_title);
            TextView roomNameView = (TextView) convertView.findViewById(R.id.talk_room_view);
            TextView authorView = (TextView) convertView.findViewById(R.id.talk_author_view);
            authorView.setText(sectionAuthor);
            roomNameView.setBackgroundColor(context.getResources().getColor(ROOM_COLORS[cursor.getPosition() % 3]));
            titleTextView.setText(title);
            roomNameView.setText(roomName);
            convertView.setTag(roomName);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.section_item_layout, null);
            return convertView;
        }
    }

    public int getScrollY() {
        return mListView.getScrollY();
    }

    public void updateScrollY(int scrollY) {
        this.mScrollY = scrollY;
        if (mListView != null) {
            mListView.getRefreshableView().scrollTo(0, this.mScrollY);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ((NewAgendaFragment) getParentFragment()).notifyScrollY(mListView.getScrollY());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            String roomName = (String) view.getTag();
            if (roomName != null) {
                int imagePosition = 0;
                if (roomName.contains("1st") || roomName.contains("1 st")) {
                    imagePosition = 0;
                } else if (roomName.contains("2nd") || roomName.contains("2 nd")) {
                    imagePosition = 1;
                } else if (roomName.contains("3rd") || roomName.contains("3 rd")) {
                    imagePosition = 2;
                }
                Intent showInternalMapIntent = new Intent(getActivity(), InternalMapActivity.class);
                showInternalMapIntent.putExtra(InternalMapActivity.EXTRA_MAP_POSITION, imagePosition);
                startActivity(showInternalMapIntent);
            }
        }

    }
}
