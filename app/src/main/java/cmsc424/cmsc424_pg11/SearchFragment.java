package cmsc424.cmsc424_pg11;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchFragment";

    //Variables
    private DrawerLayout drawer;
    private String mUserId;


    Date dateNow;

    //Load data
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseArchived;
    private DatabaseReference mDataBaseGenres;
    private DatabaseReference mDataBaseLocations;
    private DatabaseReference mDataBaseVenues;
    private ArrayList<String> mUserGenres = new ArrayList<>();

    DatabaseReference mRef;

    private Map <String, String> mDBGenres;
    private Map <String,  double[]> mDBLocations;
    private Map <String, double[]> mDBVenues;
    private Map <String, String> mDBLocationNames;

    //Button
    Button btnGenre, btnLocation, btnTime, btnFind;
    //TextView

    TextView genreTextView, locationTextView, timeTextView;

    //Dialog
    Dialog dialogGenre;
    Dialog dialogLocation;
    Dialog dialogTime;

    String timeSelected;
    String genreSelected;
    String locationSelected;

    String genreSelectedId;
    String locationSelectedId;

    //Adapter and other stuff
    private RecyclerViewAdapterSearch adapter;
    private RecyclerView mRecyclerView;



    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mVideos = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mAudios = new ArrayList<>();
    private ArrayList<String> mEventIds = new ArrayList<>();

    //Spinner Data

    String[] yearSpinnerList = new String[] {
            "2019","2018","2017","2016","2015"};

    String[] monthSpinnerList = new String[]{
            "01", "02", "03", "04", "05", "06", "07", "08", "09"
            , "10", "11", "12"
    };
    String[] daySpinnerList = new String[] {
            "01","02","03","04","05","06","07","08","09"
            ,"10","11","12","13","14","15","16","17","18","19"
            ,"20","21","22","23","24","25","26","27","28","29"
            ,"30","31"
    };
    String[] hourSpinnerList = new String[] {
            "00","01", "02", "03", "04", "05","06","07","08","09","10","11","12",
            "13", "14", "15","16", "17","18","19","20","21","22", "23",
    };
    String[] minuteSpinnerList = new String[] {
            "00","01","02","03","04","05","06","07","08","09"
            ,"10","11","12","13","14","15","16","17","18","19"
            ,"20","21","22","23","24","25","26","27","28","29"
            ,"30","31","32","33","34","35","36","37","38","39"
            ,"40","41","42","43","44","45","46","47","48","49"
            ,"50","51","52","53","54","55","56","57","58","59"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d(TAG, "onCreateView: Started.");

        timeSelected = "";

        //Selected
        genreSelected = "all genres";
        locationSelected = "all locations";
        timeSelected = "all times";

        //Toolbar
        setHasOptionsMenu(true);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mRecyclerView = view.findViewById(R.id.recycler_view_search);
        mRecyclerView.setHasFixedSize(true);



        //Data
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("messages");

        mDataBaseArchived = FirebaseDatabase.getInstance().getReference("server").child("archivedmessages");

        mDataBaseGenres = FirebaseDatabase.getInstance().getReference("server").child("genres");

        mDataBaseLocations = FirebaseDatabase.getInstance().getReference("server").child("city");

        mDataBaseVenues = FirebaseDatabase.getInstance().getReference("server").child("venues");

        mRef = FirebaseDatabase.getInstance().getReference("server").child("users").child(mUserId);


        //Initiate maps
        mDBVenues = new HashMap<>();
        mDBLocations = new HashMap<>();
        mDBLocationNames = new HashMap<String, String>();

        //Buttons
        btnGenre = view.findViewById(R.id.search_button_genres);
        btnLocation = view.findViewById(R.id.search_button_location);
        btnTime = view.findViewById(R.id.search_button_time);
        btnFind = view.findViewById(R.id.search_button_find);

        //Textviews
        genreTextView = view.findViewById(R.id.search_textview_genre);
        locationTextView = view.findViewById(R.id.search_textview_location);
        timeTextView = view.findViewById(R.id.search_textview_time);
        //Dialog
        dialogGenre = new Dialog(getContext());
        dialogLocation = new Dialog(getContext());
        dialogTime = new Dialog(getContext());


        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopupGenre(view);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopupLocation(view);
            }
        });


        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopupTime(view);
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayQuery();
            }
        });

        //List for genre and location


        //Recycler viewer
        adapter = new RecyclerViewAdapterSearch(getContext(), mTitles, mMessages, mGenres, mVideos,
                mImages, mAudios, mEventIds);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));





        //Hamburger Toggle
        drawer = getActivity().findViewById(R.id.drawer_reader_main);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        loadRecyclerViewData();

        return view;
    }//End view

    /*======================================================================================*/
    public void ShowPopupGenre(View v) {
        dialogGenre.setContentView(R.layout.genre_popup);
        dialogGenre.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialogGenre.show();

        ArrayList<String> sortedList = new ArrayList<String>(mDBGenres.values());
        Collections.sort(sortedList);

        ListView listView = dialogGenre.findViewById(R.id.genre_popup_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1
                , sortedList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                genreSelected = adapterView.getItemAtPosition(i).toString();
                genreTextView.setText(genreSelected);


                for (Map.Entry<String, String> entry : mDBGenres.entrySet()) {
                    if (Objects.equals(genreSelected, entry.getValue())) {
                        genreSelectedId = entry.getKey();

                    }
                }
                dialogGenre.dismiss();
            }
        });

    }
    /*======================================================================================*/
    public void ShowPopupLocation(View v) {
        dialogLocation.setContentView(R.layout.location_popup);
        dialogLocation.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialogLocation.show();
        //mDBLocationNames

        ArrayList<String> sortedList = new ArrayList<String>(mDBLocationNames.values());

        Collections.sort(sortedList);
        sortedList.add("all locations");

        ListView listView = dialogLocation.findViewById(R.id.location_popup_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1
                , sortedList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                locationSelected = adapterView.getItemAtPosition(i).toString();
                locationTextView.setText(locationSelected);


                for (Map.Entry<String, String> entry : mDBLocationNames.entrySet()) {
                    if (Objects.equals(locationSelected, entry.getValue())) {
                        locationSelectedId = entry.getKey();

                    }


                }



                dialogLocation.dismiss();
            }
        });


    }
    /*======================================================================================*/
    public void ShowPopupTime(View v) {
        dialogTime.setContentView(R.layout.time_popup);
        dialogTime.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialogTime.show();

        final Button timeBtn = dialogTime.findViewById(R.id.time_popup_button);
        final Button allTimes = dialogTime.findViewById(R.id.all_times_popup_button);
        final Spinner yearET = dialogTime.findViewById(R.id.time_popup_year);
        final Spinner monthET = dialogTime.findViewById(R.id.time_popup_month);
        final Spinner dayET = dialogTime.findViewById(R.id.time_popup_day);
        final Spinner hourET = dialogTime.findViewById(R.id.time_popup_hour);
        final Spinner minuteET = dialogTime.findViewById(R.id.time_popup_minute);

        //Adapters
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, yearSpinnerList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, monthSpinnerList);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, daySpinnerList);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, hourSpinnerList);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, minuteSpinnerList);
        yearET.setAdapter(adapter1);
        monthET.setAdapter(adapter2);
        dayET.setAdapter(adapter3);
        hourET.setAdapter(adapter4);
        minuteET.setAdapter(adapter5);



        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSelected = "";
                timeSelected += yearET.getSelectedItem().toString().substring(2,4);
                timeSelected += monthET.getSelectedItem().toString();
                timeSelected += dayET.getSelectedItem().toString();
                timeSelected += hourET.getSelectedItem().toString();
                timeSelected += minuteET.getSelectedItem().toString();


                //Toast.makeText(getContext(), timeSelected, Toast.LENGTH_SHORT).show();
                dateNow = Methods.stringToDate(timeSelected);

                timeTextView.setText(timeSelected.substring(2, 4) + "/" +
                        timeSelected.substring(4,6) + "/" + timeSelected.substring(0,2));
                dialogTime.dismiss();
            }
        });

        allTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSelected = "all times";
                timeTextView.setText(timeSelected);
                dialogTime.dismiss();
            }
        });
    }

    /*======================================================================================*/


    private void loadRecyclerViewData () {
        //Display refresh animation before retrieving data.
        //mSwipeRefreshLayout.setRefreshing(true);
        //Get current time
        //dateNow = new Date();

        //Get current location
        //getCoordinates();
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

    }

    /*======================================================================================*/

    private void displayQuery() {


        mTitles.clear();
        mMessages.clear();
        mGenres.clear();
        mVideos.clear();
        mImages.clear();
        mAudios.clear();
        mEventIds.clear();


        adapter = new RecyclerViewAdapterSearch(getContext(), mTitles, mMessages, mGenres, mVideos,
                mImages, mAudios, mEventIds);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




        if (timeSelected.equals("all times") && genreSelected.equals("all genres") &&
                locationSelected.equals("all locations"))  {
            mDataBase.addListenerForSingleValueEvent(valueEventListenerAll);
            mDataBaseArchived.addListenerForSingleValueEvent(valueEventListenerAll);

        }
        else {
            mDataBase.addListenerForSingleValueEvent(valueEventListener);
            mDataBaseArchived.addListenerForSingleValueEvent(valueEventListener);
        }







        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                 if (mTitles.size() == 0) {
                     Toast.makeText(getContext(), "No results. Please try a different query!", Toast.LENGTH_SHORT).show();
                 }
            }
        }, 2000);





    }

    /*======================================================================================*/

    ValueEventListener valueEventListenerAll = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    mTitles.add(snapshot.child("title").getValue(String.class));
                    mMessages.add(snapshot.child("message").getValue(String.class));
                    mGenres.add(mDBGenres.get(snapshot.child("genre").getValue(String.class)));
                    mVideos.add(snapshot.child("video").getValue(String.class));
                    mImages.add(snapshot.child("image").getValue(String.class));
                    mAudios.add(snapshot.child("sound").getValue(String.class));
                    mEventIds.add(snapshot.getKey());
                }
            }
            adapter = new RecyclerViewAdapterSearch(getContext(), mTitles, mMessages, mGenres, mVideos, mImages,
                    mAudios, mEventIds);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /*======================================================================================*/
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Get all, all null
                    /*
                    if(genreSelected == null && locationSelected == null && dateNow == null) {
                        mTitles.add(snapshot.child("title").getValue(String.class));
                        mMessages.add(snapshot.child("message").getValue(String.class));
                        mGenres.add(mDBGenres.get(snapshot.child("genre").getValue(String.class)));
                        mVideos.add(snapshot.child("video").getValue(String.class));
                        mImages.add(snapshot.child("image").getValue(String.class));
                        mAudios.add(snapshot.child("sound").getValue(String.class));
                    }
                    */





                    //Check genre
                    String eventGenre = snapshot.child("genre").getValue(String.class);

                    if (genreSelected.equals(eventGenre) ||
                            Methods.checkStringValue(genreSelectedId, eventGenre) == 1) {


                        String startTime =  snapshot.child("start_time").getValue(Long.class).toString();
                        String endTime = snapshot.child("end_time").getValue(Long.class).toString();

                        //Check Time
                        if (Methods.betweenTwoDates(startTime, endTime, dateNow)) {

                            //Check Location
                            String eventLocID = snapshot.child("venue_id").getValue(Long.class).toString();
                            float[] results = new float[1];

                            double [] eventCoordinates = {mDBVenues.get(eventLocID)[0], mDBVenues.get(eventLocID)[1]};




                            int distanceInMiles;
                            Integer eventRange = snapshot.child("range").getValue(Integer.class);

                            double[] coor = mDBLocations.get(locationSelectedId);

                            Location.distanceBetween( eventCoordinates[0], eventCoordinates[1], coor[0],
                                    coor[1], results);
                            distanceInMiles = (int) Math.floor(results[0] * 0.00062137);



                            if (eventRange != null && distanceInMiles <= eventRange) {

                                mTitles.add(snapshot.child("title").getValue(String.class));
                                mMessages.add(snapshot.child("message").getValue(String.class));
                                mGenres.add(mDBGenres.get(snapshot.child("genre").getValue(String.class)));
                                mVideos.add(snapshot.child("video").getValue(String.class));
                                mImages.add(snapshot.child("image").getValue(String.class));
                                mAudios.add(snapshot.child("sound").getValue(String.class));
                                mEventIds.add(snapshot.getKey());


                            }


                        }
                    }
                }
            }
            adapter = new RecyclerViewAdapterSearch(getContext(), mTitles, mMessages, mGenres, mVideos, mImages,
                    mAudios, mEventIds);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * Menu for fragment
     * @param menu
     * @param menuInflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.search_menu_subs, menu);

        MenuItem menuItem = menu.findItem(R.id.search_item_subscription);
        SearchView searchView = (SearchView) menuItem.getActionView();//menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        //super.onCreateOptionsMenu(menu, menuInflater);
    }


    //On query stuff


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        List<String> newTitles = new ArrayList<>();
        List<String> newMessages = new ArrayList<>();
        List<String> newGenres = new ArrayList<>();
        List<String> newVideos = new ArrayList<>();
        List<String> newImages = new ArrayList<>();
        List<String> newAudios = new ArrayList<>();
        List<String> newEventIDs = new ArrayList<>();

        //for (String value: mMessages) {

        for (int i = 0; i < mTitles.size(); i++) {
            if(mMessages.get(i).toLowerCase().contains(userInput)
                    || mTitles.get(i).toLowerCase().contains(userInput)) {
                //get genre
                newTitles.add(mTitles.get(i));
                newMessages.add(mMessages.get(i));
                newGenres.add(mGenres.get(i));
                newVideos.add(mVideos.get(i));
                newImages.add(mImages.get(i));
                newAudios.add(mAudios.get(i));
                newEventIDs.add(mEventIds.get(i));
            }
            adapter.updateList(newTitles, newMessages, newGenres, newVideos, newImages,
                    newAudios, newEventIDs);
        }


        return false;
    }


    /*============================================================================================*/

                                       //LOAD DATA IN GENERAL

    /*============================================================================================*/


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
            mDBLocations.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDBLocations.put(snapshot.getKey(), new double[]{snapshot.child("lat").getValue(Double.class),snapshot.child("long").getValue(Double.class)});
                    mDBLocationNames.put(snapshot.getKey(),snapshot.child("name").getValue(String.class) + ", " + snapshot.child("state").getValue(String.class));

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

                    //Toast.makeText(getContext(), snapshot.getKey() + "", Toast.LENGTH_SHORT).show();
                }
            }

            //Toast.makeText(getContext(), "Done with venues", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };
    /*============================================================================================*/




}
