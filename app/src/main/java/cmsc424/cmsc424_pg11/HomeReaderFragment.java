package cmsc424.cmsc424_pg11;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeReaderFragment extends Fragment {

    private static final String TAG = "HomeReaderFragment";

    //Variables
    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;
    private DatabaseReference mDataBase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_reader, container, false);

        Log.d(TAG, "onCreateView: Started.");

        mDataBase = FirebaseDatabase.getInstance().getReference("server").child("messages");//May get issues
        mDataBase.keepSynced(true);

        mRecyclerView = view.findViewById(R.id.recycler_view_reader);
        mRecyclerView.setHasFixedSize(true);


        adapter = new RecyclerViewAdapter(getContext(), mTitles, mMessages, mGenres);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




        mDataBase.addListenerForSingleValueEvent(valueEventListener);

        return view;
    }

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

                    String singleMessage = snapshot.child("message").getValue(String.class);
                    mMessages.add(singleMessage);

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



}
