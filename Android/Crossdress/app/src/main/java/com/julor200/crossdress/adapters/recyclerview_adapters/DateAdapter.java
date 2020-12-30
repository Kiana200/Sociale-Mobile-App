package com.julor200.crossdress.adapters.recyclerview_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Date;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {
    private final List<Date> mDataset;
    private final RecyclerViewClickListener mListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public final View view;
        private final RecyclerViewClickListener mListener;
        public MyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            view = v;
            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DateAdapter(List<Date> myDataset, RecyclerViewClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public DateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_textview, parent, false);
        return new MyViewHolder(v, mListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Get element at this position and replace the contents of the view with that element
        TextView tw = holder.view.findViewById(R.id.singleTextView);
        tw.setText(mDataset.get(position).getDate());
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Date getItem(int position){
        return mDataset.get(position);
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}