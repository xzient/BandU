package cmsc424.cmsc424_pg11;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.support.annotation.NonNull;

//My imports
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {   //Might need to change to extends appcompact activtty

    private GoogleMap mMap;
    Button location;
    Button coordinates;
    Double currentLat, currentLng;
    TextView message1;
    TextView message2;
    TextView message3;
    Toolbar toolbar;

    //Authentication
    private FirebaseAuth mAuth;
    public String mUserID;
    //Database
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("server").child("users");


    //Permission
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    //Location Provider
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(38.89511, -77.03637);
    private static final String TAG = MapsActivity.class.getSimpleName();
    private CameraPosition mCameraPosition;

    private static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("BandU");

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getUid();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //TextView
        message1 = findViewById(R.id.editText1);
        message2 = findViewById(R.id.editText2);
        message3 = findViewById(R.id.editText3);
        //Buttons
        //Location
        location = findViewById(R.id.mapLocation);
        //Coordinates
        coordinates = findViewById(R.id.mapGetCoordinates);




        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updateLocationUI();
                //getDeviceLocation();


                if (isLocationEnabled(MapsActivity.this)) {
                    updateLocationUI();
                    getDeviceLocation();
                }
                else {
                    Toast.makeText(MapsActivity.this, R.string.no_gps, Toast.LENGTH_SHORT).show();
                }


            }
        });


        coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getCoordinates();

                if (isLocationEnabled(MapsActivity.this)) {
                    getCoordinates();
                }
                else {
                    Toast.makeText(MapsActivity.this, R.string.no_gps, Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    /**
     * Is location enabled. Is GPS enabled or working?
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng washington = new LatLng(38.89511, -77.03637);
        //mMap.addMarker(new MarkerOptions().position(washington).title("Washington, DC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(washington));

        /*Other Stuff*/
        getLocationPermission();

        //updateLocationUI();

        //getDeviceLocation();
    }


    /**
     * This function will show the current latitute and longitute.
     * This function also updates the user's coordinates and city throgh get address.
     */
    void getCoordinates() {

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            currentLat = mLastKnownLocation.getLatitude();
                            currentLng = mLastKnownLocation.getLongitude();

                            message1.setText("Latitude: " + currentLat);
                            message2.setText("Longitude: " + currentLng);

                            getAddress(MapsActivity.this, currentLat, currentLng);

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //Get Wsahington DC default location.

                            currentLat = 38.89511;
                            currentLng = -77.03637;
                            message1.setText("Lat: " + currentLat);
                            message2.setText("Lng: " + currentLng);
                            Toast.makeText(MapsActivity.this, "Location not found. Set to default!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }




    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


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

            message3.setText("You are in " + obj.getLocality() + ", " + obj.getAdminArea());

            //Update user
            mRef.child(mUserID).child("city").setValue(obj.getLocality());
            mRef.child(mUserID).child("state").setValue(obj.getAdminArea());
            mRef.child(mUserID).child("latitude").setValue(lat);
            mRef.child(mUserID).child("longitude").setValue(lng);


            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}





