package org.barcampsaigon.android.screen;

import org.barcampsaigon.android.R;
import org.barcampsaigon.android.ui.CustomizedTabHost;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * Main screen activity.
 * 
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class MainActivity extends RoboFragmentActivity {

    private @InjectView(android.R.id.tabhost)
    CustomizedTabHost mTabHost;
    private @InjectView(android.R.id.tabs)
    TabWidget mTabWidget;

    private static final String TAB_ABOUT = "about";
    private static final String TAB_REGISTER = "register";
    private static final String TAB_AGENDA = "agenda";
    private static final String TAB_SOCIAL = "social";

    private static final String IS_FIRST_TIME_KEY = "is_first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabs(savedInstanceState == null);
        PushService.setDefaultPushCallback(this, this.getClass());
        PushService.subscribe(this, "", this.getClass());
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void initTabs(boolean firstTime) {
        mTabHost.setup(this, getSupportFragmentManager(),
                R.id.real_tab_content_id);
        TabSpec tabAbout = mTabHost.newTabSpec(TAB_ABOUT).setIndicator(
                getTabIndicator(mTabWidget, R.drawable.ic_info_selector));
        TabSpec tabAgenda = mTabHost.newTabSpec(TAB_AGENDA).setIndicator(
                getTabIndicator(mTabWidget, R.drawable.ic_agenda_selector));
        TabSpec tabRegister = mTabHost.newTabSpec(TAB_REGISTER).setIndicator(
                getTabIndicator(mTabWidget, R.drawable.ic_participant_selector));
        TabSpec tabSocial = mTabHost.newTabSpec(TAB_SOCIAL).setIndicator(
                getTabIndicator(mTabWidget, R.drawable.ic_twitter_selector));
        addTab(tabAbout, AboutFragment.class, null);
        addTab(tabAgenda, NewAgendaFragment.class, null);
        addTab(tabRegister, RegisterFragment.class, null);
        addTab(tabSocial, SocialFragment.class, null);
        if (!firstTime) {
            showTab(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FIRST_TIME_KEY, false);
    }

    protected View getTabIndicator(TabWidget tabWidget, int iconId) {
        View tabIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, tabWidget, false);
        ImageView tabIconView = (ImageView) tabIndicator.findViewById(R.id.tab_icon_view);
        tabIconView.setImageResource(iconId);
        return tabIndicator;
    }

    private void addTab(TabSpec tabSpec,
            Class<?> fragmentClass,
            Bundle args) {
        mTabHost.addTab(tabSpec, fragmentClass, args);
    }

    /**
     * Show the tab widget.
     * 
     * @param animation
     */
    public void showTab(boolean animation) {
        if (mTabWidget != null) {
            if (animation) {
                Animation showTabAnimation = AnimationUtils.loadAnimation(this, R.anim.tab_animation);
                mTabWidget.startAnimation(showTabAnimation);
            }
            mTabWidget.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
