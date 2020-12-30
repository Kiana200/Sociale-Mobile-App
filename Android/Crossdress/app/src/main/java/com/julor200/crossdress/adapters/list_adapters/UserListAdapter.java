package com.julor200.crossdress.adapters.list_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.User;

import java.util.List;

/**
 * Adapter used for lists containing Users. Used before in
 * MessageMenuFragment. This was used while testing but was later replaced by ArrayAdapter<User>
 * and then a toString()-method in User that returns the username.
 */
public class UserListAdapter extends BaseAdapter {
    private final List<User> result;
    private static LayoutInflater inflater=null;

    public UserListAdapter(Context context, List<User> userList) {
        result=userList;
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
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.simple_textview, null);
        holder.tv = rowView.findViewById(R.id.singleTextView);
        holder.tv.setText(result.get(position).getUsername()); //Display username in the list items
        return rowView;
    }

}
