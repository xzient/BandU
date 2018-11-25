package cmsc424.cmsc424_pg11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BaseActivityReader extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    DatabaseReference dbMessages;
    private static final String TAG = "BaseActivityReader";


    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    //private ArrayList<String> mMessages;
    private ArrayList<String> mTitles;
    private ArrayList<String> mGenres;
    private ArrayList<String> duck;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_reader);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle("BandU");




        drawer = findViewById(R.id.drawer_reader_main);
        NavigationView navigationView = findViewById(R.id.nav_view_reader);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*
        //RecyclerView
        recyclerView = findViewById(R.id.recycler_view_reader);
        recyclerView.setHasFixedSize(true);


        //Arrays
        //mMessages = new ArrayList<>();
        mTitles = new ArrayList<>();
        mGenres = new ArrayList<>();
        duck = new ArrayList<>();


        adapter = new RecyclerViewAdapter(this, mTitles, mGenres);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        //Get all rock
        dbMessages = FirebaseDatabase.getInstance().getReference("server").child("messages");



        */




        //Query1

        /*
        Query query1 = FirebaseDatabase.getInstance().getReference("server").child("messages")
                .orderByChild("genre").equalTo("rock");

        query1.addListenerForSingleValueEvent(valueEventListener);

        */




        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container,
                    new HomeReaderFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_reader_home);
        }


    }

    /*
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //duck.clear();
            mTitles.clear();
            //mMessages.clear();
            mGenres.clear();
            if (dataSnapshot.exists()) {
                //Toast.makeText(BaseActivityReader.this, "Hello", Toast.LENGTH_SHORT).show();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    snapshot.getValue();


                    String singleTitle = snapshot.child("title").getValue(String.class);
                    //Toast.makeText(BaseActivityReader.this, singleTitle, Toast.LENGTH_SHORT).show();
                    //duck.add(singleTitle);
                    mTitles.add(singleTitle);

                    //String singleMessage = snapshot.child("message").getValue(String.class);
                    //mMessages.add(singleMessage);

                    String singleGenre = snapshot.child("genre").getValue(String.class);
                    mGenres.add(singleGenre);

                }
                adapter.notifyDataSetChanged();

            }
        }



        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    */


    /**
     * Navigation Selection
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_reader_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new HomeReaderFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_map:
                startActivity(new Intent(BaseActivityReader.this, MapsActivity.class));
                break;
            case R.id.nav_reader_search:
                //TO DO
                break;
            case R.id.nav_reader_subscriptions:
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new SubscriptionsFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_settings:
                //TO DO
                break;

                /*
            case R.id.nav_rock:
                Query query1 = FirebaseDatabase.getInstance().getReference("server").child("messages")
                        .orderByChild("genre").equalTo("rock");

                query1.addListenerForSingleValueEvent(valueEventListener);
                break;
            case R.id.nav_classical:
                Query query2 = FirebaseDatabase.getInstance().getReference("server").child("messages")
                        .orderByChild("genre").equalTo("classical");

                query2.addListenerForSingleValueEvent(valueEventListener);
                break;

            case R.id.nav_all:
                dbMessages = FirebaseDatabase.getInstance().getReference("server").child("messages");

                dbMessages.addListenerForSingleValueEvent(valueEventListener);
                break;
                */


        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
