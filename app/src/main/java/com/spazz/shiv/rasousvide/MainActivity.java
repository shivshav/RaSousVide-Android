package com.spazz.shiv.rasousvide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.spazz.shiv.rasousvide.database.Entree;
import com.spazz.shiv.rasousvide.database.Meal;
import com.spazz.shiv.rasousvide.tabs.SousVideFragment;

import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.toolbar_bottom)
    Toolbar bottomToolbar;

    @InjectView(R.id.current_mode)
    TextView currentMode;
//    @InjectView(R.id.bottom_layout)
//    RelativeLayout bottomLayout;

    //TODO: This should only show when status changes from 'Off' to something else
    @InjectView(R.id.stop_button)
    ImageButton stopButton;

    @InjectView(R.id.send_button)
    ImageButton sendButton;

    @InjectView(R.id.menu_button)
    ImageButton menuButton;

    @InjectView(R.id.test_button)
    ImageButton testButton;

    private TabsPagerAdapter mAdapter;

    private Boolean firstTime = null;

    private boolean menuOpen;
    private float sendPos;
    private float stopPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forceNewEntries();
        Entree.firstTimeMealSetup();

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
//        setupStopAnimation();

//        if(isFirstTime()) {
//            //Execute database setup here
//            Entree.firstTimeMealSetup();
//        }
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

    @OnClick(R.id.menu_button)
    public void menuClicked(View view) {
        slide().start();
//        long animDuration = 350;
//
//        ObjectAnimator sendAlpha = ObjectAnimator.ofFloat(sendButton, "alpha", 0);
//        ObjectAnimator stopAlpha = ObjectAnimator.ofFloat(stopButton, "alpha", 0);
//        float alphaStart;
//        float alphaEnd;
//
//        ObjectAnimator sendTranslate = ObjectAnimator.ofFloat(sendButton, "translationY", 0);
//        ObjectAnimator stopTranslate = ObjectAnimator.ofFloat(stopButton, "translationY", 0);
//        float sendTranslation;
//        float stopTranslation;
//
//        ObjectAnimator menuRotate = ObjectAnimator.ofFloat(menuButton, "rotation", 0);
//        float rotateStart;
//        float rotateEnd;
//
//        if(menuOpen) {//do close animation
//            alphaStart = 1;
//            alphaEnd = 0;
//
//            sendTranslation = sendPos;
//            stopTranslation = stopPos;
//
//            rotateStart = 45;
//            rotateEnd = 0;
//
//            //make buttons non existant for layout calculation & view purposes
//            sendTranslate.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    sendButton.setVisibility(View.GONE);
//                }
//            });
//
//            stopTranslate.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    super.onAnimationStart(animation);
//                    stopButton.clearAnimation();
//                }
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    stopButton.setVisibility(View.GONE);
//                }
//            });
//
//
//
//        }
//        else {//do open animation
//
//            alphaStart = 0;
//            alphaEnd = 1;
//            //Items will be set to their desired position
//            sendTranslation = 0;
//            stopTranslation = 0;
//
//            rotateStart = 0;
//            rotateEnd = 45;
//
//            //make buttons visible
//            sendTranslate.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    super.onAnimationStart(animation);
//                    sendButton.setVisibility(View.VISIBLE);
//                }
//            });
//            stopTranslate.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    super.onAnimationStart(animation);
//                    stopButton.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    setupStopAnimation();
//                }
//            });
//        }
//        //TODO:Make alpha animations decelerate/accelerate?
//        sendAlpha.setFloatValues(alphaStart, alphaEnd);
//        sendAlpha.setDuration(animDuration);
//
//        stopAlpha.setFloatValues(alphaStart, alphaEnd);
//        stopAlpha.setDuration(animDuration);
//
//        //TODO: Make translations accelerate on close and decelerate on open?
//        sendTranslate.setFloatValues(sendTranslation);
//        sendTranslate.setDuration(animDuration);
//        sendTranslate.setInterpolator(new DecelerateInterpolator());
//
//        stopTranslate.setFloatValues(stopTranslation);
//        stopTranslate.setDuration(animDuration);
//        stopTranslate.setInterpolator(new DecelerateInterpolator());
//
//        menuRotate.setFloatValues(rotateStart, rotateEnd);
//        menuRotate.setDuration(animDuration);
//        menuRotate.setInterpolator(new BounceInterpolator());
//
//        AnimatorSet menuToggle = new AnimatorSet();
//
//        menuToggle.play(menuRotate).with(sendTranslate).with(sendAlpha).with(stopTranslate).with(stopAlpha);
//        menuToggle.start();
////        sendButton.setTranslationY(sendPos);
//
////        stopButton.setTranslationY(stopTranslation);
//
//        Log.d(TAG, "MenuOpen?:" + menuOpen + ", StopPos:" + stopPos + ", SendPos:" + sendPos);
//
//        menuOpen = !menuOpen;
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
                    super.onAnimationEnd(animation);
                    sendButton.setVisibility(View.GONE);
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
        //menuToggle.start();
//        sendButton.setTranslationY(sendPos);

//        stopButton.setTranslationY(stopTranslation);



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

        currentMode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                altModeChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

            sendAppear.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    sendButton.setVisibility(View.GONE);
                    sendButton.setRotationY(0);

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
                modeChange.play(menuToggle).before(sendFlip).with(sendAppear).with(menuFlip).with(menuAppear);
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
