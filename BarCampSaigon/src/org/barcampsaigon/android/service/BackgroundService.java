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
package org.barcampsaigon.android.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.barcampsaigon.android.cache.SqlCacheHelper;
import org.barcampsaigon.android.model.Cell;
import org.barcampsaigon.android.model.Entry;
import org.barcampsaigon.android.model.Feed;
import org.barcampsaigon.android.model.Participant;
import org.barcampsaigon.android.model.TalkSection;
import org.barcampsaigon.android.util.Config;
import org.barcampsaigon.android.util.MiscUtils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class BackgroundService extends IntentService {
    /**
     * Action to fetch agenda.
     */
    public static final String ACTION_FETCH_AGENDA = "org.barcampsaigon.FETCH_AGENDA";
    /**
     * Action to indicate agenda fetch successfully.
     */
    public static final String ACTION_FETCH_AGENDA_SUCCESS = "org.barcampsaigon.FETCH_AGENDA_SUCCESS";
    /**
     * Action to indicate twitter fetch successfully.
     */
    public static final String ACTION_FETCH_TWITTER_SUCCESS = "org.barcampsaigon.FETCH_TWITTER_SUCCESS";
    /**
     * Extra contains list of hour sections.
     */
    public static final String EXTRA_LIST_OF_HOUR_SECTIONS = "hour-sections-extra";
    /**
     * Action to indicate fetch error.
     */
    public static final String ACTION_FETCH_ERROR = "org.barcampsaigon.FETCH_ERROR";

    private static final String SERVICE_NAME = "org.barcamsaigon.BackgroundService";

    private final LocalBroadcastManager mLocalMgr = LocalBroadcastManager.getInstance(this);
    private AlarmManager mAlarmManager;

    /**
     * Default constructor.
     */
    public BackgroundService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction() != null) {
            String action = intent.getAction();
            if (ACTION_FETCH_AGENDA.equals(action)) {
                handleFetchAgendaAction();
            }
        }
    }

    private List<Participant> parseParticipants(Feed result) {
        List<Entry> entries = result.getEntries();
        List<Participant> attendees = new ArrayList<Participant>();
        int lastRow = 1;
        Participant currentParticipant = null;
        for (Entry entry : entries) {
            if (entry != null && entry.getCell() != null) {
                Cell cell = entry.getCell();
                if (cell.getRow() > 1) {
                    if (cell.getRow() > lastRow) {
                        if (currentParticipant != null) {
                            attendees.add(currentParticipant);
                        }
                        currentParticipant = new Participant();
                        lastRow = cell.getRow();
                    }
                    if (currentParticipant != null) {
                        if (cell.getCol() == Config.NAME_COL) {
                            currentParticipant.setName(entry.getContent() == null ? "" : entry.getContent()
                                    .getValue());
                        } else if (cell.getCol() == Config.FACEBOOK_COL) {
                            currentParticipant.setFacebookUrl(entry.getContent() == null ? "" : MiscUtils
                                    .getSocialId(entry
                                            .getContent()
                                            .getValue()));
                        } else if (cell.getCol() == Config.TWITTER_COL) {
                            currentParticipant.setTwiterAcc(entry.getContent() == null ? "" : MiscUtils
                                    .getSocialId(entry
                                            .getContent()
                                            .getValue()));
                        } else if (cell.getCol() == Config.POSITION_COL) {
                            currentParticipant.setPosition(entry.getContent() == null ? "" : entry.getContent()
                                    .getValue());
                        } else if (cell.getCol() == Config.COMPANY_COL) {
                            currentParticipant.setCompany(entry.getContent() == null ? "" : entry.getContent()
                                    .getValue());
                        } else if (cell.getCol() == Config.TOPIC_WANNA_HEAR_COL) {
                            currentParticipant.setTopicWannaPresent(entry.getContent() == null ? "" : entry
                                    .getContent()
                                    .getValue());
                        } else if (cell.getCol() == Config.TOPIC_WANNA_PRESENT_COL) {
                            currentParticipant.setTopicWannaHear(entry.getContent() == null ? "" : entry.getContent()
                                    .getValue());
                        }

                    }
                }
            }
        }
        return attendees;
    }

    private List<TalkSection> parseAgenda(Feed result, TreeSet<String> hourSections) {
        List<TalkSection> sections = new ArrayList<TalkSection>();
        List<String> rooms = new ArrayList<String>();
        List<Entry> entries = result.getEntries();
        String currentHourSection = null;
        int lastCol = 1;
        int lastRow = 1;
        for (Entry entry : entries) {
            if (entry != null && entry.getCell() != null) {
                Cell cell = entry.getCell();
                if (cell.getRow() == 1) {
                    if (entry.getContent() != null && !MiscUtils.isEmpty(entry.getContent().getValue())) {
                        rooms.add(entry.getContent().getValue());
                    }
                } else {
                    if (lastRow != cell.getRow()) {
                        if (lastCol < rooms.size() + 1 && lastCol > 1) {
                            for (int i = lastCol + 1; i < (rooms.size() + 2); i++) {
                                TalkSection talkSection = new TalkSection(
                                        "",
                                        currentHourSection, rooms.get(i - 2));
                                sections.add(talkSection);
                            }
                        }
                        lastCol = 1;
                        lastRow = cell.getRow();
                    }

                    if (cell.getCol() == 1 && entry.getContent() != null) {
                        currentHourSection = entry.getContent().getValue();
                        hourSections.add(currentHourSection);
                    } else {
                        if (currentHourSection != null && cell.getCol() < rooms.size() + 2) {
                            if (cell.getCol() > lastCol + 1) {
                                for (int i = lastCol + 1; i < cell.getCol(); i++) {
                                    TalkSection talkSection = new TalkSection(
                                            "",
                                            currentHourSection, rooms.get(i - 2));
                                    sections.add(talkSection);
                                }
                            }
                            String sectionStr = entry.getContent() == null ? "" : entry.getContent().getValue();
                            String sectionName = sectionStr.replaceAll("\\((.)*\\)", "");
                            String sectionAuthor = "";
                            int firstIndex = sectionStr.indexOf('(');
                            if (firstIndex != -1) {
                                int lastIndex = sectionStr.indexOf(')', firstIndex + 1);
                                if (lastIndex != -1) {
                                    sectionAuthor = sectionStr.substring(firstIndex + 1, lastIndex);
                                }
                            }
                            TalkSection talkSection = new TalkSection(sectionName, currentHourSection, rooms.get(cell
                                    .getCol() - 2));
                            talkSection.setAuthorName(sectionAuthor);
                            sections.add(talkSection);
                            lastCol = cell.getCol();
                        }
                    }
                }
            }
        }
        return sections;
    }

    private void handleFetchAgendaAction() {
        GoogleDocService googleService = new GoogleDocServiceImpl();
        try {
            Feed result = googleService.getGoogleDocsByList(Config.AGENDA_URL);

            List<TalkSection> sections = new ArrayList<TalkSection>();
            TreeSet<String> hourSections = new TreeSet<String>();
            if (result != null) {
                long docsUpdatedTime = (MiscUtils.convertGoogleDate(result.getUpdatedTime())).getTime();
                long agendaUpdatedTime = Config.getLastAgendaUpdateTime(this);
                long currentAlarmTime = Config.getAlarmTime(this, Config.AGENDA_ALARM_TIME);
                if (docsUpdatedTime > agendaUpdatedTime) {
                    Config.setLastAgendaUpdateTime(this, docsUpdatedTime);
                    Config.setAlarmTime(this, Config.AGENDA_ALARM_TIME, Config.DEFAULT_ALARM_TIME);
                    currentAlarmTime = Config.DEFAULT_ALARM_TIME;
                } else {
                    Config.setAlarmTime(this, Config.AGENDA_ALARM_TIME, currentAlarmTime * 4);
                    currentAlarmTime = currentAlarmTime * 4;
                    if (currentAlarmTime > Config.MAX_ALARM_TIME) {
                        currentAlarmTime = -1;
                    }
                }
                if (currentAlarmTime != -1) {
                    Intent startFetchAgenda = new Intent(this, BackgroundService.class);
                    startFetchAgenda.setAction(BackgroundService.ACTION_FETCH_AGENDA);
                    PendingIntent fetchAgenda = PendingIntent.getService(this, 0, startFetchAgenda,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, currentAlarmTime, fetchAgenda);
                }
                sections = parseAgenda(result, hourSections);
            }
            if (!sections.isEmpty()) {
                SqlCacheHelper sqlHelper = SqlCacheHelper.getInstance(this);
                sqlHelper.updateAllDatabase(sections);
                Config.setHourData(this, hourSections);
                Intent fetchAgendaSuccess = new Intent(ACTION_FETCH_AGENDA_SUCCESS);
                fetchAgendaSuccess.putExtra(EXTRA_LIST_OF_HOUR_SECTIONS, hourSections);
                mLocalMgr.sendBroadcast(fetchAgendaSuccess);
            } else {
                broadcastFetchFail();
            }
        } catch (IOException e) {
            e.printStackTrace();
            broadcastFetchFail();
        }
    }

    private void broadcastFetchFail() {
        Intent failIntent = new Intent(ACTION_FETCH_ERROR);
        mLocalMgr.sendBroadcast(failIntent);
    }
}
