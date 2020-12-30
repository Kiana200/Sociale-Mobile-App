package com.julor200.crossdress.fragments.filter_fragments;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.FilterList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * This fragment is used to filter the categories.
 * Activities that contain this fragment must implement the
 * {@link CategoryFilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CategoryFilterFragment extends androidx.fragment.app.ListFragment {

    private OnFragmentInteractionListener mListener;

    public CategoryFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_filter, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        Map<String, String> headers = (HashMap<String, String>) bundle.get("headers");
        String URL = "https://crossdress.herokuapp.com/get/all/categories";
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).
                getApplicationContext());
        OurGsonRequest<FilterList> request = new OurGsonRequest<>(Request.Method.GET, URL,
                FilterList.class, headers, new Response.Listener<FilterList>() {
            @Override
            public void onResponse(FilterList response) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_list_item_1,
                        response.getFilterList()
                );
                setListAdapter(adapter);
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
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView view = (TextView) v;
        mListener.onCategoryChosen(view.getText().toString());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
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
        void onCategoryChosen(String category);
    }
}
