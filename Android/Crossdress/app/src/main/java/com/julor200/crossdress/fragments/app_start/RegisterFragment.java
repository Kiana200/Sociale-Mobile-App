package com.julor200.crossdress.fragments.app_start;

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
 * This is the fragment used for registration of a new account.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {
    private EditText username;
    private EditText email;
    private EditText password;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClicked();
            }
        });
        //Initialise fields
        email = view.findViewById(R.id.editTextEmailRegister);
        username = view.findViewById(R.id.editTextUsernameRegister);
        password = view.findViewById(R.id.editTextPasswordRegister);
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
        super.onDetach();mListener = null;
    }

    private void registerClicked() {
        CharSequence userEmail = email.getText();
        CharSequence userUsername = username.getText();
        CharSequence userPassword = password.getText();
        mListener.register(userEmail.toString(), userUsername.toString(), userPassword.toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void register(String email, String username, String password);
    }
}
