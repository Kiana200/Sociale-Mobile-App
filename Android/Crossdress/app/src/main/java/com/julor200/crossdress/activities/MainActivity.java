package com.julor200.crossdress.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.julor200.crossdress.adapters.recyclerview_adapters.PostAdapter;
import com.julor200.crossdress.fragments.app_start.LoginFragment;
import com.julor200.crossdress.fragments.app_start.RegisterFragment;
import com.julor200.crossdress.fragments.app_start.StartPageFragment;
import com.julor200.crossdress.fragments.booking_fragments.BookingFragment;
import com.julor200.crossdress.fragments.booking_fragments.CalendarFragment;
import com.julor200.crossdress.fragments.filter_fragments.CategoryFilterFragment;
import com.julor200.crossdress.fragments.settings_fragments.ChangeEmailFragment;
import com.julor200.crossdress.fragments.settings_fragments.ChangeUsernameFragment;
import com.julor200.crossdress.fragments.post_fragments.CreatePostFragment;
import com.julor200.crossdress.fragments.review_fragments.CreateReviewFragment;
import com.julor200.crossdress.fragments.filter_fragments.FilterFragment;
import com.julor200.crossdress.fragments.message_fragments.MessageFragment;
import com.julor200.crossdress.fragments.message_fragments.MessageMenuFragment;
import com.julor200.crossdress.requests.OurGsonRequest;
import com.julor200.crossdress.fragments.post_fragments.PostFragment;
import com.julor200.crossdress.fragments.post_fragments.PostReviewsFragment;
import com.julor200.crossdress.fragments.profile_fragments.ProfileFragment;
import com.julor200.crossdress.R;
import com.julor200.crossdress.fragments.message_fragments.SendMessageFragment;
import com.julor200.crossdress.fragments.settings_fragments.SettingsFragment;
import com.julor200.crossdress.fragments.filter_fragments.SizeFilterFragment;
import com.julor200.crossdress.fragments.profile_fragments.UserBookingsFragment;
import com.julor200.crossdress.fragments.profile_fragments.UserPostFragment;
import com.julor200.crossdress.fragments.profile_fragments.UserReviewsFragment;
import com.julor200.crossdress.java_beans.DateList;
import com.julor200.crossdress.java_beans.MessageList;
import com.julor200.crossdress.java_beans.Post;
import com.julor200.crossdress.java_beans.PostList;
import com.julor200.crossdress.java_beans.ReviewList;
import com.julor200.crossdress.java_beans.Token;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity which handles all switching between fragments as well as handling of most interactions
 * in fragments.
 */
