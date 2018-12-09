package cmsc424.cmsc424_pg11;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeReaderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeReaderFragment";

    //Variables
    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mVideos = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mAudios = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseGenres;
    private DatabaseReference mDataBaseLocations;
    private DatabaseReference mDataBaseVenues;
    private ArrayList<String> mUserGenres = new ArrayList<>();

    private Map <String, double[]> mUserLocations;



    SwipeRefreshLayout mSwipeRefreshLayout;

    //Calendar currentTime;



    private Map <String, String> mDBGenres;
    private Map <String,  double[]> mDBLocations;
    private Map <String, double[]> mDBVenues;

    private String mUserId;

    //Location
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    Double currentLat;
    Double currentLong;

    DatabaseReference mRef;

    Date dateNow;



    /*============================================================================================*/
    //VIEW
    /*============================================================================================*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_reader, container, false);

        Log.d(TAG, "onCreateView: Started.");





        //Toast.makeText(getContext(), dateNow.toString(), Toast.LENGTH_SHORT).show();

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mDBVenues = new HashMap<>();
        mUserLocations = new HashMap<>();
        mDBLocations = new HashMap<>();





        //Get current

        //Get current time;
        //currentTime = Calendar.getInstance();


        //Get location
        getLocationPermission();
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        mRef = FirebaseDatabase.getInstance().getReference("server").child("users").child(mUserId);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getActivity().setTitle(dataSnapshot.child("name").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //getCoordinates();


        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("messages");//May get issues
        mDataBase.keepSynced(true);

        mDataBaseGenres = FirebaseDatabase.getInstance().getReference("server").child("genres");

        mDataBaseLocations = FirebaseDatabase.getInstance().getReference("server").child("city");

        mDataBaseVenues = FirebaseDatabase.getInstance().getReference("server").child("venues");


        //SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mRecyclerView = view.findViewById(R.id.recycler_view_reader);
        mRecyclerView.setHasFixedSize(true);



        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadRecyclerViewData();
            }
        });

        return view;
    }//End view


    /*============================================================================================*/
    //LOADING DATA
    /*============================================================================================*/
    /**
     * When fragment is refreshed
     */
    @Override
    public void onRefresh() {
        loadRecyclerViewData();
        //Toast.makeText(getContext(), "Data on refresh", Toast.LENGTH_SHORT).show();
        //mDataBase.addListenerForSingleValueEvent(valueEventListener);
    }

    /*============================================================================================*/

    /**
     * This method will load all the data needed.
     */
    private void loadRecyclerViewData () {
        //Display refresh animation before retrieving data.
        mSwipeRefreshLayout.setRefreshing(true);
        //Get current time
        dateNow = new Date();

        //Show time
        //Toast.makeText(getContext(), "" + Calendar.getInstance().get(Calendar.SECOND), Toast.LENGTH_SHORT).show();

        //Get current location
        getCoordinates();
        /*--------------------------------------------*/
                //Get general data
        /*--------------------------------------------*/
        //ALL GENRES
        mDataBaseGenres.addValueEventListener(valueEventListenerAllGenres);
        /*--------------------------------------------*/
        //ALL LOCATIONS
        mDataBaseLocations.addValueEventListener(valueEventListenerAllLocations);
        /*--------------------------------------------*/
        //ALL VENUES
        mDataBaseVenues.addListenerForSingleValueEvent(valueEventListenerAllVenues);
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //GETTERS
                /*--------------------------------------------*/
                //USER GENRES
                Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                        .child("genreuser").orderByChild(mUserId).equalTo(true);
                currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListenerUserGenres);
                /*--------------------------------------------*/
                //USER LOCATIONS
                Query currentGenresSubsQuery2 = FirebaseDatabase.getInstance().getReference("server")
                        .child("cityuser").orderByChild(mUserId).equalTo(true);
                currentGenresSubsQuery2.addListenerForSingleValueEvent(valueEventListenerUserLocations);
            }
        }, 2000);
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        //Wait to load data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter = new RecyclerViewAdapter(getContext(), mTitles, mMessages, mGenres, mVideos, mImages, mAudios);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mDataBase.addListenerForSingleValueEvent(valueEventListener);
            }
        }, 4000);
    }//End loadRecyclerViewData


    /*============================================================================================*/
    /**
     * Value event listener that will be added to recycler viewer
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            mTitles.clear();
            mMessages.clear();
            mGenres.clear();
            mVideos.clear();
            mImages.clear();
            mAudios.clear();

            if (dataSnapshot.exists()) {
                //Toast.makeText(BaseActivityReader.this, "Hello", Toast.LENGTH_SHORT).show();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String eventGenre = snapshot.child("genre").getValue(String.class);

                    //Check Genres
                    if(Methods.genreFoundInList(mUserGenres, eventGenre)) {
                        //CHECK LOCATION
                        //Get current location
                        String eventLocID = snapshot.child("venue_id").getValue(Long.class).toString();
                        float[] results = new float[1];

                        double [] eventCoordinates = {mDBVenues.get(eventLocID)[0], mDBVenues.get(eventLocID)[1]};

                        //Location.distanceBetween(currentLat, currentLong, eventCoordinates[0], eventCoordinates[1], results);
                        //Toast.makeText(getContext(), "Distance: " + (int) Math.floor(results[0] * 0.00062137), Toast.LENGTH_SHORT).show();

                        //Get event distance
                        int distanceInMiles;


                        Integer eventRange = snapshot.child("range").getValue(Integer.class);


                        for (double[] coor: mUserLocations.values()) {//USer locations

                            Location.distanceBetween( eventCoordinates[0], eventCoordinates[1], coor[0], coor[1], results);
                            distanceInMiles = (int) Math.floor(results[0] * 0.00062137); //to miles
                            if (eventRange != null && distanceInMiles <= eventRange) {


                                //Toast.makeText(getContext(), "Event range: " + distanceInMiles, Toast.LENGTH_SHORT).show();
                                //Log.d(TAG, "onDataChange: " + snapshot.getKey());

                                //Date condition
                                if (Methods.betweenTwoDates(snapshot.child("start_time").getValue(Long.class).toString(), snapshot.child("end_time").getValue(Long.class).toString(), dateNow)) {
                                    mTitles.add(snapshot.child("title").getValue(String.class));
                                    mMessages.add(snapshot.child("message").getValue(String.class));
                                    mGenres.add(mDBGenres.get(snapshot.child("genre").getValue(String.class)));
                                    mVideos.add(snapshot.child("video").getValue(String.class));
                                    mImages.add(snapshot.child("image").getValue(String.class));
                                    mAudios.add(snapshot.child("sound").getValue(String.class));
                                }



                                break; //Break from loop
                            }
                        }

                        //No event found within range

                        //Toast.makeText(getContext(), "Event not in range: " + snapshot.child("title").getValue(String.class), Toast.LENGTH_SHORT).show();


                        //CHECK TIME

                        //Approved values

                    }
                }

                if (mTitles.size() == 0 ) {
                    Toast.makeText(getContext(), "No current events!", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
            else {

                Toast.makeText(getContext(), "No current events in the system!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    /*============================================================================================*/
    //GETTING LOCATION
    /*============================================================================================*/


    /**
     * Get permission to use GPS location
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /*============================================================================================*/

    /**
     * This function will show the current latitute and longitute.
     * This function also updates the user's coordinates and city throgh get address.
     */
    void getCoordinates() {

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            currentLat = mLastKnownLocation.getLatitude();
                            currentLong = mLastKnownLocation.getLongitude();

                            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("server").child("users").child(mUserId);

                            //mRef.child("latitude").setValue(currentLat);
                            //Ref.child("longitude").setValue(currentLong);

                            getAddress(getContext(), currentLat, currentLong);



                            //Toast.makeText(getContext(), "Latitude: " + currentLat + " -- Longitude: " + currentLong, Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());


                            Toast.makeText(getContext(), "Location not found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    /*============================================================================================*/

    /**
     * This functions get the current state and city of a user
     * It will also update the user's current coordinates and city.
     * @param context
     * @param lat
     * @param lng
     * @return
     */
    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            /*
            String add = obj.getAddressLine(0);
            add = add + "," + obj.getAdminArea();
            add = add + "," + obj.getCountryName();
            String city = obj.getLocality();
            */


            //Update user
            mRef.child("city").setValue(obj.getLocality());
            mRef.child("state").setValue(obj.getAdminArea());
            mRef.child("latitude").setValue(lat);
            mRef.child("longitude").setValue(lng);

            //float[] results = new float[1];

            //Location.distanceBetween(currentLat, currentLong, 38.8977, -77.0365, results);
            //Toast.makeText(getContext(), "Distance: " + (int) Math.floor(results[0] * 0.00062137), Toast.LENGTH_SHORT).show();


            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /*============================================================================================*/
                                    //LOAD GENERAL DATA
    /*============================================================================================*/
    /**
     * Event Listener for all genres
     */
    ValueEventListener valueEventListenerAllGenres = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mDBGenres = (Map<String, String>) dataSnapshot.getValue();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    /*============================================================================================*/
    /**
     * Event Listener for all Locations
     */
    ValueEventListener valueEventListenerAllLocations = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            mDBLocations.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDBLocations.put(snapshot.getKey(), new double[]{snapshot.child("lat").getValue(Double.class),snapshot.child("long").getValue(Double.class)});
                    //Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    /*============================================================================================*/
    /**
     * Event Listener for all venues
     */
    ValueEventListener valueEventListenerAllVenues = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mDBVenues.clear();

            if (dataSnapshot.exists()) {
                //mDBVenues.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDBVenues.put(snapshot.getKey(), new double[]{snapshot.child("lat").getValue(Double.class),snapshot.child("long").getValue(Double.class)});

                    //Toast.makeText(getContext(), mDBVenues.get(snapshot.getKey())[0] + "", Toast.LENGTH_SHORT).show();
                }
            }

            //Toast.makeText(getContext(), "Done with venues", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };
    /*============================================================================================*/


    /*============================================================================================*/
                                            //LOAD USER DATA
    /*============================================================================================*/
    /**
     * Value event listener for all genres
     */
    ValueEventListener valueEventListenerUserGenres = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mUserGenres.clear();
            ArrayList<String> temp = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    temp.add(snapshot.getKey());
                }

                mUserGenres = Methods.reduceGenres(temp);
            }
            else {
                Toast.makeText(getContext(), "You are not subscribed to any genre!", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    /*============================================================================================*/
    /**
     * Value event listener for all locations
     */
    ValueEventListener valueEventListenerUserLocations = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mUserLocations.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mUserLocations.put(snapshot.getKey(), new double [] {mDBLocations.get(snapshot.getKey())[0], mDBLocations.get(snapshot.getKey())[1]});
                    //Toast.makeText(getContext(), mDBLocations.get(snapshot.getKey())[0] + "", Toast.LENGTH_SHORT).show();
                }
            }
            //Add current location
            mUserLocations.put("00000", new double[]{currentLat, currentLong});
            //Toast.makeText(getContext(), "Size of UserLocations: " + mUserLocations.size(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}
