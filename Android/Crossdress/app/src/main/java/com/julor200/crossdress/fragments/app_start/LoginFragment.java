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
 * This fragment gives the user the opportunity to either log in or create a new account
 * The idea is that after a person has created a new account she/he should back and then log in.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment  {
    private EditText username;
    private EditText password;
    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
       username = view.findViewById(R.id.editTextUsernameLogin);
       password = view.findViewById(R.id.editTextPasswordLogin);

        //Set listeners on buttons
        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mListener.onLoginClicked( username.getText().toString(),
                        password.getText().toString());
            }
        });
        Button registerButton = view.findViewById(R.id.newAccountButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRegisterClicked();
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

    public void onClick(View v) {
        boolean clickedLogin = (v == v.findViewById(R.id.login_button));
        CharSequence userUsername = username.getText();
        CharSequence userPassword = password.getText();
        if(clickedLogin) {
            mListener.onLoginClicked(userUsername.toString(), userPassword.toString());
        }
        else{ //Clicked register
            mListener.onRegisterClicked();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onLoginClicked(String username, String password);
        void onRegisterClicked();
    }
}
