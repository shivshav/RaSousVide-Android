package com.spazz.shiv.rasousvide;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.spazz.shiv.rasousvide.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by pula on 9/15/15.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityInstrumentationTest {
    private static final String AUTO = "Auto";
    private static final String OFF = "Off";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testFab_toggleState_offToAuto() {
        onView(withId(R.id.current_mode)).check(matches(withText(OFF)));
        onView(withId(R.id.test_button)).perform(click());
        onView(withId(R.id.current_mode)).check(matches(withText(AUTO)));
    }
}
