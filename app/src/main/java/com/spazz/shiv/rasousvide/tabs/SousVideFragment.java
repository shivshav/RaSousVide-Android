package com.spazz.shiv.rasousvide.tabs;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spazz.shiv.rasousvide.R;
import com.triggertrap.seekarc.SeekArc;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SousVideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SousVideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SousVideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";

    @InjectView(R.id.seekArcTemp) SeekArc seekArcTemp;
    @InjectView(R.id.seekTempText) TextView seekTempText;
    // TODO: Rename and change types of parameters
    private int position;

//    private OnFragmentInteractionListener mListener;

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
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sous_vide, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        seekArcTemp.setOnSeekArcChangeListener(new MyOnSeekArcChangeListener());
        seekArcTemp.setProgress(0);
        //seekTempText.setText("Placeholder son!");
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
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
