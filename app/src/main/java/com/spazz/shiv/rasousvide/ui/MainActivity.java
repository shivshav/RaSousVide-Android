package com.spazz.shiv.rasousvide.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.spazz.shiv.rasousvide.R;
import com.spazz.shiv.rasousvide.database.Entree;
import com.spazz.shiv.rasousvide.database.Meal;
import com.spazz.shiv.rasousvide.ui.prefs.SettingsActivity;
import com.spazz.shiv.rasousvide.ui.tabs.SousVideFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "MainActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.pager)
    ViewPager pager;

    @Bind(R.id.toolbar_bottom)
    Toolbar bottomToolbar;

    @Bind(R.id.current_mode)
    TextView currentMode;
//    @InjectView(R.id.bottom_layout)
//    RelativeLayout bottomLayout;

    //TODO: This should only show when status changes from 'Off' to something else
    @Bind(R.id.stop_button)
    FloatingActionButton stopButton;

    @Bind(R.id.send_button)
    FloatingActionButton sendButton;

    @Bind(R.id.menu_button)
    FloatingActionButton menuButton;

    @Bind(R.id.test_button)
    FloatingActionButton testButton;

    private TabsPagerAdapter mAdapter;

    SharedPreferences prefs;

    private Boolean firstTime = null;

    private boolean menuOpen;
    private float sendPos;
    private float stopPos;

    private Subscription modeTextChangeSubscription;

    private Observable<String> textModeObservable;

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
//        setupStopAnimation();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        

