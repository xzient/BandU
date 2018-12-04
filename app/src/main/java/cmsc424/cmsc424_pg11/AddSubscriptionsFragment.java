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
import java.util.List;
import java.util.Map;

public class AddSubscriptionsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "AddSubFragment";

    private DrawerLayout drawer;


    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mLocations = new ArrayList<>();

    private ArrayList<String> mGenreIDs = new ArrayList<>();



    private RecyclerView mRecyclerView;

    private RecyclerViewAdapterSubscriptionsAdd adapter;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBase2;
    private FirebaseUser mCurrentFirebaseUser;
    private String mUserId;

    private Map <String, String> mDBGenres;

    Button buttonGenres, buttonLocations;


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


        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("genres");
        mDataBase.keepSynced(true);

        mDataBase2 = FirebaseDatabase.getInstance().getReference("server").child("genreuser");
        mDataBase2.keepSynced(true);

        //mDataBase.addListenerForSingleValueEvent(valueEventListener);


        buttonLocations.setOnClickListener(onClickListener);
        buttonGenres.setOnClickListener(onClickListener);


        //Get genres
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDBGenres = (Map<String, String>) dataSnapshot.getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new RecyclerViewAdapterSubscriptionsAdd(getContext(), mGenres, mGenreIDs);
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
                case R.id.button_add_subscriptions_genres:
                    buttonGenres.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button3));
                    buttonLocations.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button2));

                    mGenreIDs = new ArrayList<>(mDBGenres.keySet());
                    mGenres = new ArrayList<>(mDBGenres.values());

                    adapter.notifyDataSetChanged();








                    Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                           .child("genreuser").orderByChild(mUserId).equalTo(true);



                    currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener);

                    break;
                case R.id.button_add_subscriptions_locations:
                    Toast.makeText(getContext(), "No yet implemented", Toast.LENGTH_SHORT).show();
                    buttonLocations.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button3));
                    buttonGenres.setBackground(getResources().getDrawable(R.drawable.rounded_corner_button2));

                    break;
            }



        }
    };


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //duck.clear();

            //mGenres.clear();
            //mGenreIDs.clear();
            if (dataSnapshot.exists()) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Toast.makeText(getContext(), mGenres.get(0), Toast.LENGTH_SHORT).show();
                    mGenres.remove(mDBGenres.get(snapshot.getKey()));
                    mGenreIDs.remove(snapshot.getKey());

                }
                adapter = new RecyclerViewAdapterSubscriptionsAdd(getContext(), mGenres, mGenreIDs);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                //adapter.notifyDataSetChanged();

            }
        }



        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    //Fragment menu
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
        List<String> newListGenres = new ArrayList<>();
        List<String> newListIDs = new ArrayList<>();

        for (String genre: mGenres) {
            if(genre.toLowerCase().contains(userInput)) {
                //get genre
                newListGenres.add(genre);
                //get arraylist
                newListIDs.add(mGenreIDs.get(mGenres.indexOf(genre)));
            }

        adapter.updateList(newListGenres, newListIDs);
        }
        return false;
    }
}
