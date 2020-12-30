package com.julor200.crossdress.fragments.review_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * The purpose of this fragment is that it gives the user a opportunity to review items in the app.
 *  * Activities that contain this fragment must implement the
 *  * {@link CreateReviewFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class CreateReviewFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private EditText reviewMessage;
    private String username;
    private int postId;
    private Map<String, String> headers;

    public CreateReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_review, container, false);
        //Initialise fields
        reviewMessage = view.findViewById(R.id.reviewText);
        Bundle bundle = getArguments();
        assert bundle != null;
        username = bundle.getString("username");
        postId = bundle.getInt("id");
        headers = (HashMap<String, String>) bundle.get("headers");
        Button createReviewButton = view.findViewById(R.id.createReviewButton);
        createReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReview();
            }
        });
        return view;
    }

    private void createReview(){
        String review = reviewMessage.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("post_id", String.valueOf(postId));
        params.put("review", review);
        OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.POST,
                "https://crossdress.herokuapp.com/create/review", String.class,
                headers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mListener.onReviewCreated();
                reviewMessage.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        }, params);
        queue.add(request);
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
        void onReviewCreated();
    }
}