//        if(isFirstTime()) {
//            //Execute database setup here
//            Entree.firstTimeMealSetup();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter.getAdvancedVIew() != prefs.getBoolean(SettingsActivity.KEY_PREF_ADV_VIEW, false)) {
            mAdapter.updateTabTitles(prefs, SettingsActivity.KEY_PREF_ADV_VIEW);
        }


        modeTextChangeSubscription = createModeTextChangedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::altModeChanged);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //prefs.unregisterOnSharedPreferenceChangeListener(listener);

        if(modeTextChangeSubscription != null && !modeTextChangeSubscription.isUnsubscribed()) {
            modeTextChangeSubscription.unsubscribe();
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

    private void initMenu() {

        sendPos = menuButton.getBottom() - sendButton.getBottom();
        stopPos = menuButton.getBottom() - stopButton.getBottom();

        sendButton.setTranslationY(sendPos);
//        sendButton.setVisibility(View.GONE);

        menuButton.setAlpha(0.0f);

        stopButton.setTranslationY(stopPos);
        stopButton.setVisibility(View.GONE);

        menuOpen = false;

    }

    private void slideOut() {

    }

    private void slideIn() {

    }

    private AnimatorSet slide() {
        long animDuration = 350;

        ObjectAnimator sendAlpha = ObjectAnimator.ofFloat(sendButton, "alpha", 0);
        ObjectAnimator stopAlpha = ObjectAnimator.ofFloat(stopButton, "alpha", 0);
        float alphaStart;
        float alphaEnd;

        ObjectAnimator sendTranslate = ObjectAnimator.ofFloat(sendButton, "translationY", 0);
        ObjectAnimator stopTranslate = ObjectAnimator.ofFloat(stopButton, "translationY", 0);
        float sendTranslation;
        float stopTranslation;

        ObjectAnimator menuRotate = ObjectAnimator.ofFloat(menuButton, "rotation", 0);
        float rotateStart;
        float rotateEnd;

        if(menuOpen) {//do close animation
            alphaStart = 1;
            alphaEnd = 0;

            sendTranslation = sendPos;
            stopTranslation = stopPos;

            rotateStart = 45;
            rotateEnd = 0;

            //make buttons non existant for layout calculation & view purposes
            sendTranslate.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d("SendTranslateEnd", "Disappearing send button...");
                    super.onAnimationEnd(animation);
                    sendButton.setVisibility(View.GONE);
                    Log.d("SendTranslateEnd", "Send button disappeared");
                }
            });

            stopTranslate.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    stopButton.clearAnimation();
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    stopButton.setVisibility(View.GONE);
                }
            });



        }
        else {//do open animation

            alphaStart = 0;
            alphaEnd = 1;
            //Items will be set to their desired position
            sendTranslation = 0;
            stopTranslation = 0;

            rotateStart = 0;
            rotateEnd = 45;

            //make buttons visible
            sendTranslate.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    sendButton.setVisibility(View.VISIBLE);
                }
            });
            stopTranslate.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    stopButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setupStopAnimation();
                }
            });
        }
        //TODO:Make alpha animations decelerate/accelerate?
        sendAlpha.setFloatValues(alphaStart, alphaEnd);
        sendAlpha.setDuration(animDuration);

        stopAlpha.setFloatValues(alphaStart, alphaEnd);
        stopAlpha.setDuration(animDuration);

        //TODO: Make translations accelerate on close and decelerate on open?
        sendTranslate.setFloatValues(sendTranslation);
        sendTranslate.setDuration(animDuration);
        sendTranslate.setInterpolator(new DecelerateInterpolator());

        stopTranslate.setFloatValues(stopTranslation);
        stopTranslate.setDuration(animDuration);
        stopTranslate.setInterpolator(new DecelerateInterpolator());

        menuRotate.setFloatValues(rotateStart, rotateEnd);
        menuRotate.setDuration(animDuration);
        menuRotate.setInterpolator(new BounceInterpolator());

        AnimatorSet menuToggle = new AnimatorSet();

        menuToggle.play(menuRotate).with(sendTranslate).with(sendAlpha).with(stopTranslate).with(stopAlpha);

        Log.d(TAG, "MenuOpen?:" + menuOpen + ", StopPos:" + stopPos + ", SendPos:" + sendPos);

        menuOpen = !menuOpen;

        return menuToggle;
    }

    @OnClick(R.id.menu_button)
    public void menuClicked(View view) {
        slide().start();
    }

    @OnClick(R.id.stop_button)
    public void stopClicked(View view) {
        view.clearAnimation();
    }

    @OnClick(R.id.test_button)
    public void testClicked(View view) {
        if("Auto".matches(currentMode.getText().toString())) {
            currentMode.setText("Off");
        }
        else if("Off".matches(currentMode.getText().toString())) {
            currentMode.setText("Auto");
        }
    }
    private void setupBottomToolbar() {

        textModeObservable = createModeTextChangedObservable();
    }

    private Observable<String> createModeTextChangedObservable() {
        return WidgetObservable.text(currentMode)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .map(
                        onTextChangeEvent -> {
                            String mode = onTextChangeEvent.text().toString();
                            if (!mode.isEmpty() || (!mode.matches("null"))) {
                                return mode;
                            } else {
                                return "Off";
                            }
                        }
                );
    }

    private void altModeChanged(String mode) {
        long fullAnimDuration = 500;

        AnimatorSet menuToggle = null;

        ObjectAnimator menuFlip = ObjectAnimator.ofFloat(menuButton, "rotationY", 0);
        ObjectAnimator menuAppear = ObjectAnimator.ofFloat(menuButton, "alpha", 0);
        float menuAppearStart;
        float menuAppearEnd;
        float menuFlipStart;
        float menuFlipEnd;

        ObjectAnimator sendFlip = ObjectAnimator.ofFloat(sendButton, "rotationY", 0);
        final ObjectAnimator sendAppear = ObjectAnimator.ofFloat(sendButton, "alpha", 0);
        float sendAppearStart;
        float sendAppearEnd;
        float sendFlipStart;
        float sendFlipEnd;

        boolean on;

        if("Auto".matches(mode)) {//Sous Vide was turned on to sous vide mode
            //flip send button to expandMenu button
            Toast.makeText(this, "Current Mode:" + mode, Toast.LENGTH_SHORT).show();


            on = true;

//            menuButton.setAlpha(0);

            menuAppearStart = 0;
            menuAppearEnd = 1;

            menuFlipStart = -180;
            menuFlipEnd = 0;

//            menuAppear.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    menuButton.setVisibility(View.VISIBLE);
//                }
//            });

            sendAppearStart = 1;
            sendAppearEnd = 0;

            // At the end of the animation, make send button disappear
            sendAppear.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.d("SendAppearEnd", "Disappearing send button...");
                    sendButton.setVisibility(View.GONE);
                    sendButton.setRotationY(0);
                    Log.d("SendAppearEnd", "Send button disappeared");

                }
            });

            sendFlipStart = 0;
            sendFlipEnd = 180;

        }
        else if("Off".matches(mode)) {//Sous Vide was turned off
            //flip expandMenu to send
            Toast.makeText(this, "Current Mode:" + mode, Toast.LENGTH_SHORT).show();

            if(menuOpen) {
                menuToggle = slide();
                Log.d(TAG, "MADE IT HERE!!!");
            }

            on = false;
//            sendButton.setAlpha(0);
//            sendButton.setVisibility(View.VISIBLE);

            menuAppearStart = 1;
            menuAppearEnd = 0;

            menuFlipStart = 0;
            menuFlipEnd = -180;

//            menuAppear.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    sendButton.setVisibility(View.GONE);
//                }
//            });
            sendAppearStart = 0;
            sendAppearEnd = 1;

            sendFlipStart = 180;
            sendFlipEnd = 0;

            sendAppear.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    sendButton.setVisibility(View.VISIBLE);
                    sendButton.setRotationY(0);

                }
            });
        }
        else {
            return;
        }

        menuFlip.setFloatValues(menuFlipStart, menuFlipEnd);
        menuFlip.setDuration(fullAnimDuration);
        menuFlip.setInterpolator(new AccelerateDecelerateInterpolator());

        menuAppear.setFloatValues(menuAppearStart, menuAppearEnd);
        menuAppear.setDuration(1);
        menuAppear.setStartDelay(fullAnimDuration/2);


        sendFlip.setFloatValues(sendFlipStart, sendFlipEnd);
        sendFlip.setDuration(fullAnimDuration);
        sendFlip.setInterpolator(new AccelerateDecelerateInterpolator());

        sendAppear.setFloatValues(sendAppearStart, sendAppearEnd);
        sendAppear.setDuration(1);
        sendAppear.setStartDelay(fullAnimDuration/2);

        AnimatorSet modeChange = new AnimatorSet();

        if(on) {
            modeChange.play(menuFlip).with(menuAppear).with(sendFlip).with(sendAppear);
        }
        else {
            if(menuToggle != null) {
                AnimatorSet flipSet = new AnimatorSet();
                flipSet.play(sendFlip).with(sendAppear).with(menuFlip).with(menuAppear);
                modeChange.play(menuToggle).before(flipSet);
            }
            else {
                modeChange.play(sendFlip).with(sendAppear).with(menuFlip).with(menuAppear);
            }
        }
        modeChange.start();
        Log.d(TAG, "On:" + on + "FlipStart:" + menuFlipStart + ", FlipEnd:" + menuFlipEnd);

    }
    private void modeChanged(String mode) {
        long fullAnimDuration = 500;

        ObjectAnimator menuFlip = ObjectAnimator.ofFloat(menuButton, "rotationY", 0);
        ObjectAnimator menuAppear = ObjectAnimator.ofFloat(menuButton, "alpha", 0);
        float menuAppearStart;
        float menuAppearEnd;
        float menuFlipStart;
        float menuFlipEnd;

        ObjectAnimator sendFlip = ObjectAnimator.ofFloat(sendButton, "rotationY", 0);
        ObjectAnimator sendAppear = ObjectAnimator.ofFloat(sendButton, "alpha", 0);
        float sendAppearStart;
        float sendAppearEnd;
        float sendFlipStart;
        float sendFlipEnd;

        boolean on;

        if("Auto".matches(mode)) {//Sous Vide was turned on to sous vide mode
            //flip send button to expandMenu button
            Toast.makeText(this, "Current Mode:" + mode, Toast.LENGTH_SHORT).show();

            on = true;

            menuButton.setAlpha(0);

            menuAppearStart = 0;
            menuAppearEnd = 1;

            menuFlipStart = -180;
            menuFlipEnd = 0;

            sendAppearStart = 1;
            sendAppearEnd = 0;

            sendFlipStart = 0;
            sendFlipEnd = 180;

        }
        else if("Off".matches(mode)) {//Sous Vide was turned off
            //flip expandMenu to send
            Toast.makeText(this, "Current Mode:" + mode, Toast.LENGTH_SHORT).show();

            on = false;
            sendButton.setAlpha(0);
            sendButton.setVisibility(View.VISIBLE);

            menuAppearStart = 1;
            menuAppearEnd = 0;

            menuFlipStart = 0;
            menuFlipEnd = 180;

            sendAppearStart = 0;
            sendAppearEnd = 1;

            sendFlipStart = -180;
            sendFlipEnd = 0;
        } else {
            return;
        }

        menuFlip.setFloatValues(menuFlipStart, menuFlipEnd);
        menuFlip.setDuration(fullAnimDuration);
        menuFlip.setInterpolator(new AccelerateDecelerateInterpolator());

        menuAppear.setFloatValues(menuAppearStart, menuAppearEnd);
        menuAppear.setDuration(1);
        menuAppear.setStartDelay(fullAnimDuration/2);


        sendFlip.setFloatValues(sendFlipStart, sendFlipEnd);
        sendFlip.setDuration(fullAnimDuration);
        sendFlip.setInterpolator(new AccelerateDecelerateInterpolator());

        sendAppear.setFloatValues(sendAppearStart, sendAppearEnd);
        sendAppear.setDuration(1);
        sendAppear.setStartDelay(fullAnimDuration/2);

        AnimatorSet modeChange = new AnimatorSet();

        if(on) {
            modeChange.play(menuFlip).with(menuAppear).with(sendFlip).with(sendAppear);
        }
        else {
            modeChange.play(sendFlip).with(sendAppear).with(menuFlip).with(menuAppear);

        }
        modeChange.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initMenu();
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
