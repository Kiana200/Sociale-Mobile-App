package com.julor200.crossdress.fragments.booking_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.julor200.crossdress.R;


/**
 * This is the calendarFragment which is used for booking items in the app.
 * This fragment consists of a calendarView.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */

public class CalendarFragment extends Fragment{
    private Bundle bundle;
    private OnFragmentInteractionListener mListener;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        bundle = getArguments(); //Måste skicka med till nästa fragment
        CalendarView mCalendarView = view.findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                int realMonth = month + 1; //Index starts at 0
                String monthString = Integer.toString(realMonth);
                if(realMonth < 10) monthString = "0" + monthString; //Fill with 0, e.g. 2020-04-25
                String date = year + "-" + monthString + "-" + day;
                bundle.putString("date", date);
                mListener.onDateClicked(bundle);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */

    public interface OnFragmentInteractionListener {
        void onDateClicked(Bundle bundle);
    }
}
