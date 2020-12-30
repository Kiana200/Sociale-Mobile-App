package com.julor200.crossdress.fragments.message_fragments;

import android.content.Context;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Message;
import com.julor200.crossdress.java_beans.MessageList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fragment for displaying messages in a conversation between users.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MessageFragment extends androidx.fragment.app.ListFragment {

    private OnFragmentInteractionListener mListener;
    private String username1;
    private String username2;
    private Map<String, String> headers;
    private int timesClicked;
    private long messageId;
    private List<Message> messageList;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        final View view = inflater.inflate(R.layout.list_with_send_button, container,false);
        //Set listeners on button
        Button sendButton = view.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSendMessageClicked(username2);
            }
        });
        Bundle bundle = getArguments();
        if(bundle != null) {
            username1 = bundle.getString("username1");
            username2 = bundle.getString("username2");
            headers = (HashMap<String, String>) bundle.getSerializable("headers");
            String URL = "https://crossdress.herokuapp.com/get/all/messages/" + username1 + "/" + username2;
            OurGsonRequest<MessageList> request = new OurGsonRequest<>(Request.Method.GET, URL, MessageList.class, headers, new Response.Listener<MessageList>() {
                @Override
                public void onResponse(MessageList response) {
                    messageList = response.getMessageList();
                    ArrayAdapter<Message> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                            android.R.layout.simple_list_item_1,
                            messageList);
                    setListAdapter(adapter);
                    ListView listView = getListView();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Message message = messageList.get((int)id);
                            deleteMessage(message.getId());
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
        }

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

    private void deleteMessage(long id){
        if(id == messageId && timesClicked == 1){ //Double clicked, delete message
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.DELETE,
                    "https://crossdress.herokuapp.com/delete/message/" + id, String.class,
                    headers,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mListener.messageDeleted();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(request);
        }
        else{ //Not same message clicked as last time
            messageId = id;
            timesClicked = 1; //Clicked once
            Toast toast = Toast.makeText(getActivity(), getString(R.string.double_click_to_remove), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onSendMessageClicked(String receiver);
        void messageDeleted();


    }
}
