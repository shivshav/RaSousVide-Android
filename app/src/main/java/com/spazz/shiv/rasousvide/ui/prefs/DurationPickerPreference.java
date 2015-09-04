package com.spazz.shiv.rasousvide.ui.prefs;
/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.spazz.shiv.rasousvide.R;

import java.util.HashMap;

/*
 * @author Danesh
 * @author nebkat
 */

public class DurationPickerPreference extends DialogPreference {
    private static final String HASHKEY_HOURS = "hours";
    private static final String HASHKEY_MINUTES = "minutes";
    private static final String HASHKEY_SECONDS = "seconds";

    private int hourMin, hourMax, minuteMin, minuteMax, secondMin, secondMax, pickersToShow;

//    private String mMaxExternalKey, mMinExternalKey;

    private NumberPicker hourPicker, minutePicker, secondPicker;

    public DurationPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
//        TypedArray dialogType = context.obtainStyledAttributes(attrs,
//                R..styleable.DialogPreference, 0, 0);
        TypedArray numberPickerType = context.obtainStyledAttributes(attrs,
                R.styleable.DurationPickerPreference, 0, 0);
        int id = Resources.getSystem().getIdentifier("numberpicker_input", "id", "android");


//        mMaxExternalKey = numberPickerType.getString(R.styleable.DurationPickerPreference_maxExternal);
//        mMinExternalKey = numberPickerType.getString(R.styleable.DurationPickerPreference_minExternal);

        hourMax = numberPickerType.getInt(R.styleable.DurationPickerPreference_hourMax, 8);
        hourMin = numberPickerType.getInt(R.styleable.DurationPickerPreference_hourMin, 0);

        minuteMax = numberPickerType.getInt(R.styleable.DurationPickerPreference_minuteMax, 60);
        minuteMin = numberPickerType.getInt(R.styleable.DurationPickerPreference_minuteMin, 0);

        secondMax = numberPickerType.getInt(R.styleable.DurationPickerPreference_secondMax, 60);
        secondMin = numberPickerType.getInt(R.styleable.DurationPickerPreference_secondMin, 0);

        TypedValue val = new TypedValue();
        pickersToShow = numberPickerType.getInt(R.styleable.DurationPickerPreference_pickers, 0x4|0x2|0x1);
//        dialogType.recycle();
        numberPickerType.recycle();
    }

    @Override
    protected View onCreateDialogView() {
//        int max = hourMax;
//        int min = hourMin;

//        // External values
//        if (mMaxExternalKey != null) {
//            max = getSharedPreferences().getInt(mMaxExternalKey, hourMax);
//        }
//        if (mMinExternalKey != null) {
//            min = getSharedPreferences().getInt(mMinExternalKey, hourMin);
//        }

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pref_number_picker, null);

        int time = getPersistedInt(5);

        HashMap<String, Integer> times = convertIntegerToTime(time);



        hourPicker = setupPicker(view, R.id.hour_picker, R.id.hour_label, hourMin, hourMax, times.get(HASHKEY_HOURS), pickersToShow & 0x4);
        minutePicker = setupPicker(view, R.id.minute_picker, R.id.minute_label, minuteMin, minuteMax, times.get(HASHKEY_MINUTES), pickersToShow & 0x2);
        secondPicker = setupPicker(view, R.id.second_picker, R.id.second_label, secondMin, secondMax, times.get(HASHKEY_SECONDS), pickersToShow & 0x1);

        return view;
    }

    private NumberPicker setupPicker(View view, int pickerResId, int labelResId, int min, int max, Integer defaultVal, int noShow) {
        NumberPicker numberPicker = (NumberPicker) view.findViewById(pickerResId);
        TextView pickerLabel = (TextView) view.findViewById(labelResId);

        // Initialize state
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
        numberPicker.setValue(defaultVal);
        numberPicker.setWrapSelectorWheel(true);

        if(noShow == 0) {
            numberPicker.setVisibility(View.GONE);
            pickerLabel.setVisibility(View.GONE);
        }
        //TODO: FIgure out how to stop keyboard from coming up
//        // No keyboard popup
//        EditText textInput = (EditText) numberPicker.findViewById(android.R.id.);
//
//        numberPicker.
//        textInput.setCursorVisible(false);
//        textInput.setFocusable(false);
//        textInput.setFocusableInTouchMode(false);

        return numberPicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        int hours, minutes, seconds;

        hours = hourPicker.getValue();
        minutes = minutePicker.getValue();
        seconds = secondPicker.getValue();

        if (positiveResult) {
            int time = 0;
            time += (hours * 60*60);
            time += (minutes * 60);
            time += (seconds);
            persistInt(time);
        }

        callChangeListener(summarizeTheTime(hours, minutes, seconds));
    }

    @Override
    public void setSummary(CharSequence summary) {
        int time;
        try {
            time = Integer.parseInt(String.valueOf(summary));
        } catch (NumberFormatException nfe) {
            super.setSummary(summary.toString());
            return;
        }

        HashMap<String, Integer> times = convertIntegerToTime(time);

        super.setSummary(summarizeTheTime(times.get(HASHKEY_HOURS), times.get(HASHKEY_MINUTES), times.get(HASHKEY_SECONDS)));
    }

    private HashMap<String, Integer> convertIntegerToTime(int time) {
        int defaultHours = time/(60*60);
        int defaultMinutes = (time - (defaultHours * (60*60)))/60;
        int defaultSeconds = (time - (defaultHours * (60*60)) - (defaultMinutes * 60));

        HashMap<String, Integer> values = new HashMap<>(3);
        values.put(HASHKEY_HOURS, defaultHours);
        values.put(HASHKEY_MINUTES, defaultMinutes);
        values.put(HASHKEY_SECONDS, defaultSeconds);

        return values;
    }

    private String summarizeTheTime(Integer hours, Integer minutes, Integer seconds) {

        StringBuilder sb = new StringBuilder();
        if(hours > 0) {
            sb.append(hours).append(" hours, ");
        }
        if(minutes > 0) {
            sb.append(minutes).append(" minutes, ");
        }
        if(seconds > 0) {
            if(sb.length() > 0) {
                sb.append("and ");
            }
            sb.append(seconds).append(" seconds");
        }

        return sb.toString();
    }

}
