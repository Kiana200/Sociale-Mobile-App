package com.julor200.crossdress.adapters.recyclerview_adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Post;

import java.util.List;

/**
 * Adapter used for listView containing a list of Posts. Used in StartPageFragment and
 * UserPostFragment.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private final List<Post> mDataset;
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

    public PostAdapter(List<Post> myDataset, RecyclerViewClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_recycle_row, parent, false);
        return new MyViewHolder(v, mListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Get element at this position and replace the contents of the view with that element
        TextView tw = holder.view.findViewById(R.id.startPageRubric);
        tw.setText(mDataset.get(position).getRubric());
        ImageView imageView = holder.view.findViewById(R.id.startPageImageView);
        String photoString = mDataset.get(position).getPhoto();
        byte[] photo = Base64.decode(photoString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        imageView.setImageBitmap(bitmap);
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Post getItem(int position){
        return mDataset.get(position);
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}