package com.julor200.crossdress.fragments.post_fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Post;

/**
 * This is the fragment for posts.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */

public class PostFragment extends Fragment {
    private TextView title;
    private int id;
    private String category;
    private String size;

    private OnFragmentInteractionListener mListener;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        Button bookButton = view.findViewById(R.id.post_book_button);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence rubric = title.getText();
                mListener.onPostBookButtonClicked(rubric.toString(), id, category, size);
            }
        });
        Button reviewButton = view.findViewById(R.id.postReviewsButton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPostReviewsButtonClicked(id);
            }
        });
        Bundle bundle = getArguments();
        assert bundle != null;

        // Fill all views with data
        Post post = (Post) bundle.get("Post");
        title = view.findViewById(R.id.post_rubric);
        assert post != null;
        id = post.getId();
        title.setText(post.getRubric());
        TextView postedBy = view.findViewById(R.id.post_by);
        postedBy.setText(post.getPosted_by());
        size = post.getSize();
        category = post.getCategory();
        TextView description = view.findViewById(R.id.post_description);
        description.setText(post.getDescription());
        TextView distance = view.findViewById(R.id.post_distance);
        double currentDistance = bundle.getDouble("distance");
        String distanceString = currentDistance + " " + getString(R.string.kilometres_away);
        distance.setText(distanceString);
        ImageView imageView = view.findViewById(R.id.postImageView);
        //Convert photo data back to an image
        String imageAsString = post.getPhoto();
        byte[] photo = Base64.decode(imageAsString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        imageView.setImageBitmap(bitmap);
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
        void onPostBookButtonClicked(String rubric, int id, String category, String size);
        void onPostReviewsButtonClicked(int id);
    }
}
