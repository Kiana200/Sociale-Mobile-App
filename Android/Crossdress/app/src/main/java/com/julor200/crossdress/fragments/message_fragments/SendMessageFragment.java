package com.julor200.crossdress.fragments.message_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.julor200.crossdress.R;

/**
 * Fragment for sending messages.
 * A simple {@link Fragment} subclass.
 *  * Activities that contain this fragment must implement the
 *  * {@link SendMessageFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class SendMessageFragment extends Fragment {
    private EditText receiver;
    private TextView twReceiver;
    private EditText message;

    private OnFragmentInteractionListener mListener;

    public SendMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        View view;
        if(bundle != null){ //Clicked send from MessageFragment, set receiver to user in
            // MessageFragment
            view = inflater.inflate(R.layout.fragment_send_message_with_text_view, container, false);
            twReceiver = view.findViewById(R.id.twReceiver);
            String receiverString = bundle.getString("receiver");
            twReceiver.setText(receiverString);
        }
        else{ //Clicked send from MessageMenuFragment, no user predetermined as receiver
            view = inflater.inflate(R.layout.fragment_send_message, container, false);
            receiver = view.findViewById(R.id.receiver);
        }

        message = view.findViewById(R.id.reviewText);
        Button sendButton = view.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClicked();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SendMessageFragment.OnFragmentInteractionListener) {
            mListener = (SendMessageFragment.OnFragmentInteractionListener) context;
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

    private void sendClicked() {
        CharSequence receiverText;
        if(receiver != null){
            receiverText = receiver.getText();
        }
        else{
            receiverText = twReceiver.getText();
        }
        CharSequence messageText = message.getText();
        message.setText("");
        mListener.onSendClicked(receiverText.toString(), messageText.toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onSendClicked(String receiver, String message);
    }
}
