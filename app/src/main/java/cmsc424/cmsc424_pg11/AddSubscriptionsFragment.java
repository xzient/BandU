package cmsc424.cmsc424_pg11;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSubscriptionsFragment extends Fragment {

    private static final String TAG = "AddSubFragment";


    private ArrayList<String> mGenres = new ArrayList<>();
    private ArrayList<String> mLocations = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterSubscriptions adapter;
    private DatabaseReference mDataBase;
    private FirebaseUser mCurrentFirebaseUser;
    private String mUserId;

    private Map <String, String> mDBGenres;

    Button buttonGenres, buttonLocations;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_subscriptions, container, false);

        Log.d(TAG, "onCreateView: View created.");

        mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        mUserId = mCurrentFirebaseUser.getUid();

        buttonGenres = view.findViewById(R.id.button_add_subscriptions_genres);
        buttonLocations = view.findViewById(R.id.button_add_subscriptions_locations);



        mRecyclerView = view.findViewById(R.id.recycler_view_subscribe_to_genres);
        mRecyclerView.setHasFixedSize(true);

        adapter = new RecyclerViewAdapterSubscriptions(getContext(), mGenres);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));





        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("genres");
        mDataBase.keepSynced(true);

        //mDataBase.addListenerForSingleValueEvent(valueEventListener);

        buttonLocations.setOnClickListener(onClickListener);
        buttonGenres.setOnClickListener(onClickListener);


        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDBGenres = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                .child("genres").orderByChild("subscription").equalTo(mUserId);
        */
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

                    //mDataBase.addListenerForSingleValueEvent(valueEventListener);
                    Query currentGenresSubsQuery = FirebaseDatabase.getInstance().getReference("server")
                            .child("genreuser").orderByChild(mUserId).equalTo(null);

                    currentGenresSubsQuery.addListenerForSingleValueEvent(valueEventListener);

                    break;
                case R.id.button_add_subscriptions_locations:
                    Toast.makeText(getContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
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

            mGenres.clear();
            if (dataSnapshot.exists()) {
                //Toast.makeText(BaseActivityReader.this, "Hello", Toast.LENGTH_SHORT).show();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    mGenres.add(mDBGenres.get(snapshot.getKey()));

                }
                adapter.notifyDataSetChanged();

            }
        }



        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };




}
