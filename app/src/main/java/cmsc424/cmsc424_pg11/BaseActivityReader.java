package cmsc424.cmsc424_pg11;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BaseActivityReader extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AlertDialogDeregister.AlertDialogListener  {

    private DrawerLayout drawer;
    DatabaseReference dbMessages;
    private static final String TAG = "BaseActivityReader";


    //RecyclerView
    /*
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> mMessages;
    private ArrayList<String> mTitles;
    private ArrayList<String> mGenres;
    */

    private DatabaseReference mDataBase;
    private DatabaseReference mDataBase2;
    private DatabaseReference mDataBase3;
    private DatabaseReference mDataBase4;
    private DatabaseReference mDataBase5;
    private String mUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            System.exit(0);
        }
        setContentView(R.layout.activity_base_reader);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle("BandU");

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();









        drawer = findViewById(R.id.drawer_reader_main);
        NavigationView navigationView = findViewById(R.id.nav_view_reader);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();





        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container,
                    new HomeReaderFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_reader_home);
        }


    }


    /**
     * Navigation Selection
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_reader_home:
                setTitle("Feed");
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new HomeReaderFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_favorite:
                setTitle("Favorite");
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new FavoriteFragment()).addToBackStack(TAG).commit();
                break;

            case R.id.nav_reader_search:
                setTitle("Search");
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new SearchFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_subscriptions:
                setTitle("Subscriptions");
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new SubscriptionsFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_settings:
                setTitle("Settings");
                //TO DO
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new SettingsFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_sing_out:
                endSession();



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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("users").child(mUserId);
        mDataBase2 = FirebaseDatabase.getInstance().getReference("server").child("archivedusers").child(mUserId);
        mDataBase3 = FirebaseDatabase.getInstance().getReference("server").child("archivedgenreuser");
        mDataBase4 = FirebaseDatabase.getInstance().getReference("server").child("archivedcityuser");
        mDataBase5 = FirebaseDatabase.getInstance().getReference("server").child("archivedfavorites");



        //Transfer user data
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    mDataBase2.child(snapshot.getKey()).setValue(snapshot.getValue());
                    //Toast.makeText(BaseActivityReader.this, snapshot.getKey(), Toast.LENGTH_SHORT).show();
                }

                mDataBase.removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BaseActivityReader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Transfer user gender subscriptions.
        Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                .child("genreuser").orderByChild(mUserId).equalTo(true);
        currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener);


        currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                .child("cityuser").orderByChild(mUserId).equalTo(true);
        currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener2);

        currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                .child("favorites").orderByChild(mUserId).equalTo(true);
        currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener3);



        //Delete user
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG,"User deleted.");
                    endSession();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: couldn't delete user.", e);
                
            }
        });

        endSession();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Toast.makeText(this, "No from activity", Toast.LENGTH_SHORT).show();
    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDataBase3.child(snapshot.getKey()).child(mUserId).setValue(true);

                    snapshot.child(mUserId).getRef().removeValue();
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(BaseActivityReader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDataBase4.child(snapshot.getKey()).child(mUserId).setValue(true);

                    snapshot.child(mUserId).getRef().removeValue();
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(BaseActivityReader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    ValueEventListener valueEventListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDataBase5.child(snapshot.getKey()).child(mUserId).setValue(true);

                    snapshot.child(mUserId).getRef().removeValue();
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(BaseActivityReader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };



    public void endSession () {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(BaseActivityReader.this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();


        inflater.inflate(R.menu.search_menu_subs, menu);
        return super.onCreateOptionsMenu(menu);

    }
    */


}
