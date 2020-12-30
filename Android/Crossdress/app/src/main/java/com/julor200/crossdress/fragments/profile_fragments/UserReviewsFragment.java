package com.julor200.crossdress.fragments.profile_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;
import com.julor200.crossdress.adapters.recyclerview_adapters.ReviewAdapter;
import com.julor200.crossdress.java_beans.Review;
import com.julor200.crossdress.java_beans.ReviewList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Used to display all reviews by a specific user from ProfileFragment.
 *  * Activities that contain this fragment must implement the
 *  * {@link UserReviewsFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class UserReviewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private int latestClickedPosition;
    private int clicks;
    private Map<String, String> headers;
    private OnFragmentInteractionListener mListener;

    public UserReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        //Initialise all fields (Except lastClickedPosition and clicks, they do not need to be
        // initialised here
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Bundle bundle = getArguments();
        assert bundle != null;
        ReviewList reviewList = (ReviewList) bundle.get("reviews");
        headers = (HashMap<String, String>) bundle.get("headers");
        assert reviewList != null;
        List<Review> reviews = reviewList.getReviewList();
        ReviewAdapter.RecyclerViewClickListener listener = new ReviewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                deleteReview(position);
            }
        };
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviews, listener);
        recyclerView.setAdapter(reviewAdapter);
        return view;
    }

    private void deleteReview(int id){
        if(id == latestClickedPosition && clicks == 1){ //Double click, delete review
            ReviewAdapter adapter = (ReviewAdapter) recyclerView.getAdapter();
            assert adapter != null;
            Review review = adapter.getItem(id);
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.DELETE,
                    "https://crossdress.herokuapp.com/delete/review/" + review.getId(), String.class,
                    headers,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mListener.onReviewDeleted();
                            clicks = 0;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            queue.add(request);
        }
        else{ //Either other item clicked or latestPositionClicked == null, i.e. nothing clicked
            // before
            latestClickedPosition = id;
            clicks = 1;
            Toast toast = Toast.makeText(getActivity(), getString(R.string.double_click_to_remove), Toast.LENGTH_LONG);
            toast.show();
        }
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
        void onReviewDeleted();

    }
}
