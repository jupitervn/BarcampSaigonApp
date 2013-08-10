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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.barcampsaigon.android.R;
import org.barcampsaigon.android.model.Tweet;
import org.barcampsaigon.android.model.TweetPage;
import org.barcampsaigon.android.model.TweetUser;
import org.barcampsaigon.android.service.HttpUtils;
import org.barcampsaigon.android.util.Config;
import org.barcampsaigon.android.util.MiscUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.http.HttpResponse;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class SocialFragment extends BaseFragment implements
        OnRefreshListener<ListView>,
        OnClickListener, OnItemClickListener {
    private final int REQUEST_TWITTER_SSO = 0;

    private View mRootView;
    private PullToRefreshListView mListView;
    private View mRefreshView;
    private ImageView mTweetButton;
    private boolean isLoading = false;
    private boolean isFirstTime = true;
    private TwitterFetchTask mTask;
    private TweetsAdapter mAdapter;
    private Handler mDelayedHandler;
    /**
     * Extra for tweet page data.
     */
    public static final String EXTRA_TWEET_PAGE = "extra-tweet-page";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_social_content, container, false);
        mListView = (PullToRefreshListView) mRootView.findViewById(R.id.tweets_list);
        mAdapter = new TweetsAdapter(getActivity(), R.id.tweet_content, new ArrayList<Tweet>());
        mListView.setAdapter(mAdapter);
        mTweetButton = (ImageView) mRootView.findViewById(R.id.btn_tweet);
        mRefreshView = mRootView.findViewById(R.id.refresh_panel);
        mTweetButton.setOnClickListener(this);
        mListView.setOnRefreshListener(this);
        mDelayedHandler = new Handler();
        return mRootView;
    }

    @Override
    protected void showLoadingDialog(boolean isCancelable) {
        if (mRefreshView != null) {
            mRefreshView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void hideLoadingDialog() {
        if (mRefreshView != null) {
            mRefreshView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingDialog(false);
        doHttpGetTweets();
    }

    private void doHttpGetTweets() {
        if (!isLoading) {
            if (mTask != null) {
                mTask.cancel(true);
            }
            mTask = new TwitterFetchTask(this);
            mTask.execute();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void onGetTwitterSuccess(TweetPage tweetPage) {
        hideLoadingDialog();
        if (isAdded() && getActivity() != null) {
            mListView.onRefreshComplete();
            isLoading = false;
            mAdapter = new TweetsAdapter(getActivity(), R.id.tweet_content, tweetPage.getTweets());
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
        }

    }

    private void onGetTwitterFailed() {
        hideLoadingDialog();
        mListView.onRefreshComplete();
        isLoading = false;
    }

    @Override
    public void updateWithObject(Context context, Object object, String tag) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TWITTER_SSO) {
            mDelayedHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doHttpGetTweets();
                }
            }, 1000);
        }
    }

    private static class TweetsAdapter extends ArrayAdapter<Tweet> {

        public TweetsAdapter(Context context, int textViewResourceId, List<Tweet> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet_row_item, null);
            }
            ImageView avatarView = (ImageView) convertView.findViewById(R.id.tweet_avatar);
            TextView tweetView = (TextView) convertView.findViewById(R.id.tweet_content);
            TextView tweetTime = (TextView) convertView.findViewById(R.id.tweet_time);
            TextView tweetAuthor = (TextView) convertView.findViewById(R.id.tweet_author);
            Tweet tweet = getItem(position);
            String imageUrl = null;
            if (tweet != null) {
                tweetView.setText(tweet.getText());
                tweetTime.setText(MiscUtils.convertTweetTime(getContext(), tweet.getCreatedDate()));
                TweetUser user = tweet.getUser();
                if (user != null) {
                    tweetAuthor.setText(MiscUtils.convertTweetAuthor(getContext(), user.getName(),
                            user.getScreenName()));
                    imageUrl = user.getProfileImageUrl();
                }
            }
            ImageLoader.getInstance().displayImage(imageUrl, avatarView);
            convertView.setTag(tweet);
            return convertView;
        }
    }

    private static class TwitterFetchTask extends AsyncTask<Void, Void, TweetPage> {
        private WeakReference<SocialFragment> mFragment;

        public TwitterFetchTask(SocialFragment fragment) {
            mFragment = new WeakReference<SocialFragment>(fragment);
        }

        @Override
        protected TweetPage doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();
            Map<String, String> args = new HashMap<String, String>();
            args.put("q", Config.BARCAMP_TWITTER_TAG);
            args.put("count", "100");
            try {
                HttpResponse httpResponse = HttpUtils.getTwitterPage(Config.TWITTER_URL, args);

                if (httpResponse != null && httpResponse.getStatusCode() == 200) {
                    TweetPage tweetPage = httpResponse.parseAs(TweetPage.class);
                    if (tweetPage != null && tweetPage.getTweets() != null) {
                        long endTime = System.currentTimeMillis();
                        return tweetPage;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(TweetPage result) {
            if (mFragment != null && mFragment.get() != null) {
                SocialFragment fragment = mFragment.get();
                fragment.isFirstTime = false;
                if (result != null) {
                    fragment.onGetTwitterSuccess(result);
                } else {
                    fragment.onGetTwitterFailed();
                }
            }
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isRemoving()) {
            doHttpGetTweets();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_tweet) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/intent/tweet?text=%23barcampsaigon%20"));
            startActivityForResult(browserIntent, REQUEST_TWITTER_SSO);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Tweet tweet = (Tweet) view.getTag();
        if (tweet != null) {
            Intent browserIntent;
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/retweet?tweet_id="
                    + tweet.getId()));
            startActivityForResult(browserIntent, REQUEST_TWITTER_SSO);
        }
    }

}
