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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeReaderFragment extends Fragment {

    private static final String TAG = "HomeReaderFragment";

    //Variables
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();

    private RecyclerView mRecyclerView;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        return view;
    }

    private void getData() {

        initReccyclerView();
    }


    private void initReccyclerView() {
        Log.d(TAG, "initReccyclerView: init recyclerview.");
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_reader2); // This might be wrong by view
        //RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext()); // to do
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//
    }



}
