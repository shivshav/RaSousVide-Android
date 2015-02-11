package com.spazz.shiv.rasousvide;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.spazz.shiv.rasousvide.database.Entree;
import com.spazz.shiv.rasousvide.database.Meal;
import com.spazz.shiv.rasousvide.tabs.SousVideFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.toolbar_bottom)
    Toolbar bottomToolbar;
//    @InjectView(R.id.bottom_layout)
//    RelativeLayout bottomLayout;

    //TODO: This should only show when status changes from 'Off' to something else
    @InjectView(R.id.stop_button)
    ImageButton stopButton;

    @InjectView(R.id.send_button)
    ImageButton sendButton;

    private TabsPagerAdapter mAdapter;

    private Boolean firstTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        //action bar setup
        setSupportActionBar(toolbar);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        tabs.setViewPager(pager);

//        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
//                .getDisplayMetrics());
//        pager.setPageMargin(pageMargin);
        setupBottomToolbar();
        setupStopAnimation();

        if(isFirstTime()) {
            //Execute database setup here
            Entree.firstTimeMealSetup();
        }
    }

    /**
     * Checks if the user is opening the app for the first time.
     * Note that this method should be placed inside an activity and it can be called multiple times.
     * @return boolean
     */
    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }
    private void setupStopAnimation(){
        final Animation animation = new AlphaAnimation(1.0f, 0.25f); // Change alpha from fully visible to invisible
        animation.setDuration(1500); // duration - a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        stopButton.startAnimation(animation);


//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                view.clearAnimation();
//            }
//        });
    }

    @OnClick(R.id.stop_button)
    public void stopClicked(View view) {
        view.clearAnimation();
    }
    private void setupBottomToolbar() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    // Or read size directly from the view's width/height
                    int size = getResources().getDimensionPixelSize(R.dimen.round_button_diameter);
                    outline.setOval(0, 0, size, size);
                }
            };
            sendButton.setOutlineProvider(viewOutlineProvider);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabsPagerAdapter extends FragmentPagerAdapter {

        private final String[] TAB_TITLES = getResources().getStringArray(R.array.sliding_tab_names);

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    return SousVideFragment.newInstance(position + 1);
                case 1:
                    return SousVideFragment.newInstance(position + 1);
                case 2:
                    return SousVideFragment.newInstance(position + 1);
                default: return null;
            }
        }

        @Override
        public int getCount() { return TAB_TITLES.length;}

        @Override
        public CharSequence getPageTitle(int position) { return TAB_TITLES[position];}

    }
}
