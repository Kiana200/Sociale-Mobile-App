package com.julor200.crossdress.fragments.filter_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.julor200.crossdress.R;


/**
 * This fragment is used to filter posts in the start page
 * This buttons are made to make it easier for the user to find the item they want.
 * We have one button where the user can choose category and one for size.
 *  * Activities that contain this fragment must implement the
 *  * {@link FilterFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class FilterFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        TextView chosenCategory = view.findViewById(R.id.chosenCategory);
        TextView chosenSize = view.findViewById(R.id.chosenSize);
        Bundle arguments = getArguments();
        if(arguments != null){
            //Used to set textviews to the chosen category and size
                String category = arguments.getString("Category");
                chosenCategory.setText(category);
                String size = (String) arguments.get("Size");
                chosenSize.setText(size);
        }
        //Set Listeners on buttons
        Button categoryButton = view.findViewById(R.id.chooseCategory);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChooseCategoryClicked();
            }
        });
        Button sizeButton = view.findViewById(R.id.chooseSize);
        sizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChooseSizeClicked();
            }
        });
        Button filterButton = view.findViewById(R.id.filterFragmentFilterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFilterButtonClicked();
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
        void onFilterButtonClicked();
        void onChooseCategoryClicked();
        void onChooseSizeClicked();
    }
}
