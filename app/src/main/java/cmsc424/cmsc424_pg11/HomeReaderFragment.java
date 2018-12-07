package cmsc424.cmsc424_pg11;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class HomeReaderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeReaderFragment";

    //Variables
    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mVideos = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mAudios = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseGenres;
    private ArrayList<String> mUserGenres = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;

    Calendar currentTime;



    private Map <String, String> mDBGenres;

    private String mUserId;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_reader, container, false);

        Log.d(TAG, "onCreateView: Started.");

        //Get current time;
        currentTime = Calendar.getInstance();




        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("messages");//May get issues
        mDataBase.keepSynced(true);

        mDataBaseGenres = FirebaseDatabase.getInstance().getReference("server").child("genres");


        //SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mRecyclerView = view.findViewById(R.id.recycler_view_reader);
        mRecyclerView.setHasFixedSize(true);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadRecyclerViewData();
            }
        });

        return view;
    }//End view

    /**
     * When fragment is refreshed
     */
    @Override
    public void onRefresh() {
        loadRecyclerViewData();
        //Toast.makeText(getContext(), "Data on refresh", Toast.LENGTH_SHORT).show();
        //mDataBase.addListenerForSingleValueEvent(valueEventListener);
    }




    /**
     * This method will load all the data needed.
     */
    private void loadRecyclerViewData () {
        //Get current time
        currentTime = Calendar.getInstance();

        //Show time
        //Toast.makeText(getContext(), "" + Calendar.getInstance().get(Calendar.SECOND), Toast.LENGTH_SHORT).show();


        //Display refresh animation before retrieving data.
        mSwipeRefreshLayout.setRefreshing(true);
        //Get genres
        mDataBaseGenres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDBGenres = (Map<String, String>) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //User genres
        Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                .child("genreuser").orderByChild(mUserId).equalTo(true);

        currentGenresSubsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mUserGenres.add(snapshot.getKey());
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new RecyclerViewAdapter(getContext(), mTitles, mMessages, mGenres, mVideos, mImages, mAudios);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataBase.addListenerForSingleValueEvent(valueEventListener);

    }//End loadRecyclerViewData






    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            mTitles.clear();
            mMessages.clear();
            mGenres.clear();
            mVideos.clear();
            mImages.clear();
            mAudios.clear();

            if (dataSnapshot.exists()) {
                //Toast.makeText(BaseActivityReader.this, "Hello", Toast.LENGTH_SHORT).show();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    snapshot.getValue();


                    //If user subscribed to genre.
                    if (mUserGenres.contains(snapshot.child("genre").getValue(String.class))) {
                        mTitles.add(snapshot.child("title").getValue(String.class));
                        mMessages.add(snapshot.child("message").getValue(String.class));
                        mGenres.add(mDBGenres.get(snapshot.child("genre").getValue(String.class)));
                        mVideos.add(snapshot.child("video").getValue(String.class));
                        mImages.add(snapshot.child("image").getValue(String.class));
                        mAudios.add(snapshot.child("sound").getValue(String.class));
                    }
                }
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

}
