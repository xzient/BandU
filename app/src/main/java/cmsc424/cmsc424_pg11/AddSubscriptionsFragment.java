package cmsc424.cmsc424_pg11;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import android.app.SearchManager;
import android.support.v7.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddSubscriptionsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "AddSubFragment";

    private DrawerLayout drawer;

    /*
    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mGenreIDs = new ArrayList<>();

    private ArrayList<String> mLocations = new ArrayList<>();
    private ArrayList<String> mLocationIDs = new ArrayList<>();

    */
    private ArrayList<String> mValues = new ArrayList<>();
    private ArrayList<String> mValueIDs = new ArrayList<>();



    private RecyclerView mRecyclerView;

    private RecyclerViewAdapterSubscriptionsAdd adapter;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBase2;

    private DatabaseReference mDatabaseLoc;
    private DatabaseReference getmDatabaseLoc2;
    private FirebaseUser mCurrentFirebaseUser;
    private String mUserId;

    private Map <String, String> mDBGenres;
    private Map <String, String> mDBLocations;

    Button buttonGenres, buttonLocations;

    private boolean mIsLocation = false; // 0 for Genre  - 1 for Location


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_subscriptions, container, false);

        Log.d(TAG, "onCreateView: View created.");

        //Set menu options for this fragment
        setHasOptionsMenu(true);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        mUserId = mCurrentFirebaseUser.getUid();

        buttonGenres = view.findViewById(R.id.button_add_subscriptions_genres);
        buttonLocations = view.findViewById(R.id.button_add_subscriptions_locations);

        mRecyclerView = view.findViewById(R.id.recycler_view_add_genres);
        mRecyclerView.setHasFixedSize(true);

        //Genre data
        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("genres");
        mDataBase.keepSynced(true);

        mDataBase2 = FirebaseDatabase.getInstance().getReference("server").child("genreuser");
        mDataBase2.keepSynced(true);


        //Location data
        mDatabaseLoc = FirebaseDatabase.getInstance().getReference("server").child("city");
        mDatabaseLoc.keepSynced(true);

        getmDatabaseLoc2 = FirebaseDatabase.getInstance().getReference("server").child("cityuser");
        getmDatabaseLoc2.keepSynced(true);

        mDBLocations = new HashMap<String, String>();



        //mDataBase.addListenerForSingleValueEvent(valueEventListener);





        //Get genres
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDBGenres = (Map<String, String>) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Get locations
        mDatabaseLoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Add name and state
                        mDBLocations.put(snapshot.getKey(), snapshot.child("name").getValue(String.class) + ", " + snapshot.child("state").getValue(String.class));

                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new RecyclerViewAdapterSubscriptionsAdd(getContext(), mValues, mValueIDs, mIsLocation);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));






        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG, "onScrollStateChanged: Problem");
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });



        //Hamburger Toggle
        drawer = getActivity().findViewById(R.id.drawer_reader_main);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        buttonLocations.setOnClickListener(onClickListener);
        buttonGenres.setOnClickListener(onClickListener);

        return view;
    }




    /**
     * Click listener for buttons
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_add_subscriptions_genres:
                    buttonGenres.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button3));
                    buttonLocations.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button2));

                    mValueIDs = new ArrayList<>(mDBGenres.keySet());
                    mValues = new ArrayList<>(mDBGenres.values());

                    adapter.notifyDataSetChanged();

                    mIsLocation = false;

                    Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                           .child("genreuser").orderByChild(mUserId).equalTo(true);

                    currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener);
                    break;

                //Case for Subscriptions Location
                case R.id.button_add_subscriptions_locations:
                    buttonLocations.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button3));
                    buttonGenres.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button2));

                    mValueIDs = new ArrayList<>(mDBLocations.keySet());
                    //Toast.makeText(getContext(), mValueIDs.get(0), Toast.LENGTH_SHORT).show();
                    mValues = new ArrayList<>(mDBLocations.values());

                    adapter.notifyDataSetChanged();

                    mIsLocation = true;

                    Query currentGenresSubsQuery2 = FirebaseDatabase.getInstance().getReference("server")
                            .child("cityuser").orderByChild(mUserId).equalTo(true);

                    currentGenresSubsQuery2.addListenerForSingleValueEvent(valueEventListener);

                    break;
            }



        }
    };





    /**
     * ValueEventListener for Values (Genres or Locations)
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            //mGenres.clear();
            //mGenreIDs.clear();

            if (dataSnapshot.getChildrenCount() == 0) {
                //Toast.makeText(getContext(), "Empty", Toast.LENGTH_SHORT).show();

                adapter = new RecyclerViewAdapterSubscriptionsAdd(getContext(), mValues, mValueIDs, mIsLocation);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            if (dataSnapshot.exists()) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Toast.makeText(getContext(), mGenres.get(0), Toast.LENGTH_SHORT).show();
                    if (mIsLocation) {
                        mValues.remove(mDBLocations.get(snapshot.getKey()));
                    }
                    else {
                        mValues.remove(mDBGenres.get(snapshot.getKey()));
                    }


                    mValueIDs.remove(snapshot.getKey());

                }
                adapter = new RecyclerViewAdapterSubscriptionsAdd(getContext(), mValues, mValueIDs, mIsLocation);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                //adapter.notifyDataSetChanged();

            }
        }



        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        String userInput = s.toLowerCase();
        List<String> newListValues = new ArrayList<>();
        List<String> newListIDs = new ArrayList<>();

        for (String value: mValues) {
            if(value.toLowerCase().contains(userInput)) {
                //get genre
                newListValues.add(value);
                //get arrayList
                newListIDs.add(mValueIDs.get(mValues.indexOf(value)));
            }

        adapter.updateList(newListValues, newListIDs);
        }
        return false;
    }
}
