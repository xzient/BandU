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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeReaderFragment";

    //Variables
    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mVideos = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mAudios = new ArrayList<>();
    private ArrayList<String> mEventIds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseArchived;
    private DatabaseReference mDataBaseFavorites;
    private DatabaseReference mDataBaseGenres;
    private DatabaseReference mDataBaseLocations;
    private DatabaseReference mDataBaseVenues;

    private Map <String, String> mDBGenres;


    private Boolean mFoundInMessages;



    SwipeRefreshLayout mSwipeRefreshLayout;

    //Calendar currentTime;

    boolean firstTimePermission = true;


    private String mUserId;

    DatabaseReference mRef;

    Date dateNow;



    /*============================================================================================*/
    //VIEW
    /*============================================================================================*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        Log.d(TAG, "onCreateView: Started.");


        //Toast.makeText(getContext(), dateNow.toString(), Toast.LENGTH_SHORT).show();

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Get location

        // Construct a FusedLocationProviderClient.
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

        mDataBaseFavorites = FirebaseDatabase.getInstance().getReference("server").child("favorites");

        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("messages");
        mDataBase.keepSynced(true);

        mDataBaseArchived = FirebaseDatabase.getInstance().getReference("server").child("archivedmessages");

        mDataBaseGenres = FirebaseDatabase.getInstance().getReference("server").child("genres");

        mDataBaseLocations = FirebaseDatabase.getInstance().getReference("server").child("city");

        mDataBaseVenues = FirebaseDatabase.getInstance().getReference("server").child("venues");


        //SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_favorite);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mRecyclerView = view.findViewById(R.id.recycler_view_favorite);
        mRecyclerView.setHasFixedSize(true);



        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                //Toast.makeText(getContext(), "Firstime", Toast.LENGTH_SHORT).show();


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

        mDataBaseGenres.addValueEventListener(valueEventListenerAllGenres);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //GETTERS
                /*--------------------------------------------*/

                adapter = new RecyclerViewAdapter(getContext(), mTitles, mMessages, mGenres, mVideos, mImages, mAudios, mEventIds);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                /*--------------------------------------------*/
                Query currentFavorites = FirebaseDatabase.getInstance().getReference("server")
                        .child("favorites").orderByChild(mUserId).equalTo(true);
                currentFavorites.addListenerForSingleValueEvent(valueEventListener);

            }
        }, 500);
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        //Wait to load data
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter = new RecyclerViewAdapter(getContext(), mTitles, mMessages, mGenres, mVideos, mImages, mAudios, mEventIds);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mDataBase.addListenerForSingleValueEvent(valueEventListener);
            }
        }, 4000);*/
    }//End loadRecyclerViewData


    /*============================================================================================*/
    /**
     * Value event listener that will be added to recycler viewer
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {


            mTitles.clear();
            mMessages.clear();
            mGenres.clear();
            mVideos.clear();
            mImages.clear();
            mAudios.clear();
            mEventIds.clear();

            if (dataSnapshot.exists()) {


                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    mFoundInMessages = false;

                    mDataBase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                            if(dataSnapshot2.hasChild(snapshot.getKey())) {

                                mFoundInMessages = true;
                                DataSnapshot ref = dataSnapshot2.child(snapshot.getKey());
                                mTitles.add(ref.child("title").getValue(String.class));
                                mMessages.add(ref.child("message").getValue(String.class));
                                mGenres.add(mDBGenres.get(ref.child("genre").getValue(String.class)));
                                mVideos.add(ref.child("video").getValue(String.class));
                                mImages.add(ref.child("image").getValue(String.class));
                                mAudios.add(ref.child("sound").getValue(String.class));
                                mEventIds.add(ref.getKey());
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });


                    //In archived
                    if (!mFoundInMessages) {

                        mDataBaseArchived.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if(dataSnapshot2.hasChild(snapshot.getKey())) {
                                    DataSnapshot ref = dataSnapshot2.child(snapshot.getKey());
                                    mTitles.add(ref.child("title").getValue(String.class));
                                    mMessages.add(ref.child("message").getValue(String.class));
                                    mGenres.add(mDBGenres.get(ref.child("genre").getValue(String.class)));
                                    mVideos.add(ref.child("video").getValue(String.class));
                                    mImages.add(ref.child("image").getValue(String.class));
                                    mAudios.add(ref.child("sound").getValue(String.class));
                                    mEventIds.add(ref.getKey());

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }


                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (mTitles.size() == 0 ) {
                            Toast.makeText(getContext(), "No current favorite events!", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);


                    }
                }, 500);


            }
            else {

                Toast.makeText(getContext(), "No current events in the system!", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    /*============================================================================================*/
    //GETTING Genres
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



}
