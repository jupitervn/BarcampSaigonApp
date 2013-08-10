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

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.barcampsaigon.android.R;
import org.barcampsaigon.android.cache.ParticipantCursorLoader;
import org.barcampsaigon.android.cache.SqlCacheHelper;
import org.barcampsaigon.android.model.Participant;
import org.barcampsaigon.android.service.HttpUtils;
import org.barcampsaigon.android.util.Config;
import org.barcampsaigon.android.util.MiscUtils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class RegisterFragment extends BaseFragment implements
        OnRefreshListener<ListView>,
        LoaderCallbacks<Cursor>,
        OnItemClickListener, TextWatcher, OnEditorActionListener {
    private View mRootView;
    private PullToRefreshListView mListView;
    private AttendeeAdapter mAdapter;
    private EditText mSearchEditText;
    private boolean isLoading = false;
    private boolean isFirstTime = true;
    private String mCurrentConstraint = null;
    /**
     * Extra for attendee data.
     */
    public static final String EXTRA_ATTENDESS = "extra-attendess";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_register_content, container, false);
        mListView = (PullToRefreshListView) mRootView.findViewById(R.id.participants_list);
        mSearchEditText = (EditText) mRootView.findViewById(R.id.search_input_view);
        mSearchEditText.addTextChangedListener(this);
        mSearchEditText.setOnEditorActionListener(this);
        mSearchEditText.setVisibility(View.GONE);
        mListView.setOnRefreshListener(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void updateWithObject(Context context, Object object, String tag) {

    }

    private ParticipantAsyncTask mTask = null;

    private void doGetAttendee() {
        if (!isLoading) {
            isLoading = true;
            if (mTask != null) {
                mTask.cancel(true);
            }
            mTask = new ParticipantAsyncTask(this);
            mTask.execute();

        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        doGetAttendee();
    }

    private void generateAttendee() {
        hideLoadingDialog();
        mListView.onRefreshComplete();
        getLoaderManager().restartLoader(0, null, this);

    }

    private void onHandleFetchFailed() {
        hideLoadingDialog();
        mListView.onRefreshComplete();
        isLoading = false;
        mSearchEditText.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), R.string.agenda_connection_error, Toast.LENGTH_LONG).show();
    }

    private static class AttendeeAdapter extends CursorAdapter {

        public AttendeeAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public void bindView(View convertView, Context context, Cursor cursor) {
            TextView attendeeNameText = (TextView) convertView.findViewById(R.id.attendee_name);
            TextView attendeePositionText = (TextView) convertView.findViewById(R.id.attendee_position);
            ImageView attendeeImageView = (ImageView) convertView.findViewById(R.id.attendee_image);
            Participant attendee = new Participant(cursor);
            if (attendee != null) {
                attendeeNameText.setText(attendee.getName());
                attendeePositionText.setText(attendee.getPosition());
                if (attendee.getFacebookUrl() != null && MiscUtils.isValidId(attendee.getFacebookUrl())) {
                    String profileUrl = String.format(Config.FACEBOOK_PROFILE_URL, attendee.getFacebookUrl());
                    ImageLoader.getInstance().displayImage(profileUrl, attendeeImageView);
                    //                } else if (MiscUtils.isValidId(attendee.getTwiterAcc())) {
                    //                    String profileUrl = String.format(Config.TWITTER_PROFILE_URL, attendee.getTwiterAcc());
                    //                    ImageLoader.getInstance().displayImage(profileUrl, attendeeImageView);
                } else {
                    attendeeImageView.setImageResource(R.drawable.dummy_profpic);
                }
                convertView.setTag(attendee);
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup group) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.attendee_item_layout, null);
            return convertView;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new ParticipantCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        isLoading = false;
        if (mAdapter == null) {
            mAdapter = new AttendeeAdapter(getActivity(), cursor, 0);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(cursor);
            mAdapter.notifyDataSetChanged();
        }
        if (isFirstTime) {
            mListView.setRefreshing(true);
            doGetAttendee();
            isFirstTime = false;
            if (cursor.getCount() > 0) {
                mSearchEditText.setVisibility(View.VISIBLE);
            }
        } else {
            mSearchEditText.setVisibility(View.VISIBLE);
        }
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {
                return SqlCacheHelper.getInstance(getActivity()).searchAttendeeList(constraint);
            }
        });
        if (!MiscUtils.isEmpty(mCurrentConstraint)) {
            mAdapter.getFilter().filter(mCurrentConstraint);
        }

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Participant attendee = (Participant) view.getTag();
        if (attendee != null) {
            Intent showInfo = new Intent(getActivity(), ParticipantInfoActivity.class);
            showInfo.putExtra(ParticipantInfoActivity.EXTRA_PARTICIPANT_INFO, attendee);
            showInfo.putExtra(ParticipantInfoActivity.EXTRA_PARTICIPANT_INDEX, position);
            startActivity(showInfo);
        }
    }

    private static class ParticipantAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<RegisterFragment> mFragment;

        public ParticipantAsyncTask(RegisterFragment fragment) {
            mFragment = new WeakReference<RegisterFragment>(fragment);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();
            boolean fetchResult = false;
            try {
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                GenericUrl sUrl = new GenericUrl(Config.PARTICIPANTS_CSV_URL);
                HttpRequest httpRequest;
                HttpResponse httpResponse;
                httpRequest = httpTransport.createRequestFactory().buildGetRequest(
                        sUrl);
                httpRequest.setConnectTimeout(HttpUtils.HTTP_TIMEOUT); // 3 minutes connect timeout
                httpRequest.setReadTimeout(HttpUtils.HTTP_READ_TIMEOUT);

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpUtils.GDATA_VERSION_HTTP_HEADER, "3.0");
                httpRequest.setHeaders(headers);

                httpResponse = httpRequest.execute();
                List<Participant> attendees = new ArrayList<Participant>();
                if (httpResponse.getStatusCode() == 200) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(httpResponse.getContent()));
                    String[] nextLine = csvReader.readNext();
                    nextLine = csvReader.readNext();
                    while (nextLine != null) {
                        Participant participant = new Participant();
                        participant.setName(nextLine[Config.NAME_COL - 1]);
                        participant.setCompany(nextLine[Config.COMPANY_COL - 1]);
                        participant.setPosition(nextLine[Config.POSITION_COL - 1]);
                        participant.setTopicWannaHear(nextLine[Config.TOPIC_WANNA_HEAR_COL - 1]);
                        participant.setTopicWannaPresent(nextLine[Config.TOPIC_WANNA_PRESENT_COL - 1]);
                        participant.setFacebookUrl(MiscUtils
                                .getSocialId(nextLine[Config.FACEBOOK_COL - 1]));
                        participant.setTwiterAcc(MiscUtils
                                .getSocialId(nextLine[Config.TWITTER_COL - 1]));
                        attendees.add(participant);
                        nextLine = csvReader.readNext();

                    }
                    csvReader.close();
                }
                httpResponse.ignore();

                if (attendees.isEmpty()) {
                    fetchResult = false;
                } else {
                    fetchResult = true;
                    if (mFragment.get() != null) {
                        RegisterFragment fragment = mFragment.get();
                        SqlCacheHelper sqlHelper = SqlCacheHelper.getInstance(fragment.getActivity());
                        sqlHelper.updateAttendeeList(attendees);
                        long endTime = System.currentTimeMillis();
                        Log.d("D.Vu", "PARTICIPANT TIME " + (endTime - startTime));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return fetchResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            RegisterFragment fragment = mFragment.get();
            if (fragment != null) {
                if (result) {
                    fragment.generateAttendee();
                } else {
                    fragment.onHandleFetchFailed();
                }
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s != null && mAdapter != null && mAdapter.getFilter() != null) {
            if (mAdapter != null) {
                mAdapter.getFilter().filter(s.toString());
            }
        }
        mCurrentConstraint = s.toString();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (mAdapter != null) {
                String text = v.getText().toString();
                if (!MiscUtils.isEmpty(text)) {
                    mAdapter.getFilter().filter(text);
                }
            }
        }
        return false;
    }
}
