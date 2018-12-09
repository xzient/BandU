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

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchFragment";

    //Variables
    private DrawerLayout drawer;
    private String mUserId;


    Date dateNow;

    //Load data
    private DatabaseReference mDataBase;
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

        //Toolbar
        setHasOptionsMenu(true);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);



        //Data
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("messages");

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

        //List for genre and location






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

        ListView listView = dialogLocation.findViewById(R.id.location_popup_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1
                , sortedList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                genreSelected = adapterView.getItemAtPosition(i).toString();
                locationTextView.setText(genreSelected);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
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




}
