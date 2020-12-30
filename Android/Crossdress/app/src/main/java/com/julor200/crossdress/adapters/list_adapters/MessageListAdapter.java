package com.julor200.crossdress.adapters.list_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Message;

import java.util.List;

/**
 * This is the adapter used for lists containing messages. Used before in MessageFragment.
 * This was used while testing, then we replaced it by ArrayAdapter<Message>
 * and a toString() method in Message that returns the message as well as the sender.
 */

public class MessageListAdapter extends BaseAdapter{
    private final List<Message> result;
    private static LayoutInflater inflater=null;

    public MessageListAdapter(Context context, List<Message> messageList) {
        result = messageList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return result.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.simple_textview, null);
        holder.tv = rowView.findViewById(R.id.singleTextView);
        String message = result.get(position).getMessage();
        String sender = result.get(position).getSender();
        String textViewText = sender + ": " + message;
        holder.tv.setText(textViewText);
        return rowView;
    }

}
