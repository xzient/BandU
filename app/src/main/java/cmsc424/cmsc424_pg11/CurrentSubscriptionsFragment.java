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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentSubscriptionsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "CurrentSubscriptionsFra";

    private DrawerLayout drawer;

    private ArrayList<String> mLocations = new ArrayList<>();


    private ArrayList<String> mValues = new ArrayList<>();
    private ArrayList<String> mValueIDs = new ArrayList<>();

    //IDs


    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterSubscriptions adapter;
    private DatabaseReference mDataBaseGenre;
    private DatabaseReference mDataBaseLoc;

    private FirebaseUser mCurrentFirebaseUser;
    private String mUserId;
    private Map <String, String> mDBGenres;
    private Map <String, String> mDBLocations;

    private Button buttonGenres, buttonLocations;
    private boolean mIsLocation = false; //0 for Genre, 1 for location

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_subscriptions, container, false);

        Log.d(TAG, "onCreateView: View created.");


        //Set menu options for this fragment
        setHasOptionsMenu(true);

        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        mUserId = mCurrentFirebaseUser.getUid();

        buttonGenres = view.findViewById(R.id.button_current_subscriptions_genres);
        buttonLocations = view.findViewById(R.id.button_current_subscriptions_locations);



        mRecyclerView = view.findViewById(R.id.recycler_view_current_genres);
        mRecyclerView.setHasFixedSize(true);


        mDataBaseGenre = FirebaseDatabase.getInstance().getReference("server").child("genres");
        mDataBaseGenre.keepSynced(true);

        mDBLocations = new HashMap<String, String>();


        mDataBaseLoc = FirebaseDatabase.getInstance().getReference("server").child("city");


        //mDataBase.addListenerForSingleValueEvent(valueEventListener);

        buttonLocations.setOnClickListener(onClickListener);
        buttonGenres.setOnClickListener(onClickListener);



        mDataBaseGenre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDBGenres = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get locations
        mDataBaseLoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Add name and state
                        mDBLocations.put(snapshot.getKey(), snapshot.child("name").getValue(String.class) + ", " + snapshot.child("state").getValue(String.class));
                        //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();
                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        adapter = new RecyclerViewAdapterSubscriptions(getContext(),mValues, mValueIDs, mIsLocation);
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

        return view;
    }


    /**
     * Click listener for buttons
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_current_subscriptions_genres:
                    buttonGenres.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button3));
                    buttonLocations.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button2));

                    mIsLocation = false;

                    //mDataBase.addListenerForSingleValueEvent(valueEventListener);
                    Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                            .child("genreuser").orderByChild(mUserId).equalTo(true);

                    currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener);

                    break;
                case R.id.button_current_subscriptions_locations:
                    //Toast.makeText(getContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
                    buttonLocations.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button3));
                    buttonGenres.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button2));
                    mIsLocation = true;

                    Query currentGenresSubsQuery2 = FirebaseDatabase.getInstance().getReference("server")
                            .child("cityuser").orderByChild(mUserId).equalTo(true);


                    currentGenresSubsQuery2.addListenerForSingleValueEvent(valueEventListener);

                    break;
            }
        }
    };

    /**
     * Get data to recycler view
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mValues.clear();
            mValueIDs.clear();

            if (dataSnapshot.getChildrenCount() == 0) {
                String valueType = mIsLocation? "locations" : "genres";

                Toast.makeText(getContext(), "You are not subscribed to any " + valueType, Toast.LENGTH_SHORT).show();


            }

            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (mIsLocation) {
                        mValues.add(mDBLocations.get(snapshot.getKey()));
                        //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mValues.add(mDBGenres.get(snapshot.getKey()));
                    }

                    mValueIDs.add(snapshot.getKey());
                }
                adapter = new RecyclerViewAdapterSubscriptions(getContext(),mValues, mValueIDs, mIsLocation);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    /**
     * Menu data
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

        String userInput = newText.toLowerCase();
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