public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        StartPageFragment.OnFragmentInteractionListener,
        BookingFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener,
        PostFragment.OnFragmentInteractionListener,
        CreatePostFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        FilterFragment.OnFragmentInteractionListener,
        CategoryFilterFragment.OnFragmentInteractionListener,
        SizeFilterFragment.OnFragmentInteractionListener,
        MessageMenuFragment.OnFragmentInteractionListener,
        SendMessageFragment.OnFragmentInteractionListener,
        MessageFragment.OnFragmentInteractionListener,
        ChangeEmailFragment.OnFragmentInteractionListener,
        ChangeUsernameFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        UserPostFragment.OnFragmentInteractionListener,
        UserReviewsFragment.OnFragmentInteractionListener,
        PostReviewsFragment.OnFragmentInteractionListener,
        CreateReviewFragment.OnFragmentInteractionListener,
        UserBookingsFragment.OnFragmentInteractionListener {

    private int wrongPasswordCounter = 5;
    private RequestQueue queue;
    private final Map<String, String> headers = new HashMap<>();
    private String username;
    private java.util.Date date;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String filterCategory, filterSize;
    private final static String START_URL = "https://crossdress.herokuapp.com/";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filterSize = getString(R.string.chosen_size);
        filterCategory = getString(R.string.chosen_category); //Set to default values, i.e. nothing chosen
        LoginFragment loginFragment = new LoginFragment();
        queue = Volley.newRequestQueue(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            int locationRequestCode = 1000;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        }
        //Otherwise permission already granted
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.Placeholder, loginFragment).commit();
    }

    /**
     * Called when requesting permission to use location
     * @param requestCode the request code
     * @param permissions used in super method
     * @param grantResults used in super method
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Called when a photo is taken
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data used to get the image bitmap
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView createPostImageView = findViewById(R.id.imageView_create_post);
            createPostImageView.setImageBitmap(imageBitmap);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude(); //Use location for photo taken to tag post
                        wayLongitude = location.getLongitude();
                    }
                }
            });
        }
    }

    //Methods reachable from LoginFragment:

    /**
     * When the login button is clicked in the LoginFragment
     * @param inputUsername the username written
     * @param password the password written
     */
    @Override
    public void onLoginClicked(final String inputUsername, String password) {
        //First check that account is not locked, then proceed to login if not locked
        if (wrongPasswordCounter == 0) {
            //Lock account for 5 minutes
            if (date == null) { //If not already locked, set date
                date = new java.util.Date();
                return;
            } else { //Else check time passed
                long lockedTime = date.getTime(); //ms passed since account locked
                java.util.Date currentDate = new java.util.Date();
                long currentTime = currentDate.getTime();
                long timeDifference = currentTime - lockedTime;
                int TIME_LIMIT = 1000 * 60 * 5; //Five minutes
                if (timeDifference <= TIME_LIMIT) { // Five minutes have not passed
                    Toast toast = Toast.makeText(MainActivity.this, getString(R.string.account_locked), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                } else { //Else, reset counter and date and continue with login
                    wrongPasswordCounter = 5;
                    date = null;
                }
            }
        }
        // Log in
        Map<String, String> params = new HashMap<>();
        params.put("username", inputUsername);
        params.put("password", password);
        String requestURL = START_URL + "user/login";
        OurGsonRequest<Token> request = new OurGsonRequest<>(Request.Method.POST, requestURL, Token.class, new HashMap<String, String>(), new Response.Listener<Token>() {
            @Override
            public void onResponse(Token response) {
                String token = response.getToken();
                headers.put("Authorization", "Bearer " + token);
                username = inputUsername;
                goToStartPage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse == null){ //Timeout error
                    Toast toast = Toast.makeText(MainActivity.this, getString(R.string.no_server_contact), Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(error.networkResponse.statusCode == 404){
                    Toast toast = Toast.makeText(MainActivity.this, getString(R.string.wrong_username_or_missing_data), Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    wrongPasswordCounter--; //Wrong password, one try used.
                    if (wrongPasswordCounter > 0) {
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.wrong_password) + " " + wrongPasswordCounter + " " +  getString(R.string.tries_left), Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.wrong_password_locked), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        }, params);
        queue.add(request);
    }

    /**
     * Switches to the start page, used both when logging in and when we otherwise need to go back
     * to the start page
     */
    private void goToStartPage() {
        String getPostsURL = START_URL + "get/all/posts";
        OurGsonRequest<PostList> postRequest = new OurGsonRequest<>(Request.Method.GET, getPostsURL, PostList.class, headers, new Response.Listener<PostList>() {
            @Override
            public void onResponse(PostList response) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", response); // Posts to be displayed
                StartPageFragment startPageFragment = new StartPageFragment();
                startPageFragment.setArguments(bundle);
                emptyBackStack();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Placeholder, startPageFragment).commit(); //Do not add to backstack since we do not want to get back to login page unless we log out.
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(postRequest);
    }

    /**
     * When you click the register button in the LoginFragment
     */
    @Override
    public void onRegisterClicked() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, registerFragment).commit();
    }

    //Methods reachable from RegisterFragment:

    /**
     * When you click the register button in the RegisterFragment
     * @param email the new user's email
     * @param username the new user's username
     * @param password the new user's password
     */
    @Override
    public void register(String email, String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        params.put("password", password);
        String requestURL = START_URL + "create/user";
        Map<String, String> requestHeaders = new HashMap<>();
        OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.POST, requestURL, String.class, requestHeaders, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                emptyBackStack();
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.Placeholder, loginFragment).commit(); // Back to login page
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this,
                        getString(R.string.error_try_again), Toast.LENGTH_LONG);
                toast.show();
            }
        }, params);
        queue.add(request);
    }

    //Methods reachable from StartPageFragment:

    /**
     * When you click the filter button on the start page
     */
    @Override
    public void onFilterClicked() {
        FilterFragment filterFragment = new FilterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, filterFragment).commit();
    }

    /**
     * When you click the settings button on the start page
     */
    @Override
    public void onSettingsClicked() {
        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, settingsFragment).commit();
    }

    /**
     * When you click the message button on the start page
     */
    @Override
    public void onMessagesClicked() {
        MessageMenuFragment messageMenuFragment = new MessageMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("headers", (Serializable) headers);
        bundle.putString("username", username);
        messageMenuFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, messageMenuFragment).commit();
    }

    /**
     * When you click the profile button on the start page
     */
    @Override
    public void onProfileClicked() {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        profileFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, profileFragment).commit();
    }

    /**
     *When you click the create post button on the start page
     */
    @Override
    public void onCreatePostClicked(){
        CreatePostFragment createPostFragment = new CreatePostFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, createPostFragment).commit();
    }

    /**
     * When you click on a Post on the start page
     * @param position the position in the list clicked on
     */
    @Override
    public void onPostClicked(final int position) {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    wayLatitude = location.getLatitude(); //Send current location to calculate distance to Post location
                    wayLongitude = location.getLongitude();
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    PostAdapter adapter = (PostAdapter) recyclerView.getAdapter();
                    assert adapter != null;
                    Post post = adapter.getItem(position);
                    PostFragment postFragment = new PostFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Post", post);
                    // Calculate distance between the user's and the Post's location.
                    double postLatitude = Double.parseDouble(post.getLatitude());
                    double postLongitude = Double.parseDouble(post.getLongitude());
                    Location postLocation = new Location(""); // Provider name unnecessary
                    postLocation.setLatitude(postLatitude);
                    postLocation.setLongitude(postLongitude);
                    Location userLocation =new Location("");
                    userLocation.setLatitude(wayLatitude);
                    userLocation.setLongitude(wayLongitude);
                    float distanceInMeters =  userLocation.distanceTo(postLocation);
                    // Convert to kilometers with one decimal point
                    double distanceInKilometers = Math.round(distanceInMeters /1000 * 10)/10.0;
                    bundle.putDouble("distance", distanceInKilometers);
                    postFragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.Placeholder, postFragment).commit();
                }
            }
        });
    }

    //Methods reachable from PostFragment:

    /**
     * When you press the reviews button in a Post
     * @param id the Post's id
     */
    @Override
    public void onPostReviewsButtonClicked(int id) {
        PostReviewsFragment postReviewsFragment = new PostReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("headers", (Serializable) headers);
        bundle.putInt("id", id);
        postReviewsFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, postReviewsFragment).commit();
    }

    /**
     * When you press the book button in a Post
     * @param rubric the Post's rubric
     * @param id the Post's id
     * @param category the Post's category
     * @param size the Post's size
     */
    @Override
    public void onPostBookButtonClicked(String rubric, int id, String category, String size) {
        CalendarFragment calendarFragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("rubric", rubric);
        bundle.putInt("id", id);
        bundle.putString("category", category);
        bundle.putString("size", size);
        calendarFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, calendarFragment).commit();
    }

    //Methods reachable from PostReviewsFragment:

    /**
     * When the write review button in PostReviewsFragment is pressed.
     * @param id the post's id
     */
    @Override
    public void onWriteReviewClicked(int id) {
        CreateReviewFragment createReviewFragment = new CreateReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putSerializable("headers", (Serializable) headers);
        bundle.putString("username", username);
        bundle.putSerializable("headers", (Serializable) headers);
        createReviewFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, createReviewFragment).commit();
    }

    //Methods reachable from CalendarFragment:

    /**
     * When a date in the calendar is clicked
     * @param bundle bundle to be passed on to the BookingFragment
     */
    @Override
    public void onDateClicked(Bundle bundle) {
        BookingFragment bookingFragment = new BookingFragment();
        bookingFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, bookingFragment).commit();
    }

    //Methods reachable from CreateReviewFragment:

    /**
     * Shows a message when a review has been created in CreateReviewFragment.
     */
    @Override
    public void onReviewCreated() {
        Toast toast = Toast.makeText(this, getString(R.string.review_created), Toast.LENGTH_LONG);
        toast.show();    }

    //Methods reachable from BookingFragment:

    /**
     * Called when the user presses the book button in BookingFragment
     * @param date the date to book
     * @param id the post's id
     */
    @Override
    public void onBookClicked(String date, int id) {
        Map<String, String> params = new HashMap<>();
        params.put("date", date);
        params.put("username", username);
        params.put("post_id", Integer.toString(id));
        OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.POST, START_URL + "book", String.class, headers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                goToStartPage();
                Toast toast = Toast.makeText(MainActivity.this,
                        getString(R.string.item_booked), Toast.LENGTH_LONG);
                toast.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }, params);
        queue.add(request);
    }

    //Methods reachable from FilterFragment:

    /**
     * Called when filter button is pressed in FilterFragment.
     * Gets the filtered posts and goes back to the start page, now displaying only the filtered posts.
     */
    @Override
    public void onFilterButtonClicked() {
        Map<String, String> params = new HashMap<>();
        if(!filterSize.equals(getString(R.string.chosen_size))){
            params.put("size", filterSize);
        }
        if(!filterCategory.equals(getString(R.string.chosen_category))){
            params.put("category", filterCategory);
        }
        OurGsonRequest<PostList> request = new OurGsonRequest<>(Request.Method.POST, START_URL + "filter", PostList.class, headers, new Response.Listener<PostList>(){
            @Override
            public void onResponse(PostList response) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", response); //Filtered posts
                emptyBackStack();
                StartPageFragment startPageFragment = new StartPageFragment();
                startPageFragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.Placeholder, startPageFragment).commit();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.error_try_again), Toast.LENGTH_LONG);
                toast.show();
            }
        }, params);
        queue.add(request);
        filterCategory = getString(R.string.chosen_category);
        filterSize = getString(R.string.chosen_size);
    }

    /**
     * Called when choose category button is clicked in FilterFragment, changes fragment
     * to display all categories
     */
    @Override
    public void onChooseCategoryClicked() {
        CategoryFilterFragment categoryFilterFragment = new CategoryFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("headers", (Serializable)headers);
        categoryFilterFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, categoryFilterFragment).commit();
    }

    /**
     * Called when choose size button is clicked in FilterFragment, changes fragment
     * to display all sizes
     */
    @Override
    public void onChooseSizeClicked() {
        SizeFilterFragment sizeFilterFragment = new SizeFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("headers", (Serializable) headers);
        sizeFilterFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, sizeFilterFragment).commit();
    }

    //Methods reachable from CategoryFilterFragment:

    /**
     * Called when a category in the list in CategoryFilterFragment is chosen, changes the
     * category field in MainActivity and goes back to FilterFragment
     * @param category the category clicked on
     */
    @Override
    public void onCategoryChosen(String category) {
        filterCategory = category;
        Bundle bundle = new Bundle();
        bundle.putString("Size", filterSize);
        bundle.putString("Category", filterCategory);
        FilterFragment filterFragment = new FilterFragment();
        filterFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, filterFragment).commit();
    }

    //Methods reachable from SizeFilterFragment:

    /**
     * Called when a size in the list in SizeFilterFragment is chosen, changes the size field in
     * MainActivity and goes back to FilterFragment
     * @param size the size clicked on
     */
    @Override
    public void onSizeChosen(String size) {
        filterSize = size;
        Bundle bundle = new Bundle();
        bundle.putString("Size", filterSize);
        bundle.putString("Category", filterCategory);
        FilterFragment filterFragment = new FilterFragment();
        filterFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, filterFragment).commit();
    }

    //Methods reachable from SettingsFragment:

    /**
     * Goes to fragment for changing the user's username. Not used since it does not
     * work on the server if the user has written reviews, read messages etc.
     */
    @Override
    public void onChangeUsernameClicked() {
        //Not used due to insufficient time to fix dependencies in server implementation
        ChangeUsernameFragment changeUsernameFragment = new ChangeUsernameFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, changeUsernameFragment).commit();
    }

    /**
     * Called when the change email button is clicked in SettingsFragment.
     */
    @Override
    public void onChangeEmailClicked() {
        ChangeEmailFragment changeEmailFragment = new ChangeEmailFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, changeEmailFragment).commit();
    }

    /**
     * Called when the log out button is pressed in SettingsFragment.Logs out the user and goes
     * back to the login page
     */
    @Override
    public void onLogOutClicked() {
        Map<String, String> params = new HashMap<>();
        OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.POST,
                START_URL + "user/logout", String.class, headers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                emptyBackStack();
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.Placeholder, loginFragment).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }, params);
        queue.add(request);
    }

    //Methods reachable from ChangeUsernameFragment:

    /**
     * Changes the user's username. Not used since there are dependencies to username on server side
     * causing conflicts we did not have time to resolve
     * @param newUsername username to change to
     */
    @Override
    public void onChangeUsernameButtonClicked(final String newUsername) {
        if(!newUsername.isEmpty()){
            Map<String, String> params = new HashMap<>();
            params.put("oldUsername", username);
            params.put("newUsername", newUsername);
            OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.POST,
                    START_URL + "change/username", String.class, headers,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            username = newUsername;
                            SettingsFragment settingsFragment = new SettingsFragment();
                            getSupportFragmentManager().popBackStack();
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.Placeholder, settingsFragment).commit();
                            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.username_changed), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }, params);
            queue.add(request);
        }
    }

    //Methods reachable from ChangeEmailFragment:

    /**
     * Called when the change email button is pressed in ChangeEmailFragment.
     * Changes a user's email.
     * @param newEmail email to change to
     */
    @Override
    public void onChangeEmailButtonClicked(String newEmail) {
        if(!newEmail.isEmpty()) { //We do not want to set email to empty string
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("newEmail", newEmail);
            OurGsonRequest<String> request = new OurGsonRequest<>(Request.Method.POST,
                    START_URL + "change/email", String.class, headers,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.email_changed), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }, params);
            queue.add(request);
        }
    }

    //Methods reachable from MessageMenuFragment:

    /**
     * Takes the user to SendMessageFragment when pressing the send message button in
     * MessageMenuFragment.
     */
    @Override
    public void onSendMessageClicked() {
        SendMessageFragment sendMessageFragment = new SendMessageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, sendMessageFragment).commit();
    }

    /**
     * Switches to MessageFragment when the user clicks on a username in the message menu.
     * @param username2 the user besides the logged in user who is
     * in the conversation.
     */
    @Override
    public void onMessageClicked(String username2) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username1", username);
        bundle.putString("username2", username2);
        bundle.putSerializable("headers", (Serializable) headers);
        messageFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, messageFragment).commit();
    }

    //Methods reachable from SendMessageFragment:

    /**
     * Tries to send a message when the send button in SendMessageFragment is pressed.
     * @param receiver The user to receive the message
     * @param message The message to be sent
     */
    @Override
    public void onSendClicked(String receiver, String message) {
        Map<String, String> params = new HashMap<>();
        params.put("sender", username);
        params.put("receiver", receiver);
        params.put("message", message);
        OurGsonRequest<MessageList> request = new OurGsonRequest<>(Request.Method.POST,
                START_URL + "messages/send", MessageList.class, headers, new Response.Listener<MessageList>() {
            @Override
            public void onResponse(MessageList response) {
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.message_sent), Toast.LENGTH_SHORT);
                toast.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        }, params);
        queue.add(request);
    }

    //Methods reachable from MessageFragment:

    /**
     * Goes to SendMessageFragment when the send message button is pressed from MessageFragment.
     * @param receiver the user to receive the message
     */
    @Override
    public void onSendMessageClicked(String receiver) {
        SendMessageFragment sendMessageFragment = new SendMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("receiver", receiver);
        sendMessageFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.Placeholder, sendMessageFragment).commit();
    }

    /**
     * Shows Toast on screen when a message is deleted and goes back to the start page.
     */
    @Override
    public void messageDeleted() {
        goToStartPage();
        Toast toast = Toast.makeText(this, getString(R.string.message_deleted), Toast.LENGTH_LONG);
        toast.show();
    }

    //Methods reachable from ProfileFragment:

    /**
     * Switches to UserPostFragment which displays all the posts created by the user.
     */
    @Override
    public void onGetPostsClicked() {
        OurGsonRequest<PostList> request = new OurGsonRequest<>(Request.Method.GET,
                START_URL + "get/all/posts/user/" + username, PostList.class, headers,
                new Response.Listener<PostList>() {
                    @Override
                    public void onResponse(PostList response) {
                        UserPostFragment userPostFragment = new UserPostFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("posts", response);
                        bundle.putSerializable("headers", (Serializable) headers);
                        userPostFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.Placeholder, userPostFragment).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        queue.add(request);
    }

    /**
     * Takes the user to UserReviewsFragment which displays all Reviews written by the user.
     */
    @Override
    public void onGetReviewsClicked() {
        OurGsonRequest<ReviewList> request = new OurGsonRequest<>(Request.Method.GET,
                START_URL + "get/all/reviews/user/" + username, ReviewList.class, headers,
                new Response.Listener<ReviewList>() {
                    @Override
                    public void onResponse(ReviewList response) {
                        UserReviewsFragment userReviewsFragment = new UserReviewsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("reviews", response);
                        bundle.putSerializable("headers", (Serializable) headers);
                        userReviewsFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.Placeholder, userReviewsFragment).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        queue.add(request);
    }

    /**
     * Takes the user to UserBookingsFragment which displays all dates booked by a user.
     */
    @Override
    public void onGetBookingsClicked() {
        OurGsonRequest<DateList> request = new OurGsonRequest<>(Request.Method.GET,
                START_URL + "get/all/bookings/" + username, DateList.class, headers,
                new Response.Listener<DateList>() {
                    @Override
                    public void onResponse(DateList response) {
                        UserBookingsFragment userBookingsFragment = new UserBookingsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dates", response);
                        bundle.putSerializable("headers", (Serializable) headers);
                        userBookingsFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.Placeholder, userBookingsFragment).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        queue.add(request);
    }

    //Methods reachable from UserPostFragment

    /**
     * Goes back to UserPostFragment when a Post is deleted.
     */
    @Override
    public void onPostDeleted() {
        OurGsonRequest<PostList> request = new OurGsonRequest<>(Request.Method.GET,
                START_URL + "get/all/posts/user/" + username, PostList.class, headers,
                new Response.Listener<PostList>() {
                    @Override
                    public void onResponse(PostList response) {
                        UserPostFragment userPostFragment = new UserPostFragment();
                        getSupportFragmentManager().popBackStack();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("posts", response);
                        bundle.putSerializable("headers", (Serializable) headers);
                        userPostFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.Placeholder, userPostFragment).commit();
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.post_deleted), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        queue.add(request);
    }

    //Methods reachable from UserReviewsFragment

    /**
     * Goes back to UserReviewsFragment when a Review is deleted.
     */
    @Override
    public void onReviewDeleted() {
        OurGsonRequest<ReviewList> request = new OurGsonRequest<>(Request.Method.GET,
                START_URL + "get/all/reviews/user/" + username, ReviewList.class, headers,
                new Response.Listener<ReviewList>() {
                    @Override
                    public void onResponse(ReviewList response) {
                        UserReviewsFragment userReviewsFragment = new UserReviewsFragment();
                        getSupportFragmentManager().popBackStack();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("reviews", response);
                        bundle.putSerializable("headers", (Serializable) headers);
                        userReviewsFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.Placeholder, userReviewsFragment).commit();
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.review_deleted), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        queue.add(request);
    }

    //Methods reachable from UserBookingsFragment:

    /**
     * Takes the user to UserBookingsFragment which displays all the dates the user has booked.
     */
    @Override
    public void onBookingCancelled() {
        OurGsonRequest<DateList> request = new OurGsonRequest<>(Request.Method.GET,
                START_URL + "get/all/bookings/" + username, DateList.class, headers,
                new Response.Listener<DateList>() {
                    @Override
                    public void onResponse(DateList response) {
                        UserBookingsFragment userBookingsFragment = new UserBookingsFragment();
                        getSupportFragmentManager().popBackStack();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dates", response);
                        bundle.putSerializable("headers", (Serializable) headers);
                        userBookingsFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.Placeholder, userBookingsFragment).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        queue.add(request);
        Toast toast = Toast.makeText(this, getString(R.string.booking_cancelled), Toast.LENGTH_LONG);
        toast.show();
    }

    //Methods reachable from CreatePostFragment:

    /**
     * Tries to create a post when the create post button in CreatePostFragment is pressed.
     * @param rubric The post's rubric
     * @param imageView The post's image
     * @param category The post's category
     * @param size The post's size
     * @param description The post's description
     */
    @Override
    public void createPost(String rubric, ImageView imageView, String category, String size, String description) {
        Map<String, String> params = new HashMap<>();
        //Convert image to string
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        String photo = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        //Put in needed parameters
        params.put("username", username);
        params.put("rubric", rubric);
        params.put("photo", photo);
        params.put("category", category);
        params.put("size", size);
        params.put("description", description);
        params.put("latitude", Double.toString(wayLatitude));
        params.put("longitude", Double.toString(wayLongitude));
        String requestURL = START_URL + "create/post";
        OurGsonRequest<Post> request = new OurGsonRequest<>(Request.Method.POST, requestURL, Post.class, headers, new Response.Listener<Post>() {
            @Override
            public void onResponse(Post response) {
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.post_created), Toast.LENGTH_LONG);
                toast.show();
                goToStartPage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.general_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }, params);
        queue.add(request);
    }

    /**
     * Calls method to start the camera
     */
    @Override
    public void startCamera(){
        dispatchTakePictureIntent();
    }

    //Camera methods:
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Empties the backstack to prevent user from going back to previous fragment
     */
    private void emptyBackStack(){
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack(); //Empty the backstack
        }
    }
}