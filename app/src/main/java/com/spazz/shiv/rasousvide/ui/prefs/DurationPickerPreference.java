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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.spazz.shiv.rasousvide.R;

/*
 * @author Danesh
 * @author nebkat
 */

public class DurationPickerPreference extends DialogPreference {
    private int hourMin, hourMax, minuteMin, minuteMax, secondMin, secondMax, mDefault;

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

        int defaultHours = time/(60*60);
        int defaultMinutes = (time - (defaultHours * (60*60)))/60;
        int defaultSeconds = (time - (defaultHours * (60*60)) - (defaultMinutes * 60));

        hourPicker = setupPicker(view, R.id.hour_picker, hourMin, hourMax, defaultHours);
        minutePicker = setupPicker(view, R.id.minute_picker, minuteMin, minuteMax, defaultMinutes);
        secondPicker = setupPicker(view, R.id.second_picker, secondMin, secondMax, defaultSeconds);

        return view;
    }

    private NumberPicker setupPicker(View view, int resId, int min, int max, int defaultVal) {
        NumberPicker numberPicker = (NumberPicker) view.findViewById(resId);

        // Initialize state
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
        numberPicker.setValue(defaultVal);
        numberPicker.setWrapSelectorWheel(false);

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
        if (positiveResult) {
            int time = 0;
            time += (hourPicker.getValue() * 60*60);
            time += (minutePicker.getValue() * 60);
            time += (secondPicker.getValue());
            persistInt(time);
        }
    }

}
