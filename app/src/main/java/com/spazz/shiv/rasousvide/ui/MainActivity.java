package com.spazz.shiv.rasousvide.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.spazz.shiv.rasousvide.R;
import com.spazz.shiv.rasousvide.database.Entree;
import com.spazz.shiv.rasousvide.database.Meal;
import com.spazz.shiv.rasousvide.ui.prefs.SettingsActivity;
import com.spazz.shiv.rasousvide.ui.tabs.SousVideFragment;
import com.spazz.shiv.rasousvide.CookingNotificationService;
import com.spazz.shiv.rasousvide.rest.RestClient;
import com.spazz.shiv.rasousvide.rest.model.ShivVideResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "MainActivity";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabs;
    @Bind(R.id.pager) ViewPager pager;

    @Bind(R.id.toolbar_bottom) Toolbar bottomToolbar;

    //TODO: This should only show when status changes from 'Off' to something else
    @Bind(R.id.stop_button) ImageButton stopButton;

    @Bind(R.id.send_button) ImageButton sendButton;

    @Bind(R.id.current_mode) TextView currModeTV;

    @Bind(R.id.current_temp) TextView currTempTV;
    @Bind(R.id.current_temp_units) TextView currTempUnitsTV;
    private TabsPagerAdapter mAdapter;

    SharedPreferences prefs;

    private Boolean firstTime = null;

    private boolean serviceBound;
    private CookingNotificationService cookingNotificationService;

    private Subscription querySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forceNewEntries();
        Entree.firstTimeMealSetup();

        ButterKnife.bind(this);

        //action bar setup
        setSupportActionBar(toolbar);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        tabs.setViewPager(pager);

        setupBottomToolbar();
        setupStopAnimation();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        if(isFirstTime()) {
//            //Execute database setup here
//            Entree.firstTimeMealSetup();
//        }
    }

//    private Subscription setupObservable() {
//        //TODO: Have a long and short timer interval that is for 'off' and 'on' modes
//        //TODO: Make above user configurable
//
//        return Observable.interval(5, TimeUnit.SECONDS)
//                .flatMap((num) -> RestClient.getAPI().getCurrentPiParams())
//                .distinct()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        (response) -> {
//                            currTempTV.setText(response.getTemp());
//                            currModeTV.setText(response.getMode());
//
//                            Log.d("OBSERVABLE UI", "Set temp to " + response.getTemp());
//                        },
//
//                        (e) -> Log.e("OBSERVABLE HERRE", e.getMessage() + "\nCurrent thread: " + Thread.currentThread()),
//                        () -> Log.d("OBSERVABLE", "onComplete Called")
//                );
//
//    }

    @Override
    public void onStart() {
        super.onStart();

        Intent serviceIntent = new Intent(MainActivity.this, CookingNotificationService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart Service created from activity");
        startService(serviceIntent);
        Log.d(TAG, "onStart Service start from activity");
    }

    @Override
    public void onResume() {

        super.onResume();
        if(mAdapter.getAdvancedVIew() != prefs.getBoolean(SettingsActivity.KEY_PREF_ADV_VIEW, false)) {
            mAdapter.updateTabTitles(prefs, SettingsActivity.KEY_PREF_ADV_VIEW);
        }
        Log.d(TAG, "onResume of Activity run");

        if(cookingNotificationService != null && (querySubscription == null || querySubscription.isUnsubscribed())) {
            querySubscription = createPollingSubscription();
        }
    }

    private Subscription createPollingSubscription() {
        return cookingNotificationService.getPollingObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (response) -> {
                            currTempTV.setText(response.getTemp());
                            currModeTV.setText(response.getMode());

                            Log.d("OBSERVABLE UI", "Set temp to " + response.getTemp());
                        },

                        (e) -> Log.e("OBSERVABLE HERE", e.getMessage() + "\nCurrent thread: " + Thread.currentThread()),
                        () -> Log.d("OBSERVABLE", "onComplete Called")
                );
    }

    @Override
    protected void onPause() {
        if(!querySubscription.isUnsubscribed()) {
            querySubscription.unsubscribe();
        }
        super.onPause();
        //prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(serviceBound) {
            Intent stopServiceIntent = new Intent(MainActivity.this, CookingNotificationService.class);
            stopService(stopServiceIntent);
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    private void forceNewEntries() {
        List<Meal> mealList = Meal.listAll(Meal.class);
        if(mealList != null) {
            Meal.deleteAll(Meal.class);
        }

        List<Entree> entreeList = Entree.listAll(Entree.class);
        if(entreeList != null) {
            Entree.deleteAll(Entree.class);
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
                editor.apply();
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

    @OnClick(R.id.send_button)
    public void sendButtonClick(View v) {
//        Intent cooking = new Intent(this, CookingNotificationService.class);
//       if(!serviceRunning) {
//           startService(cooking);
//           serviceRunning = true;
//       } else {
//           stopService(cooking);
//           serviceRunning = false;
//       }
//        RestClient.getAPI().getCurrentPiParams(new Callback<ShivVideResponse>() {
//            @Override
//            public void success(ShivVideResponse shivVideResponse, Response response) {
//                Log.e("SEND CALLBACK", "Status: " + response.getStatus());
//                Log.e("SEND CALLBACK", "Temp: " + shivVideResponse.getTemp());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.e("SEND CALLBACK", "FAILURE TO SEND:" + error.getBody());
//            }
//        });

    }
    @OnClick(R.id.stop_button) @SuppressWarnings("unused")
    public void stopClicked(View view) {
        view.clearAnimation();
    }
    private void setupBottomToolbar() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                @TargetApi(21)
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sf, String key) {
        Log.e("change", "pref changed");
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CookingNotificationService.LocalBinder binder = (CookingNotificationService.LocalBinder) iBinder;
            cookingNotificationService = binder.getService();
            serviceBound = true;

            querySubscription = createPollingSubscription();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(querySubscription != null && !querySubscription.isUnsubscribed()) {
                querySubscription.unsubscribe();
            }
            serviceBound = false;
        }
    };


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabsPagerAdapter extends FragmentPagerAdapter {

        private String[] tabTitles;
        private boolean advancedView = false;
        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
            updateTabTitles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), SettingsActivity.KEY_PREF_ADV_VIEW);
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
                    if(advancedView) {return SousVideFragment.newInstance(position + 1);}
                    else {return null;}
                default: return null;
            }
        }

        @Override
        public int getCount() { return tabTitles.length;}

        @Override
        public CharSequence getPageTitle(int position) { return tabTitles[position];}

        public void updateTabTitles(SharedPreferences sharedPref, String key) {
            String[] basTitles = getResources().getStringArray(R.array.sliding_tab_basic_names);
            String[] advTitles = null;
            advancedView = sharedPref.getBoolean(key, false);
            if(advancedView) {
                advTitles = getResources().getStringArray(R.array.sliding_tab_advanced_names);
            }
            tabTitles = new String[basTitles.length + (advTitles == null ? 0:advTitles.length)];
            System.arraycopy(basTitles, 0, tabTitles, 0, basTitles.length);
            if (advTitles != null) {
                System.arraycopy(advTitles, 0, tabTitles, basTitles.length, advTitles.length);
            }
            notifyDataSetChanged();
        }

        public boolean getAdvancedVIew() {
            return this.advancedView;
        }
    }
}
