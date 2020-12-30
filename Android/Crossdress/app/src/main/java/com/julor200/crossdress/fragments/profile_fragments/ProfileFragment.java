package com.julor200.crossdress.fragments.profile_fragments;

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
 * The fragment is used for displaying the user's reviews, posts and bookings as well as deleting
 * them. Contains buttons which then leads to other fragments displaying one of the above.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button getPostsButton = view.findViewById(R.id.showPosts);
        getPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGetPostsClicked();
            }
        });
        Button getReviewsButton = view.findViewById(R.id.showReviews);
        getReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGetReviewsClicked();
            }
        });
        Button getBookingsButton = view.findViewById(R.id.showBookings);
        getBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGetBookingsClicked();
            }
        });
        Bundle bundle = getArguments();
        TextView username = view.findViewById(R.id.profileUsername);
        assert bundle != null;
        String userUsername = bundle.getString("username");
        username.setText(userUsername);


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
        void onGetPostsClicked();
        void onGetReviewsClicked();
        void onGetBookingsClicked();
    }
}
