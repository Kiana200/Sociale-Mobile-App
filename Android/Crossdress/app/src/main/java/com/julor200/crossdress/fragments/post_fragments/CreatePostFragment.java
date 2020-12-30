package com.julor200.crossdress.fragments.post_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.julor200.crossdress.R;

/**
 * This is the fragment for creating posts.
 * Activities that contain this fragment must implement the
 * {@link CreatePostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CreatePostFragment extends Fragment {
    private ImageView imageView;
    private EditText rubric, category, size, description;


    private OnFragmentInteractionListener mListener;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        //Initialise fields
        rubric = view.findViewById(R.id.editText_create_rubric);
        imageView = view.findViewById(R.id.imageView_create_post);
        category = view.findViewById(R.id.choose_category);
        size = view.findViewById(R.id.choose_size);
        description = view.findViewById(R.id.add_description);
        //Set listener on button
        Button createButton = view.findViewById(R.id.create_post_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence categoryText = category.getText();
                CharSequence rubricText = rubric.getText();
                CharSequence sizeText = size.getText();
                CharSequence descriptionText = description.getText();
                if(!rubricText.toString().equals("") && !categoryText.toString().equals("") &&
                        !sizeText.toString().equals("") && !descriptionText.equals("") &&
                        imageView != null){ //imageView != null should be replaced by
                    // imageView.getDrawable() != null to work
                    mListener.createPost(rubricText.toString(), imageView, categoryText.toString(), sizeText.toString(), descriptionText.toString());
                }
                else{
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.information_missing), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startCamera();
            }
        });
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
        void createPost(String rubric, ImageView imageView, String category, String size, String description);
        void startCamera();
    }
}