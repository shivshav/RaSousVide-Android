package com.spazz.shiv.rasousvide;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.spazz.shiv.rasousvide.model.CookingNotificationService;
import com.spazz.shiv.rasousvide.model.RestClient;
import com.spazz.shiv.rasousvide.model.ShivVideResponse;
import com.spazz.shiv.rasousvide.tabs.SousVideFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MAIN ACTIVITY";
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.toolbar_bottom) Toolbar bottomToolbar;
    @InjectView(R.id.current_mode) TextView currModeTV;
    @InjectView(R.id.current_temp) TextView currTempTV;
    @InjectView(R.id.current_temp_units) TextView currTempUnitsTV;


    //TODO: This should only show when status changes from 'Off' to something else
    @InjectView(R.id.stop_button) ImageButton stopButton;
    @InjectView(R.id.send_button) ImageButton sendButton;

    private TabsPagerAdapter mAdapter;

    // Handler and Thread to query pi at interval
    private Boolean inApp = false;
    private Boolean mQuerying = false;
    final int INTERVAL = 10000;
    final Handler mHandler = new Handler();
    final Runnable mPiQueryAtInterval = new Runnable(){
        public void run() {
            if (inApp && !mQuerying) {
                querySousVide();
            }
            mHandler.postDelayed(this, INTERVAL);
        }

    };

    //TODO remove this debugging var
    private Boolean serviceRunning = false;

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
        // start interval querying of Pi
        mPiQueryAtInterval.run();
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mPiQueryAtInterval);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mHandler.postDelayed(mPiQueryAtInterval, INTERVAL);
        super.onResume();
    }

    private void querySousVide() {
        mQuerying = true;

        RestClient.getAPI().getCurrentPiParams(new Callback<ShivVideResponse>() {
            @Override
            public void success(ShivVideResponse svResponse, Response response) {
                mQuerying = false;
                // do cool shit
                currModeTV.setText(svResponse.getMode());
                currTempTV.setText(svResponse.getTemp());
            }

            @Override
            public void failure(RetrofitError error) {
                mQuerying = false;
                // do sad stuff
                // set defaults for when there is no internet
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    Log.i(TAG, "No Internet Connection: " + error);
                } else {
                    // something went wrong
                    Log.i(TAG, "Check dis error: " + error);
                }
                currModeTV.setText("Disconnected");
                currTempTV.setText("Room Temp");
                currTempUnitsTV.setText("");

            }
        });
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

    @OnClick(R.id.send_button) @SuppressWarnings("unused")
    public void sendButtonClick(View v) {
        Intent cooking = new Intent(this, CookingNotificationService.class);
       if(!serviceRunning) {
           startService(cooking);
           serviceRunning = true;
       } else {
           stopService(cooking);
           serviceRunning = false;
       }
    }
    @OnClick(R.id.stop_button) @SuppressWarnings("unused")
    public void stopClicked(View view) {
        view.clearAnimation();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupBottomToolbar() {
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
