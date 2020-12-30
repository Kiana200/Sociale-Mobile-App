package com.julor200.crossdress.fragments.profile_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.adapters.recyclerview_adapters.PostAdapter;
import com.julor200.crossdress.R;
import com.julor200.crossdress.java_beans.Post;
import com.julor200.crossdress.java_beans.PostList;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Used to display all posts by a specific user from ProfileFragment.
 *  * Activities that contain this fragment must implement the
 *  * {@link UserPostFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class UserPostFragment extends Fragment {

    private RecyclerView recyclerView;
    private int latestClickedPosition;
    private int clicks;
    private Map<String, String> headers;
    private OnFragmentInteractionListener mListener;

    public UserPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        //Initialise all fields (Except lastClickedPosition and clicks, they do not need to be
        // initialised here
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Bundle bundle = getArguments();
        assert bundle != null;
        PostList postList = (PostList) bundle.get("posts");
        headers = (Map<String, String>) bundle.get("headers");
        assert postList != null;
        List<Post> posts = postList.getPostList();
        PostAdapter.RecyclerViewClickListener listener = new PostAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                deletePost(position);
            }
        };
        PostAdapter postAdapter = new PostAdapter(posts, listener);
        recyclerView.setAdapter(postAdapter);
        return view;
    }

    private void deletePost(int id){
        if(id == latestClickedPosition && clicks == 1){ //Double click, delete post
            PostAdapter adapter = (PostAdapter) recyclerView.getAdapter();
            assert adapter != null;
            Post post = adapter.getItem(id);
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.DELETE,
                    "https://crossdress.herokuapp.com/delete/post/" + post.getId(), String.class,
                    headers,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mListener.onPostDeleted();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(request);
        }
        else{ //Either other item clicked or latestPositionClicked == null, i.e. nothing clicked
            // before
            latestClickedPosition = id;
            clicks = 1;
            Toast toast = Toast.makeText(getActivity(), getString(R.string.double_click_to_remove), Toast.LENGTH_SHORT);
            toast.show();
        }
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
        void onPostDeleted();

    }
}

