package com.julor200.crossdress.fragments.app_start;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * This is the startPage, after logging in this is the page you see.
 * It consists of five buttons and the image flow.
 *  * Activities that contain this fragment must implement the
 *  * {@link StartPageFragment.OnFragmentInteractionListener} interface
 *  * to handle interaction events.
 */
public class StartPageFragment extends Fragment {
    private RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;

    public StartPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_page, container, false);
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        //Set listeners for all buttons
        Button filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFilterClicked();
            }
        });
        Button settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSettingsClicked();
            }
        });
        Button messagesButton = view.findViewById(R.id.messages_button);
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMessagesClicked();
            }
        });
        Button profileButton = view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onProfileClicked();
            }
        });
        Button createPostButton = view.findViewById(R.id.create_post_start_page);
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCreatePostClicked();
            }
        });
        //Initialise the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        assert bundle != null;
        Set keys = bundle.keySet();
        if(keys.contains("headers")){ //Need to get post data from web
            String getPostsURL = "https://crossdress.herokuapp.com/get/all/posts";
            Map<String, String> headers = (HashMap<String, String>) bundle.get("headers");
            OurGsonRequest<PostList> postRequest = new OurGsonRequest<>(Request.Method.GET, getPostsURL, PostList.class, headers, new Response.Listener<PostList>() {
                @Override
                public void onResponse(PostList response) {
                    List<Post> posts = response.getPostList();
                    PostAdapter.RecyclerViewClickListener listener = new PostAdapter.RecyclerViewClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            mListener.onPostClicked(position);
                        }
                    };
                    PostAdapter postAdapter = new PostAdapter(posts, listener);
                    recyclerView.setAdapter(postAdapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(postRequest);
        }
        else { //Otherwise all data needed in bundle
            PostList postList = (PostList) bundle.get("data");
            assert postList != null;
            List<Post> posts = postList.getPostList();
            PostAdapter.RecyclerViewClickListener listener = new PostAdapter.RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    mListener.onPostClicked(position);
                }
            };
            PostAdapter postAdapter = new PostAdapter(posts, listener);
            recyclerView.setAdapter(postAdapter);
        }

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
        void onFilterClicked();
        void onSettingsClicked();
        void onMessagesClicked();
        void onProfileClicked();
        void onCreatePostClicked();
        void onPostClicked(int position);
    }
}