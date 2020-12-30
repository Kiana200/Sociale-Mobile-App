package com.julor200.crossdress.adapters.recyclerview_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Review;

import java.util.List;

/**
 * Adapter used for listView containing a list of Reviews.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private final List<Review> mDataset;
    private final RecyclerViewClickListener mListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public View view;
        private RecyclerViewClickListener mListener;
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

    public ReviewAdapter(List<Review> myDataset, RecyclerViewClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
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
        String review = mDataset.get(position).toString();
        tw.setText(review);
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Review getItem(int position){
        return mDataset.get(position);
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}