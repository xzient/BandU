package cmsc424.cmsc424_pg11;

import android.app.FragmentManager;
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
import android.view.Menu;
import android.view.MenuInflater;
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
                setTitle("BandU");
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new HomeReaderFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_map:
                startActivity(new Intent(BaseActivityReader.this, MapsActivity.class));
                setTitle("Map");
                break;
            case R.id.nav_reader_search:
                setTitle("Search");
                //TO DO
                break;
            case R.id.nav_reader_subscriptions:
                setTitle("Subscriptions");
                getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, new SubscriptionsFragment()).addToBackStack(TAG).commit();
                break;
            case R.id.nav_reader_user_parameters:
                setTitle("User Parameters");
                break;
            case R.id.nav_reader_settings:
                setTitle("Settings");
                //TO DO
                break;


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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();


        inflater.inflate(R.menu.search_menu_subs, menu);
        return super.onCreateOptionsMenu(menu);

    }
    */
}
