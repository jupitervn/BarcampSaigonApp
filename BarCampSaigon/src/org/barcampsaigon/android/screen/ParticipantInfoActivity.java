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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.barcampsaigon.android.R;
import org.barcampsaigon.android.model.Participant;
import org.barcampsaigon.android.util.Config;
import org.barcampsaigon.android.util.MiscUtils;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class ParticipantInfoActivity extends RoboActivity implements OnClickListener {
    @InjectView(R.id.profile_image_view)
    private ImageView mProfileImageView;
    @InjectView(R.id.profile_name_view)
    private TextView mProfileNameView;
    @InjectView(R.id.profile_position_view)
    private TextView mProfilePositionView;
    @InjectView(R.id.profile_company_view)
    private TextView mProfileCompanyView;
    @InjectView(R.id.profile_hear_topic)
    private TextView mProfileTopicWannaHear;
    @InjectView(R.id.profile_present_topic)
    private TextView mProfileTopicWannaPresent;
    @InjectView(R.id.tv_header)
    private TextView mHeader;
    @InjectView(R.id.profile_present_label)
    private TextView mTopicPresentLabel;
    @InjectView(R.id.profile_hear_label)
    private TextView mTopicHearLabel;
    @InjectView(R.id.btn_tweet)
    private ImageView mTweetButton;
    /**
     * Extra participant info.
     */
    public static final String EXTRA_PARTICIPANT_INFO = "extra-participant-info";
    public static final String EXTRA_PARTICIPANT_INDEX = "extra-participant-index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_info_layout);
        Intent intent = getIntent();
        if (intent != null) {
            Participant participant = (Participant) intent.getSerializableExtra(EXTRA_PARTICIPANT_INFO);
            int participantIndex = intent.getIntExtra(EXTRA_PARTICIPANT_INDEX, 1);
            if (participant != null) {
                mProfileNameView.setText(participant.getName());
                mHeader.setText("Barcamper #" + participantIndex);
                if (!MiscUtils.isEmpty(participant.getCompany())) {
                    mProfileCompanyView.setText(participant.getCompany());
                    mProfileCompanyView.setVisibility(View.VISIBLE);
                } else {
                    mProfileCompanyView.setVisibility(View.GONE);
                }
                if (!MiscUtils.isEmpty(participant.getPosition())) {
                    mProfilePositionView.setText(participant.getPosition());
                    mProfilePositionView.setVisibility(View.VISIBLE);
                } else {
                    mProfilePositionView.setVisibility(View.GONE);
                }

                if (!MiscUtils.isEmpty(participant.getTopicWannaHear())) {
                    mProfileTopicWannaHear.setText(participant.getTopicWannaHear());
                    mProfileTopicWannaHear.setVisibility(View.VISIBLE);
                    mTopicHearLabel.setVisibility(View.VISIBLE);
                } else {
                    mProfileTopicWannaHear.setVisibility(View.GONE);
                    mTopicHearLabel.setVisibility(View.GONE);
                }

                if (!MiscUtils.isEmpty(participant.getTopicWannaPresent())) {
                    mProfileTopicWannaPresent.setText(participant.getTopicWannaPresent());
                    mProfileTopicWannaPresent.setVisibility(View.VISIBLE);
                    mTopicPresentLabel.setVisibility(View.VISIBLE);
                } else {
                    mProfileTopicWannaPresent.setVisibility(View.GONE);
                    mTopicPresentLabel.setVisibility(View.GONE);
                }
                if (participant.getFacebookUrl() != null && MiscUtils.isValidId(participant.getFacebookUrl())) {
                    String profileUrl = String.format(Config.FACEBOOK_PROFILE_URL, participant.getFacebookUrl());
                    ImageLoader.getInstance().displayImage(profileUrl, mProfileImageView);
                } else {
                    mProfileImageView.setImageResource(R.drawable.dummy_profpic);
                }
                if (MiscUtils.isValidId(participant.getTwiterAcc())) {
                    mTweetButton.setVisibility(View.VISIBLE);
                    mTweetButton.setOnClickListener(this);
                    mTweetButton.setTag(participant.getTwiterAcc());
                } else {
                    mTweetButton.setVisibility(View.GONE);
                }

            }
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        String participantTag = (String) v.getTag();
        if (participantTag != null) {
            Intent browserIntent;
            try {
                browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/intent/tweet?text="
                                + URLEncoder.encode("@" + participantTag + " ", "utf-8")));
                startActivity(browserIntent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
