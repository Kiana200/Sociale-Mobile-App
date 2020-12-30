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
import com.julor200.crossdress.adapters.recyclerview_adapters.DateAdapter;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Date;
import com.julor200.crossdress.java_beans.DateList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * This fragment is used to display all the bookings a user has from ProfileFragment.
 * A simple {@link Fragment} subclass.
 *  * Activities that contain this fragment must implement the
 *  * {@link UserBookingsFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class UserBookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private int latestClickedPosition;
    private int clicks;
    private Map<String, String> headers;
    private OnFragmentInteractionListener mListener;

    public UserBookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Bundle bundle = getArguments();
        assert bundle != null;
        DateList reviewList = (DateList) bundle.get("dates");
        headers = (HashMap<String, String>) bundle.get("headers");
        assert reviewList != null;
        List<Date> dates = reviewList.getDateList();
        DateAdapter.RecyclerViewClickListener listener = new DateAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                cancelBooking(position);
            }
        };
        DateAdapter dateAdapter = new DateAdapter(dates, listener);
        recyclerView.setAdapter(dateAdapter);
        return view;
    }

    private void cancelBooking(int id){
        if(id == latestClickedPosition && clicks == 1){ //Double click, delete post
            DateAdapter adapter = (DateAdapter) recyclerView.getAdapter();
            assert adapter != null;
            Date date = adapter.getItem(id);
            String URL = "https://crossdress.herokuapp.com/cancel/" + date.getPost() + "/" + date.getDate();
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.DELETE,
                    URL, String.class,
                    headers,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mListener.onBookingCancelled();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(request);
        }
        else{
            latestClickedPosition = id;
            clicks = 1;
            Toast toast = Toast.makeText(getActivity(), getString(R.string.double_click_to_remove), Toast.LENGTH_SHORT);
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
    public interface OnFragmentInteractionListener{
        void onBookingCancelled();
    }
}
