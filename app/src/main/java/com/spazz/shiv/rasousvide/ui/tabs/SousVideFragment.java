package com.spazz.shiv.rasousvide.ui.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.spazz.shiv.rasousvide.R;
import com.spazz.shiv.rasousvide.database.Entree;
import com.spazz.shiv.rasousvide.database.Meal;
import com.spazz.shiv.rasousvide.rest.model.ShivVidePost;
import com.spazz.shiv.rasousvide.ui.prefs.SettingsActivity;
import com.triggertrap.seekarc.SeekArc;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemSelected;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SousVideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SousVideFragment extends Fragment{
    private static final String STATIC_TAG = "SousVideFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "pagerAdapterPosition";

    private static final String STATE_KEY_MEAL_ID = "meal_id";

    @Bind(R.id.seekArcTemp) SeekArc seekArcTemp;
    @Bind(R.id.seekTempText) TextView seekTempText;

    @Bind(R.id.meal_spinner) Spinner entreeSpinner;
    ArrayAdapter<Entree> entreeAdapter;
    @Bind(R.id.meal_spinner_sub_choice) Spinner mealSpinner;
    ArrayAdapter<Meal> mealAdapter;

    @Bind(R.id.pid_layout) RelativeLayout pidLayout;

    @Bind(R.id.k_param) EditText kParamEditTxt;
    @Bind(R.id.i_param) EditText iParamEditTxt;
    @Bind(R.id.d_param) EditText dParamEditTxt;
    // TODO: Rename and change types of parameters

    List<Entree> entrees;

    Entree selectedEntree;
    Meal selectedMeal;

    private int pagerAdapterPosition;
    private String TAG;

    SharedPreferences prefs;
    private boolean advView;
    private boolean showCelsius;

    private ShivVidePost sousVideParams;

    private CompositeSubscription rxSubscriptions;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Positioning on tab.
     * @return A new instance of fragment SousVideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SousVideFragment newInstance(int position) {
        SousVideFragment fragment = new SousVideFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    public SousVideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sousVideParams = new ShivVidePost("Auto");

        Bundle fragArgs = getArguments();
        if(fragArgs != null) {
            pagerAdapterPosition = fragArgs.getInt(ARG_POSITION);
        }
        TAG = STATIC_TAG + ", Position " + pagerAdapterPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sous_vide, container, false);
        ButterKnife.bind(this, rootView);

        ViewCompat.setElevation(rootView, 50);

        seekArcTemp.setOnSeekArcChangeListener(new MyOnSeekArcChangeListener());

        setupMealSpinner(rootView.getContext());

        return rootView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Long mealId = savedInstanceState.getLong(STATE_KEY_MEAL_ID);
            selectedMeal = Meal.findById(Meal.class, mealId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Subscription kParamSubscription;
        Subscription iParamSubscription;
        Subscription dParamSubscription;

        setupPreferences(this.getActivity());

        // We have to always create a new CompositeSubscription Object as described here
        // http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/
        rxSubscriptions = new CompositeSubscription();


        // TODO: Clean this up into a function and some more modularity
        kParamSubscription = WidgetObservable.text(kParamEditTxt)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(
                        onTextChangeEvent -> {
                            String input = onTextChangeEvent.text().toString();
                            double param;
                            try {
                                param = Double.parseDouble(input);
                            } catch (NumberFormatException nfe) {
                                param = 4.0;
                            }

                            return param;
                        })
                .subscribe(sousVideParams::setK_param);
        rxSubscriptions.add(kParamSubscription);

        iParamSubscription = WidgetObservable.text(iParamEditTxt)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(
                        onTextChangeEvent -> {
                            String input = onTextChangeEvent.text().toString();
                            double param;
                            try {
                                param = Double.parseDouble(input);
                            } catch (NumberFormatException nfe) {
                                param = 4.0;
                            }

                            return param;
                        })
                .subscribe(sousVideParams::setI_param);
        rxSubscriptions.add(iParamSubscription);

        dParamSubscription = WidgetObservable.text(dParamEditTxt)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(
                        onTextChangeEvent -> {
                            String input = onTextChangeEvent.text().toString();
                            double param;
                            try {
                                param = Double.parseDouble(input);
                            } catch (NumberFormatException nfe) {
                                param = 4.0;
                            }

                            return param;
                        })
                .subscribe(sousVideParams::setD_param);
        rxSubscriptions.add(dParamSubscription);
    }


    @Override
    public void onPause() {
        //TODO: Combine into a single subscription
        if(rxSubscriptions.hasSubscriptions()) {
            rxSubscriptions.unsubscribe();
        }

        // We have to always create a new CompositeSubscription Object as described here
        // http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/
        rxSubscriptions = null;

        super.onPause();
    }

    public void onSaveInstanceState(Bundle outState){

        // All state can be restored from saving the currently selected meal
        outState.putLong(STATE_KEY_MEAL_ID, selectedMeal.getId());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setupMealSpinner(Context context) {

        // RX Necessary
        entrees = Entree.listAll(Entree.class);

        // Setup meal spinner first so that we may set the entree/meal relationship correctly on start
        mealAdapter = new ArrayAdapter<>(context, R.layout.spinner_item);
        mealAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mealSpinner.setAdapter(mealAdapter);

        // Now set up the entree spinner
        entreeAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, entrees);
        entreeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        entreeSpinner.setAdapter(entreeAdapter);
    }

    public ShivVidePost getSousVideParams() {
        return sousVideParams;
    }

    @OnItemSelected(value = R.id.meal_spinner, callback = OnItemSelected.Callback.ITEM_SELECTED)
    void entreeSelected(int position) {
        selectedEntree = entrees.get(position);
        Log.d(TAG, "Selected entree is " + selectedEntree.getEntreeName());
        List<Meal> meals = selectedEntree.getMeals();

        if(meals != null && meals.size() > 1) {// If there is more than one way to cook the meal

            if(!mealAdapter.isEmpty()) {
                mealAdapter.clear();
            }
            mealAdapter.addAll(meals);
            mealAdapter.notifyDataSetChanged();
            mealSpinner.setAdapter(mealAdapter);

            // Purely for device orientation changes.
            // FIXME: This should be optimized. It probably doesn't lend to very fast orientation switches
            if(selectedMeal != null && selectedMeal.getEntree().getId().equals(selectedEntree.getId())) {
                /*
                 * Why Did I Do This?
                 * If we have a meal whose entree has the same ID as the one that just got selected,
                 * and we are inside the ItemSelected Callback, that means an entree was selected
                 * whose meal had already been chosen, this SHOULD only happen because of device
                 * orientations changes, otherwise selectedMeal getting set is purely a dependency
                 * on selectedEntree and one of it's meals
                 */

                ArrayAdapter<Meal> arrayAdapter = (ArrayAdapter<Meal>) mealSpinner.getAdapter();

                for (int i = 0; i < arrayAdapter.getCount(); i++) {

                    if(arrayAdapter.getItem(i).getId().equals(selectedMeal.getId())) {
                        mealSpinner.setSelection(i);
                        break;
                    }
                }
            }

            mealSpinner.setVisibility(View.VISIBLE);
        }
        else {// We only have one option to cook this meal (i.e. Chicken)
            mealAdapter.clear();
            mealAdapter.notifyDataSetChanged();

            mealSpinner.setVisibility(View.GONE);

            if(meals != null) {
                selectedMeal = meals.get(0);
                Log.d(TAG, "Selected meal has no name with setpoint " + meals.get(0).getSetPoint());
                seekArcTemp.setProgress((int) meals.get(0).getSetPoint());
            }
        }
    }

    @OnItemSelected(value = R.id.meal_spinner_sub_choice, callback = OnItemSelected.Callback.ITEM_SELECTED)
    void mealSelected(AdapterView<?> parent, int position) {

        selectedMeal = selectedEntree.getMeals().get(position);
        Log.d(TAG, "Selected Meal is " + selectedMeal.getMealType());
        seekArcTemp.setProgress((int) selectedMeal.getSetPoint());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void setupPreferences(Context context){

        Log.d(TAG,"Preference setup");

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Initialize preferences
        advView = prefs.getBoolean(SettingsActivity.KEY_PREF_GENERAL_ADV_VIEW, false);
        showCelsius = prefs.getBoolean(SettingsActivity.KEY_PREF_GENERAL_TEMP_UNITS, false);

        // Preferences
        if (advView) {
            pidLayout.setVisibility(View.VISIBLE);
        } else {
            pidLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class MyOnSeekArcChangeListener implements SeekArc.OnSeekArcChangeListener {

        @Override
        public void onStopTrackingTouch(SeekArc seekArc) {
        }

        @Override
        public void onStartTrackingTouch(SeekArc seekArc) {
        }

        @Override
        public void onProgressChanged(SeekArc seekArc, int progress,
                                      boolean fromUser) {
            seekTempText.setText(String.valueOf(progress));
            sousVideParams.setSet_point(progress);
        }
    }
}
