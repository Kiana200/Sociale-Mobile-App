package com.julor200.crossdress.fragments.booking_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.julor200.crossdress.R;

/**
 * This fragment is used for booking items.
 * The textViews describe the item plus the date that it is going to be booked.
 * Activities that contain this fragment must implement the
 * {@link BookingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BookingFragment extends Fragment {
    private TextView date;
    private int id;

    private OnFragmentInteractionListener mListener;

    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        id = (int) bundle.get("id");
        // Fill view with data
        String bookDate = (String) bundle.get("date");
        date = view.findViewById(R.id.date);
        date.setText(bookDate);
        String rubric = bundle.getString("rubric");
        TextView title = view.findViewById(R.id.rubric);
        title.setText(rubric);
        String bookSize = bundle.getString("size");
        TextView size = view.findViewById(R.id.size);
        size.setText(bookSize);
        String bookCategory = bundle.getString("category");
        TextView category = view.findViewById(R.id.category);
        category.setText(bookCategory);
        Button button = view.findViewById(R.id.book);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBookClicked(date.getText().toString(), id);
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

   /* public void onClick(View v){
        boolean clickedBook = (v == v.findViewById(R.id.book));
        CharSequence bookingDate = date.getText();
        if(clickedBook) {
            mListener.onBookClicked(bookingDate.toString(), id);
        }
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onBookClicked(String date, int id);
    }
}















