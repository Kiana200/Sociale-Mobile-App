package com.julor200.crossdress.fragments.post_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Review;
import com.julor200.crossdress.java_beans.ReviewList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Used to display all reviews for a specific Post.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostReviewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PostReviewsFragment extends androidx.fragment.app.ListFragment {

    private OnFragmentInteractionListener mListener;
    private int id;

    public PostReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_with_send_button, container, false);
        Button sendButton = view.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWriteReviewClicked(id);
            }
        });
        Bundle bundle = getArguments();
        assert bundle != null;
        id = bundle.getInt("id");
        Map<String, String> headers = (HashMap<String, String>) bundle.get("headers");
        String URL = "https://crossdress.herokuapp.com/get/all/reviews/" + id;

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        OurGsonRequest<ReviewList> request = new OurGsonRequest<>(Request.Method.GET, URL, ReviewList.class, headers, new Response.Listener<ReviewList>() {
            @Override
            public void onResponse(ReviewList response) {
                List<Review> reviewList = response.getReviewList();
                ArrayAdapter<Review> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_list_item_1,
                        reviewList);
                setListAdapter(adapter);
                //We only want to display the reviews, so no click listener needed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
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
        void onWriteReviewClicked(int id);
    }
}
