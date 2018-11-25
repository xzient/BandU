package cmsc424.cmsc424_pg11;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    Bundle bundle;

    TextView mTitle, mMessage, mGenre;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        Log.d(TAG, "onCreateView: View created.");

        bundle = this.getArguments();

        mTitle = view.findViewById(R.id.event_fragment_title);
        mGenre = view.findViewById(R.id.event_fragment_genre);
        mMessage = view.findViewById(R.id.event_fragment_message);


        //Set Values
        if(bundle != null) {

            mTitle.setText(bundle.getString("Title"));
            mGenre.setText(bundle.getString("Genre"));
            mMessage.setText(bundle.getString("Message"));
        }



        return view;
    }



}
