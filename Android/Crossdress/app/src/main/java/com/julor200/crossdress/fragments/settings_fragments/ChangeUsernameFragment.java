package com.julor200.crossdress.fragments.settings_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.julor200.crossdress.R;


/**
 * This fragment is made so that the users can change their username.
 * Activities that contain this fragment must implement the
 * {@link ChangeUsernameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChangeUsernameFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private EditText newUsername;

    public ChangeUsernameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_username, container, false);
        newUsername = view.findViewById(R.id.newUsername);
        Button button = view.findViewById(R.id.changeUsernameButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChangeUsernameButtonClicked(newUsername.getText().toString());
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
    public interface OnFragmentInteractionListener{
        void onChangeUsernameButtonClicked(String newUsername);
    }
}
