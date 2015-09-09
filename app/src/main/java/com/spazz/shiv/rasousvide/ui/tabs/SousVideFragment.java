package com.spazz.shiv.rasousvide.ui.tabs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemSelected;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SousVideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SousVideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SousVideFragment extends Fragment {
    private static final String TAG = "SousVideFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";

    @Bind(R.id.seekArcTemp) SeekArc seekArcTemp;
    @Bind(R.id.seekTempText) TextView seekTempText;
    @Bind(R.id.meal_spinner) Spinner mealSpinner;
    @Bind(R.id.meal_spinner_sub_choice) Spinner mealSubChoice;
    @Bind(R.id.pid_layout) RelativeLayout pidLayout;

    @Bind(R.id.k_param) EditText kParamEditTxt;
    @Bind(R.id.i_param) EditText iParamEditTxt;
    @Bind(R.id.d_param) EditText dParamEditTxt;
    // TODO: Rename and change types of parameters

    List<Entree> entrees;

    Meal selectedMeal;

    SharedPreferences prefs;
    public boolean advView;

//    private OnFragmentInteractionListener mListener;
    private ShivVidePost sousVideParams;

    private Subscription kParamSubscription;
    private Subscription iParamSubscription;
    private Subscription dParamSubscription;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sousVideParams = new ShivVidePost("Auto");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sous_vide, container, false);
        ButterKnife.bind(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        seekArcTemp.setOnSeekArcChangeListener(new MyOnSeekArcChangeListener());
        seekArcTemp.setProgress(0);
        //seekTempText.setText("Placeholder son!");
        setupMealSpinner();
        advView = prefs.getBoolean(SettingsActivity.KEY_PREF_ADV_VIEW, false);

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        boolean pref = prefs.getBoolean(SettingsActivity.KEY_PREF_ADV_VIEW, false);
        if(advView != pref) {
            if (pref) {
                pidLayout.setVisibility(View.VISIBLE);
            } else {
                pidLayout.setVisibility(View.GONE);
            }
            advView = pref;
        }

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


    }

    @Override
    public void onPause() {
        //TODO: Combine into a single subscription
        if(kParamSubscription != null && !kParamSubscription.isUnsubscribed()) {
            kParamSubscription.unsubscribe();
        }
        if(iParamSubscription != null && !iParamSubscription.isUnsubscribed()) {
            iParamSubscription.unsubscribe();
        }
        if(dParamSubscription != null && !dParamSubscription.isUnsubscribed()) {
            dParamSubscription.unsubscribe();
        }

        super.onPause();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setupMealSpinner() {
        entrees = Entree.listAll(Entree.class);
        List<String> entreeNames = new ArrayList<>(entrees.size());

        for(Entree e: entrees) {
            entreeNames.add(e.getEntreeName());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, entreeNames); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mealSpinner.setAdapter(spinnerArrayAdapter);
        mealSpinner.setSelection(0);

    }

    public ShivVidePost getSousVideParams() {
        return sousVideParams;
    }

    @OnItemSelected(value = R.id.meal_spinner, callback = OnItemSelected.Callback.ITEM_SELECTED)
    void entreeSelected(int position) {
        List<Meal> meals = entrees.get(position).getMeals();
        if(meals != null && meals.get(0).getMealType() != null) {
            mealSubChoice.setVisibility(View.VISIBLE);
            List<String> mealTypes = new ArrayList<>(meals.size());
            for (Meal meal : meals) {
                mealTypes.add(meal.getMealType());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, mealTypes); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mealSubChoice.setAdapter(spinnerArrayAdapter);
        }
        else {
            mealSubChoice.setVisibility(View.GONE);
            mealSubChoice.setSelection(0);
            mealSubChoice.setAdapter(null);
            if(meals != null) {
                selectedMeal = meals.get(0);
                Log.d(TAG, "Selected meal has no name with setpoint " + meals.get(0).getSetPoint());
                seekArcTemp.setProgress((int) meals.get(0).getSetPoint());
            }
        }
    }

    @OnItemSelected(value = R.id.meal_spinner_sub_choice, callback = OnItemSelected.Callback.ITEM_SELECTED)
    void mealSelected(int position) {
        selectedMeal = entrees.get(mealSpinner.getSelectedItemPosition()).getMeals().get(position);
        Log.d(TAG, "Selected Meal is " + selectedMeal.getMealType());
        seekArcTemp.setProgress((int) selectedMeal.getSetPoint());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }


}
