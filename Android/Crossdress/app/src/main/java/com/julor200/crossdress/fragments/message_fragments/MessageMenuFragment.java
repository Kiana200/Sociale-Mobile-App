package com.julor200.crossdress.fragments.message_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.User;
import com.julor200.crossdress.java_beans.UserList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fragment for displaying all Users with conversations in MessageMenuFragment.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MessageMenuFragment extends androidx.fragment.app.ListFragment  {

    private OnFragmentInteractionListener mListener;

    public MessageMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_with_send_button, container, false);
        Button button = view.findViewById(R.id.sendMessageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSendMessageClicked();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        Bundle bundle = getArguments();
        assert bundle != null;
        Map<String, String> headers = (HashMap<String, String>) bundle.get("headers");
        String username = (String) bundle.get("username");
        String URL = "https://crossdress.herokuapp.com/messages/get/all/users/" + username;
        OurGsonRequest<UserList> request = new OurGsonRequest<>(Request.Method.GET, URL, UserList.class, headers, new Response.Listener<UserList>() {
            @Override
            public void onResponse(UserList response) {
                List<User> userList = response.getUserList();
                ArrayAdapter<User> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_list_item_1,
                        userList);
                setListAdapter(adapter);
                ListView listView = getListView();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        User user = (User) parent.getAdapter().getItem(position);
                        mListener.onMessageClicked(user.getUsername());
                    }
                });
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
    public interface OnFragmentInteractionListener {
        void onMessageClicked(String username);
        void onSendMessageClicked();
    }
}
